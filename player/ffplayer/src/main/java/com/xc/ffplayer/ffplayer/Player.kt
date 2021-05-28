package com.xc.ffplayer.ffplayer

import android.view.Surface

interface Player {

    var callback:StatusCallback?

    fun setSource(source:String)

    fun prepare()

    fun start(surface: Surface)

    fun pause()

    fun resume()

    fun stop()
}

interface StatusCallback {
    fun onPrepared()
}