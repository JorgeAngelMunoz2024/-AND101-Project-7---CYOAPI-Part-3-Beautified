package com.example.moonday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moonday.ui.MoonViewModel
import com.example.moonday.ui.screens.CalendarSelectScreen
import com.example.moonday.ui.screens.HomeScreen
import com.example.moonday.ui.screens.MoonDetailsScreen

enum class AppScreen {
    HOME,
    CALENDAR,
    DETAILS
}

val BubblyFontFamily = FontFamily(
    Font(R.font.fredoka, FontWeight.Normal),
    Font(R.font.fredoka, FontWeight.Medium),
    Font(R.font.fredoka, FontWeight.Bold),
    Font(R.font.fredoka, FontWeight.ExtraBold)
)

val DownloadableCinzelFontFamily = FontFamily(
    Font(R.font.cinzel, FontWeight.Bold)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C5CE7),
    primaryContainer = Color(0xFF4834D4),
    secondary = Color(0xFF74B9FF),
    background = Color(0xFF0D0E15),
    surface = Color(0xFF171822),
    surfaceVariant = Color(0xFF252638),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA0A5C0),
    outline = Color(0xFF2A2B3D),
    error = Color(0xFFFF7675)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6C5CE7),
    primaryContainer = Color(0xFF4834D4),
    secondary = Color(0xFF0284C7),
    background = Color(0xFFF4F5FB),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDE7F6),
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1B2F),
    onSurface = Color(0xFF1A1B2F),
    onSurfaceVariant = Color(0xFF5A607F),
    outline = Color(0xFFE2E8F0),
    error = Color(0xFFE11D48)
)

class MainActivity : ComponentActivity() {

    private val viewModel: MoonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            MoonDayTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoonDayApp(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            val newMode = !isDarkTheme
                            isDarkTheme = newMode
                            AppCompatDelegate.setDefaultNightMode(
                                if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MoonDayApp(
    viewModel: MoonViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var lastSelectedYear by remember { mutableStateOf(2026) }
    var lastSelectedMonth by remember { mutableStateOf(4) }
    var lastSelectedDay by remember { mutableStateOf(15) }

    val uiState by viewModel.uiState.collectAsState()
    val weekDays by viewModel.weekDays.collectAsState()

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                onEnterDateClick = {
                    currentScreen = AppScreen.CALENDAR
                },
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }

        AppScreen.CALENDAR -> {
            CalendarSelectScreen(
                onDateConfirmed = { year, month, day ->
                    lastSelectedYear = year
                    lastSelectedMonth = month
                    lastSelectedDay = day
                    viewModel.fetchMoonPhaseForDate(year, month, day)
                    currentScreen = AppScreen.DETAILS
                },
                onBackClick = {
                    currentScreen = AppScreen.HOME
                },
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }

        AppScreen.DETAILS -> {
            MoonDetailsScreen(
                uiState = uiState,
                weekDays = weekDays,
                onDayItemClick = { item ->
                    viewModel.selectDay(item)
                },
                onBackClick = {
                    currentScreen = AppScreen.CALENDAR
                },
                onSelectAnotherDateClick = {
                    currentScreen = AppScreen.CALENDAR
                },
                onRetryClick = {
                    viewModel.fetchMoonPhaseForDate(lastSelectedYear, lastSelectedMonth, lastSelectedDay)
                },
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
fun MoonDayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val bubblyTypography = Typography(
        displayLarge = TextStyle(fontFamily = DownloadableCinzelFontFamily, fontWeight = FontWeight.Bold, fontSize = 48.sp),
        titleLarge = TextStyle(fontFamily = DownloadableCinzelFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
        titleMedium = TextStyle(fontFamily = BubblyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        bodyLarge = TextStyle(fontFamily = BubblyFontFamily, fontWeight = FontWeight.Normal, fontSize = 18.sp),
        bodyMedium = TextStyle(fontFamily = BubblyFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        labelLarge = TextStyle(fontFamily = BubblyFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = bubblyTypography,
        content = content
    )
}
