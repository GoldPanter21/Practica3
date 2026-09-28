package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private class IOSCameraController :
    CameraController {

    private var photoFilterState by
    mutableStateOf(
        PhotoFilter.ORIGINAL
    )

    private var facingState by
    mutableStateOf(
        CameraFacing.BACK
    )

    private var flashState by
    mutableStateOf(
        CameraFlashMode.OFF
    )

    override val photoFilter:
            PhotoFilter
        get() = photoFilterState

    override val facing: CameraFacing
        get() = facingState

    override val flashMode:
            CameraFlashMode
        get() = flashState

    override fun setPhotoFilter(
        filter: PhotoFilter
    ) {

        photoFilterState =
            filter
    }

    override fun toggleCamera() {

        facingState =
            if (
                facingState ==
                CameraFacing.BACK
            ) {
                CameraFacing.FRONT
            } else {
                CameraFacing.BACK
            }

        if (
            facingState ==
            CameraFacing.FRONT
        ) {
            flashState =
                CameraFlashMode.OFF
        }
    }

    override fun setFlashMode(
        mode: CameraFlashMode
    ) {

        flashState =
            mode
    }

    override fun capturePhoto(
        onResult:
            (PhotoCaptureResult) -> Unit
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
        modifier =
            modifier.background(
                Color.Black
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                "Vista previa de cámara iOS",
            color =
                Color.White
        )
    }
}