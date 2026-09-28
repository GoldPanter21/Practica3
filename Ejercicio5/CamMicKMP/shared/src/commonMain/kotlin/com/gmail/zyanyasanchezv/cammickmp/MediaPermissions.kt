package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable

interface MediaPermissionController {

    val cameraGranted: Boolean

    val microphoneGranted: Boolean

    fun requestCamera()

    fun requestMicrophone()

    fun refresh()
}

@Composable
expect fun rememberMediaPermissionController(): MediaPermissionController