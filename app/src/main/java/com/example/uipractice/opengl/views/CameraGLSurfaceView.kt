package com.example.uipractice.opengl.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.opengl.renders.CameraRender

class CameraGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    constructor(context: Context) : this(context,null)

    init {
        setEGLContextClientVersion(2)
        setRenderer(CameraRender(context as FragmentActivity,this))
        renderMode = RENDERMODE_WHEN_DIRTY
        //requestRender()
//        Log.e("CameraGLSurfaceView","init()")
    }

}