package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import com.xc.ffplayer.utils.append2File


class AudioLive(var context: Activity, var dataPush: DataPush) : Releaseable{

    private var TAG = "AudioLive"

    private val audioDecoder by lazy {
        AudioStreamEncoder()
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
        audioDecoder.prepare()
        audioProvider.startRecord()
    }

    fun stopRecode() {
        audioProvider.stop()
        audioDecoder.stop()
    }

    override fun release() {
        audioDecoder.release()
        audioProvider.release()
    }

}