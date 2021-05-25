package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import androidx.annotation.CallSuper
import com.example.uipractice.BuildConfig
import com.example.uipractice.R
import com.example.uipractice.opengl.utils.*
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class BaseFilter constructor(var context: Context,
                                  var vertexShader: Int = R.raw.base_tex_vert,
                                  var fragmentShader: Int = R.raw.base_tex_frag,
                                  var pointData:FloatArray = Constants.FULL_POINT_DATA,// gl 坐标系顶点
                                  // 纹理坐标顶点
                                  var texVertex:FloatArray = Constants.ANDROID_POINT_DATA) : Filter {

    companion object {
        const val A_POSITION = "a_Position"
        const val A_TEXCOORD = "a_TexCoord"
        const val U_TEXTURE_SAMPLER = "u_TextureSampler"
    }

    var program = 0

    var aPositionLocation = -1
    var aTexCoorLocation = -1
//    var uMatrixLocation = -1
    var uTextureSampler = -1
//    var uProjectionMatrix = -1

    private var mWidth:Int = 0
    private var mHeight:Int = 0

    //----- buffer
    val pointBuffer: FloatBuffer = BufferUtil.createFloatBuffer(pointData)
    val texBuffer: FloatBuffer = BufferUtil.createFloatBuffer(texVertex)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = OpenGLUtils.readRawTextFile(context, vertexShader)
        val fragmentShader = OpenGLUtils.readRawTextFile(context, fragmentShader)
        makeProgram(vertexShader, fragmentShader)
        aPositionLocation = getAttrib(A_POSITION)
        aTexCoorLocation = getAttrib(A_TEXCOORD)
        uTextureSampler = getUniform(U_TEXTURE_SAMPLER)

        // gl 坐标
        pointBuffer.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, pointBuffer)
        GLES20.glEnableVertexAttribArray(aPositionLocation)


        // 纹理 坐标
        texBuffer.position(0)
        GLES20.glVertexAttribPointer(aTexCoorLocation, 2, GLES20.GL_FLOAT, false, 0, texBuffer)
        GLES20.glEnableVertexAttribArray(aTexCoorLocation)

        GLES20.glClearColor(0f, 0f, 0f, 1f)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(texture: Int):Int {

        GLES20.glViewport(0, 0, mWidth, mHeight)
        GLES20.glUseProgram(program)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glUniform1i(uTextureSampler, 0)

        beforeOndraw(texture)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        return texture
    }

    open fun beforeOndraw(texture: Int) {

    }

    open fun projectionMatrix(width: Int, height: Int,projectMatrixName:String) {
        ProjectionMatrixHelper(program, projectMatrixName).enable(width, height)
    }

    /**
     * 创建OpenGL程序对象
     *
     * @param vertexShader   顶点着色器代码
     * @param fragmentShader 片段着色器代码
     */
    protected fun makeProgram(vertexShader: String, fragmentShader: String) {
        // 步骤1：编译顶点着色器
        val vertexShaderId = ShaderHelper.compileVertexShader(vertexShader)
        // 步骤2：编译片段着色器
        val fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShader)
        // 步骤3：将顶点着色器、片段着色器进行链接，组装成一个OpenGL程序
        program = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId)

        if (BuildConfig.DEBUG) {
            ShaderHelper.validateProgram(program)
        }
        // 步骤4：通知OpenGL开始使用该程序
        GLES20.glUseProgram(program)
    }

    protected fun getUniform(name: String): Int {
        return GLES20.glGetUniformLocation(program, name)
    }

    protected fun getAttrib(name: String): Int {
        return GLES20.glGetAttribLocation(program, name)
    }

    @CallSuper
    override fun release() {
        GLES20.glDeleteProgram(program)
    }
}