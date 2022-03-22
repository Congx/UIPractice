package com.xc.ffplayer.activitys

import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.xc.ffplayer.R
import com.xc.ffplayer.ffplayer.*
import kotlinx.android.synthetic.main.activity_ffmpeg.*
import java.io.File

class FFmpegActivity : AppCompatActivity(), SurfaceHolder.Callback {

    var ffplayer = FFPlayer()

    var isPlaying = false
    var seeking = false

    var volume = 100

    var speed = 1f
    var pitch = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)

        surfaceView.keepScreenOn = true
        ffplayer.glSurfaceView = surfaceView
        ffplayer.playerListener = object : IPlayerListener {
            override fun onPrepared() {
                ffplayer.start(surfaceView.holder.surface)
            }

            override fun onLoad(load: Boolean) {

            }

            override fun onCurrentTime(currentTime: Int, totalTime: Int) {
                if (!seeking) {
                    seekbar.progress = currentTime * 100 / totalTime
                }
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
//            var path = getExternalFilesDir("input")?.absolutePath + File.separator + "music.mp4"
            var path = getExternalFilesDir("input")?.absolutePath + File.separator + "music.aac"
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

        btnVolumAdd.setOnClickListener {
            volume += 5
            tvVolum.text = volume.toString()
            ffplayer.setVolume(volume)
        }

        btnVolumMinus.setOnClickListener {
            volume -= 5
            tvVolum.text = volume.toString()
            ffplayer.setVolume(volume)
        }

        btnLeftMute.setOnClickListener {
            ffplayer.setMute(MUTE_LEFT)

        }
        btnRightMute.setOnClickListener {
            ffplayer.setMute(MUTE_RIGHT)

        }
        btnStereoMute.setOnClickListener {
            ffplayer.setMute(MUTE_STEREO)
        }

        btnSpeedAdd.setOnClickListener {
            speed += 0.1f
            tvSpeed.text = speed.toString()
            ffplayer.setSpeed(speed)
        }

        btnSppedMinus.setOnClickListener {
            speed -= 0.1f
            tvSpeed.text = speed.toString()
            ffplayer.setSpeed(speed)
        }

        btnPitchAdd.setOnClickListener {
            pitch += 0.1f
            tvPitch.text = pitch.toString()
            ffplayer.setPitch(pitch)
        }

        btnPitchMinus.setOnClickListener {
            pitch -= 0.1f
            tvPitch.text = pitch.toString()
            ffplayer.setPitch(pitch)
        }

        surfaceView.holder.addCallback(this)

        seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seeking = false
                ffplayer.seek(seekBar.progress)
            }

        })
      ffplayer.speachDetectListener = object :FFPlayer.SpeachDetectListener {
        override fun onSpeechDetected() {
          tvSpeakDetect.post {
            tvSpeakDetect.text = "说话中..."
          }
        }

        override fun onNoiseDetected() {
          tvSpeakDetect.post {
            tvSpeakDetect.text = "静音中..."
          }
        }

      }
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