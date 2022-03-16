package com.example.uipractice.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.base.utils.MathUtils
import com.example.uipractice.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.layout_camera.*
import java.io.FileOutputStream


class CameraActivity : AppCompatActivity() {

    lateinit var camera: Camera

    @SuppressLint("AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_camera)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏

        RxPermissions(this).request(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe {
                initCamera()
            }

        button.setOnClickListener {
//            camera.takePicture(null,null,
//                Camera.PictureCallback { data, camera ->
//                    val fileOutputStream = FileOutputStream(filesDir.absolutePath + "/aaa.png")
//                    fileOutputStream.write(data)
//                    fileOutputStream.flush()
//                    fileOutputStream.close()
//                })
            camera.autoFocus { success, camera ->
                Log.e("autoFocus",success.toString())
            }
        }
    }

    private fun initCamera() {
        val holder = surface.holder
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                camera.release()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                val preWith = surface.measuredWidth
                val preHeight = surface.measuredHeight
                val greastCommonDivisor = MathUtils.getGreastCommonDivisor(preWith, preHeight)
                Log.e("surface size:", "${preWith}  ${preHeight}")
                Log.e(
                    "宽高比 :",
                    "${preWith / greastCommonDivisor}  ${preHeight / greastCommonDivisor}"
                )

                camera = Camera.open()
                camera.parameters.setRotation(90)
                setCameraDisplayOrientation(
                    this@CameraActivity,
                    Camera.CameraInfo.CAMERA_FACING_FRONT,
                    camera
                )
//                camera.setDisplayOrientation(90)
                camera.setPreviewDisplay(holder)
                val closelyPreSize = getCloselyPreSize(
                    true,
                    preWith,
                    preHeight,
                    camera.parameters.supportedPreviewSizes
                )

                Log.e("预览尺寸 :", "${closelyPreSize?.height}  ${closelyPreSize?.width}")
                camera.parameters.setPreviewSize(closelyPreSize!!.height, closelyPreSize!!.width)

                val matrix = calculateSurfaceHolderTransform(
                    preWith,
                    preHeight,
                    closelyPreSize.height,
                    closelyPreSize.width
                )

//                val values = FloatArray(9)
//                matrix?.getValues(values)
//                surface.setTranslationX(values[Matrix.MTRANS_X])
//                surface.setTranslationY(values[Matrix.MTRANS_Y])
//                surface.setScaleX(values[Matrix.MSCALE_X])
//                surface.setScaleY(values[Matrix.MSCALE_Y])
//                surface.invalidate()

//                val supportedPreviewSizes = camera.parameters.supportedPreviewSizes
//                for (size in supportedPreviewSizes) {
//                    val greastCommonDivisor = MathUtils.getGreastCommonDivisor(size.width, size.height)
//                    Log.e("size:","${size.width}  ${size.height}  $greastCommonDivisor")
//                    Log.e("ratio:","${size.width/greastCommonDivisor}  ${size.height/greastCommonDivisor}")
//                }
                camera.parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
//                camera.autoFocus { success, camera ->
//
//                }
                camera.startPreview();

            }

        })
    }


    private fun getCameraOri(rotation: Int, cameraId: Int): Int {
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        // result 即为在camera.setDisplayOrientation(int)的参数
        var result: Int
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        if (info.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    /**
     * 预览方向 设置
     */
    fun setCameraDisplayOrientation(
        activity: Activity,
        cameraId: Int,
        camera: Camera
    ) {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    /**
     * 拍摄帧方向
     */
    open fun onOrientationChanged(orientation: Int): Unit {
        var orientation = orientation
//        if (orientation == ORIENTATION_UNKNOWN) {
//            return
//        }
        val info = CameraInfo()
//        Camera.getCameraInfo(cameraId, info)
        orientation = (orientation + 45) / 90 * 90
        var rotation = 0
        rotation = if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            (info.orientation - orientation + 360) % 360
        } else {  // back-facing camera
            (info.orientation + orientation) % 360
        }
//        mParameters.setRotation(rotation)
    }


    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait 是否竖屏
     * @param surfaceWidth 需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    fun getCloselyPreSize(
        isPortrait: Boolean,
        surfaceWidth: Int,
        surfaceHeight: Int,
        preSizeList: List<Camera.Size>
    ): Camera.Size? {
        val reqTmpWidth: Int
        val reqTmpHeight: Int
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight
            reqTmpHeight = surfaceWidth
        } else {
            reqTmpWidth = surfaceWidth
            reqTmpHeight = surfaceHeight
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (size in preSizeList) {
            if (size.width === reqTmpWidth && size.height === reqTmpHeight) {
                return size
            }
        }

        // 得到与传入的宽高比最接近的size
        val reqRatio = reqTmpWidth.toFloat() / reqTmpHeight
        var curRatio: Float
        var deltaRatio: Float
        var deltaRatioMin = Float.MAX_VALUE
        var tempWith = 0
        var tempHeight = 0
        var retSize: Camera.Size? = null
        for (size in preSizeList) {
            curRatio = size.width.toFloat() / size.height
            deltaRatio = Math.abs(reqRatio - curRatio)

            if (deltaRatio < deltaRatioMin && size.width >= tempWith && size.height >= tempHeight) {
                deltaRatioMin = deltaRatio
                tempWith = size.width
                tempHeight = size.height
                retSize = size
            }
        }
        return retSize
    }

    private fun getOptimalSize(w: Int, h: Int): Camera.Size? {
        val cameraParameter = camera.parameters
        val sizes = cameraParameter.supportedPreviewSizes
        val ASPECT_TOLERANCE = 0.1
        // 竖屏是 h/w, 横屏是 w/h
        val targetRatio = h.toDouble() / w
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    fun calculateSurfaceHolderTransform(
        viewWidth: Int,
        viewHeight: Int,
        cameraWidth: Int,
        cameraHeight: Int
    ): Matrix? {
        // 计算出将相机的尺寸 => View 的尺寸需要的缩放倍数
        val ratioPreview = cameraWidth.toFloat() / cameraHeight // 0.7
        val ratioView = viewWidth.toFloat() / viewHeight  // 0.4
        val scaleX: Float
        val scaleY: Float
        val b = ratioView < ratioPreview
        if (b) {
            scaleX = cameraHeight / viewHeight.toFloat()
            scaleY = scaleX
        } else {
            scaleY = ratioView / ratioPreview
            scaleX = scaleY
        }

        val scaledWidth = viewWidth * scaleX
        val scaledHeight = viewHeight * scaleY
        val dx = (viewWidth - scaledWidth) / 2
        val dy = (viewHeight - scaledHeight) / 2
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)
        matrix.postTranslate(dx, dy)
        return matrix
    }
}