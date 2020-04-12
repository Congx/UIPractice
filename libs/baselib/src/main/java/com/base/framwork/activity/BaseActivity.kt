package com.base.framwork.activity

import android.os.Bundle
import android.util.TypedValue
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.base.framwork.R
import com.base.framwork.ui.utils.StatusBarUtil
import com.base.framwork.view.IBaseView

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 部分通用的实现，无侵入,这里的扩展不应该影响上层
 */
open class BaseActivity : AbstractActivity(){

    private var viewModel:ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
    }

    /**
     * 初始化一些基础的东西
     */
    open fun initBase() {
        setStatusBar()
        bindLifeIfNeed()
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
        }
    }

    /**
     * 没有使用ViewModel 返回空，可以忽略
     */
    fun getViewModel():ViewModel? {
        return viewModel
    }

    /**
     * 如果需要，绑定生命周期
     */
    open fun bindLifeIfNeed() {
        viewModel = createViewModel()
        viewModel?.let {
            if (it is LifecycleObserver) {
                lifecycle.addObserver(viewModel as LifecycleObserver)
            }
        }
    }

    //------------ 以下的功能需要就用，不需要不管

    /**
     * 创建viewmodel 需要才重写，不需要不重写
     * @return
     */
    open fun createViewModel(): ViewModel? {
        return null
    }


    /**
     * 显示dialog
     *
     * @param message 消息内容
     * @param type    弹框类型
     */
    @JvmOverloads
    fun showLoadingDialog(message: String = "", type: Int = -1) {

    }

    /**
     * 隐藏dialog
     *
     */
    fun hindLoadingDialog() {}

    /**
     * 显示loading页面
     *
     * @param message 加载信息
     */
    @JvmOverloads
    fun showLoading(message: String = "", type: Int = -1) {}

    /**
     * 空页面
     */
    @JvmOverloads
    fun showEmpty(message: String = "", type: Int = -1) {}

    /**
     * 无网络
     */
    @JvmOverloads
    fun showNoNet(message: String = "", type: Int = -1) {}

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    @JvmOverloads
    fun showError(message: String = "", type: Int = -1) {}

    /**
     * 正常展示页面
     */
    fun showNormal() {}

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    @JvmOverloads
    fun showToast(message: String = "") {}
}