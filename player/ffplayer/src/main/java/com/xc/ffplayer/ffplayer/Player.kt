package com.xc.ffplayer.ffplayer

import android.view.Surface

interface Player {

    fun setPath(url:String)

    fun start(surface: Surface)

    fun pause()

    fun resume()

    fun stop()
}