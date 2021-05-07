package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xc.ffplayer.R
import com.xc.ffplayer.clip.MusicProcess
import com.xc.ffplayer.clip.MusicProcess2
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
                MusicProcess().mixAudioTrack(this,videoInput,mp3Input,output,60 * 1000 * 1000,65 * 1000 * 1000,100, 30)
            }.start()

        }

//        var byte:Byte = 0xf
//        var int = byte.toInt()
//        var sh = byte.toInt() shl 8
//
//        Log.d("ClipAudioActivity",byte.toString(16))
//        Log.d("ClipAudioActivity",byte.toInt().toString())
//        Log.d("ClipAudioActivity",byte.toString(2))
//        Log.d("ClipAudioActivity",int.toString(2))
//        Log.d("ClipAudioActivity",sh.toString(16))
//        Log.d("ClipAudioActivity",sh.toString(2))
    }
}