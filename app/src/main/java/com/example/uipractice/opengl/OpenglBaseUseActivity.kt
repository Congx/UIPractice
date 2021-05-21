package com.example.uipractice.opengl

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import com.example.uipractice.opengl.renders.TriangleRender
import kotlinx.android.synthetic.main.activity_opengl_base_use.*

class OpenglBaseUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_base_use)

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(TriangleRender())
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.requestRender()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}