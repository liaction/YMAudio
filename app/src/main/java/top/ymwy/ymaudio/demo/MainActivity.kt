package top.ymwy.ymaudio.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_audio_wave_demo.*
import top.ymwy.ymaudio.YMAudioActivity
import top.ymwy.ymaudio.isNotNull
import java.io.File

class MainActivity : AppCompatActivity() {

    private var mAudioPath:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_audio_wave_demo)
        mPlayAudioBtn.isEnabled = false
        mPlayAudioBtn.setOnClickListener {
            playAudio(mAudioPath)
        }
        mGoToAudioBtn.setOnClickListener {
            YMAudioActivity.start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YMAudioActivity.REQUEST_CODE){
            when(resultCode){
                Activity.RESULT_OK->{
                    data?.let {
                        val audioPath = data.getStringExtra(YMAudioActivity.AUDIO_PATH)
                        if (audioPath.isNotNull()){
                            val audioFile = File(audioPath)
                            if (audioFile.exists()){
                                mAudioPath = audioPath
                                mAudioResult.text = audioPath
                                mAudioResult.setTextColor(Color.WHITE)
                                mPlayAudioBtn.isEnabled = true
                                return@let
                            }
                            mPlayAudioBtn.isEnabled = false
                            mAudioPath = ""
                            mAudioResult.text = "录音文件无效"
                            mAudioResult.setTextColor(Color.RED)
                        }
                    }
                }
                else ->{
                    mPlayAudioBtn.isEnabled = false
                    mAudioResult.text = "用户取消了录音"
                    mAudioResult.setTextColor(Color.RED)
                    showTip(this,"用户取消了录音")
                }
            }
        }
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

    fun showTip(context: Context, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}


