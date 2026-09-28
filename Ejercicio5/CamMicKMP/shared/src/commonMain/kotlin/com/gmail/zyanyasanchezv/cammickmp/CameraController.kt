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

enum class PhotoFilter {
    ORIGINAL,
    GRAYSCALE,
    SEPIA
}

interface CameraController {

    val facing: CameraFacing

    val flashMode: CameraFlashMode

    val photoFilter: PhotoFilter

    fun toggleCamera()

    fun setPhotoFilter(
        filter: PhotoFilter
    )

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