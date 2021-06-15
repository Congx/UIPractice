package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.uipractice.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class CameraFilter(context: Context): BaseFBOFilter(context,R.raw.camera_vert,R.raw.camera_frag) {

    private var height: Int = 0
    private var width: Int = 0
    private var imgWidth: Int = 0
    private var imgHeight: Int = 0
    var mtx:FloatArray? = null
    var vMatrix:FloatArray = FloatArray(16)
    var uMatrixLocation = -1
    var uvMatrixLocation = -1
    val U_MATRIX = "u_Matrix"

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        uMatrixLocation = getUniform(U_MATRIX)
        uvMatrixLocation = getUniform("uv_Matrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        // TODO
        this.width = width
        this.height = height
        val sx = 1f * imgWidth / width
        val sy = 1f * imgHeight / height
        Matrix.setIdentityM(vMatrix, 0)
//        Matrix.scaleM(vMatrix, 0, sx, sy, 1f)
    }

    override fun beforeOndraw(texture: Int) {
        GLES20.glUniformMatrix4fv(uvMatrixLocation,1,false, vMatrix,0)

        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false, mtx,0)
    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.mtx = mtx
    }

    fun setImgSize(width: Int, height: Int) {
        this.imgWidth = width
        this.imgHeight = height
    }

}