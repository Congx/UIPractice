package com.xc.ffplayer.ffplayer

open class FFPlayer:Player {

    var mUrl = ""

    override fun setUrl(url: String) {
        mUrl = url
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    external fun ffmpegInfo():String

    companion object {
        init {
            System.loadLibrary("ffmpeg")
        }
    }
}