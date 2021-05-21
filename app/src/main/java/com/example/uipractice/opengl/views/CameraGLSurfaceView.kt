package com.example.uipractice.opengl.views

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.opengl.renders.CameraRender

class CameraGLSurfaceView(context: Context) : GLSurfaceView(context) {


    init {
        setEGLContextClientVersion(2)
        setRenderer(CameraRender(context as FragmentActivity,this))
        renderMode = RENDERMODE_WHEN_DIRTY
        requestRender()
    }

}