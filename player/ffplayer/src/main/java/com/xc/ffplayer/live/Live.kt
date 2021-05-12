package com.xc.ffplayer.live

import android.app.Activity
import com.xc.ffplayer.camera2.*

open class Live(var context: Activity):Releaseable {

    private lateinit var url: String
    private var TAG = "CameraLive"

    private val dataPush:DataPush by lazy {
        DataPush()
    }

    private val videoLive:VideoLive by lazy {
        VideoLive(context,dataPush)
    }

    private val audioLive:AudioLive by lazy {
        AudioLive(context,dataPush)
    }

    fun initPreview(autoFitTextureView: AutoFitTextureView) {
        videoLive.inintPreview(autoFitTextureView)
    }

    fun startPush(url:String) {
        this.url = url
        dataPush.startPush(url)
        audioLive.startRecode()
        videoLive.startPush()
    }

    override fun stop() {
        videoLive.stop()
        audioLive.stopRecode()
        dataPush.stop()
    }

    override fun release() {
        videoLive.release()
        audioLive.release()
        dataPush.release()
    }
}