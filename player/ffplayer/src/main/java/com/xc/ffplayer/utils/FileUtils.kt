package com.xc.ffplayer.utils

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun saveNv21(context:Context,byteArray: ByteArray,width:Int,height:Int) {
    var yuvImage = YuvImage(
        byteArray,
        ImageFormat.NV21,
        width,
        height,
        null
    )
    var bos = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, bos)
    val toByteArray = bos.toByteArray()
    var path = context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "inputNV21.jpg"
    var os = FileOutputStream(File(path))
    os.write(toByteArray)
    os.flush()
    os.close()
//    var path = context.getExternalFilesDir("imgs")?.absolutePath + File.separator + "inputNV21.jpg"
//    File(path).writeBytes(byteArray)
}