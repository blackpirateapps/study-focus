package com.studyfocus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,
    secondary = Gray600,
    onSecondary = White,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray800,
    tertiary = SuccessGreen,
    background = BackgroundLight,
    onBackground = Gray900,
    surface = SurfaceLight,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    outline = Gray300,
    outlineVariant = Gray200,
    error = ErrorRed,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue400,
    onPrimary = White,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Gray400,
    onSecondary = Gray900,
    secondaryContainer = Gray800,
    onSecondaryContainer = Gray200,
    tertiary = SuccessGreen,
    background = BackgroundDark,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray400,
    outline = Gray700,
    outlineVariant = Gray800,
    error = ErrorRed,
    onError = White,
)

@Composable
fun StudyFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = StudyFocusTypography,
        content = content
    )
}
