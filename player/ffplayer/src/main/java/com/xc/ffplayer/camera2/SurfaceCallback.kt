package com.xc.ffplayer.camera2

import android.view.SurfaceHolder

open interface SurfaceCallback {
    fun addCallback(callback: SurfaceHolder.Callback)
    fun removeCallback(callback: SurfaceHolder.Callback)
}