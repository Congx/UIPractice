package com.xc.ffplayer.live

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

class AudioProvider(var sampleRate:Int = 44100,var channels:Int = 2,var audioFormat:Int = AudioFormat.ENCODING_PCM_16BIT) :StreamProvider,Releaseable,Runnable {

    private var TAG = "AudioProvider"
    private var bufferSizeInBytes = 0
    private var isStart = false

    override var dataRecived:((byteArray:ByteArray,len:Int)->Unit)? = null

    private val audioRecoder by lazy {
        var channle = if(channels == 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate,
            channle,
            audioFormat)
        return@lazy AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,                     // 采样频率
            channle,  // 通道数
            audioFormat, // 采样位数
            bufferSizeInBytes)
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
            var byteArray = ByteArray(bufferSizeInBytes)
            val ret = audioRecoder.read(byteArray, 0, bufferSizeInBytes)
//            Log.e(TAG ,"AudioRecord ret = $ret")
            when(ret) {
                AudioRecord.ERROR,
                AudioRecord.ERROR_BAD_VALUE,
                AudioRecord.ERROR_DEAD_OBJECT,
                AudioRecord.ERROR_INVALID_OPERATION-> {
                    Log.e(TAG ,"AudioRecord error")
                }
                in 0..Int.MAX_VALUE -> {
                    dataRecived?.invoke(byteArray,ret)
                }
            }

        }
    }


}