package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable

interface ThemePreferences {

    fun getTheme(): InstitutionalTheme

    fun saveTheme(
        theme: InstitutionalTheme
    )
}

@Composable
expect fun rememberThemePreferences():
        ThemePreferences