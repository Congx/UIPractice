package com.xc.ffplayer.live

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

class AudioProvider :StreamProvider,Releaseable,Runnable {

    private var TAG = "AudioProvider"
    private var sampleRate = 44100
    private var bufferSizeInBytes = 0
    private var isStart = false

    override var dataRecived:((byteArray:ByteArray,len:Int)->Unit)? = null

    private val audioRecoder by lazy {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT)
        return@lazy AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,                     // 采样频率
            AudioFormat.CHANNEL_IN_MONO,  // 通道数
            AudioFormat.ENCODING_PCM_16BIT, // 采样位数
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