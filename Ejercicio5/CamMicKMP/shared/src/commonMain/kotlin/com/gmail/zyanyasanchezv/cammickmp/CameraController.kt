package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class PhotoCaptureResult(
    val success: Boolean,
    val path: String? = null,
    val message: String
)

enum class CameraFacing {
    BACK,
    FRONT
}

enum class CameraFlashMode {
    OFF,
    ON,
    AUTO
}

interface CameraController {

    val facing: CameraFacing

    val flashMode: CameraFlashMode

    fun toggleCamera()

    fun setFlashMode(
        mode: CameraFlashMode
    )

    fun capturePhoto(
        onResult: (PhotoCaptureResult) -> Unit
    )
}

@Composable
expect fun rememberCameraController():
        CameraController

@Composable
expect fun CameraPreview(
    controller: CameraController,
    modifier: Modifier = Modifier
)