package com.xc.ffplayer

import android.view.Surface
import com.xc.ffplayer.live.Releaseable

open interface Decoder :Releaseable{

    fun prepare()
    fun decode(surface: Surface?)
    fun decode()
    fun decode(byteArray: ByteArray)
}