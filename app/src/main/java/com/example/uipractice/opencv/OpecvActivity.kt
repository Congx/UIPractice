package com.example.uipractice.opencv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import com.example.uipractice.utils.FileUtils

class OpecvActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opencv_activity)

        FileUtils.copyAssets(this, "davidcascade.xml")

    }

    /**
     * 初始化 追踪器
     * @param model
     */
    external fun init(model: String?)

    /**
     * 处理摄像头数据
     * @param data
     * @param w
     * @param h
     * @param cameraId
     */
    external fun postData(data: ByteArray?, w: Int, h: Int, cameraId: Int)

    /**
     * 释放
     */
    external fun release()

    companion object {

        init {
            System.loadLibrary("opencv-lib")
        }
    }

}