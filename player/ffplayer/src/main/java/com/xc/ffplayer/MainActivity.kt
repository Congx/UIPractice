package com.xc.ffplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnMediaCodec.setOnClickListener {
            startActivity(Intent(this,MediaCodecActivity::class.java))
        }

        btnMediaRecord.setOnClickListener {
            startActivity(Intent(this,MediaRecordActivity::class.java))
        }

        btnCameraEncode.setOnClickListener {
            startActivity(Intent(this,CameraEncodeActivity::class.java))
        }
    }
}