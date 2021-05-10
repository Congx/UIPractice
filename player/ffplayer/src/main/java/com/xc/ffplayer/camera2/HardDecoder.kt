package com.xc.ffplayer.camera2

import android.graphics.*
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.xc.ffplayer.Decoder
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class HardDecoder(var path: String) : Decoder, Runnable {

    private lateinit var mediaCodec: MediaCodec

    var extractor = MediaExtractor()
    var outputSize = 0
    var timeout = 10000L
    var surface :Surface? = null
    var width = 0
    var height = 0

    override fun prepare() {

    }

    override fun decode(surface: Surface?) {
        this.surface = surface
        if (prepare(surface)) {
            Thread(this).start()
        }
    }

    override fun decode() {
        decode(null)
    }

    override fun decode(byteArray: ByteArray) {

    }

    override fun stop() {

    }

    fun prepare(surface: Surface?): Boolean {

        var trackFormat: MediaFormat? = null
        var mime = ""
        try {
            extractor.setDataSource(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (index in 0 until extractor.trackCount) {
            // 获取媒体信息
            trackFormat = extractor.getTrackFormat(index)
            mime = trackFormat.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.isNotEmpty()) {
                if (mime.startsWith("video/")) {
                    // 选择视频轨道
                    extractor.selectTrack(index)
                    break
                }
            }
        }
        if (trackFormat == null) return false
        // 获取宽高信息
        width = trackFormat.getInteger(MediaFormat.KEY_WIDTH)
        height = trackFormat.getInteger(MediaFormat.KEY_HEIGHT)
        outputSize = width * height * 3 / 2
        // 设置yuv的输出格式
//        trackFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
//        val formate = trackFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT)
//        Log.e("formate:", formate.toString())
        Log.e("mime:", mime)
        Log.e("width:", width.toString())
        Log.e("height:", height.toString())
        mediaCodec = MediaCodec.createDecoderByType(mime)
        mediaCodec.configure(trackFormat, surface, null, 0)
        return true
    }

    override fun run() {
        // mediaCodec 是个状态机，一定要先start
        mediaCodec.start()
        doDecode()
    }

    var i = 0
    private fun doDecode() {
        var decodeFinish = false

        while (!decodeFinish) {
            if (!decodeFinish) {
                var inputIndex = mediaCodec.dequeueInputBuffer(timeout)
                if (inputIndex >= 0) {
                    var inputBuffer = mediaCodec.getInputBuffer(inputIndex) ?: continue
                    // 使用之前一定要clear
                    inputBuffer.clear()
                    // 从视频中，读取数据到buffer中
                    val readSampleData = extractor.readSampleData(inputBuffer, 0)
                    if (readSampleData > 0) {
                        // 通知解码该buffer
                        mediaCodec.queueInputBuffer(inputIndex, 0, inputBuffer.limit(), 0, 0)
                        extractor.advance()
                    } else {
                        decodeFinish = true
                        mediaCodec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    }

                } else {
                    continue
                }
            }

            val bufferInfo = MediaCodec.BufferInfo()
            val outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeout)
            if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                if (bufferInfo.size == 0) {
                    break
                }
                bufferInfo.flags = 0
            }
            if (outIndex >= 0) {
                var outputBuffer = mediaCodec.getOutputBuffer(outIndex)!!
                val tempBuffer = ByteArray(outputSize)
                outputBuffer?.get(tempBuffer, 0, outputSize)
                outputBuffer?.limit()


                outputBuffer.position(bufferInfo.offset)
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                //图像  Java C++
                val ba = ByteArray(outputBuffer.remaining())
                outputBuffer.get(ba)

                val yuvImage = YuvImage(ba, ImageFormat.NV21, width, height, null)
                val baos = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, baos)
                val jdata = baos.toByteArray() //rgb

                val bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.size)
                if (bmp != null) {
                    if (i > 5) {
                        try {
                            val myCaptureFile = File("/storage/emulated/0/Android/data/com.xc.ffplayer/files/video/", "img.png")
                            val bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
                            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                            bos.flush()
                            bos.close()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    i++
                }

                try {
                    Thread.sleep(30)
                } catch (e: Exception) {

                }
                mediaCodec.releaseOutputBuffer(outIndex, surface != null)
            }
        }
        mediaCodec.release()
    }

}