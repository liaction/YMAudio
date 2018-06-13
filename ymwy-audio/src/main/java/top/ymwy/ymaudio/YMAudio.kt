package top.ymwy.ymaudio

import android.content.Context
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Environment
import com.cleveroad.audiovisualization.DbmHandler
import omrecorder.*
import java.io.File

fun Any?.isNotNull() = this != null


enum class AudioState {
    START, STOP, PAUSE, RESUME
}

class YMAudio(val context: Context, val dbmHandler: DbmHandler<Double> = YMAudioDbmHandler()) : PullTransport.OnAudioChunkPulledListener {
    override fun onAudioChunkPulled(audioChunk: AudioChunk?) {
        audioChunk?.let {
            if (dbmHandler.isNotNull()) {
                dbmHandler.onDataReceived(if (isRecording()) audioChunk.maxAmplitude() else 0.0)
            }
        }
    }

    var audioDuration: Long = 0

    var audioFileName: String? = ""
    var audioState = AudioState.STOP
        private set
    private var recorder = initRecorder()

    var audioPath = ""
        private set

    private fun initRecorder(): Recorder {
        val innerAudioPath = File(Environment.getExternalStorageDirectory(), "video")
        if (!innerAudioPath.exists()) {
            innerAudioPath.mkdir()
        }
        val audioFile = File(innerAudioPath, if (audioFileName.isNullOrEmpty() || audioFileName.isNullOrBlank()) "audio_${System.currentTimeMillis()}.wav" else audioFileName!!.plus(".wav"))
        audioPath = audioFile.absolutePath
        return OmRecorder.wav(PullTransport.Default(PullableSource.Default(
                AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 48000
                )
        ), this), audioFile)
    }

    fun startRecording() {
        recorder = initRecorder()
        audioState = AudioState.START
        recorder.startRecording()
    }

    fun stopRecording(delete:Boolean = false) {
        if (audioState != AudioState.STOP){
            recorder.stopRecording()
        }
        if (delete){
            File(audioPath).delete()
        }
        audioState = AudioState.STOP
    }

    fun pauseRecording() {
        recorder.pauseRecording()
        audioState = AudioState.PAUSE
    }

    fun resumeRecording() {
        recorder.resumeRecording()
        audioState = AudioState.RESUME
    }

    fun isRecording() = audioState == AudioState.START || audioState == AudioState.RESUME

    override fun toString(): String {
        return "YMAudio(audioDuration=$audioDuration, audioFileName=$audioFileName, audioState=$audioState, audioPath='$audioPath')"
    }

}

class YMAudioDbmHandler : DbmHandler<Double>() {
    override fun onDataReceivedImpl(data: Double?, layersCount: Int, dBmArray: FloatArray, ampsArray: FloatArray) {
        data?.let {
            var amplitude = data / 100
            if (amplitude <= 0.5) {
                amplitude = 0.0
            } else if (amplitude > 0.5 && amplitude <= 0.6) {
                amplitude = 0.2
            } else if (amplitude > 0.6 && amplitude <= 0.7) {
                amplitude = 0.6
            } else if (amplitude > 0.7) {
                amplitude = 1.0
            }
            try {
                dBmArray[0] = amplitude.toFloat()
                ampsArray[0] = amplitude.toFloat()
            } catch (e: Exception) {
            }
        }
    }
}