package com.xc.ffplayer.live

import android.app.Activity
import java.util.concurrent.CountDownLatch


class AudioLive(
    var context: Activity,
    var dataPush: DataPush,
    var countDownLatch: CountDownLatch
) : Releaseable{

    private var TAG = "AudioLive"

    private var start = false

    private val audioDecoder by lazy {
        AudioStreamEncoder(countDownLatch = countDownLatch)
    }

    private val audioProvider by lazy {
        AudioProvider()
    }

    init {
        audioDecoder.callback = {
//            Log.d(TAG,"解码成功")
//            it.bytes.append2File("audio.aac")
            dataPush.addData(it)
        }

        audioProvider.dataRecived = {
//            Log.d(TAG ,"byteArray size ${it.size}")
//            it.append2File("audio.pcm")
            audioDecoder.decode(it)
        }
    }

    fun startRecode() {
        start = true
        audioDecoder.prepare()
        audioProvider.startRecord()
    }

    fun stopRecode() {
        if (!start) return
        audioProvider.stop()
        audioDecoder.stop()
        start = false
    }

    override fun release() {
        if (!start) return
        audioDecoder.release()
        audioProvider.release()
        start = false
    }

}