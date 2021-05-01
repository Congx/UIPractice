package com.example.uipractice.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.layout_camera2.*


class Camera2Activity : AppCompatActivity() {

    @SuppressLint("AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_camera2)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏

        RxPermissions(this).request(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe {
                initCamera()
            }

        button.setOnClickListener {
            camera2Provider.takePhoto()
        }

        btnStopPreview.setOnClickListener {
            camera2Provider.stopPreview()
        }

        btnStartPreview.setOnClickListener {
            camera2Provider.startPreview()
        }
    }

    private val camera2Provider: Camera2Provider by lazy {
        Camera2Provider(this)
    }

    private fun initCamera() {
        var provider = camera2Provider
        provider.initTexture(textureView)

//        Camera2Helper(this,textureView)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        var degree = 0
//        when(display!!.rotation) {
//                Surface.ROTATION_0 -> degree = 0
//                Surface.ROTATION_90 -> degree =90
//                Surface.ROTATION_180 -> degree =180
//                Surface.ROTATION_270 -> degree =270
//        }


        Log.d("onConfigurationChanged", "degree = ${degree}")

    }

}