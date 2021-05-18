package com.xc.ffplayer.live

import android.util.Log
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingDeque

open class DataPush(var countDownLatch: CountDownLatch) : Runnable,Releaseable {

    var TAG = "LivePush"

    private lateinit var url: String
    @Volatile
    var isConnected = false

    var queue = LinkedBlockingDeque<RTMPPackage>(100)

    init {
        nativeInit()
    }

    fun addData(packge:RTMPPackage) {
//        Log.e(TAG,"add RTMPPackage")
//        if (!isConnected) {
//            Log.e(TAG,"add no connect")
//            return
//        }
//        if (!queue.offer(packge)) {
//            Log.e(TAG,"RTMPPackage 丢弃")
//            queue.clear()
//        }

        if (isConnected) {
            sendData(packge.bytes,packge.bytes.size,packge.timeStamp,packge.type)
        }
    }

    fun sendVideo2Native(NV12bytes: ByteArray, len:Int) {
        if (isConnected) {
            nativeSendNV12Data(NV12bytes,len)
        }
    }

    fun sendAudio2Native(audioData: ByteArray, len:Int) {
        if (isConnected) {
            nativeSendPCMAudioData(audioData,len)
        }
    }

    fun startPush(url: String) {
        this.url = url
        connect(url)
//        LiveTaskManager.execute(this)
    }

    override fun run() {
//        connect(url)
//        countDownLatch.await()
//        if (connect(url)){
//            Log.e(TAG,"rtmp  链接成功")
//            connected = true
//            countDownLatch.await()
//            countDownLatch.countDown()
//        }else {
//            Log.e(TAG,"rtmp  链接失败")
//            connected = false
//        }
//        isConnected = true
//        try {
//            while (!(Thread.currentThread().isInterrupted) && isConnected) {
//                val rtmpPackage = queue.take()
//                rtmpPackage?.let {
////                    it.bytes.append2File("livedata.h264")
//                    sendData(it.bytes,it.bytes.size,it.timeStamp,it.type)
//                }
//
//            }
//        }catch (e:InterruptedException) {
//
//        }catch (e:Exception) {
//
//        }

    }

    /**
     * native 回调
     */
    fun rtmpConnected() {
        Log.e(TAG,"rtmpConnected")
        isConnected = true
        countDownLatch.countDown()
    }

    /**
     * native 回调
     */
    fun rtmpConnectedFailure() {
        Log.e(TAG,"rtmpConnectedFailure")
        release()
    }

    override fun stop() {
        isConnected = false
    }

    override fun release() {
        Thread.currentThread().interrupt()
        nativeStop()
    }


    private external fun nativeInit()
    private external fun connect(url: String):Boolean
    private external fun sendData(bytes: ByteArray,size :Int,timeStamp: Long,type:Int)
    external fun nativeSetVideoEncodeInfo(width:Int,height:Int,fps:Int,bitrate:Int)
    external fun nativeSetAudioEncodeInfo(sampleRate:Int,channels:Int,sampleBit:Int = 16)
    private external fun nativeSendNV12Data(NV12bytes: ByteArray, len:Int)
    private external fun nativeSendPCMAudioData(pcmArr: ByteArray, len:Int)
    private external fun nativeStop()
    private external fun nativeRelease()

    companion object {

        init {
            System.loadLibrary("ffplayer")
        }
    }
}