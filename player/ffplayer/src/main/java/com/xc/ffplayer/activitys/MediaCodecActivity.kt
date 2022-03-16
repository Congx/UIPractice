package com.xc.ffplayer.activitys

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.xc.ffplayer.camera2.H264Player
import com.xc.ffplayer.R
import kotlinx.android.synthetic.main.activity_media_codec.*
import java.io.File

class MediaCodecActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_codec)


        btnScreen.setOnClickListener {
            //判断当前屏幕方向
            if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //切换竖屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }else{
                //切换横屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }

        initUI()
    }

    private fun initUI() {
        var path = getExternalFilesDir("input")?.absolutePath + File.separator + "input.mp4"
//        var path = getExternalFilesDir("video")?.absolutePath + File.separator + "input.mp4"
        Log.e("path:" , path)
        Log.e("exists:" , File(path).exists().toString())

        var player = H264Player(video_surfaceView, path)
    }
}