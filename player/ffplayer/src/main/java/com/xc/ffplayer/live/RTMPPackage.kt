package com.xc.ffplayer.live

const val RTMP_PKG_VIDEO = 0 // type
const val RTMP_PKG_AUDIO = 1
const val RTMP_PKG_AUDIO_HEAD = 2

data class RTMPPackage(var bytes: ByteArray, var timeStamp: Long, var type: Int)