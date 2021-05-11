package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xc.ffplayer.camera2.Camera2Provider
import com.xc.ffplayer.camera2.CameraPreviewCallback
import com.xc.ffplayer.R
import com.xc.ffplayer.camera2.StreamDecoder
import kotlinx.android.synthetic.main.activity_camera_encode.*

class CameraEncodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_encode)

//        val heightPixels = resources.displayMetrics.heightPixels
//        val widthPixels = resources.displayMetrics.widthPixels

        val height = 1280
        val width = 720

        var cameraProvide = Camera2Provider(this)
        var streamDecoder: StreamDecoder? = null

        cameraProvide.streamByteCallback = {
            streamDecoder?.decode(it)
        }

        cameraProvide.cameraPreviewCallback = object :
            CameraPreviewCallback {
            override fun previewSize(width: Int, height: Int) {
                // 宽高要替换
//                streamDecoder = StreamDecoder(height, width)
//                streamDecoder?.prepare()
            }

            override fun streamSize(width: Int, height: Int) {
                // 宽高要替换
                streamDecoder = StreamDecoder(height, width)
                streamDecoder?.prepare()
            }

            override fun cameraInited() {
//                cameraProvide.startPreview()
            }

        }

        cameraProvide.inintPreview(textureView)

        btnPush.setOnClickListener {
            cameraProvide.startStream()
        }
    }
}