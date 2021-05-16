package com.xc.ffplayer.live

import android.app.Activity
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.camera.view.PreviewView
import androidx.fragment.app.FragmentActivity
import com.xc.ffplayer.camera2.AutoFitTextureView
import com.xc.ffplayer.camera2.Camera2Provider
import com.xc.ffplayer.camera2.CameraPreviewCallback
import com.xc.ffplayer.camera2.VideoStreamEncoder
import java.util.concurrent.CountDownLatch

open class VideoLive(
    var context: FragmentActivity,
    var dataPush: DataPush,
    var countDownLatch: CountDownLatch
):Releaseable{

    private var TAG = "VideoLive"

    lateinit var encoder: VideoStreamEncoder

//    private val camera2Provider: Camera2Provider by lazy {
////        val height = 1280
////        val width = 720
//        val height = 640
//        val width = 480
////        Camera2Provider(context)
//        Camera2Provider(context,width,height)
//    }

    val liveStream:LiveStream by lazy {
        val height = 640
        val width = 480
        CameraXProvider(context,width,height)
    }

    var decodeCallback:((packate:RTMPPackage) -> Unit)? = {
        // 解码成功
//        Log.e(TAG,"vidio 解码成功 bytes---------${it.bytes.size}")
//        it.bytes.append2File("output.h264")
        dataPush.addData(it)
    }

    init {

        liveStream.callback = object :LiveStreamCallback {
            override fun onPreviewSize(size: Size) {

            }

            override fun onStreamSize(size: Size) {
                encoder = VideoStreamEncoder(size.width,size.height,decodeCallback,countDownLatch)
                encoder.prepare()
            }

            override fun onStreamPreperaed(byteArray: ByteArray, len: Int) {
                encoder.decode(byteArray)
            }

        }
//        camera2Provider.cameraPreviewCallback = object : CameraPreviewCallback {
//            override fun previewSize(width: Int, height: Int) {
//
//            }
//            override fun streamSize(width: Int, height: Int) {
//                // 1920,1080
//                encoder = VideoStreamEncoder(height,width,decodeCallback,countDownLatch)
//                encoder.prepare()
//            }
//
//            // 详解初始化成功
//            override fun cameraInited() {
//                startStream()
////                startPreview()
//            }
//
//        }
//        camera2Provider.streamByteCallback = {
////            Log.e(TAG,"stream 回调")
//            encoder.decode(it)
//            // 3110400
////            Log.d(TAG,"streamByteCallback---------${it.size}")
//        }
    }

    fun initPreview(autoFitTextureView: TextureView) {
        liveStream.initPreview(autoFitTextureView)
    }

    fun initPreview(autoFitTextureView: PreviewView) {
        liveStream.initPreview(autoFitTextureView)
    }

    fun startPush() {
        liveStream.startPush()
    }

    fun startPreview() {
//        camera2Provider.startPreview()
    }

    fun stopPreview() {
//        camera2Provider.stopPreview()
    }

    fun startStream() {
//        camera2Provider.startStream()
    }

    fun stopStream() {
//        camera2Provider.stopStream()
    }

    override fun stop() {
//        stopStream()
        liveStream.stopPush()
        encoder.stop()
    }

    override fun release() {
        liveStream.stopPush()
        encoder.release()
    }

}