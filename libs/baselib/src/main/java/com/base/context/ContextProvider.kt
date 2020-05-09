package com.base.context

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @date 26/3/2020
 * @Author luffy
 * @description
 */
object ContextProvider {

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var mContext: Context? = null

    /**
     * 获取上下文
     */
    fun getContext(): Context? {
        if(mContext == null) {
            mContext = ApplicationContextProvider.mContext
        }
        return mContext
    }

    fun getApplication(): Application? {
        return mContext?.applicationContext as Application
    }
}