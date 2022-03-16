package com.xc.ffplayer.camera2

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.Decoder
import com.xc.ffplayer.MyApplication
import okhttp3.Interceptor
import java.io.File
import java.lang.Exception
import java.util.concurrent.ArrayBlockingQueue

class StreamDecoder(
    var width: Int,
    var height: Int,
    var callback: ((byteArray: ByteArray) -> Unit)? = null
) : Decoder, Runnable {

    var TAG = "StreamDecoder"

    var array: ArrayBlockingQueue<ByteArray> = ArrayBlockingQueue(10)
    var start = true

    private lateinit var mediaCodec: MediaCodec
    var thread: Thread? = null

    override fun prepare() {
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        Log.d(TAG,"width = $width,height = $height")
        var format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, width * height)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        thread = Thread(this)
        thread?.start()
    }

    override fun decode(surface: Surface?) {

    }

    override fun decode() {
    }

    override fun decode(byteArray: ByteArray) {
        array.offer(byteArray)
    }

    override fun stop() {
        start = false
        if (this::mediaCodec.isInitialized) mediaCodec?.stop()
        thread?.interrupt()
    }

    override fun release() {

    }

    override fun run() {
        mediaCodec.start()
        try {
            while (!Thread.currentThread().isInterrupted && start) {
                var byteArray = array.take()
                if (byteArray != null) {
                    val outIndex = mediaCodec.dequeueInputBuffer(1_000)
                    if (outIndex >= 0) {
                        val inputBuffer = mediaCodec.getInputBuffer(outIndex)
                        inputBuffer?.clear()
                        inputBuffer?.put(byteArray)
                        mediaCodec.queueInputBuffer(
                            outIndex,
                            0,
                            byteArray.size,
                            System.currentTimeMillis() * 1000 + 138,
                            0
                        )
                    }
                }

                var bufferInfo = MediaCodec.BufferInfo()
                var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1_000)
                while (outIndex >= 0) {
                    val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
                    val inputBuffer = ByteArray(outputBuffer.remaining())
                    outputBuffer.get(inputBuffer)

                    if (callback == null) {
                        var path =
                            MyApplication.application.getExternalFilesDir("video")?.absolutePath + File.separator + "input.h265"
                        val file = File(path)
                        file.appendBytes(inputBuffer)
                    } else {
                        callback?.invoke(inputBuffer)
                    }

                    mediaCodec.releaseOutputBuffer(outIndex, false)
                    outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)
                }

            }

        } catch (e: InterruptedException) {

        }

    }

    private fun computePresentationTime(frameIndex: Long): Long {
        return 132 + frameIndex * 1000000 / 15
    }


}