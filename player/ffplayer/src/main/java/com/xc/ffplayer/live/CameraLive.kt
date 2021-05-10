package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import com.xc.ffplayer.Decoder
import com.xc.ffplayer.camera2.*
import com.xc.ffplayer.utils.append2File

open class CameraLive(var context: Activity) {

    private lateinit var url: String
    var TAG = "CameraLive"

    val camera2Provider:Camera2Provider by lazy {
        val height = 1280
        val width = 720
//        Camera2Provider(context)
        Camera2Provider(context,width,height)
    }

    val livePush:LivePush by lazy {
        LivePush()
    }

    lateinit var decoder: Decoder

    var callback:((byteArray:ByteArray,timeStamp:Long)-> Unit)? = { bytes: ByteArray, timeStamp: Long ->
        // 解码成功
//        Log.d(TAG,"bytes---------${bytes.size}")
        livePush.addData(bytes,timeStamp)
    }

    init {
        camera2Provider.cameraPreviewCallback = object : CameraPreviewCallback {
            override fun previewSize(width: Int, height: Int) {
            }
            override fun streamSize(width: Int, height: Int) {
                // 解码
//                Log.d(TAG,"streamSize---------$width,$height")
                // 1920,1080
                decoder = LiveStreamDecoder(height,width,callback)
                decoder.prepare()
            }

            // 详解初始化成功
            override fun cameraInited() {
                camera2Provider.startPushStream()
            }

        }
        camera2Provider.streamByteCallback = {
            decoder.decode(it)
            // 3110400
//            Log.d(TAG,"streamByteCallback---------${it.size}")
        }
    }

    fun startPreview(autoFitTextureView: AutoFitTextureView) {
        camera2Provider.startPreview(autoFitTextureView)
    }

    fun startPush(url:String) {
        this.url = url
        livePush.startPush(url)
    }

    fun release() {
        camera2Provider.release()
        decoder.stop()
        livePush.release()
    }
}