package com.base.framwork.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
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
     * 我们现在的UI设计绝大多数都是状态栏透明，然后View窗口沉浸式，所以这里简单处理
     *
     */
    open fun setStatusBar() {
        // 直接从 attr中获取样式
        val statubarColorType = TypedValue()
        val fullScreenTyped = TypedValue()
        val statubarColor = theme.resolveAttribute(R.attr.statubarColor, statubarColorType, true)
        val fullScreen = theme.resolveAttribute(R.attr.fullScreen, fullScreenTyped, true)

        // 先判断主题中是否后全屏的样式,默认全屏
        var isfullScreen = true
        // 是否有沉浸式
        if (fullScreen) {
            // 状态栏透明
            if (fullScreenTyped.type == TypedValue.TYPE_INT_BOOLEAN) {
                isfullScreen = fullScreenTyped.data != 0
            }else {
                isfullScreen = false
            }
        }

        if (statubarColor) {
            val color = statubarColorType.data
            // 全屏
            if (isfullScreen) {
                StatusBarUtil.setTranslucentWithFitSystemUI(this)
                // 设置状态栏字体颜色
                if (isLightColor(color)) {
                    StatusBarUtil.setLightMode(this)
                }else {
                    StatusBarUtil.setDarkMode(this)
                }
            }else {
                // 设置状态栏颜色
                StatusBarUtil.setColor(this, color,0)
                // 设置状态栏字体颜色
                if (isLightColor(color)) {
                    StatusBarUtil.setLightModeWithNoFitSystem(this)
                }else {
                    StatusBarUtil.setDarkModeWithNoFitSystem(this)
                }
            }

        }else {
            if (fullScreen) {
                StatusBarUtil.setTranslucentWithFitSystemUI(this)
            }
        }
    }

    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     */
    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }

}