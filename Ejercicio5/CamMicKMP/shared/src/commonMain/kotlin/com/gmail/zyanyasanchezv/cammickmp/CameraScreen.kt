package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun CameraScreen(
    cameraController: CameraController
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
        when (cameraController.flashMode) {

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
                MaterialTheme.typography.headlineSmall,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
        ) {

            CameraPreview(
                controller =
                    cameraController,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            20.dp
                        )
                    )
            )

            if (countdown > 0) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            countdown.toString(),
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
                Modifier.height(12.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
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
                            cameraController.facing ==
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
                    cameraController.facing ==
                            CameraFacing.BACK,

                onClick = {

                    val nextMode =
                        when (
                            cameraController.flashMode
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
                    text = flashText
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text = "Temporizador",
            fontWeight =
                FontWeight.SemiBold
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {

            listOf(
                0,
                3,
                5
            ).forEach { seconds ->

                val text =
                    if (seconds == 0) {
                        "Sin timer"
                    } else {
                        "${seconds}s"
                    }

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

                        Text(text)
                    }

                } else {

                    OutlinedButton(
                        onClick = {
                            timerSeconds =
                                seconds
                        }
                    ) {

                        Text(text)
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Button(
            enabled =
                !isCapturing,

            onClick = {

                isCapturing =
                    true

                captureMessage =
                    null

                coroutineScope.launch {

                    if (
                        timerSeconds > 0
                    ) {

                        for (
                        second in
                        timerSeconds downTo 1
                        ) {

                            countdown =
                                second

                            delay(1000)
                        }

                        countdown =
                            0
                    }

                    captureMessage =
                        "Capturando..."

                    cameraController
                        .capturePhoto { result ->

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
                    if (isCapturing) {
                        "Procesando..."
                    } else {
                        "📸 Capturar fotografía"
                    }
            )
        }

        captureMessage?.let { message ->

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = message,
                color =
                    MaterialTheme.colorScheme.primary,
                textAlign =
                    TextAlign.Center,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}