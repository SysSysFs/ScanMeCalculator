package com.example.scanmecalculator.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.scanmecalculator.BuildConfig

private val RedDarkColorPalette = darkColors(
    primary = Red200,
    primaryVariant = Red700,
    secondary = Brown200
)

private val RedLightColorPalette = lightColors(
    primary = Red500,
    primaryVariant = Red700,
    secondary = Brown200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val GreenDarkColorPalette = darkColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Purple200
)

private val GreenLightColorPalette = lightColors(
    primary = Green500,
    primaryVariant = Green700,
    secondary = Purple200
)


@Composable
fun ScanMeCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (BuildConfig.FLAVOR_themeMode) {
        "green" -> {
            if (darkTheme) {
                GreenDarkColorPalette
            } else {
                GreenLightColorPalette
            }
        }
        "red" -> {
            if (darkTheme) {
                RedDarkColorPalette
            } else {
                RedLightColorPalette
            }
        }
        else -> {
            if (darkTheme) {
                RedDarkColorPalette
            } else {
                RedLightColorPalette
            }
        }
    }

    CompositionLocalProvider(LocalSpacing provides Dimensions()) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}