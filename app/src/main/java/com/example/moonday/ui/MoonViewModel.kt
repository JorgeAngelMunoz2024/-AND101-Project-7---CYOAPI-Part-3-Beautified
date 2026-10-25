package com.example.moonday.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moonday.data.model.InterpretationInfo
import com.example.moonday.data.model.MoonDayItem
import com.example.moonday.data.model.MoonPhaseResponse
import com.example.moonday.data.model.MoonVisualInfo
import com.example.moonday.data.model.PhaseInfo
import com.example.moonday.data.model.TraditionalMoonInfo
import com.example.moonday.data.model.ZodiacInfo
import com.example.moonday.data.repository.MoonRepository
import com.example.moonday.data.repository.MoonRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MoonViewModel(
    private val repository: MoonRepository = MoonRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MoonUiState>(MoonUiState.Idle)
    val uiState: StateFlow<MoonUiState> = _uiState.asStateFlow()

    private val _weekDays = MutableStateFlow<List<MoonDayItem>>(emptyList())
    val weekDays: StateFlow<List<MoonDayItem>> = _weekDays.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    // Persistent in-memory cache mapping date string (YYYY-MM-DD) to MoonPhaseResponse
    private val cachedPhaseResponses = mutableMapOf<String, MoonPhaseResponse>()

    fun fetchMoonPhase(date: String? = null) {
        if (date != null && date.contains("-")) {
            val parts = date.split("-")
            if (parts.size == 3) {
                val y = parts[0].toIntOrNull() ?: 2026
                val m = parts[1].toIntOrNull() ?: 4
                val d = parts[2].toIntOrNull() ?: 15
                fetchMoonPhaseForDate(y, m, d)
                return
            }
        }
        fetchMoonPhaseForDate(2026, 4, 15)
    }

    fun fetchMoonPhaseForDate(year: Int, month: Int, dayOfMonth: Int) {
        val targetDateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayOfMonth)
        _selectedDate.value = targetDateStr

        val weekDates = generateWeekDates(year, month, dayOfMonth)

        // If target date is already in cache, return immediately with 0 API requests
        if (cachedPhaseResponses.containsKey(targetDateStr)) {
            val cachedTarget = cachedPhaseResponses[targetDateStr]!!
            generateWeekDaysFromCacheAndModel(weekDates, cachedTarget)
            _uiState.value = MoonUiState.Success(cachedTarget)
            return
        }

        viewModelScope.launch {
            _uiState.value = MoonUiState.Loading

            // Try live API first; if rate-limited (HTTP 429) or offline, fallback to local calculation
            val apiResult = repository.getMoonPhase(targetDateStr)
            val response = apiResult.getOrElse {
                calculateLocalMoonPhase(targetDateStr)
            }

            cachedPhaseResponses[targetDateStr] = response
            generateWeekDaysFromCacheAndModel(weekDates, response)
            _uiState.value = MoonUiState.Success(response)
        }
    }

    private fun generateWeekDaysFromCacheAndModel(weekDates: List<String>, baseResponse: MoonPhaseResponse) {
        val baseAge = baseResponse.phase?.ageDays ?: 15.0
        val baseIllum = baseResponse.phase?.illumination ?: 0.5
        val baseWaxing = baseResponse.phase?.isWaxing ?: true

        val items = mutableListOf<MoonDayItem>()

        weekDates.forEachIndexed { index, dateStr ->
            val offset = index - 3 // -3, -2, -1, 0, 1, 2, 3

            if (cachedPhaseResponses.containsKey(dateStr)) {
                val cached = cachedPhaseResponses[dateStr]!!
                items.add(
                    MoonDayItem(
                        calendarDate = dateStr,
                        timestamp = cached.timestamp,
                        phase = cached.phase,
                        moonVisual = cached.moonVisual,
                        zodiac = cached.zodiac,
                        traditionalMoon = cached.traditionalMoon
                    )
                )
            } else {
                val derivedAge = (baseAge + offset + 29.53) % 29.53
                val derivedIllum = (baseIllum + (offset * 0.033)).coerceIn(0.05, 0.99)
                val derivedPhaseName = determinePhaseName(derivedAge)

                val derivedPhase = PhaseInfo(
                    name = derivedPhaseName,
                    illumination = derivedIllum,
                    ageDays = derivedAge,
                    isWaxing = baseWaxing
                )

                val derivedResponse = MoonPhaseResponse(
                    timestamp = dateStr,
                    phase = derivedPhase,
                    zodiac = baseResponse.zodiac,
                    moonVisual = baseResponse.moonVisual,
                    traditionalMoon = baseResponse.traditionalMoon,
                    interpretation = InterpretationInfo(
                        title = "$derivedPhaseName Insights",
                        body = "$derivedPhaseName is ${(derivedIllum * 100).toInt()}% illuminated on $dateStr.",
                        tone = "mystical"
                    )
                )

                cachedPhaseResponses[dateStr] = derivedResponse

                items.add(
                    MoonDayItem(
                        calendarDate = dateStr,
                        timestamp = dateStr,
                        phase = derivedPhase,
                        moonVisual = baseResponse.moonVisual,
                        zodiac = baseResponse.zodiac,
                        traditionalMoon = baseResponse.traditionalMoon
                    )
                )
            }
        }

        _weekDays.value = items
    }

    private fun calculateLocalMoonPhase(dateStr: String): MoonPhaseResponse {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = try { sdf.parse(dateStr) } catch (_: Exception) { null } ?: Date()

        val epochMillis = 947182440000L // Jan 6, 2000 New Moon epoch
        val diffMillis = date.time - epochMillis
        val diffDays = diffMillis / (1000.0 * 60 * 60 * 24)
        val synodicMonth = 29.53058867
        val moonAge = (diffDays % synodicMonth + synodicMonth) % synodicMonth

        val angle = (moonAge / synodicMonth) * 2.0 * Math.PI
        val illumination = (1.0 - Math.cos(angle)) / 2.0
        val phaseAngleDeg = (moonAge / synodicMonth) * 360.0
        val isWaxing = moonAge <= 14.765

        val phaseName = determinePhaseName(moonAge)
        val distanceKm = (384400 + 21000 * Math.cos(angle)).toLong()
        val svgContent = generateMoonSvg(illumination, isWaxing)

        val zodiacSigns = listOf("Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces")
        val zodiacIdx = ((diffDays / 2.5) % 12).toInt().let { Math.abs(it) % 12 }
        val zodiacSign = zodiacSigns[zodiacIdx]

        val interpretation = InterpretationInfo(
            title = "$phaseName Insights",
            body = "$phaseName is ${(illumination * 100).toInt()}% illuminated on $dateStr. The moon resides in $zodiacSign at an age of ${String.format(Locale.US, "%.1f", moonAge)} days. This period brings focus to reflection, balance, and cosmic alignment.",
            tone = "mystical",
            tags = listOf("lunar", "astronomy", zodiacSign.lowercase())
        )

        return MoonPhaseResponse(
            timestamp = dateStr,
            phase = PhaseInfo(
                name = phaseName,
                illumination = (illumination * 100).toInt() / 100.0,
                ageDays = (moonAge * 10).toInt() / 10.0,
                distanceKm = distanceKm,
                phaseAngleDeg = (phaseAngleDeg * 10).toInt() / 10.0,
                isWaxing = isWaxing
            ),
            zodiac = ZodiacInfo(
                sign = zodiacSign,
                degree = 15.0
            ),
            moonVisual = MoonVisualInfo(
                type = "svg",
                svg = svgContent
            ),
            traditionalMoon = TraditionalMoonInfo(
                name = "Celestial Moon"
            ),
            interpretation = interpretation
        )
    }

    private fun generateMoonSvg(illumination: Double, isWaxing: Boolean): String {
        val r = 100
        val cx = 100
        val cy = 100
        val rx = (r * Math.abs(2.0 * illumination - 1.0)).toInt()

        val pathD = if (illumination >= 0.5) {
            if (isWaxing) {
                "M 100 0 A 100 100 0 1 1 100 200 A $rx 100 0 0 0 100 0 Z"
            } else {
                "M 100 0 A 100 100 0 1 0 100 200 A $rx 100 0 0 1 100 0 Z"
            }
        } else {
            if (isWaxing) {
                "M 100 0 A 100 100 0 0 1 100 200 A $rx 100 0 0 0 100 0 Z"
            } else {
                "M 100 0 A 100 100 0 0 0 100 200 A $rx 100 0 0 1 100 0 Z"
            }
        }

        return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 200 200\" width=\"200\" height=\"200\">" +
                "<circle cx=\"$cx\" cy=\"$cy\" r=\"$r\" fill=\"#1B1C2A\" />" +
                "<path d=\"$pathD\" fill=\"#FDE047\" />" +
                "</svg>"
    }

    private fun determinePhaseName(ageDays: Double): String {
        return when {
            ageDays < 1.84 -> "New Moon"
            ageDays < 5.53 -> "Waxing Crescent"
            ageDays < 9.22 -> "First Quarter"
            ageDays < 12.91 -> "Waxing Gibbous"
            ageDays < 16.61 -> "Full Moon"
            ageDays < 20.30 -> "Waning Gibbous"
            ageDays < 23.99 -> "Last Quarter"
            ageDays < 27.68 -> "Waning Crescent"
            else -> "New Moon"
        }
    }

    fun selectDay(item: MoonDayItem) {
        val dateStr = item.calendarDate ?: item.timestamp ?: return
        _selectedDate.value = dateStr

        val cachedResponse = cachedPhaseResponses[dateStr]
        if (cachedResponse != null) {
            _uiState.value = MoonUiState.Success(cachedResponse)
            return
        }

        val phaseName = item.phase?.name ?: "Moon Phase"
        val illumPercent = item.phase?.illumination?.let { (it * 100).toInt() } ?: 50

        val generatedInterpretation = InterpretationInfo(
            title = "$phaseName Insights",
            body = "$phaseName is $illumPercent% illuminated today.",
            tone = "mystical"
        )

        val selectedPhaseResponse = MoonPhaseResponse(
            timestamp = dateStr,
            phase = item.phase,
            zodiac = item.zodiac,
            moonVisual = item.moonVisual,
            traditionalMoon = item.traditionalMoon,
            interpretation = generatedInterpretation
        )

        _uiState.value = MoonUiState.Success(selectedPhaseResponse)
    }

    fun getCachedResponseForDate(dateStr: String?): MoonPhaseResponse? {
        return if (dateStr != null) cachedPhaseResponses[dateStr] else null
    }

    private fun generateWeekDates(year: Int, month: Int, dayOfMonth: Int): List<String> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dates = mutableListOf<String>()
        for (i in -3..3) {
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, i)
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH) + 1
            val d = cal.get(Calendar.DAY_OF_MONTH)
            dates.add(String.format(Locale.US, "%04d-%02d-%02d", y, m, d))
        }
        return dates
    }
}
