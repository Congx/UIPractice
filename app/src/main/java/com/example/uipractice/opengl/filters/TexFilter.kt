package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.R
import com.example.uipractice.opengl.utils.BufferUtil
import com.example.uipractice.opengl.utils.ProjectionMatrixHelper
import com.example.uipractice.opengl.utils.ShaderHelper
import com.example.uipractice.utils.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TexFilter(var context: Context): Filter {

    companion object {
        private val VERTEX_SHADER = """
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                // 纹理坐标：2个分量，S和T坐标
                attribute vec2 a_TexCoord;
                varying vec2 v_TexCoord;
                void main() {
                    v_TexCoord = a_TexCoord;
                    gl_Position = u_Matrix*a_Position;
                }
                """
        private val FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 v_TexCoord;
                // sampler2D：二维纹理数据的数组
                uniform sampler2D u_TextureUnit;
                void main() {
                    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);
                }
                """


        private val POINT_DATA = floatArrayOf(
                -0.5f, -0.5f,
                -0.5f, 0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f)

        /**
         * 纹理坐标
         */
        private val TEX_VERTEX = floatArrayOf(
                0f, 1f,
                0f, 0f,
                1f, 1f,
                1f, 0f)



    }

    var vertexBuffer = BufferUtil.createFloatBuffer(POINT_DATA)
    var texBuffer = BufferUtil.createFloatBuffer(TEX_VERTEX)

    var program = 0
    var aPositioLocation = 0
    var aTexCoordLocation = 0
    var uTexSamplerLocation = 0

    /**
     * 纹理数据
     */
    private var mTextureBean: TextureHelper.TextureBean? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER)
        val fragShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER)
        program = ShaderHelper.linkProgram(vertexShader, fragShader)

        GLES20.glUseProgram(program)

        aPositioLocation = GLES20.glGetAttribLocation(program, "a_Position")
        aTexCoordLocation = GLES20.glGetAttribLocation(program, "a_TexCoord")
        uTexSamplerLocation = GLES20.glGetUniformLocation(program, "u_TextureUnit")

        // 纹理数据
        mTextureBean = TextureHelper.loadTexture(context, R.drawable.pikachu)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(aPositioLocation,2,GLES20.GL_FLOAT,false,0,vertexBuffer)
        GLES20.glEnableVertexAttribArray(aPositioLocation)

        texBuffer.position(0)
        GLES20.glVertexAttribPointer(aTexCoordLocation,2,GLES20.GL_FLOAT,false,0,texBuffer)
        GLES20.glEnableVertexAttribArray(aTexCoordLocation)

        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // 开启纹理透明混合，这样才能绘制透明图片
        GLES20.glEnable(GL10.GL_BLEND)
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        ProjectionMatrixHelper(program,"u_Matrix").enable(width,height)
    }

    override fun onDrawFrame(texture: Int): Int {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // 设置当前活动的纹理单元为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        GLES20.glUniform1i(uTexSamplerLocation, 0)

        // 将纹理ID绑定到当前活动的纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBean!!.textureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        return 0
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}