package com.example.moonday.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moonday.BubblyFontFamily
import com.example.moonday.R
import com.example.moonday.ui.components.MoonAnimationView

@Composable
fun HomeScreen(
    onEnterDateClick: () -> Unit,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = stringResource(R.string.content_desc_theme_toggle),
                        tint = if (isDarkTheme) Color(0xFFFDCB6E) else Color(0xFF6C5CE7)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sparkle),
                        contentDescription = stringResource(R.string.content_desc_sparkle),
                        tint = if (isDarkTheme) Color(0xFFFDCB6E) else Color(0xFFD97706),
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = BubblyFontFamily,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_sparkle),
                        contentDescription = stringResource(R.string.content_desc_sparkle),
                        tint = if (isDarkTheme) Color(0xFFFDCB6E) else Color(0xFFD97706),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                fontFamily = BubblyFontFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        MoonAnimationView(
            size = 220.dp,
            isDarkTheme = isDarkTheme
        )

        Button(
            onClick = onEnterDateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_icon),
                contentDescription = stringResource(R.string.content_desc_calendar_icon),
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.home_enter_date_button),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BubblyFontFamily
            )
        }
    }
}
