package com.xc.ffplayer.live

import android.media.*
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.Decoder
import java.util.concurrent.ArrayBlockingQueue

/**
 * pcm to aac
 */
class AudioStreamEncoder(var callback: ((packate:RTMPPackage) -> Unit)? = null):
    Decoder,Releaseable,Runnable{

    var TAG = "AudioStreamDecoder"

    private var array: ArrayBlockingQueue<ByteArray> = ArrayBlockingQueue(20)

    @Volatile
    var start = false

    private lateinit var mediaCodec: MediaCodec

    private var startTime = 0L

    override fun prepare() {
        try {
            start = true
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            var format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,44100,1)
            format.setInteger(
                MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC
            )
            format.setInteger(MediaFormat.KEY_BIT_RATE, 64_000)
            var bufferSizeInBytes = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,bufferSizeInBytes*2)
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            LiveTaskManager.execute(this)
        }catch (e:Exception) {
            e.printStackTrace()
            Log.d(TAG,"初始化解码器失败")
        }
    }

    override fun decode(surface: Surface?) {

    }

    override fun decode() {

    }

    override fun decode(byteArray: ByteArray) {
        if (!start) {
            return
        }
        if(!array.offer(byteArray)) {
            Log.d(TAG,"丢失音频数据")
            array.clear()
        }
    }

    override fun stop() {
        // todo
        start = false
    }

    override fun release() {
        Thread.currentThread().interrupt()
        startTime = 0L
        mediaCodec.stop()
        mediaCodec.release()
    }

    override fun run() {
        mediaCodec.start()
        try {
            sendHead()
            while (!Thread.currentThread().isInterrupted && start) {
                var byteArray = array.take()
//                Log.d(TAG,"byteArray  ${byteArray.size}")
                val stamp = System.currentTimeMillis() * 1000
                if (byteArray != null && start) {
                    val outIndex = mediaCodec.dequeueInputBuffer(1_000)
                    if (outIndex >= 0) {
                        val inputBuffer = mediaCodec.getInputBuffer(outIndex)
//                        Log.d(TAG,"inputBuffer  ${inputBuffer?.remaining()}")
                        inputBuffer?.clear()
                        inputBuffer?.put(byteArray)
                        mediaCodec.queueInputBuffer(outIndex, 0, byteArray.size, stamp, 0)
                    }
                }

                var bufferInfo = MediaCodec.BufferInfo()
                var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1_000)
                while (outIndex >= 0 && start) {
                    val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
                    val inputBuffer = ByteArray(outputBuffer.remaining())
                    outputBuffer.get(inputBuffer)

                    if (startTime == 0L) {
                        // 记录第一帧的时间
                        startTime = bufferInfo.presentationTimeUs / 1000
                    }

                    var rtmpPackage = RTMPPackage(inputBuffer,bufferInfo.presentationTimeUs/1000-startTime,
                        RTMP_PKG_AUDIO)
                    callback?.invoke(rtmpPackage)

                    mediaCodec.releaseOutputBuffer(outIndex, false)
                    outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1_000)
                }

            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun sendHead() {
        var byte:ByteArray = byteArrayOf(0x12,0x08)
        var rtmpPackage = RTMPPackage(byte,0, RTMP_PKG_AUDIO_HEAD)
        callback?.invoke(rtmpPackage)

    }


}