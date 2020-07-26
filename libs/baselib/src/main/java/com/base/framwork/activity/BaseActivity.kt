package com.base.framwork.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.base.framwork.R
import com.base.framwork.ui.utils.StatusBarUtil

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 部分通用的实现，无侵入,这里的扩展不应该影响上层
 */
open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
    }

    /**
     * 初始化一些基础的东西
     */
    open fun initBase() {
        setStatusBar()
    }

    /**
     * 状态栏跟随主题变色
     */
    open fun setStatusBar() {
        val typedValue = TypedValue()
        val b =
            theme.resolveAttribute(R.attr.statuBarColor, typedValue, true)
        if (b) {
            val color = typedValue.data
            StatusBarUtil.setColor(this, color)
        }else {
            StatusBarUtil.setTranslucentForImageView(this,null)
        }
    }

}