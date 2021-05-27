package com.xc.ffplayer.ffplayer

interface Player {

    fun setUrl(url:String)

    fun start()

    fun pause()

    fun resume()

    fun stop()
}