package com.example.uipractice.opengl.renders

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.camera.CameraXGLProvider
import com.example.uipractice.camera.SurfaceTextureProvider
import com.example.uipractice.opengl.filters.CameraFilter
import com.example.uipractice.opengl.utils.OpenGLUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(var context: FragmentActivity,var glSurfaceView: GLSurfaceView):GLSurfaceView.Renderer, SurfaceTextureProvider,
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
    var provider:CameraXGLProvider? = null
    var mtx = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        futureTask.run()

//        surfaceTexture.attachToGLContext(mOESTextureId)
//        var width = 1280 // 480
//        var height = 1960 // 640

        surfaceTexture = SurfaceTexture(mOESTextureId)

//        surfaceTexture?.attachToGLContext(mOESTextureId)
        var width = 720 // 480
        var height = 1280 // 640
        surfaceTexture?.setDefaultBufferSize(width, height)
        provider =  CameraXGLProvider(context = context,width = width,height = height,surfaceTextureProvider = this)
        surfaceTexture?.setOnFrameAvailableListener(this)

        cameraFilter = CameraFilter(context)
        cameraFilter?.onSurfaceCreated(gl,config)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.e("CameraRender","width = $width,height = $height")
        cameraFilter?.onSurfaceChanged(gl,width,height)
    }


    override fun onDrawFrame(gl: GL10?) {
//        Log.e("CameraRender","onDrawFrame")
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(mtx)
        cameraFilter?.setTransformMatrix(mtx)
        cameraFilter?.onDrawFrame(mOESTextureId)
    }

    override fun provideSurface(): SurfaceTexture {
        return surfaceTexture!!
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        glSurfaceView.requestRender()
    }
}