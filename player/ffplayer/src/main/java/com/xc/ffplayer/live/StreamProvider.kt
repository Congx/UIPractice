package com.xc.ffplayer.live

open interface StreamProvider {

    var dataRecived:((ByteArray:ByteArray,len:Int)->Unit)?

}