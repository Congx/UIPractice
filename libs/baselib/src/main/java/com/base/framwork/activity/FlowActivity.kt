package com.base.framwork.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.base.framwork.R
import com.base.framwork.ui.utils.StatusBarUtil
import com.base.framwork.view.ILifeProcessor

/**
 * @date 2020-01-03
 * @Author luffy
 * @description 规范开发的方法 其他的，暂时还没想好
 */
abstract class FlowActivity : AppCompatActivity(),
    ILifeProcessor {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams(intent)
        if (generateIdLayout() > 0) {
            setContentView(generateIdLayout())
        } else if (generateViewLayout() != null) {
            setContentView(generateViewLayout())
        }
        setStatusBar()
        initView()
        initData()
        initEvent()
    }

    /**
     * 状态栏跟随主题变色
     */
    override fun setStatusBar() {
        val typedValue = TypedValue()
        val b =
            theme.resolveAttribute(R.attr.statuBarColor, typedValue, true)
        if (b) {
            val color = typedValue.data
            StatusBarUtil.setColor(this, color)
        }
    }
}