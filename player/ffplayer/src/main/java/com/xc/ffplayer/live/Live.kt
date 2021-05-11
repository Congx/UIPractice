package com.xc.ffplayer.live

import android.app.Activity
import com.xc.ffplayer.camera2.*

open class Live(var context: Activity) {

    private lateinit var url: String
    private var TAG = "CameraLive"

    private val dataPush:DataPush by lazy {
        DataPush()
    }

    private val videoLive:VideoLive by lazy {
        VideoLive(context,dataPush)
    }

    fun initPreview(autoFitTextureView: AutoFitTextureView) {
        videoLive.inintPreview(autoFitTextureView)
    }

    fun startPush(url:String) {
        this.url = url
        dataPush.startPush(url)
    }

    fun stopPush() {
        videoLive.stopStream()
    }

    fun release() {
        videoLive.release()
        dataPush.release()
    }
}