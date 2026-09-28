package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private const val CATEGORY_PREFIX =
    "category_"

private class IosMediaCategoryRepository :
    MediaCategoryRepository {

    private val defaults =
        NSUserDefaults.standardUserDefaults

    override fun getCategory(
        mediaPath: String
    ): String {

        return defaults.stringForKey(
            CATEGORY_PREFIX + mediaPath
        ) ?: DEFAULT_MEDIA_CATEGORY
    }

    override fun saveCategory(
        mediaPath: String,
        category: String
    ) {

        defaults.setObject(
            category,
            forKey =
                CATEGORY_PREFIX + mediaPath
        )
    }
}

@Composable
actual fun rememberMediaCategoryRepository():
        MediaCategoryRepository {

    return remember {
        IosMediaCategoryRepository()
    }
}