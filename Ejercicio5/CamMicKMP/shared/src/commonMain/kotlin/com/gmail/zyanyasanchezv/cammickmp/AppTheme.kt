package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class InstitutionalTheme {
    GUINDA,
    AZUL
}

// Colores institucionales base
private val Guinda = Color(0xFF6C1D45)
private val GuindaClaro = Color(0xFF9D4F73)
private val GuindaOscuro = Color(0xFF3E0F27)

private val Azul = Color(0xFF006699)
private val AzulClaro = Color(0xFF4B97C3)
private val AzulOscuro = Color(0xFF003B5C)

private val GuindaLightColors = lightColorScheme(
    primary = Guinda,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF2D8E5),
    onPrimaryContainer = GuindaOscuro,
    secondary = GuindaClaro,
    background = Color(0xFFFFF8FA),
    surface = Color(0xFFFFF8FA)
)

private val GuindaDarkColors = darkColorScheme(
    primary = Color(0xFFE6AEC9),
    onPrimary = GuindaOscuro,
    primaryContainer = Guinda,
    onPrimaryContainer = Color.White,
    secondary = GuindaClaro
)

private val AzulLightColors = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4ECF8),
    onPrimaryContainer = AzulOscuro,
    secondary = AzulClaro,
    background = Color(0xFFF7FAFC),
    surface = Color(0xFFF7FAFC)
)

private val AzulDarkColors = darkColorScheme(
    primary = Color(0xFF8CC9EA),
    onPrimary = AzulOscuro,
    primaryContainer = Azul,
    onPrimaryContainer = Color.White,
    secondary = AzulClaro
)

@Composable
fun CamMicTheme(
    institutionalTheme: InstitutionalTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (institutionalTheme) {
        InstitutionalTheme.GUINDA ->
            if (darkTheme) GuindaDarkColors else GuindaLightColors

        InstitutionalTheme.AZUL ->
            if (darkTheme) AzulDarkColors else AzulLightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}