package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.opengl.renders.FrameBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class BaseFBOFilter(context: Context, vertexShader: Int, fragmentShader: Int) :
        BaseFilter(context, vertexShader, fragmentShader) {

    var fboBuffer:FrameBuffer? = null

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        fboBuffer = FrameBuffer()
        fboBuffer?.setup(width,height)
    }

    override fun onDrawFrame(texture: Int):Int {
        fboBuffer?.bind()
        super.onDrawFrame(texture)
        fboBuffer?.unbind()
        return fboBuffer?.textureId ?: texture
    }

    override fun release() {
        super.release()
        fboBuffer?.release()
    }

}