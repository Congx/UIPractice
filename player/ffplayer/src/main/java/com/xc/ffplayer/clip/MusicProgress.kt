package com.xc.ffplayer.clip

import android.content.Context
import android.media.*
import android.util.Log
import com.xc.ffplayer.utils.PcmToWavUtil
import java.io.File
import java.io.IOException
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

    @Throws(IOException::class)
    private fun mixVideoAndMusic(
        videoInput: String,
        output: String,
        startTimeUs: Int,
        endTimeUs: Int?,
        wavFile: File
    ) {


        //        初始化一个视频封装容器
        val mediaMuxer = MediaMuxer(output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

//            一个轨道    既可以装音频 又视频   是 1 不是2
//            取音频轨道  wav文件取配置信息
//            先取视频
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(videoInput)
        //            拿到视频轨道的索引
        val videoIndex: Int = selectTrack(mediaExtractor, false)
        val audioIndex: Int = selectTrack(mediaExtractor, true)


//            视频配置 文件
        val videoFormat = mediaExtractor.getTrackFormat(videoIndex)
        //开辟了一个 轨道   空的轨道   写数据     真实
        mediaMuxer.addTrack(videoFormat)

//        ------------音频的数据已准备好----------------------------
//            视频中音频轨道   应该取自于原视频的音频参数
        val audioFormat = mediaExtractor.getTrackFormat(audioIndex)
        val audioBitrate = audioFormat.getInteger(MediaFormat.KEY_BIT_RATE)
        audioFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC)
        //        添加一个空的轨道  轨道格式取自 视频文件，跟视频所有信息一样
        val muxerAudioIndex = mediaMuxer.addTrack(audioFormat)


//            音频轨道开辟好了  输出开始工作
        mediaMuxer.start()

//音频的wav
        val pcmExtrator = MediaExtractor()
        pcmExtrator.setDataSource(wavFile.absolutePath)
        val audioTrack: Int = selectTrack(pcmExtrator, true)
        pcmExtrator.selectTrack(audioTrack)
        val pcmTrackFormat = pcmExtrator.getTrackFormat(audioTrack)


        //最大一帧的 大小
        var maxBufferSize = 0
        maxBufferSize = if (audioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            pcmTrackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        } else {
            100 * 1000
        }


//    最终输出   后面   混音   -----》     重采样   混音     这个下节课讲
        val encodeFormat = MediaFormat.createAudioFormat(
            MediaFormat.MIMETYPE_AUDIO_AAC,
            44100, 2
        ) //参数对应-> mime type、采样率、声道数
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioBitrate) //比特率
        //            音质等级
        encodeFormat.setInteger(
            MediaFormat.KEY_AAC_PROFILE,
            MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        //            解码  那段
        encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize)
        //解码 那
        val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
        //            配置AAC 参数  编码 pcm   重新编码     视频文件变得更小
        encoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        encoder.start()
        //            容器
        var buffer = ByteBuffer.allocateDirect(maxBufferSize)
        val info = MediaCodec.BufferInfo()
        var encodeDone = false
        while (!encodeDone) {
            val inputBufferIndex = encoder.dequeueInputBuffer(10000)
            if (inputBufferIndex >= 0) {
                val sampleTime = pcmExtrator.sampleTime
                if (sampleTime < 0) {
//                        pts小于0  来到了文件末尾 通知编码器  不用编码了
                    encoder.queueInputBuffer(
                        inputBufferIndex,
                        0,
                        0,
                        0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                } else {
                    val flags = pcmExtrator.sampleFlags
                    //
                    val size = pcmExtrator.readSampleData(buffer, 0)
                    //                    编辑     行 1 还是不行 2   不要去用  空的
                    val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                    inputBuffer!!.clear()
                    inputBuffer.put(buffer)
                    inputBuffer.position(0)
                    encoder.queueInputBuffer(inputBufferIndex, 0, size, sampleTime, flags)
                    //                        读完这一帧
                    pcmExtrator.advance()
                }
            }
            //                获取编码完的数据
            var outputBufferIndex = encoder.dequeueOutputBuffer(
                info,
                1000
            )
            while (outputBufferIndex >= 0) {
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    encodeDone = true
                    break
                }
                val encodeOutputBuffer =
                    encoder.getOutputBuffer(outputBufferIndex)
                //                    将编码好的数据  压缩 1     aac
                mediaMuxer.writeSampleData(muxerAudioIndex, encodeOutputBuffer!!, info)
                encodeOutputBuffer.clear()
                encoder.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = encoder.dequeueOutputBuffer(
                    info,
                    1000
                )
            }
        }
        //    把音频添加好了
        if (audioTrack >= 0) {
            mediaExtractor.unselectTrack(audioTrack)
        }
        //视频
        mediaExtractor.selectTrack(videoIndex)
        mediaExtractor.seekTo(startTimeUs.toLong(), MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
        maxBufferSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        buffer = ByteBuffer.allocateDirect(maxBufferSize)
        //封装容器添加视频轨道信息
        while (true) {
            val sampleTimeUs = mediaExtractor.sampleTime
            if (sampleTimeUs == -1L) {
                break
            }
            if (sampleTimeUs < startTimeUs) {
                mediaExtractor.advance()
                continue
            }
            if (endTimeUs != null && sampleTimeUs > endTimeUs) {
                break
            }
            //                pts      0
            info.presentationTimeUs = sampleTimeUs - startTimeUs + 600
            info.flags = mediaExtractor.sampleFlags
            //                读取视频文件的数据  画面 数据   压缩1  未压缩2
            info.size = mediaExtractor.readSampleData(buffer, 0)
            if (info.size < 0) {
                break
            }
            //                视频轨道  画面写完了
            mediaMuxer.writeSampleData(videoIndex, buffer, info)
            mediaExtractor.advance()
        }
        try {
            pcmExtrator.release()
            mediaExtractor.release()
            encoder.stop()
            encoder.release()
            mediaMuxer.release()
        } catch (e: Exception) {
        }
    }

    fun selectTrack(extractor: MediaExtractor, audio: Boolean): Int {
        val numTracks = extractor.trackCount
        for (i in 0 until numTracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (audio) {
                if (mime!!.startsWith("audio/")) {
                    return i
                }
            } else {
                if (mime!!.startsWith("video/")) {
                    return i
                }
            }
        }
        return -5
    }
}