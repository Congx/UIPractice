package com.example.uipractice.opengl.views

import android.content.Context
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.opengl.camera.CameraApi14
import com.example.uipractice.opengl.renders.CameraRender
import com.example.uipractice.opengl.renders.L11_1_CameraRenderer

class CameraGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    val camera = CameraApi14()
    val cameraRender = CameraRender(context as FragmentActivity, this)

    init {
//        openCamera()
        setEGLContextClientVersion(2)
        setRenderer(cameraRender)
//        setRenderer(L11_1_CameraRenderer(getContext(),camera))
        renderMode = RENDERMODE_WHEN_DIRTY
        requestRender()
//        Log.e("CameraGLSurfaceView","init()")
    }

    private fun openCamera() {
        camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        camera.preview()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cameraRender.release()
    }
}