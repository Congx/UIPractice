package com.xc.ffplayer.camera2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
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
import com.xc.ffplayer.live.Releaseable
import com.xc.ffplayer.utils.CameraUtil
import com.xc.ffplayer.utils.ImageUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * https://www.jianshu.com/p/df3c8683bb90
 */
open class Camera2Provider(
    private val context: Activity,
    private var width: Int = 0,
    private var height: Int = 0
) :Releaseable{

    companion object {

        private const val TAG = "Camera2Provider"
        private const val MSG_OPEN_CAMERA = 1
        private const val MSG_CLOSE_CAMERA = 2
        private const val REQUIRED_SUPPORTED_HARDWARE_LEVEL =
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL

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
    private var picImageReader: ImageReader? = null
    private var streamImageReader: ImageReader? = null
    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    var streamByteCallback: ((ByteArray) -> Unit)? = null
    var cameraPreviewCallback: CameraPreviewCallback? = null

    var streamSize:Size? = null

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

    override fun release() {
        closeCamera()
        handler.looper.quit()
    }

    fun inintPreview(textureView: TextureView) {
        this.textureView = textureView
        textureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                var w = if (this@Camera2Provider.width > 0) this@Camera2Provider.width else width
                var h = if (this@Camera2Provider.height > 0) this@Camera2Provider.height else height
                initInfoCamera(surface, w, h)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                configureTransform(textureView, width, height)

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
                    CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
                )
                capabilities?.contains(
                    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                ) ?: false
            }

            if (cameraIdList == null || cameraIdList.isEmpty()) return
            var map: StreamConfigurationMap? = null
            var cameraCharacteristics: CameraCharacteristics? = null
            for (id in cameraIdList) {
                // 获取摄像头特性
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id)

                cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)//是否支持闪光灯
                cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);//是否支持自动对焦
                // 摄像头类型：前置、后置
                val integer = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // 选择后置摄像头
                if (integer != null && integer == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraId = id
                    // 获取配置信息
                    map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    var flashUseable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
//                    Log.e(TAG, "flashUseable: $flashUseable")
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
//                previewSize = CameraUtil.getOptimalSize(it, width, height)
                previewSize = getPreviewOutputSize(
                    textureView!!.display,
                    cameraCharacteristics!!,
                    SurfaceTexture::class.java,
                )
                surfaceTexture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
                surface = Surface(surfaceTexture)

//                Log.d(TAG, "要求尺寸 ： width = ${width}, height = ${height}")
                Log.d(TAG, "预览尺寸 ： width = ${previewSize!!.width}, height = ${previewSize!!.height}")
            }
            // 获取输出拍照保存的图片的尺寸
//            val savePicSize = map.getOutputSizes(ImageFormat.JPEG)
//            savePicSize?.let {
//                logSize(savePicSize, "图片的尺寸")
//            }

