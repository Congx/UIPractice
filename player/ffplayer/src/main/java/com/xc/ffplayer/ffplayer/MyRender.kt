package com.xc.ffplayer.ffplayer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRender(var context: Context):GLSurfaceView.Renderer {

    private var yuv420Texture: YUV420Texture? = null

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        yuv420Texture = YUV420Texture(context)
        yuv420Texture?.initYUV()
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //宽高
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //设置背景颜色
//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        yuv420Texture?.draw()
    }

    fun setYuvData(
        width: Int,
        height: Int,
        y: ByteArray?,
        u: ByteArray?,
        v: ByteArray?
    ) {
        if (yuv420Texture != null) {
            yuv420Texture?.setYUVData(width, height, y, u, v)
        }
    }
}