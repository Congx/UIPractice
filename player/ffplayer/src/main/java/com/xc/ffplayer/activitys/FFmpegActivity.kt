package com.xc.ffplayer.activitys

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.xc.ffplayer.R
import com.xc.ffplayer.ffplayer.FFPlayer
import com.xc.ffplayer.ffplayer.StatusCallback
import com.xc.ffplayer.live.LiveTaskManager
import kotlinx.android.synthetic.main.activity_ffmpeg.*
import java.io.File

class FFmpegActivity : AppCompatActivity(), SurfaceHolder.Callback {

    var ffplayer = FFPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)


        ffplayer.callback = object : StatusCallback {
            override fun onPrepared() {
                ffplayer.start(surfaceView.holder.surface)
            }
        }
        btnPlay.setOnClickListener {
            var path = getExternalFilesDir("input")?.absolutePath + File.separator + "input.mp4"
            ffplayer.setSource(path)
            ffplayer.prepare()
        }

        surfaceView.holder.addCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ffplayer.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}