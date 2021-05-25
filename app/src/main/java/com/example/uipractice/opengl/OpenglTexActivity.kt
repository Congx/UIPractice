package com.example.uipractice.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import com.example.uipractice.opengl.renders.CameraRender
import com.example.uipractice.opengl.renders.TextureRender
import com.example.uipractice.opengl.views.CameraGLSurfaceView
import com.example.uipractice.opengl.views.RecordButton
import kotlinx.android.synthetic.main.activity_opengl_cameractivity.*
import kotlinx.android.synthetic.main.activity_opengl_effect_activity.*

class OpenglTexActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_effect_activity)

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(TextureRender(this))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.requestRender()

    }

}