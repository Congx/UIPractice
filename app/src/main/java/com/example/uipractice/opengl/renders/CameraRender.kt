package com.example.uipractice.opengl.renders

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import androidx.fragment.app.FragmentActivity
import com.example.uipractice.camera.CameraXProvider
import com.example.uipractice.camera.SurfaceTextureProvider
import com.example.uipractice.opengl.filters.CameraFilter
import com.example.uipractice.opengl.utils.GUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(var context: FragmentActivity,var glSurfaceView: GLSurfaceView):GLSurfaceView.Renderer, SurfaceTextureProvider,
    SurfaceTexture.OnFrameAvailableListener {


    var mOESTextureId = GUtils.createOESTextureObject()

    val surfaceTexture by lazy {
        return@lazy SurfaceTexture(mOESTextureId)
    }

    var cameraFilter:CameraFilter? = null

    init {
        CameraXProvider(context = context,width = 480,height = 640,surfaceTextureProvider = this)
    }

    var mtx = FloatArray(16)


    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture.releaseTexImage()
        surfaceTexture.getTransformMatrix(mtx)
        cameraFilter?.setTransformMatrix(mtx)

        cameraFilter?.onDrawFrame(mOESTextureId)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraFilter?.onSurfaceChanged(gl,width,height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceTexture.attachToGLContext(mOESTextureId)
        surfaceTexture.setOnFrameAvailableListener(this)
        cameraFilter = CameraFilter(context)
        cameraFilter?.onSurfaceCreated(gl,config)
    }

    override fun provideSurface(): SurfaceTexture {
        return surfaceTexture
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        glSurfaceView.requestRender()
    }
}