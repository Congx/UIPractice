package com.xc.ffplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_camera_encode.*

class CameraEncodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_encode)

//        val heightPixels = resources.displayMetrics.heightPixels
//        val widthPixels = resources.displayMetrics.widthPixels

        val heightPixels = 720
        val widthPixels = 1280

        var cameraProvide = Camera2Provider(this)

        var streamDecoder:StreamDecoder? = null
//        streamDecoder.prepare()

        cameraProvide.streamByteCallback = {
            streamDecoder?.decode(it)
        }

        cameraProvide.cameraPreviewCallback = object :CameraPreviewCallback {
            override fun previewSize(width: Int, height: Int) {

                streamDecoder = StreamDecoder(width,height)
                streamDecoder?.prepare()
            }

        }

        cameraProvide.initTexture(textureView)

        btnPush.setOnClickListener {
            cameraProvide.startPushStream()
        }
    }
}