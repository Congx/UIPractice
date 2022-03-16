package com.xc.ffplayer.utils

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Environment
import android.util.Log
import com.xc.ffplayer.MyApplication
import okhttp3.internal.and
import java.io.*

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
//    File(path).writeBytes(bytes)
}

fun byte2String(byte: Byte): String? {
    val HEX_CHAR_TABLE = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )
    val sb = StringBuilder()
    sb.append(HEX_CHAR_TABLE[byte and 0xf0 shr 4])
    sb.append(HEX_CHAR_TABLE[byte and 0x0f])
    return sb.toString()
}

fun writeContent(array: ByteArray): String? {
    val HEX_CHAR_TABLE = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )
    val sb = StringBuilder()
    for (b in array) {
        sb.append(HEX_CHAR_TABLE[b and 0xf0 shr 4])
        sb.append(HEX_CHAR_TABLE[b and 0x0f])
    }
//    var writer: FileWriter? = null
//    try {
//        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//        writer = FileWriter(
//            Environment.getExternalStorageDirectory().toString() + "/codecH265.txt", true
//        )
//        writer.write(sb.toString())
//        writer.write("\n")
//    } catch (e: IOException) {
//        e.printStackTrace()
//    } finally {
//        try {
//            writer?.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
    return sb.toString()
}

fun ByteArray.append2File(fileName:String) {
    var path = MyApplication.application.getExternalFilesDir("output")?.absolutePath + File.separator + fileName
    val file = File(path)
    file.appendBytes(this)
}