package com.xc.ffplayer

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class H264Player(var surfaceView: SurfaceView,var path: String) :
    SurfaceHolder.Callback ,LifecycleObserver{

    var decoder:Decoder

    init {
        surfaceView.holder.addCallback(this)
        decoder = HardDecoder(path)
    }

    var surfaceValide = false

    fun release() {
        surfaceView.holder.removeCallback(this)
    }

    // ------- SurfaceHolder.Callback
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceValide = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceValide = true
//        decoder.start(holder.surface)
        decoder.start(null)

//        val h264Player2 = H264Player2(null, path, holder.surface)
//        h264Player2.play()

    }
    // ------- SurfaceHolder.Callback  end

//    fun start() {
//
//    }


    // lifecycle -------
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory() {
        release()
    }
    // lifecycle ---- end

}