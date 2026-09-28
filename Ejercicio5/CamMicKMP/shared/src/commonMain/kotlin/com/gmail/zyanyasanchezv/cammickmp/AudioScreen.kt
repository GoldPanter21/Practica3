package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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

@Composable
internal fun AudioScreen(
    controller: AudioController
) {

    val isRecording by
    controller
        .isRecording
        .collectAsState()

    val elapsedSeconds by
    controller
        .elapsedSeconds
        .collectAsState()

    val audioLevel by
    controller
        .audioLevel
        .collectAsState()

    val sensitivity by
    controller
        .sensitivity
        .collectAsState()

    val recordings by
    controller
        .recordings
        .collectAsState()

    val playingPath by
    controller
        .playingPath
        .collectAsState()

    val statusMessage by
    controller
        .statusMessage
        .collectAsState()

    var maxDuration by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        controller
            .refreshRecordings()
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
            text = "Micrófono",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text =
                formatAudioTime(
                    elapsedSeconds
                ),
            fontSize = 48.sp,
            fontWeight =
                FontWeight.Bold,
            color =
                if (isRecording) {
                    MaterialTheme
                        .colorScheme
                        .primary
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                }
        )

        Text(
            text =
                if (isRecording) {
                    "● Grabando"
                } else {
                    "Listo para grabar"
                },
            color =
                MaterialTheme
                    .colorScheme
                    .primary,
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text = "Nivel de entrada",
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(
                    RoundedCornerShape(
                        12.dp
                    )
                )
                .background(
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        audioLevel
                            .coerceIn(
                                0f,
                                1f
                            )
                    )
                    .fillMaxHeight()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .primary
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text =
                "Nivel: ${
                    (audioLevel * 100)
                        .toInt()
                }%"
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text = "Sensibilidad",
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            AudioSensitivity
                .entries
                .forEach {
                        option ->

                    val label =
                        when (option) {

                            AudioSensitivity.LOW ->
                                "Baja"

                            AudioSensitivity.NORMAL ->
                                "Normal"

                            AudioSensitivity.HIGH ->
                                "Alta"
                        }

                    if (
                        sensitivity ==
                        option
                    ) {

                        Button(
                            onClick = {

                                controller
                                    .setSensitivity(
                                        option
                                    )
                            }
                        ) {

                            Text(label)
                        }

                    } else {

                        OutlinedButton(
                            onClick = {

                                controller
                                    .setSensitivity(
                                        option
                                    )
                            }
                        ) {

                            Text(label)
                        }
                    }
                }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text =
                "Temporizador de grabación",
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            listOf(
                0,
                15,
                30
            ).forEach {
                    seconds ->

                val label =
                    if (
                        seconds == 0
                    ) {
                        "Manual"
                    } else {
                        "${seconds}s"
                    }

                if (
                    maxDuration ==
                    seconds
                ) {

                    Button(
                        enabled =
                            !isRecording,

                        onClick = {
                            maxDuration =
                                seconds
                        }
                    ) {

                        Text(label)
                    }

                } else {

                    OutlinedButton(
                        enabled =
                            !isRecording,

                        onClick = {
                            maxDuration =
                                seconds
                        }
                    ) {

                        Text(label)
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        if (isRecording) {

            Button(
                onClick = {

                    controller
                        .stopRecording()
                }
            ) {

                Text(
                    text =
                        "⏹ Detener grabación"
                )
            }

        } else {

            Button(
                onClick = {

                    controller
                        .startRecording(
                            maxDurationSeconds =
                                maxDuration
                        )
                }
            ) {

                Text(
                    text =
                        "🎙 Iniciar grabación"
                )
            }
        }

        statusMessage?.let {
                message ->

            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )

            Text(
                text = message,
                color =
                    MaterialTheme
                        .colorScheme
                        .primary,
                textAlign =
                    TextAlign.Center
            )
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        HorizontalDivider()

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "Grabaciones",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            OutlinedButton(
                onClick = {

                    controller
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
                Modifier.height(12.dp)
        )

        if (
            recordings.isEmpty()
        ) {

            Text(
                text =
                    "Aún no hay grabaciones."
            )

        } else {

            recordings
                .forEach {
                        audio ->

                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical =
                                        6.dp
                                ),
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
                                Alignment
                                    .CenterVertically,
                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween
                        ) {

                            Column(
                                modifier =
                                    Modifier
                                        .weight(
                                            1f
                                        )
                            ) {

                                Text(
                                    text =
                                        audio.name,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Guardada localmente",
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }

                            Button(
                                onClick = {

                                    controller
                                        .togglePlayback(
                                            audio.path
                                        )
                                }
                            ) {

                                Text(
                                    text =
                                        if (
                                            playingPath ==
                                            audio.path
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
        }
    }
}

private fun formatAudioTime(
    totalSeconds: Int
): String {

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return (
            minutes
                .toString()
                .padStart(
                    2,
                    '0'
                )
                    +
                    ":" +
                    seconds
                        .toString()
                        .padStart(
                            2,
                            '0'
                        )
            )
}
