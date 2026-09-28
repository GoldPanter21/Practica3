package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private class IOSAudioController :
    AudioController {

    private val _isRecording =
        MutableStateFlow(false)

    override val isRecording:
            StateFlow<Boolean> =
        _isRecording

    private val _elapsedSeconds =
        MutableStateFlow(0)

    override val elapsedSeconds:
            StateFlow<Int> =
        _elapsedSeconds

    private val _audioLevel =
        MutableStateFlow(0f)

    override val audioLevel:
            StateFlow<Float> =
        _audioLevel

    private val _sensitivity =
        MutableStateFlow(
            AudioSensitivity.NORMAL
        )

    override val sensitivity:
            StateFlow<AudioSensitivity> =
        _sensitivity

    private val _recordings =
        MutableStateFlow<List<SavedAudio>>(
            emptyList()
        )

    override val recordings:
            StateFlow<List<SavedAudio>> =
        _recordings

    private val _playingPath =
        MutableStateFlow<String?>(
            null
        )

    override val playingPath:
            StateFlow<String?> =
        _playingPath

    private val _statusMessage =
        MutableStateFlow<String?>(
            "Audio iOS pendiente de AVFoundation"
        )

    override val statusMessage:
            StateFlow<String?> =
        _statusMessage

    override fun setSensitivity(
        sensitivity:
        AudioSensitivity
    ) {

        _sensitivity.value =
            sensitivity
    }

    override fun startRecording(
        maxDurationSeconds: Int
    ) {

        _statusMessage.value =
            "La implementación AVAudioRecorder se completará desde Xcode"
    }

    override fun stopRecording() {
    }

    override fun refreshRecordings() {
    }

    override fun togglePlayback(
        path: String
    ) {
    }

    override fun stopPlayback() {
    }

    override fun release() {
    }
}

@Composable
actual fun rememberAudioController():
        AudioController {

    return remember {
        IOSAudioController()
    }
}