package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.collectAsState

enum class AppScreen(
    val title: String,
    val symbol: String
) {
    INICIO("Inicio", "⌂"),
    CAMARA("Cámara", "📷"),
    AUDIO("Audio", "🎙"),
    GALERIA("Galería", "▦"),
    AJUSTES("Ajustes", "⚙")
}

@Composable
@Preview
fun App() {
    var selectedScreen by remember {
        mutableStateOf(AppScreen.INICIO)
    }

    val themePreferences =
        rememberThemePreferences()

    var selectedTheme by remember {
        mutableStateOf(
            themePreferences.getTheme()
        )
    }

    val permissionController =
        rememberMediaPermissionController()

    val cameraController =
        rememberCameraController()

    val photoGalleryController =
        rememberPhotoGalleryController()

    val audioController =
        rememberAudioController()

    val mediaCategoryRepository =
        rememberMediaCategoryRepository()

    CamMicTheme(
        institutionalTheme = selectedTheme
    ) {
        MainScreen(
            selectedScreen = selectedScreen,
            selectedTheme = selectedTheme,
            permissionController = permissionController,
            cameraController = cameraController,
            photoGalleryController = photoGalleryController,
            audioController = audioController,
            mediaCategoryRepository =
                mediaCategoryRepository,
            onScreenSelected = {
                selectedScreen = it
            },
            onThemeSelected = { theme ->

                selectedTheme =
                    theme

                themePreferences
                    .saveTheme(theme)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    selectedScreen: AppScreen,
    selectedTheme: InstitutionalTheme,
    permissionController: MediaPermissionController,
    cameraController: CameraController,
    photoGalleryController: PhotoGalleryController,
    audioController: AudioController,
    mediaCategoryRepository: MediaCategoryRepository,
    onScreenSelected: (AppScreen) -> Unit,
    onThemeSelected: (InstitutionalTheme) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CamMic KMP",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = selectedScreen.title,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                AppScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = selectedScreen == screen,
                        onClick = {
                            onScreenSelected(screen)
                        },
                        icon = {
                            Text(
                                text = screen.symbol,
                                fontSize = 18.sp
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedScreen) {

                AppScreen.INICIO ->
                    HomeScreen()

                AppScreen.CAMARA -> {

                    if (
                        permissionController
                            .cameraGranted
                    ) {

                        CameraScreen(
                            cameraController =
                                cameraController
                        )

                    } else {

                        PermissionFeatureScreen(
                            title = "Cámara",
                            symbol = "📷",
                            description =
                                "Permite capturar fotografías, aplicar filtros, utilizar flash y configurar un temporizador.",
                            permissionGranted = false,
                            permissionName = "cámara",
                            onRequestPermission = {
                                permissionController
                                    .requestCamera()
                            }
                        )
                    }
                }

                AppScreen.AUDIO -> {

                    if (
                        permissionController
                            .microphoneGranted
                    ) {

                        AudioScreen(
                            controller =
                                audioController
                        )

                    } else {

                        PermissionFeatureScreen(
                            title = "Micrófono",
                            symbol = "🎙",
                            description =
                                "Permite grabar audio, consultar el nivel de entrada y utilizar un temporizador.",
                            permissionGranted =
                                false,
                            permissionName =
                                "micrófono",
                            onRequestPermission = {

                                permissionController
                                    .requestMicrophone()
                            }
                        )
                    }
                }

                AppScreen.GALERIA ->
                    GalleryScreen(
                        photoController =
                            photoGalleryController,
                        audioController =
                            audioController,
                        categoryRepository =
                            mediaCategoryRepository
                    )

                AppScreen.AJUSTES ->
                    SettingsScreen(
                        selectedTheme = selectedTheme,
                        onThemeSelected = onThemeSelected
                    )
            }
        }
    }
}
