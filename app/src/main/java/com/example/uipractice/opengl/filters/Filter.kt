package com.example.uipractice.opengl.filters

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

interface Filter {

    fun onDrawFrame(texture:Int)

    fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)

    fun onSurfaceCreated(gl: GL10?, config: EGLConfig?)
}