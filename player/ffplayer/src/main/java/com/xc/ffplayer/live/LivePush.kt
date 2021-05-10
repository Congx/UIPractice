package com.xc.ffplayer.live

import com.xc.ffplayer.utils.append2File
import java.util.concurrent.LinkedBlockingDeque

open class LivePush : Thread(){

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
        start()
    }

    fun release() {
        isLiveing = false
        close()
    }

    var ssss = true
    override fun run() {
//        if (!connect(url)) return
        connect(url)
        try {
            while (!(currentThread().isInterrupted) && isLiveing) {
                val rtmpPackage = queue.take()
                rtmpPackage?.let {
                    if (ssss) {
                        it.bytes.append2File("liveData2.h264")
                        ssss = false
                    }
                    sendData(it.bytes,it.bytes.size,it.timeStamp)
                }

            }
        }catch (e:InterruptedException) {

        }catch (e:Exception) {

        }

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