package com.xc.ffplayer.live

import com.xc.ffplayer.utils.append2File
import java.util.concurrent.LinkedBlockingDeque

open class DataPush : Runnable,Releaseable {

    var TAG = "LivePush"

    private lateinit var url: String
    var isLiveing = false
    var queue = LinkedBlockingDeque<RTMPPackage>(100)

    fun addData(bytes: ByteArray, timeStamp: Long) {
        if (!isLiveing) return
        if (!queue.offer(RTMPPackage(bytes,timeStamp))) {
            queue.pollLast()
        }

    }

    fun startPush(url: String) {
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
                    it.bytes.append2File("livedata.h264")
                    sendData(it.bytes,it.bytes.size,it.timeStamp)
                }

            }
        }catch (e:InterruptedException) {

        }catch (e:Exception) {

        }

    }

    override fun release() {
        isLiveing = false
        Thread.currentThread().interrupt()
        close()
    }


    private external fun sendData(bytes: ByteArray,size :Int,timeStamp: Long)
    private external fun connect(url: String):Boolean
    private external fun close()

    companion object {
        init {
            System.loadLibrary("rtmpdump")
        }
    }
}