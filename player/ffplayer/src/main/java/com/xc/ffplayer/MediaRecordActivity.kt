package com.xc.ffplayer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_media_record.*
import java.io.*

class MediaRecordActivity : AppCompatActivity() {

    lateinit var systemService:MediaProjectionManager
    lateinit var mediaProjection:MediaProjection
    var recode = true
    private val mediaCodec: MediaCodec by lazy {
        return@lazy MediaCodec.createEncoderByType("video/avc")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_record)

        btnStart.setOnClickListener {
            start()
        }

        btnStop.setOnClickListener {
            recode = false
            mediaProjection.stop()
            mediaCodec.stop()
        }
        systemService = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun start() {
        val intent = systemService.createScreenCaptureIntent()
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            mediaProjection = systemService.getMediaProjection(resultCode, data!!)
            codec()
        }
    }

    private fun codec() {
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        val mediaCodec = mediaCodec
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 540, 960)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE,400_000)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,2)
        format.setInteger(MediaFormat.KEY_FRAME_RATE,17)
        mediaCodec.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)

        Thread {
            val inputSurface = mediaCodec.createInputSurface()
            mediaCodec.start()
            mediaProjection.createVirtualDisplay("screen-recode",540,960,1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,inputSurface,null,null)

            val bufferInfo = MediaCodec.BufferInfo()
            while (recode) {
                val index = mediaCodec.dequeueOutputBuffer(bufferInfo, 100000)
                if (index >= 0) {
                    Log.e("index" ,index.toString())
                    val outputBuffer = mediaCodec.getOutputBuffer(index)!!
//                    outputBuffer.position(bufferInfo.offset)
//                    outputBuffer.limit(bufferInfo.size + bufferInfo.offset)
                    var byteArr = ByteArray(bufferInfo.size)
                    outputBuffer.get(byteArr)
                    writeByte(byteArr)
                    mediaCodec.releaseOutputBuffer(index,false)
                }
            }
        }.start()

    }

    fun  writeByte(arr:ByteArray) {
        var os = FileOutputStream(getExternalFilesDir("encode")?.absolutePath + File.separator + "encode.h264",true)
        os.write(arr)
    }
}