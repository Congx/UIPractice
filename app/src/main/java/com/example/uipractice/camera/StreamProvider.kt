package com.example.uipractice.camera

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.TextureView
import androidx.camera.view.PreviewView

open class StreamProvider(var width:Int = 0, var height:Int = 0, var callback: StreamProviderCallback? = null) {

    var previewVieew:PreviewView? = null
    var textureView:TextureView? = null
    @Volatile
    var isStarting = false

    // 用于cameraX
    open fun initPreview(previewView:PreviewView) {
        this.previewVieew = previewVieew
    }

    // 用于camera
    open fun initPreview(textureView :TextureView) {
        this.textureView = textureView
    }

    open fun startPush() {
        isStarting = true
    }

    open fun stopPush() {
        isStarting = false
    }
}


open interface StreamProviderCallback {
    fun onPreviewSize(size: Size)
    fun onStreamSize(size: Size)
    fun onStreamPreperaed(byteArray: ByteArray, len:Int)
    fun onSurfaceTextureCreated(surfaceTexture: SurfaceTexture){}
}

open interface SurfaceTextureProvider {
    fun provideSurface():SurfaceTexture
}