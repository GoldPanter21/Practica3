package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private class IOSCameraController :
    CameraController {

    override fun capturePhoto(
        onResult: (PhotoCaptureResult) -> Unit
    ) {

        onResult(
            PhotoCaptureResult(
                success = false,
                message =
                    "La implementación AVFoundation se completará desde Xcode"
            )
        )
    }
}

@Composable
actual fun rememberCameraController():
        CameraController {

    return remember {
        IOSCameraController()
    }
}

@Composable
actual fun CameraPreview(
    controller: CameraController,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .background(Color.Black),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                "Vista previa de cámara iOS",
            color = Color.White
        )
    }
}