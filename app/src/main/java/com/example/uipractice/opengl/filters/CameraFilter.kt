package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.R
import com.example.uipractice.opengl.utils.BufferUtil
import com.example.uipractice.opengl.utils.OpenGLUtils
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class CameraFilter(var context: Context): BaseFilter() {

    companion object {
        const val A_POSITION = "a_Position"
        const val A_TEXCOORD = "a_TexCoord"
        const val U_MATRIX = "u_Matrix"
        const val U_TEXTUREUNIT = "u_TextureUnit"
    }

    var mtx:FloatArray? = null
    var aPositionLocation = -1
    var aTexCoorLocation = -1
    var uMatrixLocation = -1
    var uTextureUnit = -1

    //----- buffer
    val pointBuffer:FloatBuffer = BufferUtil.createFullVertexBuffer()
    val texBuffer:FloatBuffer = BufferUtil.createAndroidVertexBuffer()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = OpenGLUtils.readRawTextFile(context, R.raw.camera_vert)
        val fragmentShader = OpenGLUtils.readRawTextFile(context, R.raw.camera_frag)
        makeProgram(vertexShader,fragmentShader)
        aPositionLocation = getAttrib(A_POSITION)
        aTexCoorLocation = getAttrib(A_TEXCOORD)
        uMatrixLocation = getUniform(U_MATRIX)
        uTextureUnit = getUniform(U_TEXTUREUNIT)

        // gl 坐标
        pointBuffer.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation,2,GLES20.GL_FLOAT,false,0,pointBuffer)
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // 纹理 坐标
        texBuffer.position(0)
        GLES20.glVertexAttribPointer(aTexCoorLocation,2,GLES20.GL_FLOAT,false,0,texBuffer)
        GLES20.glEnableVertexAttribArray(aTexCoorLocation)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(texture: Int) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture)
        GLES20.glUniform1i(uTextureUnit,0)
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,mtx,0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)
    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.mtx = mtx
    }

}