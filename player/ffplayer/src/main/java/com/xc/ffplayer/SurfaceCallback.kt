package com.xc.ffplayer

import android.view.SurfaceHolder

open interface SurfaceCallback {
    fun addCallback(callback: SurfaceHolder.Callback)
    fun removeCallback(callback: SurfaceHolder.Callback)
}