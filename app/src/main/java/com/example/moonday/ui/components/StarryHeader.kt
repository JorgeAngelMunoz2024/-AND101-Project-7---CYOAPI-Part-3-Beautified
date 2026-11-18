package com.example.moonday.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moonday.BubblyFontFamily
import com.example.moonday.R
import kotlin.random.Random

@Composable
fun StarryHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onThemeToggle: (() -> Unit)? = null,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier
) {
    val stars = remember {
        val random = Random(42)
        List(45) {
            Triple(
                random.nextFloat(),
                random.nextFloat(),
                random.nextFloat() * 2.5f + 1f
            )
        }
    }

    val headerGradient = if (isDarkTheme) {
        listOf(
            Color(0xFF2C1E62),
            Color(0xFF131428),
            Color(0xFF0D0E15)
        )
    } else {
        listOf(
            Color(0xFF6C5CE7),
            Color(0xFF818CF8),
            Color(0xFFC7D2FE)
        )
    }

    val starColor = if (isDarkTheme) Color.White else Color(0xFF4C1D95)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(colors = headerGradient))
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 14.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            stars.forEach { (xPct, yPct, radius) ->
                drawCircle(
                    color = starColor.copy(alpha = (0.3f + (xPct * 0.6f)).coerceIn(0.2f, 0.85f)),
                    radius = radius.dp.toPx(),
                    center = Offset(
                        x = size.width * xPct,
                        y = size.height * yPct
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.content_desc_back),
                        tint = Color.White
                    )
                }
            } else {
                Box(modifier = Modifier.padding(start = 48.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = BubblyFontFamily,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            if (onThemeToggle != null) {
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = stringResource(R.string.content_desc_theme_toggle),
                        tint = if (isDarkTheme) Color(0xFFFDCB6E) else Color(0xFF1E1B4B)
                    )
                }
            } else {
                Box(modifier = Modifier.padding(end = 48.dp))
            }
        }
    }
}
