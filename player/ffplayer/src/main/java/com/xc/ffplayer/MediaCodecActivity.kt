package com.xc.ffplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.base.utils.toPX
import kotlinx.android.synthetic.main.activity_media_codec.*

class MediaCodecActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_codec)

        btnChangeSize.setOnClickListener {
            surfaceView.layoutParams.width = 300.toPX()
            surfaceView.layoutParams.width = 300.toPX()
        }
    }
}