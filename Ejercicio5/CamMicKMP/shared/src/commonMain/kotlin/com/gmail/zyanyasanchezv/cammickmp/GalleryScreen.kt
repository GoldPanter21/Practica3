package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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


private const val ALL_CATEGORIES =
    "Todas"


private sealed interface GalleryMediaItem {

    val lastModified: Long

    val key: String

    val path: String


    data class Photo(
        val photo: SavedPhoto
    ) : GalleryMediaItem {

        override val lastModified: Long
            get() =
                photo.lastModified

        override val key: String
            get() =
                "photo_${photo.path}"

        override val path: String
            get() =
                photo.path
    }


    data class Audio(
        val audio: SavedAudio
    ) : GalleryMediaItem {

        override val lastModified: Long
            get() =
                audio.lastModified

        override val key: String
            get() =
                "audio_${audio.path}"

        override val path: String
            get() =
                audio.path
    }
}


@Composable
internal fun GalleryScreen(
    photoController:
    PhotoGalleryController,
    audioController:
    AudioController,
    categoryRepository:
    MediaCategoryRepository
) {

    var selectedFilter by remember {
        mutableStateOf(
            GalleryFilter.ALL
        )
    }


    var selectedCategory by remember {
        mutableStateOf(
            ALL_CATEGORIES
        )
    }


    var categoryVersion by remember {
        mutableStateOf(0)
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

        photoController
            .refresh()

        audioController
            .refreshRecordings()
    }


    val galleryItems =
        remember(
            photoController.photos,
            recordings,
            selectedFilter,
            selectedCategory,
            categoryVersion
        ) {

            val photos =
                photoController
                    .photos
                    .map {
                        GalleryMediaItem
                            .Photo(it)
                    }


            val audios =
                recordings
                    .map {
                        GalleryMediaItem
                            .Audio(it)
                    }


            val filteredByType:
                    List<GalleryMediaItem> =
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


            filteredByType
                .filter { item ->

                    selectedCategory ==
                            ALL_CATEGORIES ||
                            categoryRepository
                                .getCategory(
                                    item.path
                                ) ==
                            selectedCategory
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

        /*
         * Encabezado
         */
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
         * Filtro por tipo de contenido
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            GalleryFilter
                .entries
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


        /*
         * Filtro por categoría
         */
        Text(
            text = "Categoría",
            fontWeight =
                FontWeight.SemiBold
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            val categories =
                listOf(
                    ALL_CATEGORIES
                ) +
                        MEDIA_CATEGORIES


            categories
                .forEach {
                        category ->

                    if (
                        selectedCategory ==
                        category
                    ) {

                        Button(
                            onClick = {

                                selectedCategory =
                                    category
                            }
                        ) {

                            Text(
                                text = category
                            )
                        }

                    } else {

                        OutlinedButton(
                            onClick = {

                                selectedCategory =
                                    category
                            }
                        ) {

                            Text(
                                text = category
                            )
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


        /*
         * Cantidad total almacenada
         */
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


        /*
         * Contenido de la galería
         */
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
                        when {

                            selectedCategory !=
                                    ALL_CATEGORIES ->

                                "No hay contenido en la categoría $selectedCategory"


                            selectedFilter ==
                                    GalleryFilter.PHOTOS ->

                                "Aún no hay fotografías"


                            selectedFilter ==
                                    GalleryFilter.AUDIOS ->

                                "Aún no hay grabaciones"


                            else ->

                                "Aún no hay contenido"
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
                                    item.photo,
                                category =
                                    categoryRepository
                                        .getCategory(
                                            item.photo.path
                                        ),
                                onCategorySelected = {
                                        category ->

                                    categoryRepository
                                        .saveCategory(
                                            mediaPath =
                                                item.photo.path,
                                            category =
                                                category
                                        )

                                    categoryVersion++
                                }
                            )
                        }


                        is GalleryMediaItem.Audio -> {

                            AudioGalleryCard(
                                audio =
                                    item.audio,
                                category =
                                    categoryRepository
                                        .getCategory(
                                            item.audio.path
                                        ),
                                isPlaying =
                                    playingPath ==
                                            item.audio.path,
                                onCategorySelected = {
                                        category ->

                                    categoryRepository
                                        .saveCategory(
                                            mediaPath =
                                                item.audio.path,
                                            category =
                                                category
                                        )

                                    categoryVersion++
                                },
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
    photo: SavedPhoto,
    category: String,
    onCategorySelected:
        (String) -> Unit
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


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                MediaCategorySelector(
                    category =
                        category,
                    onCategorySelected =
                        onCategorySelected
                )
            }
        }
    }
}


@Composable
private fun AudioGalleryCard(
    audio: SavedAudio,
    category: String,
    isPlaying: Boolean,
    onCategorySelected:
        (String) -> Unit,
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
                    text =
                        "🎙 Audio",
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


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                MediaCategorySelector(
                    category =
                        category,
                    onCategorySelected =
                        onCategorySelected
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


@Composable
private fun MediaCategorySelector(
    category: String,
    onCategorySelected:
        (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }


    Box {

        OutlinedButton(
            onClick = {

                expanded =
                    true
            }
        ) {

            Text(
                text =
                    "Categoría: $category"
            )
        }


        DropdownMenu(
            expanded =
                expanded,
            onDismissRequest = {

                expanded =
                    false
            }
        ) {

            MEDIA_CATEGORIES
                .forEach {
                        option ->

                    DropdownMenuItem(
                        text = {

                            Text(
                                text = option
                            )
                        },
                        onClick = {

                            onCategorySelected(
                                option
                            )

                            expanded =
                                false
                        }
                    )
                }
        }
    }
}
