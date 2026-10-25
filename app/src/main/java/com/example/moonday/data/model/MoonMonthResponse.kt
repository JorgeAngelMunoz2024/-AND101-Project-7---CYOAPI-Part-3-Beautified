package com.example.moonday.data.model

import com.google.gson.annotations.SerializedName

data class MoonMonthResponse(
    @SerializedName("year") val year: Int? = null,
    @SerializedName("month") val month: Int? = null,
    @SerializedName("month_name") val monthName: String? = null,
    @SerializedName("days_in_month") val daysInMonth: Int? = null,
    @SerializedName("days") val days: List<MoonDayItem>? = null
)

data class MoonDayItem(
    @SerializedName("calendar_date") val calendarDate: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("phase") val phase: PhaseInfo? = null,
    @SerializedName("moon_visual") val moonVisual: MoonVisualInfo? = null,
    @SerializedName("zodiac") val zodiac: ZodiacInfo? = null,
    @SerializedName("traditional_moon") val traditionalMoon: TraditionalMoonInfo? = null
)
