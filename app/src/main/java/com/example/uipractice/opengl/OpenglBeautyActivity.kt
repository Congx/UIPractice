package com.example.uipractice.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import com.example.uipractice.opengl.filters.BaseFBOFilter
import com.example.uipractice.opengl.filters.BaseFilter
import com.example.uipractice.opengl.filters.SoulEffectFilter
import com.example.uipractice.opengl.renders.CameraRender
import kotlinx.android.synthetic.main.activity_opengl_effect_activity.*

class OpenglBeautyActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_effect_activity)

        glSurfaceView.setEGLContextClientVersion(2)
        val filter = BaseFBOFilter(this, vertexShader = R.raw.base_tex_vert,fragmentShader = R.raw.split_effect_frag)
        glSurfaceView.setRenderer(CameraRender(this,glSurfaceView, mutableListOf(filter)))
//        glSurfaceView.setRenderer(CameraRender(this,glSurfaceView))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.requestRender()

    }

}