package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.R
import javax.microedition.khronos.opengles.GL10

class BeautyFilter(context: Context): BaseFBOFilter(context, R.raw.base_tex_vert,R.raw.beauty_frag) {
    var widthLocation = 0
    var heightLocation = 0
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        widthLocation = getUniform("width")
        heightLocation = getUniform("height")

    }

    override fun beforeOndraw(texture: Int) {
        super.beforeOndraw(texture)
        GLES20.glUniform1i(widthLocation,mWidth)
        GLES20.glUniform1i(heightLocation,mHeight)
    }

}