package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text = "CamMic KMP",
            style =
                MaterialTheme
                    .typography
                    .headlineLarge,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Cámara y micrófono multiplataforma",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            color =
                MaterialTheme
                    .colorScheme
                    .primary,
            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(32.dp)
        )

        FeatureCard(
            symbol = "📷",
            title = "Cámara",
            description =
                "Captura y administra fotografías."
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        FeatureCard(
            symbol = "🎙",
            title = "Audio",
            description =
                "Graba y reproduce archivos de audio."
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        FeatureCard(
            symbol = "▦",
            title = "Galería",
            description =
                "Consulta todo tu contenido local."
        )
    }
}