# YMAudio
android audio recorder

[ ![Download](https://api.bintray.com/packages/liaction/ymwycs/YMAudio/images/download.svg?version=0.1.3) ](https://bintray.com/liaction/ymwycs/YMAudio/0.1.3/link)

# THANKS
- [WaveInApp](https://github.com/Cleveroad/WaveInApp)
- [OmRecorder](https://github.com/kailash09dabhi/OmRecorder)

# HOW TO

>```
repositories {
    maven {
        url  "https://dl.bintray.com/liaction/YMAudio"
    }
}
dependencies{
	implementation 'top.ymwy.ymaudio:ymwy-audio:0.1.3'
}
```

# USE
> First
> ```kotlin
> 	YMAudioActivity.start(activity)
> ```
> Second
> ```kotlin
> 	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YMAudioActivity.REQUEST_CODE){
            when(resultCode){
                Activity.RESULT_OK->{
                    data?.let {
                        val audioPath = data.getStringExtra(YMAudioActivity.AUDIO_PATH)
                        if (audioPath.isNotNull()){
                            val audioFile = File(audioPath)
                            if (audioFile.exists()){
                                // TODO "成功返回录音文件"
                                return@let
                            }
                            // TODO "录音文件无效"
                        }
                    }
                }
                Activity.RESULT_CANCELED->{
                    // TODO 用户取消了录音
                }
                YMAudioActivity.PERMISSIONS_NOT_GRANTED->{
                   // TODO 权限被拒绝
                }
            }
        }
    }
> ```
