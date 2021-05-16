package com.example.uipractice.camera

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.extensions.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

class CameraXProvider(var context:FragmentActivity,width:Int = 0, height:Int = 0, callback: StreamProviderCallback? = null):
    StreamProvider(width,height,callback) {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
//        private val resolution: Size = Size(480, 640)
    }

    private var cameraProvider:ProcessCameraProvider? = null
    private lateinit var previewView:PreviewView
    private var executors = Executors.newSingleThreadExecutor()
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var lock = ReentrantLock()
    private var streamSize:Size? = null

    override fun initPreview(previewView: PreviewView) {
        this.previewView = previewView
        if (cameraProvider == null) {
            initCameraProvider()
        }
    }

    private fun initCameraProvider() {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(Runnable {
            cameraProvider = future.get()

            if (cameraProvider == null) {
                Toast.makeText(context, "无可用的设备cameraId!,请检查设备的相机是否被占用", Toast.LENGTH_SHORT).show();
                return@Runnable
            }

//            lensFacing = getLensFacing()
//            if (lensFacing == -1) return@Runnable

            bindCamera()

        }, ContextCompat.getMainExecutor(context))
    }

    var yuv:ByteArray? = null
    var buffer:ByteArray? = null

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCamera() {

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        //预览方面的设置
        var builder = Preview.Builder()
//        setPreviewExtender(builder,cameraSelector)
        var size = getPreviewViewSize()
        var preview = builder
            .setTargetAspectRatio(aspectRatio(getPreviewViewSize().width,size.height))
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)
        //                preview.setSurfaceProvider { request ->
        //                    request.provideSurface(Surface(view_finder.surfaceTexture), ContextCompat.getMainExecutor(this@CameraXProvider),
        //                        Consumer {
        //
        //                    })
        //                }

        // 捕获流
        var imageAnalysis = ImageAnalysis.Builder()
            .setBackgroundExecutor(executors)
            .setTargetResolution(Size(width,height))
            .build()

        imageAnalysis.setAnalyzer(executors, ImageAnalysis.Analyzer { image->
            if (streamSize == null) {
                streamSize = Size(image.height,image.width)
                callback?.onStreamSize(streamSize!!)
            }
            if (!isStarting) {
                image.close()
                return@Analyzer
            }
            lock.lock()
            // 测试方法
//            dataTest(image)
            if (yuv == null) {
                val size = (image.width * image.height) * 3 / 2
                yuv = ByteArray(size)
                buffer = ByteArray(size)
            }

            ImageUtil.getBytesFromImageAsType(image.image, ImageUtil.YUV420SPNV12, buffer)
            ImageUtil.rotateYUV420SP(buffer, yuv,image.width, image.height)

//            decodeTest(yuv,image.height,image.width)
            callback?.onStreamPreperaed(yuv!!,yuv!!.size)
            image.close()
            lock.unlock()
        })

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(context, cameraSelector, preview,imageAnalysis)

    }

    fun switchCamera() {
        if (CameraSelector.LENS_FACING_FRONT == lensFacing){
            lensFacing = CameraSelector.LENS_FACING_BACK
        }else {
            lensFacing = CameraSelector.LENS_FACING_FRONT
        }
        streamSize = null
        bindCamera()
    }

    private fun getLensFacing(): Int {
        if (hasBackCamera()) {
            return CameraSelector.LENS_FACING_BACK
        }
        return if (hasFrontCamera()) {
            CameraSelector.LENS_FACING_FRONT
        } else -1
    }

    /**
     * 是否有后摄像头
     */
    private fun hasBackCamera(): Boolean {
        try {
            return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 是否有前摄像头
     */
    private fun hasFrontCamera(): Boolean {
        try {
            return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    private fun getPreviewViewSize():Size {
        if (textureView != null) {
           return Size(textureView!!.display.width,textureView!!.display.height)
        }else if (previewView != null) {
            return Size(previewView!!.display.width,previewView!!.display.height)
        }
        return Size(context.resources.displayMetrics.widthPixels,context.resources.displayMetrics.heightPixels)
    }

    /**
     * 给预览设置外部扩展
     * @param builder
     * @param cameraSelector
     */
    private fun setPreviewExtender(
            builder: Preview.Builder,
            cameraSelector: CameraSelector
    ) {
        val extender = AutoPreviewExtender.create(builder)
        if (extender.isExtensionAvailable(cameraSelector)) {
            extender.enableExtension(cameraSelector)
        }
        val bokehPreviewExtender = BokehPreviewExtender.create(builder)
        if (bokehPreviewExtender.isExtensionAvailable(cameraSelector)) {
            bokehPreviewExtender.enableExtension(cameraSelector)
        }
        val hdrPreviewExtender = HdrPreviewExtender.create(builder)
        if (hdrPreviewExtender.isExtensionAvailable(cameraSelector)) {
            hdrPreviewExtender.enableExtension(cameraSelector)
        }
        val beautyPreviewExtender = BeautyPreviewExtender.create(builder)
        if (beautyPreviewExtender.isExtensionAvailable(cameraSelector)) {
            beautyPreviewExtender.enableExtension(cameraSelector)
        }
        val nightPreviewExtender = NightPreviewExtender.create(builder)
        if (nightPreviewExtender.isExtensionAvailable(cameraSelector)) {
            nightPreviewExtender.enableExtension(cameraSelector)
        }
    }

    /**
     * 给拍照设置外部预览
     * @param builder
     * @param cameraSelector
     */
    private fun setImageCaptureExtender(
            builder: ImageCapture.Builder,
            cameraSelector: CameraSelector
    ) {
        val autoImageCaptureExtender =
                AutoImageCaptureExtender.create(builder)
        if (autoImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            autoImageCaptureExtender.enableExtension(cameraSelector)
        }
        val bokehImageCaptureExtender =
                BokehImageCaptureExtender.create(builder)
        if (bokehImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            bokehImageCaptureExtender.enableExtension(cameraSelector)
        }
        val hdrImageCaptureExtender =
                HdrImageCaptureExtender.create(builder)
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector)
        }
        val beautyImageCaptureExtender =
                BeautyImageCaptureExtender.create(builder)
        if (beautyImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            beautyImageCaptureExtender.enableExtension(cameraSelector)
        }
        val nightImageCaptureExtender =
                NightImageCaptureExtender.create(builder)
        if (nightImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            nightImageCaptureExtender.enableExtension(cameraSelector)
        }
    }

    private fun aspectRatio(widthPixels: Int, heightPixels: Int): Int {
        val previewRatio =
                Math.max(widthPixels, heightPixels).toDouble() / Math.min(
                        widthPixels,
                        heightPixels
                ).toDouble()
        return if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }
    
    
    ////// 测试代码
    var isWrite = true
    @SuppressLint("UnsafeOptInUsageError")
    private fun dataTest(image: ImageProxy) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        // 宽高是未经过 宽高转换的
        Log.d("CameraXProvider", "width = ${image.width} ,height = ${image.height}")
        Log.d("CameraXProvider", "rotationDegrees = ${rotationDegrees}")

        image.toString()
        val y = image.planes[0].buffer
        val u = image.planes[1].buffer
        val v = image.planes[2].buffer

        val ylimit = image.planes[0].buffer.limit() - image.planes[0].buffer.position()
        val ulimit = image.planes[1].buffer.limit() - image.planes[1].buffer.position()
        val ySize = image.planes[0].buffer.remaining()
        val uSize = image.planes[1].buffer.remaining()
        val vSize = image.planes[2].buffer.remaining()

        Log.d("CameraXProvider", "ylimit = ${ylimit}")
        Log.d("CameraXProvider", "ulimit = ${ulimit}")
        Log.d("CameraXProvider", "ySize = ${ySize}")
        Log.d("CameraXProvider", "uSize = ${uSize}")
        Log.d("CameraXProvider", "vSize = ${vSize}")
        Log.d("CameraXProvider", "rotationDegrees = ${rotationDegrees}")

        Log.d("CameraXProvider", "-----")

        Log.d("CameraXProvider", "image.planes[0].pixelStride = ${image.planes[0].pixelStride}")
        Log.d("CameraXProvider", "image.planes[0].pixelStride = ${image.planes[0].rowStride}")
        Log.d("CameraXProvider", "image.planes[1].pixelStride = ${image.planes[1].pixelStride}")
        Log.d("CameraXProvider", "image.planes[1].pixelStride = ${image.planes[1].rowStride}")
        Log.d("CameraXProvider", "image.planes[2].pixelStride = ${image.planes[2].pixelStride}")
        Log.d("CameraXProvider", "image.planes[2].pixelStride = ${image.planes[2].rowStride}")

        val size = (image.width * image.height) * 3 / 2
        var buffer = ByteArray(size)
        if (yuv == null) {
            yuv = ByteArray(size)
        }
        ImageUtil.getBytesFromImageAsType(image.image, ImageUtil.YUV420SPNV21, buffer)
        ImageUtil.rotateYUV420SP(buffer, yuv,image.width, image.height)

        // 写入文件调试
        if (isWrite) {
            var yuvImage = YuvImage(yuv, ImageFormat.NV21, image.height, image.width, null)
            var bos = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.height, image.width), 100, bos)
            var path = context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "input.jpg"
            var os = FileOutputStream(path)
            val toByteArray = bos.toByteArray()
            os.write(toByteArray)
            os.close()

            isWrite = false
        }
    }

    var decoder : StreamDecoder? = null
    private fun decodeTest(buffer: ByteArray, width:Int, height:Int) {
        if (decoder == null) {
            decoder = StreamDecoder(width,height)
            decoder?.prepare()
        }
        decoder?.decode(buffer)
    }
}