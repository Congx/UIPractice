package com.xc.ffplayer.ffplayer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class FFGLSurfaceView(context: Context,attrs: AttributeSet?) : GLSurfaceView(context,attrs) {
    private var myRender: MyRender? = null

    init {
        setEGLContextClientVersion(2)
        myRender = MyRender(context)
        setRenderer(myRender)
        //mode=GLSurfaceView.RENDERMODE_WHEN_DIRTY之后  调用requestRender()触发Render的onDrawFrame函数
        //mode=GLSurfaceView.RENDERMODE_CONTINUOUSLY之后  自动调用onDrawFrame  60fps左右
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setYUVData(
        width: Int,
        height: Int,
        y: ByteArray?,
        u: ByteArray?,
        v: ByteArray?
    ) {
        if (myRender != null) {
            myRender?.setYuvData(width, height, y, u, v)
            requestRender()
        }
    }
}