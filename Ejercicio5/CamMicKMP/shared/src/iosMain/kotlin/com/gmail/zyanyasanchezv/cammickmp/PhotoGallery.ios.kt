package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

private class IOSPhotoGalleryController :
    PhotoGalleryController {

    override val photos:
            List<SavedPhoto>
        get() = emptyList()

    override fun refresh() {
        // Se implementará con almacenamiento local de iOS.
    }
}

@Composable
actual fun rememberPhotoGalleryController():
        PhotoGalleryController {

    return remember {
        IOSPhotoGalleryController()
    }
}

@Composable
actual fun SavedPhotoThumbnail(
    path: String,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .background(
                MaterialTheme
                    .colorScheme
                    .surfaceVariant
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = "Foto iOS"
        )
    }
}