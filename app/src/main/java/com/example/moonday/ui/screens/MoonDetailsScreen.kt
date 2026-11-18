package com.example.moonday.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moonday.BubblyFontFamily
import com.example.moonday.R
import com.example.moonday.data.model.InterpretationInfo
import com.example.moonday.data.model.MoonDayItem
import com.example.moonday.data.model.MoonPhaseResponse
import com.example.moonday.ui.MoonUiState
import com.example.moonday.ui.components.MoonSvgView
import com.example.moonday.ui.components.MoonWeekRecyclerView
import com.example.moonday.ui.components.StarryHeader
import kotlinx.coroutines.launch

enum class DetailsTab(@param:StringRes val titleRes: Int, val icon: ImageVector) {
    PHASE(R.string.tab_phase, Icons.Default.Nightlight),
    ASTRONOMY(R.string.tab_astronomy, Icons.Default.Public),
    GUIDANCE(R.string.tab_guidance, Icons.Default.AutoAwesome),
    FORECAST(R.string.tab_forecast, Icons.Default.DateRange)
}

@Composable
fun MoonDetailsScreen(
    uiState: MoonUiState,
    weekDays: List<MoonDayItem>,
    onDayItemClick: (MoonDayItem) -> Unit,
    onBackClick: () -> Unit,
    onSelectAnotherDateClick: () -> Unit,
    onRetryClick: () -> Unit,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTabOrdinal by remember { mutableIntStateOf(DetailsTab.PHASE.ordinal) }
    val currentTab = DetailsTab.entries[selectedTabOrdinal]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StarryHeader(
                title = stringResource(R.string.details_header_title),
                onBackClick = onBackClick,
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        },
        bottomBar = {
            if (uiState is MoonUiState.Success) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    DetailsTab.entries.forEach { tab ->
                        val tabTitle = stringResource(tab.titleRes)
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { selectedTabOrdinal = tab.ordinal },
                            icon = { Icon(tab.icon, contentDescription = tabTitle) },
                            label = {
                                Text(
                                    text = tabTitle,
                                    fontSize = 14.sp,
                                    fontFamily = BubblyFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is MoonUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 5.dp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = stringResource(R.string.details_loading_text),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            fontFamily = BubblyFontFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is MoonUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.details_error_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BubblyFontFamily,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = uiState.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 18.sp,
                                    fontFamily = BubblyFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onRetryClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.details_retry_button),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BubblyFontFamily
                                    )
                                }
                            }
                        }
                    }
                }

                is MoonUiState.Success -> {
                    val currentData = uiState.response
                    val selectedDateStr = currentData.timestamp?.take(10)

                    val virtualPageCount = if (weekDays.isNotEmpty()) 1000 * weekDays.size else 1
                    val initialSelectedIdx = remember(weekDays) {
                        val idx = weekDays.indexOfFirst { it.calendarDate == selectedDateStr }
                        if (idx >= 0) idx else 0
                    }
                    val initialPage = if (weekDays.isNotEmpty()) (500 * weekDays.size) + initialSelectedIdx else 0

                    val pagerState = rememberPagerState(
                        initialPage = initialPage,
                        pageCount = { virtualPageCount }
                    )
                    val coroutineScope = rememberCoroutineScope()

                    // Sync Pager Page Swipe -> Day Highlight Selection with Loop Support
                    LaunchedEffect(pagerState.currentPage) {
                        if (weekDays.isNotEmpty()) {
                            val actualIdx = pagerState.currentPage % weekDays.size
                            val targetItem = weekDays[actualIdx]
                            if (targetItem.calendarDate != selectedDateStr) {
                                onDayItemClick(targetItem)
                            }
                        }
                    }

                    // Sync Top RecyclerView Tap -> Pager Smooth Scroll with Loop Math
                    LaunchedEffect(selectedDateStr) {
                        if (selectedDateStr != null && weekDays.isNotEmpty()) {
                            val targetIdx = weekDays.indexOfFirst { it.calendarDate == selectedDateStr }
                            if (targetIdx >= 0) {
                                val currentP = pagerState.currentPage
                                val currentIdx = currentP % weekDays.size
                                val diff = targetIdx - currentIdx
                                val targetPage = currentP + diff
                                if (targetPage != currentP) {
                                    pagerState.animateScrollToPage(targetPage)
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DataDisplayDate(selectedDateStr ?: currentData.timestamp)

                        // Top Weekly Horizontal RecyclerView
                        if (weekDays.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.details_weekly_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = BubblyFontFamily,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp)
                            )

                            MoonWeekRecyclerView(
                                weekDays = weekDays,
                                selectedDate = selectedDateStr,
                                isDarkTheme = isDarkTheme,
                                onDayClick = { item ->
                                    val targetIdx = weekDays.indexOf(item)
                                    if (targetIdx >= 0) {
                                        val currentP = pagerState.currentPage
                                        val currentIdx = currentP % weekDays.size
                                        val diff = targetIdx - currentIdx
                                        val targetPage = currentP + diff
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(targetPage)
                                        }
                                    }
                                    onDayItemClick(item)
                                },
                                height = 195.dp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        // Horizontal Pager for Looping Sliding Moon Cards
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { page ->
                            val actualPage = if (weekDays.isNotEmpty()) page % weekDays.size else 0
                            val pageItem = if (weekDays.isNotEmpty()) weekDays[actualPage] else null
                            val pageData = if (pageItem?.calendarDate == selectedDateStr) {
                                currentData
                            } else {
                                buildPhaseResponseForDayItem(pageItem, currentData)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (currentTab) {
                                    DetailsTab.PHASE -> PhaseTabContent(pageData, isDarkTheme)
                                    DetailsTab.ASTRONOMY -> AstronomyTabContent(pageData)
                                    DetailsTab.GUIDANCE -> GuidanceTabContent(pageData)
                                    DetailsTab.FORECAST -> ForecastTabContent(pageData)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onSelectAnotherDateClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.details_select_another_date_button),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BubblyFontFamily
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun DataDisplayDate(timestamp: String?) {
    timestamp?.let { ts ->
        Text(
            text = stringResource(R.string.details_date_label, ts),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            fontFamily = BubblyFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

private fun buildPhaseResponseForDayItem(item: MoonDayItem?, fallback: MoonPhaseResponse): MoonPhaseResponse {
    if (item == null) return fallback
    val dateStr = item.calendarDate ?: item.timestamp ?: fallback.timestamp
    val phaseName = item.phase?.name ?: "Moon Phase"
    val illumPercent = item.phase?.illumination?.let { (it * 100).toInt() } ?: 50

    val generatedInterpretation = InterpretationInfo(
        title = "$phaseName Insights",
        body = "$phaseName is $illumPercent% illuminated today. The moon is in ${item.zodiac?.sign ?: "the sky"}${item.phase?.ageDays?.let { " at an age of $it days" } ?: ""}. This is an optimal period for lunar tracking and astrological alignment.",
        tone = "mystical",
        tags = listOf("lunar", "astronomy")
    )

    return MoonPhaseResponse(
        timestamp = dateStr,
        phase = item.phase ?: fallback.phase,
        zodiac = item.zodiac ?: fallback.zodiac,
        moonVisual = item.moonVisual ?: fallback.moonVisual,
        traditionalMoon = item.traditionalMoon ?: fallback.traditionalMoon,
        interpretation = fallback.interpretation ?: generatedInterpretation,
        nextPhases = fallback.nextPhases,
        specialMoon = fallback.specialMoon
    )
}

@Composable
private fun PhaseTabContent(data: MoonPhaseResponse, isDarkTheme: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val svgData = data.moonVisual?.svg
            if (!svgData.isNullOrEmpty()) {
                MoonSvgView(
                    svgContent = svgData,
                    height = 250.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = data.phase?.name ?: stringResource(R.string.phase_default_name),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = BubblyFontFamily,
                color = MaterialTheme.colorScheme.onSurface
            )

            data.phase?.illumination?.let { illum ->
                val illumPercent = (illum * 100).toInt()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.phase_illumination_format, illumPercent),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BubblyFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            data.traditionalMoon?.name?.let { traditionalName ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.phase_traditional_name_format, traditionalName),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BubblyFontFamily,
                    color = if (isDarkTheme) Color(0xFFFDCB6E) else Color(0xFFD97706)
                )
            }

            data.specialMoon?.labels?.let { labels ->
                if (labels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.phase_special_features_format, labels.joinToString(", ")),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BubblyFontFamily,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun AstronomyTabContent(data: MoonPhaseResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(R.string.astronomy_title),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = BubblyFontFamily,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            data.phase?.let { p ->
                InfoRow(
                    label = stringResource(R.string.astronomy_moon_age),
                    value = stringResource(R.string.astronomy_moon_age_format, p.ageDays?.toString() ?: "-")
                )
                InfoRow(
                    label = stringResource(R.string.astronomy_distance),
                    value = stringResource(R.string.astronomy_distance_format, p.distanceKm?.toString() ?: "-")
                )
                InfoRow(
                    label = stringResource(R.string.astronomy_phase_angle),
                    value = stringResource(R.string.astronomy_phase_angle_format, p.phaseAngleDeg?.toString() ?: "-")
                )
                InfoRow(
                    label = stringResource(R.string.astronomy_waxing_waning),
                    value = if (p.isWaxing == true) stringResource(R.string.astronomy_waxing) else stringResource(R.string.astronomy_waning)
                )
            }

            data.zodiac?.let { z ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = MaterialTheme.colorScheme.outline
                )
                InfoRow(
                    label = stringResource(R.string.astronomy_tropical_zodiac),
                    value = stringResource(R.string.astronomy_zodiac_format, z.sign ?: "-", z.degree?.toString() ?: "-")
                )
            }
        }
    }
}

@Composable
private fun GuidanceTabContent(data: MoonPhaseResponse) {
    data.interpretation?.let { interp ->
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = interp.title ?: stringResource(R.string.guidance_default_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = BubblyFontFamily,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = interp.body ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontFamily = BubblyFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                )
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.guidance_no_data),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            fontFamily = BubblyFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun ForecastTabContent(data: MoonPhaseResponse) {
    data.nextPhases?.let { next ->
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.forecast_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = BubblyFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                next.newMoon?.let { InfoRow(stringResource(R.string.forecast_next_new_moon), it) }
                next.firstQuarter?.let { InfoRow(stringResource(R.string.forecast_first_quarter), it) }
                next.fullMoon?.let { InfoRow(stringResource(R.string.forecast_next_full_moon), it) }
                next.lastQuarter?.let { InfoRow(stringResource(R.string.forecast_last_quarter), it) }
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.forecast_no_data),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            fontFamily = BubblyFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            fontFamily = BubblyFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            fontFamily = BubblyFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
