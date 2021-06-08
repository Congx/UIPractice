package com.xc.ffplayer.ffplayer

import android.content.Context
import android.opengl.GLES20
import com.xc.ffplayer.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


open class YUV420Texture(context: Context?) {
    private var context: Context? = null

    //顶点坐标
    var vertexData = floatArrayOf( // in counterclockwise order:
        -1f, -1f, 0.0f,  // bottom left
        1f, -1f, 0.0f,  // bottom right
        -1f, 1f, 0.0f,  // top left
        1f, 1f, 0.0f
    )

    //纹理坐标
    var textureData = floatArrayOf( // in counterclockwise order:
        0f, 1f, 0.0f,  // bottom left
        1f, 1f, 0.0f,  // bottom right
        0f, 0f, 0.0f,  // top left
        1f, 0f, 0.0f
    )

    //每一次取点的时候取几个点
    val COORDS_PER_VERTEX = 3

    private val vertexCount = vertexData.size / COORDS_PER_VERTEX

    //每一次取的总的点 大小
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


    //位置
    private var vertexBuffer: FloatBuffer? = null

    //纹理
    private var textureBuffer: FloatBuffer? = null

    private var program = 0

    //顶点位置
    private var avPosition = 0

    //纹理位置
    private var afPosition = 0

    //shader  yuv变量
    private var sampler_y = 0
    private var sampler_u = 0
    private var sampler_v = 0
    private lateinit var textureId_yuv: IntArray


    //YUV数据
    private var width_yuv = 0
    private var height_yuv = 0
    private var y: ByteBuffer? = null
    private var u: ByteBuffer? = null
    private var v: ByteBuffer? = null


    init {
        this.context = context
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        vertexBuffer?.position(0)
        textureBuffer = ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(textureData)
        textureBuffer?.position(0)
    }

    fun initYUV() {
        val vertexSource: String = ShaderUtil.readRawTxt(context!!, R.raw.vertex_shader) ?: ""
        val fragmentSource: String = ShaderUtil.readRawTxt(context!!, R.raw.fragment_shader) ?: ""
        program = ShaderUtil.createProgram(vertexSource, fragmentSource)
        if (program > 0) {
            //获取顶点坐标字段
            avPosition = GLES20.glGetAttribLocation(program, "av_Position")
            //获取纹理坐标字段
            afPosition = GLES20.glGetAttribLocation(program, "af_Position")
            //获取yuv字段
            sampler_y = GLES20.glGetUniformLocation(program, "sampler_y")
            sampler_u = GLES20.glGetUniformLocation(program, "sampler_u")
            sampler_v = GLES20.glGetUniformLocation(program, "sampler_v")
            textureId_yuv = IntArray(3)
            //创建3个纹理
            GLES20.glGenTextures(3, textureId_yuv, 0)

            //绑定纹理
            for (id in textureId_yuv) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
                //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT
                )
                //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
                )
            }
        }
    }

    fun setYUVData(
        width: Int,
        height: Int,
        y: ByteArray?,
        u: ByteArray?,
        v: ByteArray?
    ) {
        width_yuv = width
        height_yuv = height
        this.y = ByteBuffer.wrap(y)
        this.u = ByteBuffer.wrap(u)
        this.v = ByteBuffer.wrap(v)
    }

    fun draw() {
        if (width_yuv > 0 && height_yuv > 0 && y != null && u != null && v != null) {
            GLES20.glUseProgram(program)
            GLES20.glEnableVertexAttribArray(avPosition)
            GLES20.glVertexAttribPointer(
                avPosition,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
            GLES20.glEnableVertexAttribArray(afPosition)
            GLES20.glVertexAttribPointer(
                afPosition,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                textureBuffer
            )

            //激活纹理0来绑定y数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[0])
            //glTexImage2D (int target,
            //                int level,
            //                int internalformat,
            //                int width,
            //                int height,
            //                int border,
            //                int format,
            //                int type,
            //                Buffer pixels)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_LUMINANCE,
                width_yuv,
                height_yuv,
                0,
                GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE,
                y
            )

            //激活纹理1来绑定u数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[1])
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_LUMINANCE,
                width_yuv / 2,
                height_yuv / 2,
                0,
                GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE,
                u
            )

            //激活纹理2来绑定u数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[2])
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_LUMINANCE,
                width_yuv / 2,
                height_yuv / 2,
                0,
                GLES20.GL_LUMINANCE,
                GLES20.GL_UNSIGNED_BYTE,
                v
            )

            //给fragment_shader里面yuv变量设置值   0 1 2 标识纹理x
            GLES20.glUniform1i(sampler_y, 0)
            GLES20.glUniform1i(sampler_u, 1)
            GLES20.glUniform1i(sampler_v, 2)

            //绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)
            y?.clear()
            u?.clear()
            v?.clear()
            y = null
            u = null
            v = null
            GLES20.glDisableVertexAttribArray(afPosition)
            GLES20.glDisableVertexAttribArray(avPosition)
        }
    }
}