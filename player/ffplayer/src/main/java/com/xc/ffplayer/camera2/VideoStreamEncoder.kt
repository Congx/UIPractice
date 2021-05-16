package com.xc.ffplayer.camera2

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.Decoder
import com.xc.ffplayer.MyApplication
import com.xc.ffplayer.live.LiveTaskManager
import com.xc.ffplayer.live.RTMPPackage
import com.xc.ffplayer.live.RTMP_PKG_VIDEO
import com.xc.ffplayer.live.Releaseable
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch

class VideoStreamEncoder(
    var width: Int,
    var height: Int,
    var callback: ((packate: RTMPPackage) -> Unit)? = null,
    var countDownLatch: CountDownLatch
) : Decoder, Runnable{

    private var TAG = "LiveStreamDecoder"

    private var array: ArrayBlockingQueue<ByteArray> = ArrayBlockingQueue(10)

    @Volatile
    var start = false

    private lateinit var mediaCodec: MediaCodec

    private var timeStamp = 0L
    private var startTime = 0L

    override fun prepare() {
//        Log.d(TAG,"width = $width,height = $height")
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        var format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible // nv12
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, width * height)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        LiveTaskManager.execute(this)
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
            array.clear()
        }
    }

//    fun start() {
//        LiveTaskManager.execute(this)
//    }

    override fun stop() {
        start = false
    }

    override fun release() {
        start = false
        mediaCodec.stop()
        mediaCodec.release()
        Thread.currentThread().interrupt()
        startTime = 0L
    }

    override fun run() {
        mediaCodec.start()
        try {
            // 等待rtmp 链接
            if (countDownLatch.count > 0) {
                Log.e(TAG, "等待rtmp 链接...")
                countDownLatch.await()
            }

            start = true

            Log.e(TAG, "开始视频解码")

            while (!Thread.currentThread().isInterrupted && start) {
                var byteArray = array.take()
                if (byteArray != null && start) {
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

                // 两秒一个I帧
                if (System.currentTimeMillis() - timeStamp > 2000) {
                    var bundle = Bundle()
                    bundle.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0)
                    mediaCodec.setParameters(bundle)
                    timeStamp = System.currentTimeMillis()
                }

                var bufferInfo = MediaCodec.BufferInfo()
                var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1_000)
                while (outIndex >= 0) {

                    val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
                    val inputBuffer = ByteArray(outputBuffer.remaining())
                    outputBuffer.get(inputBuffer)

                    if (startTime == 0L) {
                        startTime = bufferInfo.presentationTimeUs / 1000
                    }

//                    if (callback == null) {
//                        var path =
//                            MyApplication.application.getExternalFilesDir("video")?.absolutePath + File.separator + "input.h265"
//                        val file = File(path)
//                        file.appendBytes(inputBuffer)
//                    } else {
                    var pkg = RTMPPackage(
                        inputBuffer, bufferInfo.presentationTimeUs / 1000 - startTime,
                        RTMP_PKG_VIDEO
                    )
                    callback?.invoke(pkg)
//                    }

                    mediaCodec.releaseOutputBuffer(outIndex, false)
                    outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1_000)
                }

            }

        } catch (e: InterruptedException) {

        }
    }

    private fun computePresentationTime(frameIndex: Long): Long {
        return 132 + frameIndex * 1000000 / 15
    }

}