package com.example.uipractice.opengl.filters

import android.opengl.GLES20
import com.example.uipractice.BuildConfig
import com.example.uipractice.opengl.utils.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class BaseFilter {

    var program = 0

    abstract fun onSurfaceCreated(gl: GL10?, config: EGLConfig?)

    abstract fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)

    abstract fun onDrawFrame(texture:Int)

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
}