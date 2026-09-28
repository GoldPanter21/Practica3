package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsScreen(
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
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Selecciona el tema institucional de la aplicación."
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        ThemeOption(
            title = "Guinda IPN",
            selected =
                selectedTheme ==
                        InstitutionalTheme.GUINDA,
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
                selectedTheme ==
                        InstitutionalTheme.AZUL,
            onClick = {

                onThemeSelected(
                    InstitutionalTheme.AZUL
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(32.dp)
        )

        Text(
            text = "Modo de color",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Claro u oscuro según la configuración del sistema.",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium
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
        onClick =
            onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {

                        MaterialTheme
                            .colorScheme
                            .primaryContainer

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surface
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
                    if (selected) {
                        "Seleccionado ✓"
                    } else {
                        "Seleccionar"
                    },
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
            )
        }
    }
}