//            map.getOutputSizes(MediaRecorder.class)//录制的视频支持尺寸

            if (map.isOutputSupportedFor(ImageFormat.JPEG)) {
                picImageReader = ImageReader.newInstance(
                    previewSize!!.width,
                    previewSize!!.height,
                    ImageFormat.JPEG,
                    3
                )
                picImageReader?.setOnImageAvailableListener(imageAvailableListener, handler)
            }


            if (map.isOutputSupportedFor(ImageFormat.YUV_420_888)) {
                val outputSizes = map.getOutputSizes(ImageFormat.YUV_420_888)
                streamSize = CameraUtil.getPerfectSize(outputSizes, width, height)
                Log.e(TAG, "设定流尺寸 ： width = ${width}, height = ${height}")
                Log.e(TAG, "实际流尺寸 ： width = ${streamSize!!.width}, height = ${streamSize!!.height}")
                streamSize?.let {
                    cameraPreviewCallback?.streamSize(streamSize!!.width,streamSize!!.height)
                    streamImageReader = ImageReader.newInstance(
                        streamSize!!.width,
                        streamSize!!.height,
                        ImageFormat.YUV_420_888,
                        1
                    )
                    streamImageReader?.setOnImageAvailableListener(
                        streamImageAvailableListener,
                        handler
                    )
                }
            }

            cameraPreviewCallback?.previewSize(previewSize!!.width, previewSize!!.height)

            configureTransform(textureView,previewSize!!.height, previewSize!!.width)

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

        val image = picImageReader?.acquireNextImage()
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
            var path =
                context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "input.jpg"
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

    val buffer by lazy {
        val size = (previewSize!!.width * previewSize!!.height) * 3 / 2
        return@lazy ByteArray(size)
    }

    val yuv by lazy {
        val size = (streamSize!!.width * streamSize!!.height) * 3 / 2
        return@lazy ByteArray(size)
    }

    var streamImageAvailableListener = ImageReader.OnImageAvailableListener {

        val image = streamImageReader?.acquireNextImage()
        if (image != null) {
//            Log.d(TAG, "yuv 得到的流尺寸： width = ${image.width}, height = ${image.height}")
//            val size = (image.width * image.height) * 3 / 2
//            var buffer = ByteArray(size)
            ImageUtil.getBytesFromImageAsType(
                image,
                ImageUtil.YUV420SPNV12, // 在视频解码端只能支持 nv12,包括mediaCodec、x264
                buffer
            )
            ImageUtil.rotateYUV420SP(buffer, yuv,image.width, image.height)
//            YuvUtils.portraitNV21Data2Raw(
//                buffer,
//                yuv,
//                image.width,
//                image.height
//            )
            streamByteCallback?.invoke(yuv)

//            saveImg(image)
//            saveNv21(context,yuv,image.height,image.width)

        }

        image?.close()
    }

    var isSave = false
    private fun saveImg(image: Image) {
        if (isSave) return
        isSave = true
        val planes = image.planes
        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]
        val yBuffer = yPlane.buffer // Data from Y channel
        val uBuffer = uPlane.buffer // Data from U channel
        val vBuffer = vPlane.buffer // Data from V channel

        val allocate = ByteBuffer.allocate(yBuffer.remaining() + uBuffer.remaining() + vBuffer.remaining())
