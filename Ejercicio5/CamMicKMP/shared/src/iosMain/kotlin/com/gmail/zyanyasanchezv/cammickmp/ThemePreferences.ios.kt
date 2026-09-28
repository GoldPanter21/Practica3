package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private const val KEY_THEME =
    "institutional_theme"

private class IosThemePreferences :
    ThemePreferences {

    private val defaults =
        NSUserDefaults.standardUserDefaults

    override fun getTheme():
            InstitutionalTheme {

        val savedTheme =
            defaults.stringForKey(
                KEY_THEME
            )

        return try {

            if (savedTheme != null) {

                InstitutionalTheme
                    .valueOf(savedTheme)

            } else {

                InstitutionalTheme.GUINDA
            }

        } catch (_: Exception) {

            InstitutionalTheme.GUINDA
        }
    }

    override fun saveTheme(
        theme: InstitutionalTheme
    ) {

        defaults.setObject(
            theme.name,
            forKey = KEY_THEME
        )
    }
}

@Composable
actual fun rememberThemePreferences():
        ThemePreferences {

    return remember {
        IosThemePreferences()
    }
}