package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class PhotoCaptureResult(
    val success: Boolean,
    val path: String? = null,
    val message: String
)

interface CameraController {

    fun capturePhoto(
        onResult: (PhotoCaptureResult) -> Unit
    )
}

@Composable
expect fun rememberCameraController(): CameraController

@Composable
expect fun CameraPreview(
    controller: CameraController,
    modifier: Modifier = Modifier
)