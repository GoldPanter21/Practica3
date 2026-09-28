package com.gmail.zyanyasanchezv.cammickmp

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

    CamMicTheme(
        institutionalTheme = selectedTheme
    ) {
        MainScreen(
            selectedScreen = selectedScreen,
            selectedTheme = selectedTheme,
            permissionController = permissionController,
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

                AppScreen.CAMARA ->
                    PermissionFeatureScreen(
                        title = "Cámara",
                        symbol = "📷",
                        description =
                            "Permite capturar fotografías, aplicar filtros, utilizar flash y configurar un temporizador.",
                        permissionGranted =
                            permissionController.cameraGranted,
                        permissionName = "cámara",
                        onRequestPermission = {
                            permissionController.requestCamera()
                        }
                    )

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
                    FeatureScreen(
                        title = "Galería",
                        symbol = "▦",
                        description =
                            "Consulta las fotografías y grabaciones almacenadas localmente."
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