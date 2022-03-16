package com.example.uipractice.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.YuvImage
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.FlashMode
import androidx.camera.extensions.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

class CameraXGLProvider(var context:FragmentActivity,
                        width:Int = 0, height:Int = 0,
                        callback: StreamProviderCallback? = null,
                        var surfaceTextureProvider: SurfaceTextureProvider? = null):
    StreamProvider(width,height,callback) {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
//        private val resolution: Size = Size(480, 640)
    }

    private var cameraProvider:ProcessCameraProvider? = null
    private lateinit var previewView:PreviewView
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    var previewCase:Preview? = null

    var camera:Camera? = null

    init {
        if (surfaceTextureProvider != null){
            initCameraProvider()
        }
    }

    override fun initPreview(previewView: PreviewView) {
        this.previewView = previewView
        if (cameraProvider == null) {
            initCameraProvider()
        }
    }

    fun initCameraProvider() {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(Runnable {
            cameraProvider = future.get()

            if (cameraProvider == null) {
                Toast.makeText(context, "无可用的设备cameraId!,请检查设备的相机是否被占用", Toast.LENGTH_SHORT).show();
                return@Runnable
            }

            bindCamera()

        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCamera() {

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        //预览方面的设置
        var builder = Preview.Builder()
        var size = Size(width,height)
        previewCase = builder
            .setTargetResolution(size)
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

        previewCase?.setSurfaceProvider { request ->
            request.provideSurface(
                Surface(surfaceTextureProvider?.provideSurface()), ContextCompat.getMainExecutor(context),
                Consumer {

                })
        }

        cameraProvider?.unbindAll()
        camera = cameraProvider?.bindToLifecycle(context, cameraSelector, previewCase)

    }

    /**
     * 前后置切换
     */
    fun switchCamera() {
        if (CameraSelector.LENS_FACING_FRONT == lensFacing){
            lensFacing = CameraSelector.LENS_FACING_BACK
        }else {
            lensFacing = CameraSelector.LENS_FACING_FRONT
        }
        bindCamera()
    }
}