package com.gmail.zyanyasanchezv.cammickmp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val PREFERENCES_NAME =
    "cammic_media_categories"

private const val CATEGORY_PREFIX =
    "category_"

private class AndroidMediaCategoryRepository(
    context: Context
) : MediaCategoryRepository {

    private val preferences =
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    override fun getCategory(
        mediaPath: String
    ): String {

        return preferences.getString(
            CATEGORY_PREFIX + mediaPath,
            DEFAULT_MEDIA_CATEGORY
        ) ?: DEFAULT_MEDIA_CATEGORY
    }

    override fun saveCategory(
        mediaPath: String,
        category: String
    ) {

        preferences
            .edit()
            .putString(
                CATEGORY_PREFIX + mediaPath,
                category
            )
            .apply()
    }
}

@Composable
actual fun rememberMediaCategoryRepository():
        MediaCategoryRepository {

    val context =
        LocalContext.current

    return remember {

        AndroidMediaCategoryRepository(
            context.applicationContext
        )
    }
}