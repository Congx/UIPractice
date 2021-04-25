package com.xc.ffplayer

import android.view.Surface

open interface Decoder {

    fun start(surface: Surface?)
    fun start()

}