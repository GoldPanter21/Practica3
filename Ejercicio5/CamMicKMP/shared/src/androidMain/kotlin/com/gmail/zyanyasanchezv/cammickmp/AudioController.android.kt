package com.gmail.zyanyasanchezv.cammickmp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

private class AndroidAudioController(
    private val context: Context
) : AudioController {

    private val scope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Default
        )

    private var recorder:
            MediaRecorder? = null

    private var player:
            MediaPlayer? = null

    private var currentAudioFile:
            File? = null

    private var timerJob:
            Job? = null

    private var levelJob:
            Job? = null

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
            null
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

    @Suppress("DEPRECATION")
    private fun createRecorder():
            MediaRecorder {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            MediaRecorder(context)

        } else {

            MediaRecorder()
        }
    }

    override fun startRecording(
        maxDurationSeconds: Int
    ) {

        if (_isRecording.value) {
            return
        }

        stopPlayback()

        val directory =
            File(
                context.filesDir,
                "audio"
            ).apply {

                if (!exists()) {
                    mkdirs()
                }
            }

        val file =
            File(
                directory,
                "AUD_${System.currentTimeMillis()}.m4a"
            )

        try {

            val newRecorder =
                createRecorder()

            newRecorder.apply {

                setAudioSource(
                    MediaRecorder
                        .AudioSource
                        .MIC
                )

                setOutputFormat(
                    MediaRecorder
                        .OutputFormat
                        .MPEG_4
                )

                setAudioEncoder(
                    MediaRecorder
                        .AudioEncoder
                        .AAC
                )

                setAudioEncodingBitRate(
                    128000
                )

                setAudioSamplingRate(
                    44100
                )

                setOutputFile(
                    file.absolutePath
                )

                prepare()

                start()
            }

            recorder =
                newRecorder

            currentAudioFile =
                file

            _elapsedSeconds.value =
                0

            _audioLevel.value =
                0f

            _isRecording.value =
                true

            _statusMessage.value =
                "Grabando audio..."

            startTimer(
                maxDurationSeconds
            )

            startLevelMonitor()

        } catch (
            exception: Exception
        ) {

            recorder
                ?.release()

            recorder = null

            file.delete()

            _isRecording.value =
                false

            _statusMessage.value =
                "Error: ${
                    exception.message
                        ?: "No se pudo iniciar la grabación"
                }"
        }
    }

    private fun startTimer(
        maxDurationSeconds: Int
    ) {

        timerJob?.cancel()

        timerJob =
            scope.launch {

                while (
                    _isRecording.value
                ) {

                    delay(1000)

                    if (
                        !_isRecording.value
                    ) {
                        break
                    }

                    _elapsedSeconds.value =
                        _elapsedSeconds.value +
                                1

                    if (
                        maxDurationSeconds > 0 &&
                        _elapsedSeconds.value >=
                        maxDurationSeconds
                    ) {

                        stopRecording()

                        _statusMessage.value =
                            "Grabación finalizada por temporizador"

                        break
                    }
                }
            }
    }

    private fun startLevelMonitor() {

        levelJob?.cancel()

        levelJob =
            scope.launch {

                while (
                    _isRecording.value
                ) {

                    delay(120)

                    val rawAmplitude =
                        try {

                            recorder
                                ?.maxAmplitude
                                ?: 0

                        } catch (
                            _: Exception
                        ) {

                            0
                        }

                    val multiplier =
                        when (
                            _sensitivity.value
                        ) {

                            AudioSensitivity.LOW ->
                                0.5f

                            AudioSensitivity.NORMAL ->
                                1f

                            AudioSensitivity.HIGH ->
                                2f
                        }

                    _audioLevel.value =
                        (
                                rawAmplitude /
                                        32767f *
                                        multiplier
                                )
                            .coerceIn(
                                0f,
                                1f
                            )
                }
            }
    }

    override fun stopRecording() {

        if (
            !_isRecording.value
        ) {
            return
        }

        _isRecording.value =
            false

        timerJob?.cancel()
        levelJob?.cancel()

        timerJob = null
        levelJob = null

        try {

            recorder?.stop()

            _statusMessage.value =
                "Grabación guardada correctamente"

        } catch (
            _: Exception
        ) {

            currentAudioFile
                ?.delete()

            _statusMessage.value =
                "La grabación fue demasiado corta"

        } finally {

            try {
                recorder?.reset()
            } catch (
                _: Exception
            ) {
            }

            recorder?.release()

            recorder =
                null

            currentAudioFile =
                null

            _audioLevel.value =
                0f

            refreshRecordings()
        }
    }

    override fun refreshRecordings() {

        val directory =
            File(
                context.filesDir,
                "audio"
            )

        if (!directory.exists()) {

            _recordings.value =
                emptyList()

            return
        }

        _recordings.value =
            directory
                .listFiles()
                ?.filter { file ->

                    file.isFile &&
                            file.extension
                                .equals(
                                    "m4a",
                                    ignoreCase = true
                                )
                }
                ?.sortedByDescending {
                    it.lastModified()
                }
                ?.map { file ->

                    SavedAudio(
                        path =
                            file.absolutePath,
                        name =
                            file.name,
                        lastModified =
                            file.lastModified()
                    )
                }
                ?: emptyList()
    }

    override fun togglePlayback(
        path: String
    ) {

        if (
            _playingPath.value ==
            path
        ) {

            stopPlayback()

            return
        }

        stopPlayback()

        val file =
            File(path)

        if (!file.exists()) {

            _statusMessage.value =
                "El archivo de audio ya no existe"

            refreshRecordings()

            return
        }

        try {

            val newPlayer =
                MediaPlayer()

            player =
                newPlayer

            newPlayer.apply {

                setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setContentType(
                            AudioAttributes
                                .CONTENT_TYPE_SPEECH
                        )
                        .setUsage(
                            AudioAttributes
                                .USAGE_MEDIA
                        )
                        .build()
                )

                setDataSource(
                    file.absolutePath
                )

                setOnPreparedListener {

                    _playingPath.value =
                        path

                    it.start()
                }

                setOnCompletionListener {

                    _playingPath.value =
                        null

                    it.release()

                    if (
                        player === it
                    ) {
                        player = null
                    }
                }

                setOnErrorListener {
                        mediaPlayer,
                        _,
                        _ ->

                    _playingPath.value =
                        null

                    mediaPlayer.release()

                    if (
                        player ===
                        mediaPlayer
                    ) {
                        player = null
                    }

                    _statusMessage.value =
                        "No se pudo reproducir el audio"

                    true
                }

                prepareAsync()
            }

        } catch (
            exception: Exception
        ) {

            player?.release()

            player = null

            _playingPath.value =
                null

            _statusMessage.value =
                "Error: ${
                    exception.message
                        ?: "No se pudo reproducir"
                }"
        }
    }

    override fun stopPlayback() {

        try {

            player?.stop()

        } catch (
            _: Exception
        ) {
        }

        player?.release()

        player =
            null

        _playingPath.value =
            null
    }

    override fun release() {

        if (
            _isRecording.value
        ) {
            stopRecording()
        }

        stopPlayback()

        scope.cancel()
    }
}

@Composable
actual fun rememberAudioController():
        AudioController {

    val context =
        LocalContext.current

    val controller =
        remember {

            AndroidAudioController(
                context.applicationContext
            )
        }

    DisposableEffect(controller) {

        onDispose {

            controller.release()
        }
    }

    return controller
}