package com.xc.ffplayer.camera2

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.view.Surface
import com.xc.ffplayer.Decoder
import com.xc.ffplayer.MyApplication
import java.io.File
import java.util.concurrent.ArrayBlockingQueue

class StreamDecoder(var width:Int,var height:Int) : Decoder, Runnable {

    var array: ArrayBlockingQueue<ByteArray> = ArrayBlockingQueue(10)
    var start = true

    private lateinit var mediaCodec: MediaCodec


    fun prepare() {
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC)
        var format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC,width,height)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
        format.setInteger(MediaFormat.KEY_BIT_RATE,width*height)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,2)
        format.setInteger(MediaFormat.KEY_FRAME_RATE,16)
        mediaCodec.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)
        Thread(this).start()
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
        mediaCodec.stop()
    }

    override fun run() {
        mediaCodec.start()
        while (start) {
            var byteArray = array.take()
            if (byteArray != null) {
                val outIndex = mediaCodec.dequeueInputBuffer(100_000)
                if (outIndex >= 0) {
                    val inputBuffer = mediaCodec.getInputBuffer(outIndex)
                    inputBuffer?.clear()
                    inputBuffer?.put(byteArray)
                    mediaCodec.queueInputBuffer(outIndex,0,byteArray.size,System.currentTimeMillis(),0)
                }
            }

            var bufferInfo = MediaCodec.BufferInfo()
            var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)
            while (outIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
                val inputBuffer = ByteArray(outputBuffer.remaining())
                outputBuffer.get(inputBuffer)

                var path = MyApplication.application.getExternalFilesDir("video")?.absolutePath + File.separator + "input.h265"

                val file = File(path)
                file.appendBytes(inputBuffer)

                mediaCodec.releaseOutputBuffer(outIndex,false)
                outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)
            }

        }
    }


}