package com.example.uipractice.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.extensions.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_camera_x.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock


class CameraXActivity : AppCompatActivity() {

   var cameraXProvider :CameraXProvider? = null

    @SuppressLint("AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA),100)
        }

        startCamera()

        btn_switch.setOnClickListener {
            cameraXProvider?.switchCamera()
        }

        btn_start.setOnClickListener {
            cameraXProvider?.startPush()
        }
    }

    @SuppressLint("MissingPermission")
    fun startCamera() {
        var width = 480
        var height= 640
        if (cameraXProvider == null) {
            cameraXProvider = CameraXProvider(this,width,height)
            cameraXProvider?.initPreview(view_finder)
            cameraXProvider?.callback = object : StreamProviderCallback {

                override fun onPreviewSize(size: Size) {

                }

                override fun onStreamSize(size: Size) {
                    Log.d("CameraXActivity","onStreamSize :" + size.toString())
                }

                override fun onStreamPreperaed(byteArray: ByteArray, len: Int) {
                    Log.d("CameraXActivity","onStreamPreperaed  :" + len.toString())
                }

            }
        }

    }

}