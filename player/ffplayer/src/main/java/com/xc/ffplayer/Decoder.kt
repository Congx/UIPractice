package com.xc.ffplayer

import android.view.Surface

open interface Decoder {

    fun prepare()
    fun decode(surface: Surface?)
    fun decode()
    fun decode(byteArray: ByteArray)

    fun stop()
}