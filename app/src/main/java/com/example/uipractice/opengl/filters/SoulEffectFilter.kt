package com.example.uipractice.opengl.filters

import android.content.Context
import android.opengl.GLES20
import com.example.uipractice.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SoulEffectFilter(context: Context):BaseFBOFilter(context, R.raw.base_tex_vert,R.raw.soul_effect_frag) {
    var scalePercent = 0
    var mixturePercent = 0

    var scale = 0f
    var mix = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        scalePercent = GLES20.glGetUniformLocation(program,"scalePercent")
        mixturePercent = GLES20.glGetUniformLocation(program,"mixturePercent")
    }

    override fun beforeOndraw(texture: Int) {
        super.beforeOndraw(texture)
        GLES20.glUniform1f(scalePercent,scale + 1f)
        GLES20.glUniform1f(mixturePercent ,1f-mix)

        scale +=0.08f
        mix +=0.08f
        if (scale >= 1f) {
            scale = 0f
        }

        if (mix >= 1f) {
            mix = 0f
        }
    }

}