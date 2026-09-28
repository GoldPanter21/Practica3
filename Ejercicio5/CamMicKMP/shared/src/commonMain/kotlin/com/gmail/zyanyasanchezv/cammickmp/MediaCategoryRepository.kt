package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable

internal const val DEFAULT_MEDIA_CATEGORY =
    "General"

internal val MEDIA_CATEGORIES =
    listOf(
        "General",
        "Escuela",
        "Personal",
        "Proyecto"
    )

interface MediaCategoryRepository {

    fun getCategory(
        mediaPath: String
    ): String

    fun saveCategory(
        mediaPath: String,
        category: String
    )
}

@Composable
expect fun rememberMediaCategoryRepository():
        MediaCategoryRepository
