package com.xc.ffplayer.ffplayer

import android.view.Surface
import androidx.annotation.IntDef
import java.lang.annotation.ElementType
import java.lang.annotation.Target

const val MUTE_RIGHT = 0 // 右声道
const val MUTE_LEFT = 1  // 左声道
const val MUTE_STEREO = 2 // 立体声

interface Player {

    var playerListener:IPlayerListener?

    fun setSource(source: String)

    fun prepare()

    fun start(surface: Surface)

    fun pause()

    fun resume()

    fun stop()

    fun seek(progress: Int)

    fun setVolume(volume: Int)

    fun setMute(@IMute mute: Int)
}

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(ElementType.FIELD)
@IntDef(value = [MUTE_RIGHT, MUTE_LEFT, MUTE_STEREO])
annotation class IMute


interface IPlayerListener {
    fun onPrepared()
    fun onLoad(load: Boolean)
    fun onCurrentTime(currentTime: Int, totalTime: Int)
    fun onError(code: Int, msg: String?)
    fun onPause(pause: Boolean)
    fun onDbValue(db: Int)
    fun onComplete()
    fun onNext(): String?
}