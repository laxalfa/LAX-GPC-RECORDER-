package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LaxDarkColorScheme =
  darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003840),
    onPrimaryContainer = CyanPrimary,
    secondary = CyanSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF002244),
    onSecondaryContainer = Color(0xFF99CCFF),
    tertiary = CyanTertiary,
    onTertiary = Color.Black,
    background = EsportsBlack,
    onBackground = CyanTextPrimary,
    surface = DarkGraphiteSurface,
    onSurface = CyanTextPrimary,
    surfaceVariant = DarkGraphiteCard,
    onSurfaceVariant = CyanTextSecondary,
    outline = DarkGraphiteCardBorder,
    outlineVariant = Color(0xFF1E283A),
    error = EsportsError,
    onError = Color.White,
    errorContainer = EsportsErrorContainer,
    onErrorContainer = Color(0xFFFFB4AB),
  )

@Composable
fun MyApplicationTheme(
  // LAX GPC RECORDER is an esports studio app with a signature dark theme
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = LaxDarkColorScheme,
    typography = Typography,
    content = content,
  )
}
