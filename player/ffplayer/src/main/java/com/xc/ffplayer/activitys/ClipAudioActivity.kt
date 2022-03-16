package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xc.ffplayer.R
import com.xc.ffplayer.clip.MusicProcess
import com.xc.ffplayer.clip.MusicProcess2
import kotlinx.android.synthetic.main.activity_clip_audio.*
import java.io.File
import kotlin.experimental.and

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

        var byte:Byte = -17
        var int = byte.toInt()
        var sh = byte.toInt() shl 8

        var heght:Short = 0x0f

        Log.d("ClipAudioActivity",0x80.toString(2))
        val toInt = (byte and 0x80.toByte()).toInt()
        val i = toInt shr 7
        Log.d("ClipAudioActivity",i.toString())
        Log.d("ClipAudioActivity",i.toString(2))
        Log.d("ClipAudioActivity",toInt.toString(2))

        Log.d("ClipAudioActivity","-------------")
//
        Log.d("ClipAudioActivity",byte.toString())
        Log.d("ClipAudioActivity",int.toString())
        Log.d("ClipAudioActivity",sh.toString())

        Log.d("ClipAudioActivity","-------------")

        Log.d("ClipAudioActivity",byte.toString(16))
        Log.d("ClipAudioActivity",int.toString(16))
        Log.d("ClipAudioActivity",sh.toString(16))

        Log.d("ClipAudioActivity","-------------")

        Log.d("ClipAudioActivity",byte.toString(2))
        Log.d("ClipAudioActivity",int.toString(2))
        Log.d("ClipAudioActivity",sh.toString(2))

        var result = sh or heght.toInt()

        Log.d("ClipAudioActivity","-------------")

        Log.d("ClipAudioActivity",heght.toInt().toString(16))
        Log.d("ClipAudioActivity",result.toString(16))
        Log.d("ClipAudioActivity",result.toString(2))
    }
}