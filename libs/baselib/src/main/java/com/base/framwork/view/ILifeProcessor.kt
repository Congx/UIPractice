package com.base.framwork.view

import android.content.Intent
import android.view.View


/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
@Deprecated("弃用")
interface ILifeProcessor {

    /**
     * 初始化一intent参数
     */
    fun initParams(intent: Intent)

    /**
     * 初始化状态栏
     */
    fun setStatusBar()

    /**
     * 布局id
     * @return layout id
     */
    fun generateIdLayout(): Int?

    /**
     * 布局view
     * @return layout view
     */
    fun generateViewLayout(): View?

    /**
     * 初始化Views
     */
    fun initView()

    /**
     * 初始化Listener
     */
    fun initEvent()

    /**
     * 初始化数据
     */
    fun initData()

}