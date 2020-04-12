package com.base.framwork.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.base.framwork.view.IBaseView
import com.base.framwork.activity.BaseActivity

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 同[BaseActivity]
 */
open class BaseFragment : LazyFragment(), IBaseView {

    var rootView: View? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
    }

    @CallSuper
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    /**
     * 初始化一些基础的东西
     */
    open fun initBase() {
        bindLifeIfNeed()
    }

    /**
     * 如果需要，绑定生命周期
     */
    open fun bindLifeIfNeed() {
        var viewModel = createViewModel()
        viewModel?.let {
            if (it is LifecycleObserver) {
                lifecycle.addObserver(viewModel as LifecycleObserver)
            }
        }
    }

    /**
     * 提供给java 用，尽早用kotlin吧
     * @param id
     * @param <T>
     * @return
    </T> */
    open fun <T : View> findViewById(@IdRes id: Int): T? {
        return rootView?.findViewById(id)
    }

    /**
     * 如果不用ViewMode 直接返回null
     * @return
     */
    fun createViewModel(): ViewModel? {
        return null
    }

    /**
     * 显示dialog
     *
     * @param message 消息内容
     * @param type    弹框类型
     */
    @JvmOverloads
    override fun showLoadingDialog(message: String, type: Int) {

    }

    /**
     * 隐藏dialog
     *
     */
    override fun hindLoadingDialog() {}

    /**
     * 显示loading页面
     *
     * @param message 加载信息
     */
    override fun showLoading(message: String, type: Int) {}

    /**
     * 空页面
     */
    override fun showEmpty(message: String, type: Int) {}

    /**
     * 无网络
     */
    override fun showNoNet(message: String, type: Int) {}

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    override fun showError(message: String, type: Int) {}

    /**
     * 正常展示页面
     */
    override fun showNormal() {}

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    override fun showToast(message: String) {}
}