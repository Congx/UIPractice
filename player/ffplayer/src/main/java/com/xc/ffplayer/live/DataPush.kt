package com.xc.ffplayer.live

import android.util.Log
import java.util.concurrent.LinkedBlockingDeque

open class DataPush : Runnable,Releaseable {

    var TAG = "LivePush"

    private lateinit var url: String
    @Volatile
    var isLiveing = false
    var queue = LinkedBlockingDeque<RTMPPackage>(100)

    fun addData(bytes: ByteArray, timeStamp: Long,type:Int) {
        if (!isLiveing) return
        if (!queue.offer(RTMPPackage(bytes,timeStamp,type))) {
            queue.pollLast()
        }
    }

    fun addData(packge:RTMPPackage) {
        if (!isLiveing) {
            return
        }
        if (!queue.offer(packge)) {
            queue.pollLast()
        }

    }

    fun startPush(url: String) {
        Log.d(TAG,"startPush")
        isLiveing = true
        this.url = url
        LiveTaskManager.execute(this)
    }

    override fun run() {
//        if (!connect(url)) return
        connect(url)
        try {
            while (!(Thread.currentThread().isInterrupted) && isLiveing) {
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
        isLiveing = false
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