package com.xc.ffplayer.live

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.nio.ByteBuffer

class AudioProvider(
    var sampleRate: Int = 44100,
    var channels: Int = 2,
    var audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
    var inputBufferSize: Int = 0
) :StreamProvider,Releaseable,Runnable {

    private var TAG = "AudioProvider"
    private var isStart = false
    private var minBufferSize = 0
    override var dataRecived:((byteArray:ByteArray,len:Int)->Unit)? = null

    lateinit var buffer:ByteArray

    private val audioRecoder by lazy {
        var channle = if(channels == 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO
//        Log.e(TAG,"minBufferSize = $minBufferSize")
//        minBufferSize = Math.max(bufferSizeInBytes,inputBufferSize)
        minBufferSize = inputBufferSize
//        Log.e(TAG,"minBufferSize = $minBufferSize")
        buffer = ByteArray(minBufferSize)
        return@lazy AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,                     // 采样频率
            channle,  // 通道数
            audioFormat, // 采样位数
            minBufferSize)
    }

    fun startRecord() {
        isStart = true
        audioRecoder.startRecording()
        LiveTaskManager.execute(this)
    }

//    fun stopRecord() {
//        isStart = false
//        audioRecoder.stop()
//        Thread.currentThread().interrupt()
//    }

    override fun stop() {
        isStart = false
    }

    override fun release() {
        Thread.currentThread().interrupt()
        audioRecoder.stop()
        audioRecoder.release()
    }

    override fun run() {
        while (!Thread.currentThread().isInterrupted &&
            (audioRecoder.recordingState == AudioRecord.RECORDSTATE_RECORDING)
            && isStart) {

            val ret = audioRecoder.read(buffer, 0, minBufferSize)
            Log.e(TAG ,"AudioRecord ret = $ret")
            when(ret) {
                AudioRecord.ERROR,
                AudioRecord.ERROR_BAD_VALUE,
                AudioRecord.ERROR_DEAD_OBJECT,
                AudioRecord.ERROR_INVALID_OPERATION-> {
                    Log.e(TAG ,"AudioRecord error")
                }
                in 0..Int.MAX_VALUE -> {
                    dataRecived?.invoke(buffer,ret)
                }
            }

        }
    }


}