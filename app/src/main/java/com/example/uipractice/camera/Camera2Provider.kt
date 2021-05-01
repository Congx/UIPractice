package com.example.uipractice.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * https://www.jianshu.com/p/df3c8683bb90
 */
internal class Camera2Provider(private val context: Activity) {

    companion object {

        private const val TAG = "Camera2Provider"
        private const val MSG_OPEN_CAMERA = 1
        private const val MSG_CLOSE_CAMERA = 2
        private const val REQUIRED_SUPPORTED_HARDWARE_LEVEL = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL

        private val ORIENTATIONS: SparseIntArray = SparseIntArray()

        private fun logSize(sizes: Array<Size>, des: String) {
            for (size in sizes) {
                Log.i(TAG, des + "--> width = " + size.width + "，height = " + size.height)
            }
        }

    }

    private lateinit var cameraDevice: CameraDevice
    private val handler: Handler
    private var textureView: TextureView? = null
    private var backCameraId: String? = null
    private var frontCameraId: String? = null
    private var previewSize: Size? = null
    private var surface: Surface? = null
    private var imageReader: ImageReader? = null
    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    init {

        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)


        val handlerThread = HandlerThread("camera")
        handlerThread.start()
        handler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_OPEN_CAMERA -> openCamera()
                    MSG_CLOSE_CAMERA -> closeCamera()
                }
            }
        }
    }

    private fun closeCamera() {
        cameraDevice.close()
    }

    fun release() {
        handler.looper.quit()
    }

    fun initTexture(textureView: TextureView) {
        this.textureView = textureView
        textureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                initInfoCamera(surface, width, height)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                configureTextureViewTransform(width, height)

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    private fun initInfoCamera(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        try {
            val cameraIdList = cameraManager.cameraIdList.filter {
                val characteristics = cameraManager.getCameraCharacteristics(it)
                val capabilities = characteristics.get(
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                capabilities?.contains(
                        CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
            }

            if (cameraIdList == null || cameraIdList.isEmpty()) return
            var map: StreamConfigurationMap? = null
            for (id in cameraIdList) {
                // 获取摄像头特性
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
//                if (!cameraCharacteristics.isHardwareLevelSupported(REQUIRED_SUPPORTED_HARDWARE_LEVEL)) {
//                    Log.e(TAG, "硬件不支持")
//                    return
//                }
                // 摄像头类型：前置、后置
                val integer = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // 选择后置摄像头
                if (integer != null && integer == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraId = id
                    // 获取配置信息
                    map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    var flashUseable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    Log.d(TAG, "flashUseable: $flashUseable")
                    break
                } else if (integer != null && integer == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = id
                }
            }
            if (map == null) return
//            map.isOutputSupportedFor()
            // 获取所支持预览尺寸
            val previewSizes = map.getOutputSizes(SurfaceTexture::class.java) ?: return
            previewSizes?.let {
                logSize(previewSizes, "支持预览尺寸")
                previewSize = CameraUtil.getOptimalSize(it, width, height)
                surfaceTexture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
                surface = Surface(surfaceTexture)

                Log.d(TAG, "预览尺寸 ： width = ${previewSize!!.width}, height = ${previewSize!!.height}")
            }
            // 获取输出拍照保存的图片的尺寸
            val savePicSize = map.getOutputSizes(ImageFormat.JPEG)
            savePicSize?.let {
                logSize(savePicSize, "图片的尺寸")
            }

            if (map.isOutputSupportedFor(ImageFormat.JPEG)) {
                imageReader = ImageReader.newInstance(previewSize!!.width, previewSize!!.height, ImageFormat.JPEG, 3)
                imageReader?.setOnImageAvailableListener(imageAvailableListener, handler)
            }

            configureTextureViewTransform(previewSize!!.width, previewSize!!.height)
            sendOpenMsg()

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun sendOpenMsg() {
        handler.obtainMessage(MSG_OPEN_CAMERA).sendToTarget()
    }

    /**
     * 判断相机的 Hardware Level 是否大于等于指定的 Level。
     */
    fun CameraCharacteristics.isHardwareLevelSupported(requiredLevel: Int): Boolean {
//        val sortedLevels = intArrayOf(
//                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
//                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
//                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
//                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
//        )
//        val deviceLevel = this[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
//        if (requiredLevel == deviceLevel) {
//            return true
//        }
//        for (sortedLevel in sortedLevels) {
//            if (requiredLevel == sortedLevel) {
//                return true
//            } else if (deviceLevel == sortedLevel) {
//                return false
//            }
//        }
        return true
    }

    var imageAvailableListener = ImageReader.OnImageAvailableListener {

        val image = imageReader?.acquireNextImage()
        if (image != null) {
            val planes = image.planes
            val yPlane = planes[0]
//            val uPlane = planes[1]
//            val vPlane = planes[2]
            val yBuffer = yPlane.buffer // Data from Y channel
//            val uBuffer = uPlane.buffer // Data from U channel
//            val vBuffer = vPlane.buffer // Data from V channel

//            val allocate = ByteBuffer.allocate(yBuffer.remaining() + uBuffer.remaining() + vBuffer.remaining())
//
//            allocate.put(yBuffer)
//            allocate.put(vBuffer)
//            allocate.put(uBuffer)

//            BitmapFactory.decodeByteArray(yBuffer.array(),previewSize!!.width, previewSize!!.height)

//            var yuvImage = YuvImage(allocate.array(), ImageFormat.NV21, previewSize!!.width, previewSize!!.height, null)
            var bos = ByteArrayOutputStream()
//            yuvImage.compressToJpeg(Rect(0, 0, previewSize!!.width, previewSize!!.height), 100, bos)
            val toByteArray = bos.toByteArray()
            var path = context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "input.jpg"
//            var os = FileOutputStream(File(path))
////            os.write(toByteArray)
//            os.write(yBuffer.array())
//            os.flush()
//            os.close()

            val array = ByteArray(yBuffer.remaining())
            yBuffer.get(array)
            File(path).writeBytes(array)
        }
        image?.close()
    }

    private var cameraStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "打开相机成功")
            cameraDevice = camera
            openSession(camera)
        }

        override fun onDisconnected(camera: CameraDevice) {

        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            Log.d(TAG, "相机关闭")
        }

    }

    private fun openSession(camera: CameraDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            var outputConfiguration = OutputConfiguration(surface!!)
            val listOf = mutableListOf(outputConfiguration)
            imageReader?.let {
                listOf.add(OutputConfiguration(it.surface))
            }
            var config = SessionConfiguration(SessionConfiguration.SESSION_REGULAR, listOf, Executors.newSingleThreadExecutor(), sessionStateCallback)
            camera.createCaptureSession(config)
        } else {
            val listOf = mutableListOf(surface)
            imageReader?.let {
                listOf.add(it.surface)
            }
            camera.createCaptureSession(listOf, sessionStateCallback, handler)
        }
    }

    var requestSateCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureStarted(session: CameraCaptureSession, request: CaptureRequest, timestamp: Long, frameNumber: Long) {

        }
    }

    lateinit var cameraCaptureSession: CameraCaptureSession

    private var sessionStateCallback = object : CameraCaptureSession.StateCallback() {

        override fun onConfigureFailed(session: CameraCaptureSession) {
            session.close()
        }

        override fun onConfigured(session: CameraCaptureSession) {
            cameraCaptureSession = session
            startPreview()
        }

        override fun onClosed(session: CameraCaptureSession) {
            session.close()
        }
    }

    fun stopPreview() {
        cameraCaptureSession.stopRepeating()
    }

    fun startPreview() {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuilder.addTarget(surface!!)
        val request = requestBuilder.build()
        cameraCaptureSession.setRepeatingRequest(request, requestSateCallback, handler)
    }

    fun takePhoto() {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        requestBuilder.addTarget(imageReader!!.surface)
        // 自动对焦
        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            // 打开闪光灯
        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        // 根据设备方向计算设置照片的方向
        // 获取手机方向
        val rotation: Int = context.windowManager.defaultDisplay.rotation
        requestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))
        cameraCaptureSession.capture(requestBuilder.build(),null,handler)
    }

    /**
     * 开始打开摄像头
     */
    private fun openCamera() {
        var id = backCameraId ?: frontCameraId
        if (id == null) {
            Log.e(TAG, "打开相机失败，找不到cameraId")
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), 1)
        }
        cameraManager.openCamera(id, cameraStateCallback, handler)
    }

    private fun configureTextureViewTransform(viewWidth: Int, viewHeight: Int) {
        if (null == textureView) {
            return
        }
        val rotation = 0 /*activity.getWindowManager().getDefaultDisplay().getRotation();*/
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize!!.getHeight().toFloat(), previewSize!!.getWidth().toFloat())
        val centerX: Float = viewRect.centerX()
        val centerY: Float = viewRect.centerY()
        if (Surface.ROTATION_90 === rotation || Surface.ROTATION_270 === rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale: Float = Math.max(
                    viewHeight.toFloat() / previewSize!!.getHeight(),
                    viewWidth.toFloat() / previewSize!!.getWidth())
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 === rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView?.setTransform(matrix)
    }


}