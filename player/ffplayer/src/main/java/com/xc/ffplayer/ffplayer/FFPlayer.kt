package com.xc.ffplayer.ffplayer

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.live.LiveTaskManager
import com.xc.ffplayer.utils.MainExecuter

open class FFPlayer:Player {

    var glSurfaceView: FFGLSurfaceView? = null
    override var playerListener: IPlayerListener? = null

    var mUrl = ""
    var duration = 0

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
        LiveTaskManager.execute(Runnable {
            nativeStart(surface)
        })
    }

    override fun pause() {
        nativePause()
    }

    override fun resume() {
        nativeResume()
    }

    override fun stop() {
        LiveTaskManager.execute(Runnable {
            nativeStop()
            nativeRelease()
            audioTrack?.stop()
            audioTrack?.release()
        })
    }

    override fun seek(progress: Int) {
        nativeSeek((progress.toFloat() / 100 * duration).toInt())
    }

    override fun setVolume(volume: Int) {
        nativeSetVolume(volume)
    }

    override fun setMute(@IMute mute: Int) {
        nativeSetMute(mute)
    }

    override fun setSpeed(speed: Float) {
        nativeSetSpeed(speed)
    }

    override fun setPitch(pitch: Float) {
        nativeSetPitch(pitch)
    }

    /**
     * 时间，native回调
     */
    fun onCurrentTime(currentTime: Int, totalTime: Int) {
        duration = totalTime
        MainExecuter.execute {
//            Log.d(TAG,"currentTime = $currentTime, totalTime = $totalTime")
            playerListener?.onCurrentTime(currentTime,totalTime)
        }
    }

    private fun onLoading(isLoading:Boolean) {
        Log.d(TAG, "isLoading = $isLoading")
    }

    private fun onCallRenderYUV(
        width: Int,
        height: Int,
        y: ByteArray?,
        u: ByteArray?,
        v: ByteArray?
    ) {
//        Log.d(TAG, "width = $width,height = $height,y = ${y?.size},u = ${u?.size},v = ${v?.size},")
        glSurfaceView?.setYUVData(width,height,y,u,v)
    }

    /**
     * 播放器初始化成功，native回调
     */
    private fun onPrepared(width:Int,height:Int,fps:Int) {
        Log.d(TAG, "onPreparedwidth = $width, height = $height, fps = $fps")
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
    private external fun nativeSetMute(mute: Int)
    private external fun nativeSetSpeed(speed: Float)
    private external fun nativeSetPitch(pitch: Float)
    private external fun nativeSetVolume(volume:Int)
    private external fun nativeRelease()
    private external fun nativeSeek(progress: Int)

    companion object {
        init {
            System.loadLibrary("ffmpeg")
        }

//        const val MUTE_RIGHT = 0 // 右声道
//        const val MUTE_LEFT = 0  // 左声道
//        const val MUTE_STEREO = 0 // 立体声

        val TAG = FFPlayer::class.java.simpleName.toString()
    }
}