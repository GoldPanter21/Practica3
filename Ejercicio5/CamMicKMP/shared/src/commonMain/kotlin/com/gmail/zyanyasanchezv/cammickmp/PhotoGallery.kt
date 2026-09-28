package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class SavedPhoto(
    val path: String,
    val name: String,
    val lastModified: Long
)

interface PhotoGalleryController {

    val photos: List<SavedPhoto>

    fun refresh()
}

@Composable
expect fun rememberPhotoGalleryController():
        PhotoGalleryController

@Composable
expect fun SavedPhotoThumbnail(
    path: String,
    modifier: Modifier = Modifier
)