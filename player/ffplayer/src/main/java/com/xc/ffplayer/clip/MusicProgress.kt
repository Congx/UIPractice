package com.xc.ffplayer.clip

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.xc.ffplayer.utils.PcmToWavUtil
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class MusicProgress {

    fun mixAudioTrack(
        context: Context,
        inputPath1: String,
        inputPath2: String,
        outputPath: String,
        volume1: Int,
        volume2: Int,
        startTimeUs: Long,
        endTimeUs: Long
    ) {
        var tempOutPath1 = context.getExternalFilesDir("output")?.absolutePath + File.separator + "temp1.pcm"
        var tempPath2 = context.getExternalFilesDir("output")?.absolutePath + File.separator + "temp2.pcm"
        decodeToPCM(context, inputPath1, tempOutPath1, startTimeUs, endTimeUs, volume1, volume2)
//        decodeToPCM(context,inputPath2,tempPath2,startTimeUs,endTimeUs,volume1,volume2)
    }

    fun decodeToPCM(
        context: Context,
        intputPath: String,
        outputPath: String,
        start: Long,
        end: Long,
        volume1: Int,
        volume2: Int
    ) {
        val file = File(outputPath)
//        var channel = FileOutputStream(file,true).channel
        if(file.exists()) file.delete()

        val mediaExtractor = MediaExtractor().also {
            it.setDataSource(intputPath)
        }

        lateinit var trackFormat: MediaFormat
        var audioIndex: Int = -1
        for (index in 0 until mediaExtractor.trackCount) {
            trackFormat = mediaExtractor.getTrackFormat(index)
            val string = trackFormat.getString(MediaFormat.KEY_MIME) ?: ""
            if (string.startsWith("audio/")) {
                audioIndex = index
                break
            }

        }

        if (audioIndex == -1) return
        mediaExtractor.selectTrack(audioIndex)
        mediaExtractor.seekTo(start, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

        val mime = trackFormat.getString(MediaFormat.KEY_MIME) ?: return
        Log.d("MusicProgress", "mime = $mime")
        // 通道数
        val channelCount = trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        Log.d("MusicProgress", "channelCount = $channelCount")

        // 通道数
        val sampleRate = trackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        Log.d("MusicProgress", "sampleRate = $sampleRate")

        //根据pcmEncoding编码格式，得到采样精度，MediaFormat.KEY_PCM_ENCODING这个值不一定有
        val pcmEncoding: Int = if (trackFormat.containsKey(MediaFormat.KEY_PCM_ENCODING))
            trackFormat.getInteger(MediaFormat.KEY_PCM_ENCODING)
        else
            AudioFormat.ENCODING_PCM_16BIT

        // 采样位数
        var bitCount = when (pcmEncoding) {
            AudioFormat.ENCODING_PCM_FLOAT -> 32
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 16
        }

        Log.d("MusicProgress", "采样位数 = $bitCount")

        var maxBufferSize: Int
        if (trackFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            maxBufferSize = trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            Log.d("MusicProgress", "maxBufferSize = $maxBufferSize")
        } else {
            maxBufferSize = 1024 * 100
        }

        val mediaCodec = MediaCodec.createDecoderByType(mime)
        mediaCodec.configure(trackFormat, null, null, 0)

        var sampleTime: Long

        var bufferInfo = MediaCodec.BufferInfo()
        var buffer = ByteBuffer.allocateDirect(maxBufferSize)

        mediaCodec.start()
        var decodeFinish = false
        while (!decodeFinish) {
            sampleTime = mediaExtractor.sampleTime
            Log.d("MusicProgress", "sampleTime = $sampleTime")
            if (sampleTime < start) {
                mediaExtractor.advance()
                continue
            } else if (sampleTime > end) {
                break
            } else if (sampleTime == -1L) {
                break
            }else {
                val iIndex = mediaCodec.dequeueInputBuffer(100_000)
                if (iIndex >= 0) {

                    bufferInfo.flags = mediaExtractor.sampleFlags
                    bufferInfo.presentationTimeUs = sampleTime

                    val inputBuffer = mediaCodec.getInputBuffer(iIndex) ?: break
                    inputBuffer.clear()
                    val size = mediaExtractor.readSampleData(buffer, 0)

                    var byteArray = ByteArray(buffer.remaining())
                    buffer.get(byteArray)

                    inputBuffer.put(byteArray)

                    bufferInfo.size = size
                    if (size > 0) {
                        mediaCodec.queueInputBuffer(
                            iIndex,
                            0,
                            size,
                            sampleTime,
                            bufferInfo.flags
                        )
                        mediaExtractor.advance()
                    }else {
                        decodeFinish = true
                    }
                }
            }

            var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)

            while (outIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
                outputBuffer.clear()
                val outBuffer = ByteArray(outputBuffer.remaining())
                outputBuffer.get(outBuffer)
                file.appendBytes(outBuffer)
                mediaCodec.releaseOutputBuffer(outIndex, false)
                outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)
            }

        }

        mediaExtractor.release()
        mediaCodec.stop()
        mediaCodec.release()

        Log.d("MusicProgress", "解码完成")

        var out = context.getExternalFilesDir("audio")?.absolutePath + File.separator + "temp.mp3"
        PcmToWavUtil.pcmToWav(file.absolutePath,out,sampleRate,maxBufferSize)

        Log.d("MusicProgress", "文件保存完成")
    }
}