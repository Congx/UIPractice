package com.xc.ffplayer.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xc.ffplayer.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnMediaCodec.setOnClickListener {
            startActivity(Intent(this, MediaCodecActivity::class.java))
        }

        btnMediaRecord.setOnClickListener {
            startActivity(Intent(this, MediaRecordActivity::class.java))
        }

        btnCameraEncode.setOnClickListener {
            startActivity(Intent(this,
                CameraEncodeActivity::class.java))
        }

        btnClip.setOnClickListener {
            startActivity(Intent(this,
                ClipAudioActivity::class.java))
        }

        btnMediaMuxre.setOnClickListener {
            startActivity(Intent(this,
                MediaMuxreActivity::class.java))
        }

        btnLive.setOnClickListener {
            startActivity(Intent(this,
                LivePushActivity::class.java))
        }

        btnffmpeg.setOnClickListener {
            startActivity(Intent(this,
                FFmpegActivity::class.java))
        }
    }
}