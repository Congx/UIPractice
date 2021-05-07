package com.xc.ffplayer.clip

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.xc.ffplayer.utils.PcmToWavUtil
import com.xc.ffplayer.utils.byte2String
import java.io.*
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or

class MusicProcess {

    fun mixAudioTrack(
        context: Context,
        inputPath1: String,
        inputPath2: String,
        outputPath: String,
        startTimeUs: Long,
        endTimeUs: Long,
        volume1: Int,
        volume2: Int
    ) {
        var tempOutPath1 = context.getExternalFilesDir("output")?.absolutePath + File.separator + "temp1.pcm"
        var tempPath2 = context.getExternalFilesDir("output")?.absolutePath + File.separator + "temp2.pcm"
        decodeToPCM(context, inputPath1, tempOutPath1, startTimeUs, endTimeUs, volume1, volume2)
        decodeToPCM(context,inputPath2,tempPath2,startTimeUs,endTimeUs,volume1,volume2)

        var mixPath = context.getExternalFilesDir("output")?.absolutePath + File.separator + "mix.pcm"
        mixPcm(tempOutPath1,tempPath2,mixPath,volume1,volume2)
        Log.d("MusicProgress", "合成PCM完成")
    }

    fun decodeToPCM(
        context: Context,
        intputPath: String,
        outputPath: String,
        startTime: Long,
        endTime: Long,
        volume1: Int,
        volume2: Int
    ) {
        val file = File(outputPath)
//        var channel = FileOutputStream(file,true).channel
        if(file.exists()) file.delete()

        val mediaExtractor = MediaExtractor().also {
            Log.d("MusicProgress", "intputPath = $intputPath")
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
        Log.d("MusicProgress", "audioIndex = $audioIndex")
        mediaExtractor.selectTrack(audioIndex)
        mediaExtractor.seekTo(startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

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
//            Log.d("MusicProgress", "sampleTime = $sampleTime")
            if (sampleTime < startTime) {
                mediaExtractor.advance()
                continue
            } else if (sampleTime > endTime) {
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
                    val size = mediaExtractor.readSampleData(inputBuffer, 0)
                    bufferInfo.size = size
                    if (size > 0) {
                        mediaCodec.queueInputBuffer(iIndex, 0, size, sampleTime, bufferInfo.flags)
                        mediaExtractor.advance()
                    }else {
                        decodeFinish = true
                    }
                }
            }

            var outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100_000)

            while (outIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outIndex) ?: break
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

//        var out = context.getExternalFilesDir("output")?.absolutePath + File.separator + "temp.mp3"
//        PcmToWavUtil.pcmToWav(file.absolutePath,out,sampleRate,maxBufferSize)

//        Log.d("MusicProgress", "文件保存完成")
    }

    fun mixPcm(path1:String,path2:String,outputPath:String,volume1: Int, volume2: Int) {

        var vol1 = volume1.toFloat() / 100.toFloat()
        var vol2 = volume2.toFloat() / 100.toFloat()

        var file1 = File(path1)
        var file2 = File(path2)
        var outputFile = File(outputPath)

        Log.d("MusicProgress", "vol1 = $vol1, vol2 = $vol2")

        var bis1 = BufferedInputStream(FileInputStream(file1))
        var bis2 = BufferedInputStream(FileInputStream(file2))
        var bos = BufferedOutputStream(FileOutputStream(outputFile))

        var tempBuffer1 = ByteArray(2048)
        var tempBuffer2 = ByteArray(2048)
        var tempBuffer3 = ByteArray(2048)

        var len1 = 0
        var len2 = 0

        var temp1: Short
        var temp2: Short
        var temp = 0

        while (len1 != -1 || len2 != -1) {
            if (len1 != -1) {
                len1 = bis1.read(tempBuffer1)
            }
//            Log.d("MusicProgress", "len1 = $len1")
            if (len2 != -1) {
                len2 = bis2.read(tempBuffer2)
            }
//            Log.d("MusicProgress", "len2 = $len2")
            var max = len1.coerceAtLeast(len2)

            if (max <= 0) break

//            Log.d("MusicProgress", "max = $max")

            for (i in 0 until (max - 1) step 2) {
                if (len1 == -1 || i >= len1 - 1) {
                    temp1 = 0
                }else {
                    temp1 = ((tempBuffer1[i].toInt() and 0xff) or ((tempBuffer1[i+1].toInt() and 0xff) shl 8)).toShort()
                }

                if (len2 == -1 || i >= len2 - 1) {
                    temp2 = 0
                }else {
                    temp2 = ((tempBuffer2[i].toInt() and 0xff) or ((tempBuffer2[i+1].toInt() and 0xff) shl 8)).toShort()
                }

                temp = ((temp1 * vol1) + (temp2 * vol2)).toInt()
//                temp = ((temp1.toInt() * 0.5f)).toInt()
//                temp = temp2.toInt()

                if (temp > 32767) {
                    temp = 32767
                }else if (temp < -32767) {
                    temp = -32767
                }

//                Thread.sleep(2)

                // 低八位
                tempBuffer3[i] = (temp and 0xff).toByte()
                // 高八位
                tempBuffer3[i+1] = (((temp shr 8 and 0xff)).toByte())

//                Log.d("MusicProgress", "tempBuffer3[i] = ${tempBuffer3[i].toString(16)}")
//                Log.d("MusicProgress", "tempBuffer3[i+1] = ${tempBuffer3[i+1].toString(16)}")

//                 低八位
//                tempBuffer3[i] = (temp2.toInt() and 0xff).toByte()
//                // 高八位
//                tempBuffer3[i+1] = (((temp2.toInt() shr 8 and 0xff)).toByte())

            }

            bos.write(tempBuffer3,0,max)
        }

        bis1.close()
        bis2.close()
        bos.close()
    }
}