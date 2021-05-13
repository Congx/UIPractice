package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import com.xc.ffplayer.camera2.*
import java.util.concurrent.CountDownLatch

open class Live(var context: Activity):Releaseable {

    private lateinit var url: String
    private var TAG = "CameraLive"

    private var countDownLatch = CountDownLatch(1)

    private val dataPush:DataPush by lazy {
        DataPush(countDownLatch)
    }

    private val videoLive:VideoLive by lazy {
        VideoLive(context,dataPush,countDownLatch)
    }

    private val audioLive:AudioLive by lazy {
        AudioLive(context,dataPush,countDownLatch)
    }

    fun initPreview(autoFitTextureView: AutoFitTextureView) {
        videoLive.inintPreview(autoFitTextureView)
    }

    fun startPush(url:String) {
        Log.e(TAG,"startPush 调用")
        this.url = url
        dataPush.startPush(url)
        audioLive.startRecode()
    }

    override fun stop() {
        videoLive.stop()
        audioLive.stop()
        dataPush.stop()
    }

    override fun release() {
        videoLive.release()
        audioLive.release()
        dataPush.release()
    }
}