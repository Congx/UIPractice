package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xc.ffplayer.R
import com.xc.ffplayer.clip.MusicProgress
import kotlinx.android.synthetic.main.activity_clip_audio.*
import java.io.File

class ClipAudioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip_audio)

        btnStart.setOnClickListener {
            Thread {
                var videoInput = getExternalFilesDir("video")?.absolutePath + File.separator + "input1.mp4"
                var mp3Input = getExternalFilesDir("video")?.absolutePath + File.separator + "music.mp3"
                var output = getExternalFilesDir("audio")?.absolutePath + File.separator + "output.mp3"
                MusicProgress().mixAudioTrack(this,videoInput,mp3Input,output,60 * 1000 * 1000, 70 * 1000 * 1000,20_000_000,30_000_000)
            }.start()

        }
    }
}