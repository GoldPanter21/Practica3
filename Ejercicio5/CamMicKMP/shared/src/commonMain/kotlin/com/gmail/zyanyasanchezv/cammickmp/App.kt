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

    var selectedTheme by remember {
        mutableStateOf(InstitutionalTheme.GUINDA)
    }

    val permissionController =
        rememberMediaPermissionController()

    val cameraController =
        rememberCameraController()

    val photoGalleryController =
        rememberPhotoGalleryController()

    CamMicTheme(
        institutionalTheme = selectedTheme
    ) {
        MainScreen(
            selectedScreen = selectedScreen,
            selectedTheme = selectedTheme,
            permissionController = permissionController,
            cameraController = cameraController,
            photoGalleryController = photoGalleryController,
            onScreenSelected = {
                selectedScreen = it
            },
            onThemeSelected = {
                selectedTheme = it
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

                AppScreen.AUDIO ->
                    PermissionFeatureScreen(
                        title = "Micrófono",
                        symbol = "🎙",
                        description =
                            "Permite grabar audio, consultar el nivel de entrada y utilizar un temporizador.",
                        permissionGranted =
                            permissionController.microphoneGranted,
                        permissionName = "micrófono",
                        onRequestPermission = {
                            permissionController.requestMicrophone()
                        }
                    )

                AppScreen.GALERIA ->
                    GalleryScreen(
                        controller =
                            photoGalleryController
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

@Composable
private fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "CamMic KMP",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Cámara y micrófono multiplataforma",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        FeatureCard(
            symbol = "📷",
            title = "Cámara",
            description = "Captura y administra fotografías."
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        FeatureCard(
            symbol = "🎙",
            title = "Audio",
            description = "Graba y reproduce archivos de audio."
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        FeatureCard(
            symbol = "▦",
            title = "Galería",
            description = "Consulta todo tu contenido local."
        )
    }
}

@Composable
private fun FeatureCard(
    symbol: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = symbol,
                fontSize = 34.sp,
                modifier = Modifier.size(52.dp)
            )

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FeatureScreen(
    title: String,
    symbol: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            fontSize = 72.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Funcionalidad en desarrollo",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PermissionFeatureScreen(
    title: String,
    symbol: String,
    description: String,
    permissionGranted: Boolean,
    permissionName: String,
    onRequestPermission: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = symbol,
            fontSize = 72.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        if (permissionGranted) {

            Text(
                text = "Permiso concedido ✓",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

        } else {

            Text(
                text = "Se requiere permiso de $permissionName",
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onRequestPermission
            ) {

                Text(
                    text = "Conceder permiso"
                )
            }
        }
    }
}

@Composable
private fun CameraScreen(
    cameraController:
    CameraController
) {

    var captureMessage by remember {
        mutableStateOf<String?>(null)
    }

    var timerSeconds by remember {
        mutableStateOf(0)
    }

    var countdown by remember {
        mutableStateOf(0)
    }

    var isCapturing by remember {
        mutableStateOf(false)
    }

    val coroutineScope =
        rememberCoroutineScope()

    val flashText =
        when (
            cameraController
                .flashMode
        ) {

            CameraFlashMode.OFF ->
                "⚡ Apagado"

            CameraFlashMode.ON ->
                "⚡ Encendido"

            CameraFlashMode.AUTO ->
                "⚡ Auto"
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "Cámara",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(
                        4f / 5f
                    )
        ) {

            CameraPreview(
                controller =
                    cameraController,

                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                20.dp
                            )
                        )
            )

            if (
                countdown > 0
            ) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            countdown
                                .toString(),
                        fontSize =
                            80.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )

        /*
         * Controles de cámara
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement
                    .SpaceEvenly
        ) {

            OutlinedButton(
                onClick = {

                    cameraController
                        .toggleCamera()
                }
            ) {

                Text(
                    text =
                        if (
                            cameraController
                                .facing ==
                            CameraFacing.BACK
                        ) {
                            "🔄 Frontal"
                        } else {
                            "🔄 Trasera"
                        }
                )
            }

            OutlinedButton(
                enabled =
                    cameraController
                        .facing ==
                            CameraFacing.BACK,

                onClick = {

                    val nextMode =
                        when (
                            cameraController
                                .flashMode
                        ) {

                            CameraFlashMode.OFF ->
                                CameraFlashMode.ON

                            CameraFlashMode.ON ->
                                CameraFlashMode.AUTO

                            CameraFlashMode.AUTO ->
                                CameraFlashMode.OFF
                        }

                    cameraController
                        .setFlashMode(
                            nextMode
                        )
                }
            ) {

                Text(
                    text =
                        flashText
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )

        /*
         * Temporizador
         */
        Text(
            text =
                "Temporizador",
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement
                    .SpaceEvenly
        ) {

            listOf(
                0,
                3,
                5
            ).forEach {
                    seconds ->

                if (
                    timerSeconds ==
                    seconds
                ) {

                    Button(
                        onClick = {
                            timerSeconds =
                                seconds
                        }
                    ) {

                        Text(
                            text =
                                if (
                                    seconds == 0
                                ) {
                                    "Sin timer"
                                } else {
                                    "${seconds}s"
                                }
                        )
                    }

                } else {

                    OutlinedButton(
                        onClick = {
                            timerSeconds =
                                seconds
                        }
                    ) {

                        Text(
                            text =
                                if (
                                    seconds == 0
                                ) {
                                    "Sin timer"
                                } else {
                                    "${seconds}s"
                                }
                        )
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(
                    14.dp
                )
        )

        Button(
            enabled =
                !isCapturing,

            onClick = {

                isCapturing =
                    true

                captureMessage =
                    null

                coroutineScope
                    .launch {

                        if (
                            timerSeconds >
                            0
                        ) {

                            for (
                            second in
                            timerSeconds
                                    downTo 1
                            ) {

                                countdown =
                                    second

                                delay(
                                    1000
                                )
                            }

                            countdown =
                                0
                        }

                        captureMessage =
                            "Capturando..."

                        cameraController
                            .capturePhoto {
                                    result ->

                                captureMessage =
                                    if (
                                        result.success
                                    ) {

                                        "✓ ${result.message}"

                                    } else {

                                        "Error: ${result.message}"
                                    }

                                isCapturing =
                                    false
                            }
                    }
            }
        ) {

            Text(
                text =
                    if (
                        isCapturing
                    ) {
                        "Procesando..."
                    } else {
                        "📸 Capturar fotografía"
                    }
            )
        }

        captureMessage
            ?.let {
                    message ->

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )

                Text(
                    text =
                        message,
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,
                    textAlign =
                        TextAlign.Center,
                    fontWeight =
                        FontWeight
                            .SemiBold
                )
            }
    }
}

@Composable
private fun GalleryScreen(
    controller: PhotoGalleryController
) {

    LaunchedEffect(Unit) {
        controller.refresh()
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
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Button(
                onClick = {
                    controller.refresh()
                }
            ) {

                Text(
                    text = "Actualizar"
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        if (controller.photos.isEmpty()) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "Aún no hay fotografías",
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
                        controller.photos,
                    key = { photo ->
                        photo.path
                    }
                ) { photo ->

                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
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
                                        photo.name,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            4.dp
                                        )
                                )

                                Text(
                                    text =
                                        "Guardada localmente",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    selectedTheme: InstitutionalTheme,
    onThemeSelected: (InstitutionalTheme) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Apariencia",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Selecciona el tema institucional de la aplicación."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        ThemeOption(
            title = "Guinda IPN",
            selected =
                selectedTheme == InstitutionalTheme.GUINDA,
            onClick = {
                onThemeSelected(
                    InstitutionalTheme.GUINDA
                )
            }
        )

        HorizontalDivider()

        ThemeOption(
            title = "Azul ESCOM",
            selected =
                selectedTheme == InstitutionalTheme.AZUL,
            onClick = {
                onThemeSelected(
                    InstitutionalTheme.AZUL
                )
            }
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = "Modo de color",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Claro u oscuro según la configuración del sistema.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
            )

            Text(
                text =
                    if (selected) "Seleccionado ✓"
                    else "Seleccionar",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}