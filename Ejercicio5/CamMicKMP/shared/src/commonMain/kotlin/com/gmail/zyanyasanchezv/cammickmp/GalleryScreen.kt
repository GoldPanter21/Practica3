package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GalleryFilter {
    ALL,
    PHOTOS,
    AUDIOS
}

private sealed interface GalleryMediaItem {

    val lastModified: Long
    val key: String

    data class Photo(
        val photo: SavedPhoto
    ) : GalleryMediaItem {

        override val lastModified: Long
            get() = photo.lastModified

        override val key: String
            get() = "photo_${photo.path}"
    }

    data class Audio(
        val audio: SavedAudio
    ) : GalleryMediaItem {

        override val lastModified: Long
            get() = audio.lastModified

        override val key: String
            get() = "audio_${audio.path}"
    }
}

@Composable
internal fun GalleryScreen(
    photoController:
    PhotoGalleryController,
    audioController:
    AudioController
) {

    var selectedFilter by remember {
        mutableStateOf(
            GalleryFilter.ALL
        )
    }

    val recordings by
    audioController
        .recordings
        .collectAsState()

    val playingPath by
    audioController
        .playingPath
        .collectAsState()

    LaunchedEffect(Unit) {

        photoController.refresh()

        audioController
            .refreshRecordings()
    }

    val galleryItems =
        remember(
            photoController.photos,
            recordings,
            selectedFilter
        ) {

            val photos =
                photoController
                    .photos
                    .map {
                        GalleryMediaItem.Photo(
                            it
                        )
                    }

            val audios =
                recordings
                    .map {
                        GalleryMediaItem.Audio(
                            it
                        )
                    }

            when (
                selectedFilter
            ) {

                GalleryFilter.ALL ->
                    photos + audios

                GalleryFilter.PHOTOS ->
                    photos

                GalleryFilter.AUDIOS ->
                    audios
            }
                .sortedByDescending {
                    it.lastModified
                }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "Galería",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            OutlinedButton(
                onClick = {

                    photoController
                        .refresh()

                    audioController
                        .refreshRecordings()
                }
            ) {

                Text(
                    text = "Actualizar"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )

        /*
         * Filtros
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            GalleryFilter.entries
                .forEach {
                        filter ->

                    val label =
                        when (filter) {

                            GalleryFilter.ALL ->
                                "Todo"

                            GalleryFilter.PHOTOS ->
                                "Fotos"

                            GalleryFilter.AUDIOS ->
                                "Audios"
                        }

                    if (
                        selectedFilter ==
                        filter
                    ) {

                        Button(
                            onClick = {
                                selectedFilter =
                                    filter
                            }
                        ) {

                            Text(label)
                        }

                    } else {

                        OutlinedButton(
                            onClick = {
                                selectedFilter =
                                    filter
                            }
                        ) {

                            Text(label)
                        }
                    }
                }
        }

        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )

        Text(
            text =
                "${photoController.photos.size} fotos · " +
                        "${recordings.size} audios",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .primary
        )

        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )

        if (
            galleryItems.isEmpty()
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        when (
                            selectedFilter
                        ) {

                            GalleryFilter.ALL ->
                                "Aún no hay contenido"

                            GalleryFilter.PHOTOS ->
                                "Aún no hay fotografías"

                            GalleryFilter.AUDIOS ->
                                "Aún no hay grabaciones"
                        },
                    textAlign =
                        TextAlign.Center
                )
            }

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                items(
                    items =
                        galleryItems,
                    key = {
                        it.key
                    }
                ) {
                        item ->

                    when (item) {

                        is GalleryMediaItem.Photo -> {

                            PhotoGalleryCard(
                                photo =
                                    item.photo
                            )
                        }

                        is GalleryMediaItem.Audio -> {

                            AudioGalleryCard(
                                audio =
                                    item.audio,
                                isPlaying =
                                    playingPath ==
                                            item.audio.path,
                                onPlayClick = {

                                    audioController
                                        .togglePlayback(
                                            item.audio.path
                                        )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoGalleryCard(
    photo: SavedPhoto
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            SavedPhotoThumbnail(
                path =
                    photo.path,
                modifier =
                    Modifier
                        .size(
                            96.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                12.dp
                            )
                        )
            )

            Column(
                modifier =
                    Modifier.padding(
                        start = 16.dp
                    )
            ) {

                Text(
                    text =
                        "📷 Fotografía",
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )

                Text(
                    text =
                        photo.name,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Guardada localmente",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }
        }
    }
}

@Composable
private fun AudioGalleryCard(
    audio: SavedAudio,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                16.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(
                            72.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        )
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "🎙",
                    fontSize =
                        34.sp
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(
                            1f
                        )
                        .padding(
                            start = 16.dp
                        )
            ) {

                Text(
                    text = "🎙 Audio",
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )

                Text(
                    text =
                        audio.name,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Guardado localmente",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

            Button(
                onClick =
                    onPlayClick
            ) {

                Text(
                    text =
                        if (
                            isPlaying
                        ) {
                            "■"
                        } else {
                            "▶"
                        }
                )
            }
        }
    }
}


