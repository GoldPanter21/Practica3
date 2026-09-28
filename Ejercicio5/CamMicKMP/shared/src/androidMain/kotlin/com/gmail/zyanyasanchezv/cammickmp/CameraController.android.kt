package com.gmail.zyanyasanchezv.cammickmp

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

private class AndroidCameraController(
    private val context: Context
) : CameraController {

    val nativeController =
        LifecycleCameraController(context)

    private var facingState by
    mutableStateOf(
        CameraFacing.BACK
    )

    private var flashModeState by
    mutableStateOf(
        CameraFlashMode.OFF
    )

    override val facing: CameraFacing
        get() = facingState

    override val flashMode: CameraFlashMode
        get() = flashModeState

    init {

        nativeController.cameraSelector =
            CameraSelector
                .DEFAULT_BACK_CAMERA

        nativeController
            .imageCaptureFlashMode =
            ImageCapture.FLASH_MODE_OFF
    }

    override fun toggleCamera() {

        val newFacing =
            if (
                facingState ==
                CameraFacing.BACK
            ) {
                CameraFacing.FRONT
            } else {
                CameraFacing.BACK
            }

        try {

            // Aseguramos que la linterna
            // quede apagada al cambiar.
            nativeController
                .enableTorch(false)

            nativeController
                .cameraSelector =
                if (
                    newFacing ==
                    CameraFacing.BACK
                ) {

                    CameraSelector
                        .DEFAULT_BACK_CAMERA

                } else {

                    CameraSelector
                        .DEFAULT_FRONT_CAMERA
                }

            facingState =
                newFacing

            // Para esta práctica no
            // implementaremos flash de
            // pantalla en cámara frontal.
            if (
                newFacing ==
                CameraFacing.FRONT
            ) {

                setFlashMode(
                    CameraFlashMode.OFF
                )
            }

        } catch (_: Exception) {

            // Si el dispositivo no tiene
            // la cámara seleccionada,
            // conservamos la anterior.
        }
    }

    override fun setFlashMode(
        mode: CameraFlashMode
    ) {

        if (
            facingState ==
            CameraFacing.FRONT &&
            mode != CameraFlashMode.OFF
        ) {
            return
        }

        flashModeState =
            mode

        nativeController
            .imageCaptureFlashMode =
            when (mode) {

                CameraFlashMode.OFF ->
                    ImageCapture
                        .FLASH_MODE_OFF

                CameraFlashMode.ON ->
                    ImageCapture
                        .FLASH_MODE_ON

                CameraFlashMode.AUTO ->
                    ImageCapture
                        .FLASH_MODE_AUTO
            }
    }

    override fun capturePhoto(
        onResult: (PhotoCaptureResult) -> Unit
    ) {

        val photosDirectory =
            File(
                context.filesDir,
                "photos"
            ).apply {

                if (!exists()) {
                    mkdirs()
                }
            }

        val photoFile =
            File(
                photosDirectory,
                "IMG_${System.currentTimeMillis()}.jpg"
            )

        val outputOptions =
            ImageCapture
                .OutputFileOptions
                .Builder(photoFile)
                .build()

        try {

            nativeController
                .takePicture(

                    outputOptions,

                    ContextCompat
                        .getMainExecutor(
                            context
                        ),

                    object :
                        ImageCapture
                        .OnImageSavedCallback {

                        override fun onImageSaved(
                            outputFileResults:
                            ImageCapture
                            .OutputFileResults
                        ) {

                            onResult(
                                PhotoCaptureResult(
                                    success = true,
                                    path =
                                        photoFile
                                            .absolutePath,
                                    message =
                                        "Fotografía guardada correctamente"
                                )
                            )
                        }

                        override fun onError(
                            exception:
                            ImageCaptureException
                        ) {

                            onResult(
                                PhotoCaptureResult(
                                    success = false,
                                    message =
                                        exception.message
                                            ?: "No se pudo capturar la fotografía"
                                )
                            )
                        }
                    }
                )

        } catch (
            exception: Exception
        ) {

            onResult(
                PhotoCaptureResult(
                    success = false,
                    message =
                        exception.message
                            ?: "La cámara todavía no está disponible"
                )
            )
        }
    }
}

@Composable
actual fun rememberCameraController():
        CameraController {

    val context =
        LocalContext.current

    return remember {

        AndroidCameraController(
            context.applicationContext
        )
    }
}

@Composable
actual fun CameraPreview(
    controller: CameraController,
    modifier: Modifier
) {

    val lifecycleOwner =
        LocalLifecycleOwner.current

    val androidController =
        controller as
                AndroidCameraController

    DisposableEffect(
        lifecycleOwner,
        androidController
    ) {

        androidController
            .nativeController
            .bindToLifecycle(
                lifecycleOwner
            )

        onDispose {

            androidController
                .nativeController
                .unbind()
        }
    }

    AndroidView(
        modifier = modifier,

        factory = { context ->

            PreviewView(context).apply {

                scaleType =
                    PreviewView
                        .ScaleType
                        .FILL_CENTER

                this.controller =
                    androidController
                        .nativeController
            }
        }
    )
}