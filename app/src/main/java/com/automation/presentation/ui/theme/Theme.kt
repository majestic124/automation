package com.automation.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RawColors.blue.getValue("400"),
    onPrimary = RawColors.grey.getValue("900"),

    primaryContainer = RawColors.green.getValue("500"),
    onPrimaryContainer = RawColors.grey.getValue("0"),

    secondaryContainer = RawColors.blue.getValue("50"),
    onSecondaryContainer = RawColors.grey.getValue("900"),

    background = RawColors.grey.getValue("800"),
    onBackground = RawColors.grey.getValue("100"),

    onError = RawColors.red.getValue("700"),

    outline = RawColors.grey.getValue("700"),
)

private val LightColorScheme = lightColorScheme(
    primary = RawColors.blue.getValue("600"),
    onPrimary = RawColors.grey.getValue("0"),

    primaryContainer = RawColors.green.getValue("400"),
    onPrimaryContainer = RawColors.grey.getValue("0"),

    secondaryContainer = RawColors.blue.getValue("50"),
    onSecondaryContainer = RawColors.grey.getValue("900"),

    background = RawColors.grey.getValue("100"),
    onBackground = RawColors.grey.getValue("900"),

    onError = RawColors.red.getValue("700"),

    outline = RawColors.grey.getValue("200"),
)

@Composable
fun AutoKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
//            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}