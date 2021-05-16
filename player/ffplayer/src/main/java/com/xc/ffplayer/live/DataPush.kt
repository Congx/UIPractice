package com.xc.ffplayer.live

import android.util.Log
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingDeque

open class DataPush(var countDownLatch: CountDownLatch) : Runnable,Releaseable {

    var TAG = "LivePush"

    private lateinit var url: String
    @Volatile
    var connected = false

    var queue = LinkedBlockingDeque<RTMPPackage>(100)

    fun addData(bytes: ByteArray, timeStamp: Long,type:Int) {
        if (!connected) return
        if (!queue.offer(RTMPPackage(bytes,timeStamp,type))) {
            queue.clear()
        }
    }

    fun addData(packge:RTMPPackage) {
//        Log.e(TAG,"add RTMPPackage")
        if (!connected) {
            Log.e(TAG,"add no connect")
            return
        }
        if (!queue.offer(packge)) {
            Log.e(TAG,"RTMPPackage 丢弃")
            queue.clear()
        }

    }

    fun startPush(url: String) {
        this.url = url
        LiveTaskManager.execute(this)
    }

    override fun run() {
        if (connect(url)){
            Log.e(TAG,"rtmp  链接成功")
            connected = true
            countDownLatch.countDown()
        }else {
            Log.e(TAG,"rtmp  链接失败")
            connected = false
        }
        try {
            while (!(Thread.currentThread().isInterrupted) && connected) {
                val rtmpPackage = queue.take()
                rtmpPackage?.let {
//                    it.bytes.append2File("livedata.h264")
                    sendData(it.bytes,it.bytes.size,it.timeStamp,it.type)
                }

            }
        }catch (e:InterruptedException) {

        }catch (e:Exception) {

        }

    }

    override fun stop() {
        connected = false
    }

    override fun release() {
        Thread.currentThread().interrupt()
        close()
    }


    private external fun sendData(bytes: ByteArray,size :Int,timeStamp: Long,type:Int)
    private external fun connect(url: String):Boolean
    private external fun close()

    companion object {

        init {
            System.loadLibrary("rtmpdump")
        }
    }
}