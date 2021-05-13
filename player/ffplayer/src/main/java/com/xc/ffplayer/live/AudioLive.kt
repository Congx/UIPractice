package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import com.xc.ffplayer.utils.append2File
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
//            Log.e(TAG,"音频 解码成功")
//            it.bytes.append2File("audio.aac")
            dataPush.addData(it)
        }

        audioProvider.dataRecived = { bytes: ByteArray, len: Int ->
//            Log.e(TAG ,"音频 回调byteArray size ${bytes.size}")
//            bytes.append2File("audio.pcm")
            audioDecoder.addData(bytes,len)
        }
    }

    fun startRecode() {
        start = true
        audioDecoder.prepare()
        audioProvider.startRecord()
    }

    override fun stop() {
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