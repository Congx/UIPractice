package com.example.uipractice.opengl.views

import android.content.Context
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.opengl.camera.CameraApi14
import com.example.uipractice.opengl.renders.CameraRender

class CameraGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
//    val camera = CameraApi14()
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

//    private fun openCamera() {
//        camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
//        camera.preview()
//    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cameraRender.release()
    }

    private var mSpeed = Speed.MODE_NORMAL

    enum class Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

    fun setSpeed(speed: Speed) {
        mSpeed = speed
    }

    fun startRecord() {
        //速度  时间/速度 speed小于就是放慢 大于1就是加快
        var speed = 1f
        speed = when (mSpeed) {
            Speed.MODE_EXTRA_SLOW -> 0.3f
            Speed.MODE_SLOW -> 0.5f
            Speed.MODE_NORMAL -> 1f
            Speed.MODE_FAST -> 2f
            Speed.MODE_EXTRA_FAST -> 3f
        }
        cameraRender.startRecord(speed)
    }

    fun stopRecord() {
        cameraRender.stopRecord()
    }
}