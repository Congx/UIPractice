package com.example.uipractice.opengl.filters

import android.content.Context
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class CameraFilter(var context: Context):Filter {

    var mtx:FloatArray? = null

    override fun onDrawFrame(texture: Int) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.mtx = mtx
    }

}