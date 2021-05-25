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
import com.example.uipractice.opengl.filters.PreviewFilter
import com.example.uipractice.opengl.utils.OpenGLUtils
import java.io.File
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(var context: FragmentActivity, var glSurfaceView: GLSurfaceView):GLSurfaceView.Renderer, SurfaceTextureProvider,
    SurfaceTexture.OnFrameAvailableListener {


    val mOESTextureId by lazy {
        return@lazy OpenGLUtils.createOESTextureObject()
    }
//    var mOESTextureId = 0

//    val surfaceTexture by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
//        return@lazy SurfaceTexture(mOESTextureId)
//    }

    var surfaceTexture:SurfaceTexture? = null

//    var futureTask = FutureTask<SurfaceTexture> {
//        return@FutureTask surfaceTexture
//    }

    var cameraFilter:CameraFilter? = null
    var previewFilter:PreviewFilter? = null
    var provider:CameraXGLProvider? = null
    var mtx = FloatArray(16)

    private var mRecorder: MediaRecorder? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        futureTask.run()

//        surfaceTexture.attachToGLContext(mOESTextureId)
//        var width = 1280 // 480
//        var height = 1960 // 640

        surfaceTexture = SurfaceTexture(mOESTextureId)

//        surfaceTexture?.attachToGLContext(mOESTextureId)
//        var width = 720 // 480
//        var height = 1280 // 640
        var width = 480 // 480
        var height = 640 // 640
        surfaceTexture?.setDefaultBufferSize(width, height)
        provider =  CameraXGLProvider(context = context, width = width, height = height, surfaceTextureProvider = this)
        surfaceTexture?.setOnFrameAvailableListener(this)

        cameraFilter = CameraFilter(context)
        cameraFilter?.onSurfaceCreated(gl, config)

        previewFilter = PreviewFilter(context)
        previewFilter?.onSurfaceCreated(gl, config)

        var path = ContextProvider.getContext()?.getExternalFilesDir("output")?.absolutePath + File.separator + "output.mp4"
        mRecorder = MediaRecorder(glSurfaceView.getContext(), path,
                EGL14.eglGetCurrentContext(),
                width, height)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        Log.e("CameraRender","width = $width,height = $height")
        cameraFilter?.onSurfaceChanged(gl, width, height)
        previewFilter?.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
//        Log.e("CameraRender","onDrawFrame")
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(mtx)
        cameraFilter?.setTransformMatrix(mtx)
        var texId = cameraFilter!!.onDrawFrame(mOESTextureId)
        texId = previewFilter!!.onDrawFrame(texId)

        mRecorder?.fireFrame(texId, surfaceTexture!!.timestamp)
    }

    override fun provideSurface(): SurfaceTexture {
        return surfaceTexture!!
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        glSurfaceView.requestRender()
    }

    public fun release() {
        cameraFilter?.release()
        previewFilter?.release()
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