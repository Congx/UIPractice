package com.xc.ffplayer.ffplayer

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.live.LiveTaskManager
import com.xc.ffplayer.utils.MainExecuter
import kotlinx.android.synthetic.main.activity_ffmpeg.*

open class FFPlayer:Player {

    override var playerListener: IPlayerListener? = null

    var mUrl = ""

    override fun setSource(url: String) {
        mUrl = url
    }

    override fun prepare() {
        nativePrepare(mUrl)
    }

    override fun start(surface: Surface) {
        if(mUrl.isEmpty()) {
            return
        }
        LiveTaskManager.execute {
            nativeStart(surface)
        }
    }

    override fun pause() {
        nativePause()
    }

    override fun resume() {
        nativeResume()
    }

    override fun stop() {
        nativeStop()
        nativeRelease()
        audioTrack?.stop()
        audioTrack?.release()
    }

    /**
     * 时间，native回调
     */
    fun onCurrentTime(currentTime: Int, totalTime: Int) {
        MainExecuter.execute {
            Log.d(TAG,"currentTime = $currentTime, totalTime = $totalTime")
            playerListener?.onCurrentTime(currentTime,totalTime)
        }
    }

    /**
     * 播放器初始化成功，native回调
     */
    private fun onPrepared() {
        Log.d(TAG,"onPrepared")
        playerListener?.onPrepared()
    }

    /**
     * native 调用
     */
    var audioTrack:AudioTrack? = null
    private fun createAudioTrack(channels: Int,sampleRate:Int,sampleBitCount:Int) {
        Log.d(TAG,"channels = $channels,sampleRate = $sampleRate,sampleBitCount = $sampleBitCount")
        var channelCfg = AudioFormat.CHANNEL_OUT_MONO
        if (channels == 1) {
            channelCfg = AudioFormat.CHANNEL_OUT_MONO
        }else if (channels == 2) {
            channelCfg = AudioFormat.CHANNEL_OUT_STEREO
        }
        var minBufferSize = AudioTrack.getMinBufferSize(sampleRate,channelCfg,AudioFormat.ENCODING_PCM_16BIT)
        audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,channelCfg,AudioFormat.ENCODING_PCM_16BIT,minBufferSize,AudioTrack.MODE_STREAM)
        audioTrack?.play()
    }

    /**
     * native 调用
     */
    private fun playAudio(byteArray: ByteArray,len:Int) {
        audioTrack?.write(byteArray,0,len)
//        Log.d(TAG,"playAudio()")
    }

    private external fun nativeffmpegInfo():String
    private external fun nativePrepare(url: String):Int
    private external fun nativeStart(surface: Surface):Int
    private external fun nativePause()
    private external fun nativeResume()
    private external fun nativeStop()
    private external fun nativeRelease()

    companion object {
        init {
            System.loadLibrary("ffmpeg")
        }

        val TAG = FFPlayer::class.java.simpleName.toString()
    }
}