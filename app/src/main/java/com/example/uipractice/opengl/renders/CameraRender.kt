package com.example.uipractice.opengl.renders

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLSurfaceView
import androidx.fragment.app.FragmentActivity
import com.base.context.ContextProvider
import com.example.uipractice.camera.CameraXGLProvider
import com.example.uipractice.camera.SurfaceTextureProvider
import com.example.uipractice.opengl.MediaRecorder
import com.example.uipractice.opengl.filters.CameraFilter
import com.example.uipractice.opengl.filters.Filter
import com.example.uipractice.opengl.filters.PreviewFilter
import com.example.uipractice.opengl.filters.SoulEffectFilter
import com.example.uipractice.opengl.utils.OpenGLUtils
import java.io.File
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(var context: FragmentActivity, var glSurfaceView: GLSurfaceView,
                   effectFilters:MutableList<Filter> = mutableListOf())
    :GLSurfaceView.Renderer, SurfaceTextureProvider,
    SurfaceTexture.OnFrameAvailableListener {


    var cameraFilter:CameraFilter = CameraFilter(context)

    val mOESTextureId by lazy {
        return@lazy OpenGLUtils.createOESTextureObject()
    }

    var surfaceTexture:SurfaceTexture? = null

    //    var previewFilter:PreviewFilter? = null
    var provider:CameraXGLProvider? = null

//    var soulEffectFilter:Filter? = null

    var mtx = FloatArray(16)

    private var mRecorder: MediaRecorder? = null

    var filters = mutableListOf<Filter>()

    init {
        // fbo 离屏渲染
        filters.add(0,cameraFilter)
        filters.addAll(effectFilters)
        // 把fbo的数据预览输出
        filters.add(PreviewFilter(context))
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        surfaceTexture.attachToGLContext(mOESTextureId)
//        var width = 1280 // 480
//        var height = 1960 // 640

        surfaceTexture = SurfaceTexture(mOESTextureId)

//        var width = 720 // 480
//        var height = 1280 // 640
        var width = 480 // 480
        var height = 640 // 640
        surfaceTexture?.setDefaultBufferSize(width, height)
        provider =  CameraXGLProvider(context = context, width = width, height = height, surfaceTextureProvider = this)
        surfaceTexture?.setOnFrameAvailableListener(this)

        for (filter in filters) {
            filter.onSurfaceCreated(gl, config)
        }

        // fbo 离屏渲染
//        cameraFilter = CameraFilter(context)
//        cameraFilter?.onSurfaceCreated(gl, config)
//
//        // 把fbo的数据预览输出
//        previewFilter = PreviewFilter(context)
//        previewFilter?.onSurfaceCreated(gl, config)
//
//        // 灵魂出窍特效
//        soulEffectFilter = SoulEffectFilter(context)
//        soulEffectFilter?.onSurfaceCreated(gl, config)

        // 把fbo的数据进行录制
        var path = ContextProvider.getContext()?.getExternalFilesDir("output")?.absolutePath + File.separator + "output.mp4"
        mRecorder = MediaRecorder(glSurfaceView.getContext(), path,
                EGL14.eglGetCurrentContext(),
                width, height)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        Log.e("CameraRender","width = $width,height = $height")
//        cameraFilter?.onSurfaceChanged(gl, width, height)
//        previewFilter?.onSurfaceChanged(gl, width, height)
//        soulEffectFilter?.onSurfaceChanged(gl, width, height)

        for (filter in filters) {
            filter.onSurfaceChanged(gl, width,height)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
//        Log.e("CameraRender","onDrawFrame")
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(mtx)
        cameraFilter.setTransformMatrix(mtx)

//        var texId = cameraFilter!!.onDrawFrame(mOESTextureId)
//        texId = soulEffectFilter!!.onDrawFrame(texId)
//        texId = previewFilter!!.onDrawFrame(texId)

        var textureId = mOESTextureId
        for (filter in filters) {
            textureId = filter.onDrawFrame(textureId)
        }
        mRecorder?.fireFrame(textureId, surfaceTexture!!.timestamp)
    }

    override fun provideSurface(): SurfaceTexture {
        return surfaceTexture!!
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        glSurfaceView.requestRender()
    }

    public fun release() {
        for (filter in filters) {
            filter.release()
        }
    }

    fun startRecord(speed: Float) {
        try {
            mRecorder!!.start(speed)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecord() {
        mRecorder!!.stop()
    }
}