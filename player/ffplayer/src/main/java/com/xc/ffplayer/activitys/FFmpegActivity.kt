package com.xc.ffplayer.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xc.ffplayer.R
import com.xc.ffplayer.ffplayer.FFPlayer
import kotlinx.android.synthetic.main.activity_ffmpeg.*

class FFmpegActivity : AppCompatActivity() {

    var ffplayer = FFPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)
        textView.text = ffplayer.ffmpegInfo()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}