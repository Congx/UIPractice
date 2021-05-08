package com.xc.ffplayer.clip

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
import android.util.Log
import java.nio.ByteBuffer

class MediaProcess {

    companion object {

        fun start(videoPath1:String, videoPath2:String, outputPath:String) {

            var mediaMuxer = MediaMuxer(outputPath,MUXER_OUTPUT_MPEG_4)

            // 第一个视频
            var mediaExtractor1 = MediaExtractor()
            mediaExtractor1.setDataSource(videoPath1)

            val videoTrackIndex1 = selectTrackIndex(mediaExtractor1)
            val audioTrackIndex1 = selectTrackIndex(mediaExtractor1,false)

            val videoTrackFormat1 = mediaExtractor1.getTrackFormat(videoTrackIndex1)
            val audioTrackFormat1 = mediaExtractor1.getTrackFormat(audioTrackIndex1)
            val duration1 = audioTrackFormat1.getLong(MediaFormat.KEY_DURATION)
            val duration = audioTrackFormat1.getLong(MediaFormat.KEY_DURATION)

            Log.d("MediaProcess","duration1 = $duration1")
            Log.d("MediaProcess","duration = $duration")

            // 第二个视频
            var mediaExtractor2 = MediaExtractor()
            mediaExtractor2.setDataSource(videoPath2)

            val videoTrackIndex2 = selectTrackIndex(mediaExtractor2)
            val audioTrackIndex2 = selectTrackIndex(mediaExtractor2,false)

            val videoTrackFormat2 = mediaExtractor2.getTrackFormat(videoTrackIndex2)
            val audioTrackFormat2 = mediaExtractor2.getTrackFormat(audioTrackIndex2)

            val videoAddTrack = mediaMuxer.addTrack(videoTrackFormat1)
            val audioAddTrack = mediaMuxer.addTrack(audioTrackFormat1)

//            val mediaMuxer = MediaMuxer(outputPath, MUXER_OUTPUT_MPEG_4)
//
//            val mediaExtractor1 = MediaExtractor()
//            mediaExtractor1.setDataSource(videoPath1)
//
//            val mediaExtractor2 = MediaExtractor()
//            mediaExtractor2.setDataSource(videoPath2)
//
////            var videoAddTrack = -1
////            var audioAddTrack = -1
////            var duration = 0L
//
//            var videoTrackIndex1 = selectTrackIndex(mediaExtractor1)
//            var audioTrackIndex1 = selectTrackIndex(mediaExtractor1,false)
//
//
//            var videoTrackFormat1 = mediaExtractor1.getTrackFormat(videoTrackIndex1)
//            var audioTrackFormat1 = mediaExtractor1.getTrackFormat(audioTrackIndex1)
//            var duration = audioTrackFormat1.getLong(MediaFormat.KEY_DURATION)
//            Log.d("MediaProcess","duration1 = $duration")
//
//            var videoAddTrack = mediaMuxer.addTrack(videoTrackFormat1!!)
//            var audioAddTrack = mediaMuxer.addTrack(audioTrackFormat1!!)
//
//            var videoTrackIndex2 = -1
//            var audioTrackIndex2 = -1
//            for (index in 0 until mediaExtractor2.trackCount) {
//                val format = mediaExtractor2.getTrackFormat(index)
//                val mime = format.getString(MediaFormat.KEY_MIME)
//                if (mime!!.startsWith("video/")) {
//                    videoTrackIndex2 = index
//                } else if (mime!!.startsWith("audio/")) {
//                    audioTrackIndex2 = index
//                }
//            }
//
//            if (mediaMuxer == null) return


            mediaMuxer.start()

            // 选择第一个视频
            mediaExtractor1.selectTrack(videoTrackIndex1)

            var byteBuffer = ByteBuffer.allocate(500*1024)
            var bufferInfo = MediaCodec.BufferInfo()
            while (true) {
                byteBuffer.clear()
                val size = mediaExtractor1.readSampleData(byteBuffer, 0)
                if (size <= 0) break
                bufferInfo.presentationTimeUs = mediaExtractor1.sampleTime
                bufferInfo.flags = mediaExtractor1.sampleFlags
                bufferInfo.size = size
                mediaMuxer.writeSampleData(videoAddTrack,byteBuffer,bufferInfo)
                mediaExtractor1.advance()
            }

            mediaExtractor1.unselectTrack(videoTrackIndex1)
            mediaExtractor1.selectTrack(audioTrackIndex1)

            bufferInfo = MediaCodec.BufferInfo()
            byteBuffer = ByteBuffer.allocate(500*1024)
            while (true) {
                byteBuffer.clear()
                val size = mediaExtractor1.readSampleData(byteBuffer, 0)
                if (size <= 0) break
                bufferInfo.presentationTimeUs = mediaExtractor1.sampleTime
                bufferInfo.flags = mediaExtractor1.sampleFlags
                bufferInfo.size = size
                mediaMuxer.writeSampleData(audioAddTrack,byteBuffer,bufferInfo)
                mediaExtractor1.advance()
            }

            Log.d("MediaProcess","视频1拼接完成")

            mediaExtractor2.selectTrack(videoTrackIndex2)
            bufferInfo = MediaCodec.BufferInfo()
            byteBuffer = ByteBuffer.allocate(500*1024)
            while (true) {
                byteBuffer.clear()
                val size = mediaExtractor2.readSampleData(byteBuffer, 0)
                if (size <= 0) break
                bufferInfo.presentationTimeUs = mediaExtractor2.sampleTime + duration
                bufferInfo.flags = mediaExtractor2.sampleFlags
                bufferInfo.size = size
                mediaMuxer.writeSampleData(videoAddTrack,byteBuffer,bufferInfo)
                mediaExtractor2.advance()
            }
//
            mediaExtractor2.unselectTrack(videoTrackIndex2)
            mediaExtractor2.selectTrack(audioTrackIndex2)

//            Log.d("MediaProcess","videoTrackIndex2 = $videoTrackIndex2")
//            Log.d("MediaProcess","audioTrackIndex2 = $audioTrackIndex2")

//            bufferInfo = MediaCodec.BufferInfo()
//            byteBuffer = ByteBuffer.allocate(500*1024)
            while (true) {
                byteBuffer.clear()
                val size = mediaExtractor2.readSampleData(byteBuffer, 0)
                if (size <= 0) break
                bufferInfo.presentationTimeUs = mediaExtractor2.sampleTime + duration
                bufferInfo.flags = mediaExtractor2.sampleFlags
                bufferInfo.size = size
                bufferInfo.offset = 0
                mediaMuxer.writeSampleData(audioAddTrack,byteBuffer,bufferInfo)
                mediaExtractor2.advance()
            }

            mediaExtractor1.release()
            mediaExtractor2.release()

            mediaMuxer.stop()
            mediaMuxer.release()

            Log.d("MediaProcess","视频2拼接完成")
        }


        fun selectTrackIndex(mediaExtractor: MediaExtractor, isVideo:Boolean = true):Int {

            for (index in 0 until mediaExtractor.trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(index)
                val string = trackFormat.getString(MediaFormat.KEY_MIME) ?: ""
                if (string.startsWith("video/") && isVideo) {
                    return index
                }else if (string.startsWith("audio/") && !isVideo) {
                    return index
                }
            }

            return -1
        }
    }
}