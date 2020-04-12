package com.base.framwork.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.base.framwork.activity.BaseActivity

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 同[BaseActivity]
 */
open class BaseFragment : LazyFragment(){

    var rootView: View? = null
    private var viewModel:ViewModel? = null

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
}