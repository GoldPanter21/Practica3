package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberMediaPermissionController(): MediaPermissionController {

    return remember {

        object : MediaPermissionController {

            override val cameraGranted: Boolean
                get() = false

            override val microphoneGranted: Boolean
                get() = false

            override fun requestCamera() {
                // Se implementará con AVFoundation en iOS.
            }

            override fun requestMicrophone() {
                // Se implementará con AVFoundation en iOS.
            }

            override fun refresh() {
                // Se implementará al probar el proyecto en Xcode.
            }
        }
    }
}