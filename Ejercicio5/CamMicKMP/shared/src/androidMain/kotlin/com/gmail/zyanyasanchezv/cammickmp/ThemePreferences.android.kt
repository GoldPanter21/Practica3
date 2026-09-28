package com.gmail.zyanyasanchezv.cammickmp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val PREFERENCES_NAME =
    "cammic_preferences"

private const val KEY_THEME =
    "institutional_theme"

private class AndroidThemePreferences(
    context: Context
) : ThemePreferences {

    private val preferences =
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    override fun getTheme():
            InstitutionalTheme {

        val savedTheme =
            preferences.getString(
                KEY_THEME,
                InstitutionalTheme.GUINDA.name
            )

        return try {

            InstitutionalTheme.valueOf(
                savedTheme
                    ?: InstitutionalTheme.GUINDA.name
            )

        } catch (_: Exception) {

            InstitutionalTheme.GUINDA
        }
    }

    override fun saveTheme(
        theme: InstitutionalTheme
    ) {

        preferences
            .edit()
            .putString(
                KEY_THEME,
                theme.name
            )
            .apply()
    }
}

@Composable
actual fun rememberThemePreferences():
        ThemePreferences {

    val context =
        LocalContext.current

    return remember {

        AndroidThemePreferences(
            context.applicationContext
        )
    }
}