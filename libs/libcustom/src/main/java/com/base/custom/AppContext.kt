package com.base.custom

import android.content.Context
import java.lang.IllegalStateException

/**
 * @date 2020-01-10
 * @Author luffy
 * @description  基础库用到的 上下文
 */
object AppContext {

    private var appContext:Context? = null

    fun init(context: Context?) {
        appContext = context
    }

    fun getContext():Context {
        if (appContext == null)
            throw IllegalStateException("AppContext don't init")
        else return appContext!!
    }
}