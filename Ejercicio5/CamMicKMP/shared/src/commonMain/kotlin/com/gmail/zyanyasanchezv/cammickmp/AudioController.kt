package com.gmail.zyanyasanchezv.cammickmp

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

data class SavedAudio(
    val path: String,
    val name: String,
    val lastModified: Long
)

enum class AudioSensitivity {
    LOW,
    NORMAL,
    HIGH
}

interface AudioController {

    val isRecording: StateFlow<Boolean>

    val elapsedSeconds: StateFlow<Int>

    val audioLevel: StateFlow<Float>

    val sensitivity: StateFlow<AudioSensitivity>

    val recordings: StateFlow<List<SavedAudio>>

    val playingPath: StateFlow<String?>

    val statusMessage: StateFlow<String?>

    fun setSensitivity(
        sensitivity: AudioSensitivity
    )

    fun startRecording(
        maxDurationSeconds: Int = 0
    )

    fun stopRecording()

    fun refreshRecordings()

    fun togglePlayback(
        path: String
    )

    fun stopPlayback()

    fun release()
}

@Composable
expect fun rememberAudioController():
        AudioController