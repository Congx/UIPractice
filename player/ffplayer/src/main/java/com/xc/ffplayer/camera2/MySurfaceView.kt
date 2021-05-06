package com.xc.ffplayer.camera2

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup


class MySurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback ,Runnable,
    SurfaceCallback {

    var isValidate = false
    var callbacks = mutableListOf<SurfaceHolder.Callback>()

    init {
        holder.addCallback(this)
    }

    override fun addCallback(callback: SurfaceHolder.Callback) {
        callbacks.add(callback)
    }

    override fun removeCallback(callback: SurfaceHolder.Callback) {
        callbacks.remove(callback)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        callbacks.forEach {
            it.surfaceChanged(holder,format,width,height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isValidate = false
        callbacks.forEach {
            it.surfaceDestroyed(holder)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isValidate = true
        callbacks.forEach {
            it.surfaceCreated(holder)
        }
    }

    override fun run() {
        // todo
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            //切换到竖屏
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = dp2px(context,270f)

        }else{
            //切换到横屏
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    // 方法1
    fun dp2px(ctx: Context, dp: Float): Int {
        val scale = ctx.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


}