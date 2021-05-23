package com.example.uipractice.opengl.renders

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.uipractice.opengl.utils.BufferUtil
import com.example.uipractice.opengl.utils.ShaderHelper
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRender:GLSurfaceView.Renderer {
    companion object {
        //关键字 概念：
        // 1. uniform 由外部程序传递给 shader，就像是C语言里面的常量，shader 只能用，不能改；
        // 2. attribute 是只能在 vertex shader 中使用的变量；
        // 3. varying 变量是 vertex shader 和 fragment shader 之间做数据传递用的。
        // 更多说明：http://blog.csdn.net/jackers679/article/details/6848085
        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         */
        private val VERTEX_SHADER = """
                // vec4：4个分量的向量：x、y、z、w
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                void main()
                {
                // gl_Position：GL中默认定义的输出变量，决定了当前顶点的最终位置
                    gl_Position = u_Matrix * a_Position;
                // gl_PointSize：GL中默认定义的输出变量，决定了当前顶点的大小
                 //   gl_PointSize = 40.0;
                }
        """
        /**
         * 片段着色器
         */
        private val FRAGMENT_SHADER = """
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                uniform vec4 u_Color;
                void main()
                {
                // gl_FragColor：GL中默认定义的输出变量，决定了当前片段的最终颜色
                   gl_FragColor = u_Color;
                }
        """

        private val U_MATRIX = "u_Matrix"
    }

    var program = 0

    var pointData = floatArrayOf(
            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f)

    var color = floatArrayOf(1f,0f,0f,1f)


    lateinit var vertexBuffer:FloatBuffer

    var positionLocation = 0
    var colorLocation = 0

    /**
     * 矩阵数组
     */
    private val mProjectionMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置刷新屏幕时候使用的颜色值,顺序是RGBA，值的范围从0~1。GLES20.glClear调用时使用该颜色值。
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        val vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER)
        val fragmentShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER)
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        vertexBuffer = BufferUtil.createFloatBuffer(pointData)
//        vertexBuffer.position(0)

        GLES20.glUseProgram(program)

        vertexBuffer.position(0)

        positionLocation = GLES20.glGetAttribLocation(program, "a_Position")
        GLES20.glVertexAttribPointer(positionLocation,2,GLES20.GL_FLOAT,false,0,vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionLocation)

        colorLocation = GLES20.glGetUniformLocation(program, "u_Color")

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 设置宽高
        GLES20.glViewport(0, 0, width, height)
        var aspectRatio = if (width > height) width.toFloat() / height.toFloat() else height.toFloat() / width.toFloat()

        if (width > height) {
            Matrix.orthoM(mProjectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f)
        }else {
            Matrix.orthoM(mProjectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f)
        }

        val vMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)

        GLES20.glUniformMatrix4fv(vMatrixLocation,1,false,mProjectionMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 很重要！！！ 刷新缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUniform4fv(colorLocation,1,color,0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 4)
//        GLES20.glDisableVertexAttribArray(positionLocation)
    }

}