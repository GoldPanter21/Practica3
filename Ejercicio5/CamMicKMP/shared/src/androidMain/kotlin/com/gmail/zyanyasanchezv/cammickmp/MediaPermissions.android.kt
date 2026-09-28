package com.gmail.zyanyasanchezv.cammickmp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberMediaPermissionController(): MediaPermissionController {

    val context = LocalContext.current

    var cameraAllowed by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var microphoneAllowed by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            cameraAllowed = granted
        }

    val microphoneLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            microphoneAllowed = granted
        }

    return object : MediaPermissionController {

        override val cameraGranted: Boolean
            get() = cameraAllowed

        override val microphoneGranted: Boolean
            get() = microphoneAllowed

        override fun requestCamera() {
            cameraLauncher.launch(
                Manifest.permission.CAMERA
            )
        }

        override fun requestMicrophone() {
            microphoneLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }

        override fun refresh() {

            cameraAllowed =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

            microphoneAllowed =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
        }
    }
}