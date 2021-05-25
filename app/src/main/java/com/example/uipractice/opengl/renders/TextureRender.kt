package com.example.uipractice.opengl.renders

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.uipractice.opengl.filters.TexFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRender(var context:Context):GLSurfaceView.Renderer {

    lateinit var filter:TexFilter

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        filter = TexFilter(context)
        filter.onSurfaceCreated(gl,config)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        filter.onSurfaceChanged(gl,width,height)
    }

    override fun onDrawFrame(gl: GL10?) {
        filter.onDrawFrame(0)
    }
}