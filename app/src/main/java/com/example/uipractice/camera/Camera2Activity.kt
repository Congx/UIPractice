package com.example.uipractice.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.base.utils.MathUtils
import com.example.uipractice.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.layout_camera.*
import java.io.FileOutputStream


class Camera2Activity : AppCompatActivity() {

    lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_camera)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏

        RxPermissions(this).request(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe {
                initCamera()
            }

        button.setOnClickListener {
        }
        Choreographer.getInstance();
    }

    private fun initCamera() {

    }


}