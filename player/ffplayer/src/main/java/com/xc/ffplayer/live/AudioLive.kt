package com.xc.ffplayer.live

import android.app.Activity
import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import java.util.concurrent.CountDownLatch


class AudioLive(
    var context: Activity,
    var dataPush: DataPush,
    var countDownLatch: CountDownLatch
) : Releaseable{

    private var TAG = "AudioLive"

    private var start = false
    var isHardDecoder = false // 硬编、软编开关

    private var sampleRate = 44100
    private var channles = 2 // 默认立体
    private var sampleBit = AudioFormat.ENCODING_PCM_16BIT // 采样位数
    var inputBufferSize = 0

    private val audioDecoder by lazy {
        AudioStreamEncoder(countDownLatch = countDownLatch)
    }

    private val audioProvider by lazy {
        AudioProvider(sampleRate,channles,sampleBit,inputBufferSize)
    }

    init {
        var bitCount = when (sampleBit) {
            AudioFormat.ENCODING_PCM_FLOAT -> 32
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 16
        }
        if (!isHardDecoder) {
            // 软解，初始化faac
            // 最小buffer的大小
            inputBufferSize = dataPush.nativeSetAudioEncodeInfo(sampleRate, channles, bitCount)
            Log.e(TAG,"inputBufferSize = $inputBufferSize")
        }else {
            inputBufferSize = AudioRecord.getMinBufferSize(sampleRate, channles, sampleBit)
        }

        audioDecoder.callback = {
            Log.e(TAG,"audio 硬编 解码成功")
//            it.bytes.append2File("audio.aac")
            dataPush.addData(it)
        }

        audioProvider.dataRecived = { bytes: ByteArray, len: Int ->
//            Log.e(TAG ,"音频 回调byteArray size ${bytes.size}")
//            bytes.append2File("audio.pcm")
            if(isHardDecoder) {
                Log.e(TAG,"audio 硬解流")
                audioDecoder.addData(bytes,len)
            }else {
                // 软解
                Log.e(TAG,"audio 软解流")
                dataPush.sendAudio2Native(bytes,len)
            }

        }
    }

    fun startRecode() {
        start = true
        if(isHardDecoder) {
            audioDecoder.prepare()
        }
        audioProvider.startRecord()
    }

    override fun stop() {
        if (!start) return
        audioProvider.stop()
        audioDecoder.stop()
        start = false
    }

    override fun release() {
        if (!start) return
        audioDecoder.release()
        audioProvider.release()
        start = false
    }

}