//        val allocate = ByteBuffer.allocate(yBuffer.remaining())

        allocate.put(yBuffer)
        allocate.put(vBuffer)
        allocate.put(uBuffer)

        var yuvImage = YuvImage(
            allocate.array(),
            ImageFormat.NV21,
            previewSize!!.width,
            previewSize!!.height,
            null
        )
        var bos = ByteArrayOutputStream()
         yuvImage.compressToJpeg(Rect(0, 0, previewSize!!.width, previewSize!!.height), 100, bos)
        val toByteArray = bos.toByteArray()
        var path = context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "inputNV21.jpg"
            var os = FileOutputStream(File(path))
            os.write(toByteArray)
            os.flush()
            os.close()

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
            picImageReader?.let {
                listOf.add(OutputConfiguration(it.surface))
            }
            streamImageReader?.let {
                listOf.add(OutputConfiguration(it.surface))
            }
            var config = SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR,
                listOf,
                Executors.newSingleThreadExecutor(),
                sessionStateCallback
            )
            camera.createCaptureSession(config)
        } else {
            val listOf = mutableListOf(surface)
            picImageReader?.let {
                listOf.add(it.surface)
            }
            streamImageReader?.let {
                listOf.add(it.surface)
            }
            camera.createCaptureSession(listOf, sessionStateCallback, handler)
        }
    }

    var requestSateCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureStarted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            timestamp: Long,
            frameNumber: Long
        ) {

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
            cameraPreviewCallback?.cameraInited()
        }

        override fun onClosed(session: CameraCaptureSession) {
            session.close()
        }
    }

    fun stopPreview() {
        cameraCaptureSession.stopRepeating()
    }

    fun startPreview() {
        /**
         * templateType 类别 如下几个
         * TEMPLATE_PREVIEW 预览
         * TEMPLATE_RECORD 录制视频
         * TEMPLATE_STILL_CAPTURE 拍照
         * TEMPLATE_VIDEO_SNAPSHOT //没用到 igonre
         * TEMPLATE_MANUAL  //手动，貌似需要硬件是full级别才能全支持，没详细了解
         **/
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuilder.addTarget(surface!!)
        val request = requestBuilder.build()
        requestBuilder.set(
            CaptureRequest.STATISTICS_FACE_DETECT_MODE,
            CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE
        )
        cameraCaptureSession.setRepeatingRequest(request, requestSateCallback, handler)

    }

    /**
     * 推流
     */
    fun startStream() {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuilder.addTarget(surface!!)
        requestBuilder.addTarget(streamImageReader!!.surface)
        val request = requestBuilder.build()
        requestBuilder.set(
            CaptureRequest.STATISTICS_FACE_DETECT_MODE,
            CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE
        )
        cameraCaptureSession.setRepeatingRequest(request, requestSateCallback, handler)
    }

    /**
     * 停止流
     */
    fun stopStream() {
        startPreview()
    }

    fun takePhoto() {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        requestBuilder.addTarget(picImageReader!!.surface)
        // 自动对焦
        requestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        );
        // 打开闪光灯
        requestBuilder.set(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        )

        /**
         * //1. 自动聚焦相关：
        CaptureRequest.Builder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        //2.自动曝光相关
        CaptureRequest.Builder.set(CaptureRequest.CONTROL_AE_MODE,
        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        //3.预览放大缩小
        CaptureRequest.Builder.set(CaptureRequest.SCALER_CROP_REGION, region);
        //4.自动控制模式
        CaptureRequest.Builder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        //5.手动触发对焦
        CaptureRequest.Builder.set(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
        //6.手动触发曝光
        CaptureRequest.Builder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        //7.人脸检测模式
        captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE)

         */
        // 根据设备方向计算设置照片的方向
        // 获取手机方向
        val rotation: Int = context.windowManager.defaultDisplay.rotation
        requestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))
//        requestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE)

        cameraCaptureSession.capture(requestBuilder.build(), null, handler)
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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), 1)
            return
        }

        cameraManager.openCamera(id, cameraStateCallback, handler)
    }

    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     */
//    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
//        if (null == textureView || null == previewSize) {
//            return
//        }
//        val rotation: Int = context.windowManager.defaultDisplay.rotation
//        val matrix = Matrix()
//        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
//        val bufferRect = RectF(0f, 0f, previewSize!!.height.toFloat(), previewSize!!.width.toFloat())
//        val centerX = viewRect.centerX()
//        val centerY = viewRect.centerY()
//        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
//            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
//            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
//            val scale: Float = Math.max(
//                    viewHeight.toFloat() / previewSize!!.getHeight(),
//                    viewWidth.toFloat() / previewSize!!.getWidth())
//            matrix.postScale(scale, scale, centerX, centerY)
//            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
//        } else if (Surface.ROTATION_180 == rotation) {
//            matrix.postRotate(180f, centerX, centerY)
//        }
//        textureView!!.setTransform(matrix)
//    }

    private fun configureTransform(
        textureView: TextureView?,
        viewWidth: Int,
        viewHeight: Int
    ) {
        if (null == previewSize) {
            return
        }
        val rotation = context.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect =
            RectF(0f, 0f, previewSize!!.height.toFloat(), previewSize!!.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale: Float = Math.max(
                viewHeight.toFloat() / previewSize!!.height,
                viewWidth.toFloat() / previewSize!!.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView?.setTransform(matrix)
    }

}

interface CameraPreviewCallback {
    fun previewSize(width: Int, height: Int)
    fun streamSize(width: Int, height: Int)
    fun cameraInited()
}