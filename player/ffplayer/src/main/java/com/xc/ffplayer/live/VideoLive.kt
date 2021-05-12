package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import com.xc.ffplayer.camera2.AutoFitTextureView
import com.xc.ffplayer.camera2.Camera2Provider
import com.xc.ffplayer.camera2.CameraPreviewCallback
import com.xc.ffplayer.camera2.VideoStreamEncoder

open class VideoLive(var context: Activity, var dataPush: DataPush):Releaseable{

    private var TAG = "CameraLive"

    lateinit var decoder: VideoStreamEncoder

    private val camera2Provider: Camera2Provider by lazy {
        val height = 1280
        val width = 720
//        Camera2Provider(context)
        Camera2Provider(context,width,height)
    }

    var decodeCallback:((packate:RTMPPackage) -> Unit)? = {
        // 解码成功
//        Log.d(TAG,"vidio 解码成功 bytes---------${it.bytes.size}")
        dataPush.addData(it)
    }

    init {
        camera2Provider.cameraPreviewCallback = object : CameraPreviewCallback {
            override fun previewSize(width: Int, height: Int) {

            }
            override fun streamSize(width: Int, height: Int) {
//                Log.d(TAG,"streamSize---------$width,$height")
                // 1920,1080
                decoder = VideoStreamEncoder(height,width,decodeCallback)
                decoder.prepare()
            }

            // 详解初始化成功
            override fun cameraInited() {
                startStream()
//                startPreview()
            }

        }
        camera2Provider.streamByteCallback = {
            decoder.decode(it)
            // 3110400
//            Log.d(TAG,"streamByteCallback---------${it.size}")
        }
    }

    fun inintPreview(autoFitTextureView: AutoFitTextureView) {
        camera2Provider.inintPreview(autoFitTextureView)
    }

    fun startPush() {
        decoder.start()
    }

    fun startPreview() {
        camera2Provider.startPreview()
    }

    fun stopPreview() {
        camera2Provider.stopPreview()
    }

    fun startStream() {
        camera2Provider.startStream()
    }

    fun stopStream() {
        camera2Provider.stopStream()
    }

    override fun stop() {
        stopStream()
        decoder.stop()
    }

    override fun release() {
        camera2Provider.release()
        decoder.release()
    }

}