package com.example.uipractice.opengl

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import com.example.uipractice.R
import com.example.uipractice.camera.CameraXGLProvider
import com.example.uipractice.camera.CameraXProvider
import com.example.uipractice.camera.StreamProviderCallback
import com.example.uipractice.camera.SurfaceTextureProvider
import com.example.uipractice.opengl.renders.L1_1_PointRenderer
import com.example.uipractice.opengl.renders.TriangleRender
import kotlinx.android.synthetic.main.activity_opengl_base_use.*

class OpenglBaseUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_base_use)

//        glSurfaceView.setEGLContextClientVersion(2)
//        glSurfaceView.setRenderer(TriangleRender())
////        glSurfaceView.setRenderer(L1_1_PointRenderer())
//        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//        glSurfaceView.requestRender()

        CameraXGLProvider(context = this,width = 480,height = 640,surfaceTextureProvider = object :SurfaceTextureProvider {
            override fun provideSurface(): SurfaceTexture {
                return glSurfaceView.surfaceTexture!!
            }

        })

    }

//    override fun onResume() {
//        super.onResume()
//        glSurfaceView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        glSurfaceView.onPause()
//    }
}