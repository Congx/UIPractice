package com.example.uipractice.opengl.filters

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

interface Filter {

    fun onSurfaceCreated(gl: GL10?, config: EGLConfig?)

    fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)

    fun onDrawFrame(texture:Int):Int
}