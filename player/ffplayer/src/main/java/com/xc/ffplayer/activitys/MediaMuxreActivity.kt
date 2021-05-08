package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xc.ffplayer.R
import com.xc.ffplayer.clip.MediaProcess
import kotlinx.android.synthetic.main.activity_media_mexure.*
import java.io.File

class MediaMuxreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_mexure)

        var videoPath1 = getExternalFilesDir("video")?.absolutePath + File.separator + "input1.mp4"
        var videoPath2 = getExternalFilesDir("video")?.absolutePath + File.separator + "input.mp4"
        var outPut = getExternalFilesDir("output")?.absolutePath + File.separator + "output.mp4"
        btnStart.setOnClickListener {

            Thread{
                MediaProcess.start(videoPath1,videoPath2,outPut)
            }.start()
        }
    }
}