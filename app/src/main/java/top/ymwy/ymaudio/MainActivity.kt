package top.ymwy.ymaudio

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_audio_wave.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private fun showLog(msg: String) {
        Log.e("msg", msg)
    }

    fun showTip(context: Context, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private val ymAudio = YMAudio(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_audio_wave)
        mVisualizerView.linkTo(ymAudio.dbmHandler)
        mAudioStartOrPause.setOnClickListener {
            when (ymAudio.audioState) {
                AudioState.STOP -> {
                    ymAudio.audioDuration = 0
                    ymAudio.startRecording()
                    mAudioTime.base = SystemClock.elapsedRealtime()
                    mAudioTime.start()
                }
                AudioState.PAUSE -> {
                    ymAudio.resumeRecording()
                    mAudioTime.base = SystemClock.elapsedRealtime() - ymAudio.audioDuration
                    mAudioTime.start()
                }
                AudioState.START, AudioState.RESUME -> {
                    mAudioTime.stop()
                    ymAudio.pauseRecording()
                    ymAudio.audioDuration = SystemClock.elapsedRealtime() - mAudioTime.base
                }
            }

            toggleView()
        }
        mAudioReStart.setOnClickListener {
            mAudioTime.stop()
            ymAudio.stopRecording(true)
            ymAudio.audioDuration = 0
            ymAudio.startRecording()
            mAudioTime.base = SystemClock.elapsedRealtime()
            mAudioTime.start()
            toggleView()
        }
        mAudioPlay.setOnClickListener {
            showLog(ymAudio.toString())
            playAudio(ymAudio.audioPath)
        }

        mAudioStop.setOnClickListener {
            mAudioTime.stop()
            ymAudio.stopRecording()
            ymAudio.audioDuration = SystemClock.elapsedRealtime() - mAudioTime.base
            toggleView()
            mAudioPlay.visibility = View.VISIBLE
            mAudioStartOrPause.visibility = View.GONE
        }

        mAudioOk.setOnClickListener {
            ymAudio.stopRecording()
            showLog("保存录音：" + ymAudio.toString())
        }

        mAudioCancel.setOnClickListener {
            ymAudio.stopRecording(delete = true)
            showLog("取消录音了")
        }

    }

    private fun toggleView() {
        if (ymAudio.isRecording()) {
            mAudioPlay.visibility = View.GONE
            mAudioReStart.visibility = View.GONE
            mAudioOk.visibility = View.GONE
            mAudioStop.visibility = View.VISIBLE
            mAudioStartOrPause.visibility = View.VISIBLE
            return
        }
        mAudioReStart.visibility = View.VISIBLE
        mAudioOk.visibility = View.VISIBLE
        mAudioStop.visibility = View.GONE
    }

    private fun playAudio(path: String) {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val file = File(path)
        if (!file.exists()) {
            showTip(this, "文件不存在")
            return
        }

        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "audio/*")

        if (intent.resolveActivity(packageManager).isNotNull()) {
            startActivity(intent)
        } else {
            showTip(this, "播放失败,请确保手机上安装了相关音频软件")
        }
    }

    override fun onResume() {
        super.onResume()
        mVisualizerView.onResume()
    }

    override fun onPause() {
        mVisualizerView.onPause()
        if (ymAudio.isRecording()){
            mAudioTime.stop()
            ymAudio.pauseRecording()
            ymAudio.audioDuration = SystemClock.elapsedRealtime() - mAudioTime.base
        }
        super.onPause()
    }

    override fun onDestroy() {
        mVisualizerView.release()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}


