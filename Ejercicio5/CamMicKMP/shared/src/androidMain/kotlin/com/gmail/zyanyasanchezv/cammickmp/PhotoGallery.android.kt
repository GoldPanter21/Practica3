package com.gmail.zyanyasanchezv.cammickmp

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

private class AndroidPhotoGalleryController(
    private val photosDirectory: File
) : PhotoGalleryController {

    private var photoState by
    mutableStateOf<List<SavedPhoto>>(
        emptyList()
    )

    override val photos: List<SavedPhoto>
        get() = photoState

    override fun refresh() {

        if (!photosDirectory.exists()) {

            photoState = emptyList()
            return
        }

        photoState =
            photosDirectory
                .listFiles()
                ?.filter { file ->

                    file.isFile &&
                            (
                                    file.extension
                                        .equals(
                                            "jpg",
                                            ignoreCase = true
                                        ) ||
                                            file.extension
                                                .equals(
                                                    "jpeg",
                                                    ignoreCase = true
                                                )
                                    )
                }
                ?.sortedByDescending {
                    it.lastModified()
                }
                ?.map { file ->

                    SavedPhoto(
                        path =
                            file.absolutePath,
                        name =
                            file.name,
                        lastModified =
                            file.lastModified()
                    )
                }
                ?: emptyList()
    }
}

@Composable
actual fun rememberPhotoGalleryController():
        PhotoGalleryController {

    val context =
        LocalContext.current

    val controller =
        remember {

            AndroidPhotoGalleryController(
                photosDirectory =
                    File(
                        context
                            .applicationContext
                            .filesDir,
                        "photos"
                    )
            )
        }

    LaunchedEffect(controller) {

        controller.refresh()
    }

    return controller
}

@Composable
actual fun SavedPhotoThumbnail(
    path: String,
    modifier: Modifier
) {

    val bitmap =
        remember(path) {

            BitmapFactory
                .decodeFile(path)
                ?.asImageBitmap()
        }

    if (bitmap != null) {

        Image(
            bitmap = bitmap,
            contentDescription =
                "Fotografía guardada",
            modifier = modifier,
            contentScale =
                ContentScale.Crop
        )

    } else {

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
                text = "Sin vista previa"
            )
        }
    }
}
