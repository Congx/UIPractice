package com.xc.ffplayer.live

import java.lang.Exception
import java.util.concurrent.LinkedBlockingDeque

open class LivePush : Thread(){

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
        start()
    }

    fun release() {
        isLiveing = false
    }

    override fun run() {
        connect(url)
        try {
            while (!currentThread().isInterrupted && isLiveing) {
                val rtmpPackage = queue.take()

            }
        }catch (e:InterruptedException) {

        }catch (e:Exception) {

        }

    }


    private external fun connect(url: String)

    companion object {
        init {
            System.loadLibrary("rtmpdump")
        }
    }
}