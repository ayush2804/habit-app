package com.ayushpandey.habitapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Lagoon = Color(0xFF0E7C7B)
private val Coral = Color(0xFFFF6B4A)
private val Sun = Color(0xFFF4A259)
private val Ink = Color(0xFF14212B)
private val Foam = Color(0xFFF4FBFA)
private val SoftError = Color(0xFFE85D75)

private val LightColors = lightColorScheme(
    primary = Lagoon,
    onPrimary = Color.White,
    secondary = Coral,
    onSecondary = Color.White,
    tertiary = Sun,
    onTertiary = Ink,
    background = Foam,
    onBackground = Ink,
    surface = Color.White.copy(alpha = 0.92f),
    onSurface = Ink,
    surfaceVariant = Color(0xFFE0F2F1),
    onSurfaceVariant = Color(0xFF3A4A52),
    error = SoftError,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5ED1CF),
    onPrimary = Color(0xFF003736),
    secondary = Color(0xFFFF8F75),
    onSecondary = Color(0xFF3B0A00),
    tertiary = Color(0xFFFFC57A),
    onTertiary = Color(0xFF2A1A00),
    background = Color(0xFF0B1618),
    onBackground = Color(0xFFE7F4F3),
    surface = Color(0xFF132226),
    onSurface = Color(0xFFE7F4F3),
    error = SoftError,
    onError = Color.White
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp
    )
)

val AppBackdropBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFE7F8F7),
        Color(0xFFFFF5EE),
        Color(0xFFEAF3FF)
    )
)

val AppBackdropBrushDark = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0B1618),
        Color(0xFF122028),
        Color(0xFF1A1520)
    )
)

@Composable
fun HabitAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun AppBackdrop(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkTheme) AppBackdropBrushDark else AppBackdropBrush)
    ) {
        content()
    }
}
