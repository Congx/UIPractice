package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class CameraFilter(context: Context): BaseFBOFilter(context,R.raw.camera_vert,R.raw.camera_frag) {

    var mtx:FloatArray? = null
    var uMatrixLocation = -1
    val U_MATRIX = "u_Matrix"

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        uMatrixLocation = getUniform(U_MATRIX)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        // TODO
    }

    override fun beforeOndraw(texture: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false, mtx,0)
    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.mtx = mtx
    }

}