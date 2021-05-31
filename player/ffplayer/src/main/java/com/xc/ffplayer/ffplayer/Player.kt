package com.xc.ffplayer.ffplayer

import android.view.Surface

interface Player {

    var playerListener:IPlayerListener?

    fun setSource(source: String)

    fun prepare()

    fun start(surface: Surface)

    fun pause()

    fun resume()

    fun stop()

    fun seek(progress: Int)
    fun setVolume(volume: Int)
}

interface IPlayerListener {
    fun onPrepared()
    fun onLoad(load: Boolean)
    fun onCurrentTime(currentTime: Int, totalTime: Int)
    fun onError(code: Int, msg: String?)
    fun onPause(pause: Boolean)
    fun onDbValue(db: Int)
    fun onComplete()
    fun onNext(): String?
}