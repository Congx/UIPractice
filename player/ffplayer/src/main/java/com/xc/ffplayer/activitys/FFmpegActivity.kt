package com.xc.ffplayer.activitys

import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.xc.ffplayer.R
import com.xc.ffplayer.ffplayer.DisplayUtils
import com.xc.ffplayer.ffplayer.FFPlayer
import com.xc.ffplayer.ffplayer.IPlayerListener
import kotlinx.android.synthetic.main.activity_ffmpeg.*
import java.io.File

class FFmpegActivity : AppCompatActivity(), SurfaceHolder.Callback {

    var ffplayer = FFPlayer()

    var isPlaying = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)

        surfaceView.keepScreenOn = true

        ffplayer.playerListener = object : IPlayerListener {
            override fun onPrepared() {
                ffplayer.start(surfaceView.holder.surface)
            }

            override fun onLoad(load: Boolean) {

            }

            override fun onCurrentTime(currentTime: Int, totalTime: Int) {
                seekbar.setProgress(currentTime * 100 / totalTime)
                tvTimer.text = DisplayUtils.secdsToDateFormat(currentTime,totalTime)
                btnPause.isEnabled = true
            }

            override fun onError(code: Int, msg: String?) {

            }

            override fun onPause(pause: Boolean) {

            }

            override fun onDbValue(db: Int) {

            }

            override fun onComplete() {

            }

            override fun onNext(): String? {
                return ""
            }
        }
        btnPlay.setOnClickListener {
            var path = getExternalFilesDir("input")?.absolutePath + File.separator + "input.mp4"
            ffplayer.setSource(path)
            ffplayer.prepare()
            isPlaying = true
            btnPause.isEnabled = false
        }

        btnPause.setOnClickListener {
            if (isPlaying) {
                ffplayer.pause()
                btnPause.text = "播放"
            }else {
                ffplayer.resume()
                btnPause.text = "暂停"
            }
            isPlaying = !isPlaying
        }

        surfaceView.holder.addCallback(this)

        seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
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