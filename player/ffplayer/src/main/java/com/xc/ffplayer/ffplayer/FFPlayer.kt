package com.xc.ffplayer.ffplayer

import android.view.Surface

open class FFPlayer:Player {

    var mUrl = ""

    override fun setPath(url: String) {
        mUrl = url
        nativeffmpegInfo()
        nativeSetPath(url)
    }

    override fun start(surface: Surface) {
        nativeStart(surface)
    }

    override fun pause() {
        nativePause()
    }

    override fun resume() {
        nativeResume()
    }

    override fun stop() {
        nativeStop()
    }

    external private fun nativeffmpegInfo():String
    external private fun nativeSetPath(string: String):Int
    external private fun nativeStart(surface: Surface):Int
    external private fun nativePause()
    external private fun nativeResume()
    external private fun nativeStop()

    companion object {
        init {
            System.loadLibrary("ffmpeg")
        }
    }
}