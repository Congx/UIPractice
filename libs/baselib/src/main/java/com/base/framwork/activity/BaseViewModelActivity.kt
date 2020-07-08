package com.base.framwork.activity

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @date 2019-12-08
 * @Author luffy
 * @description  封装和[com.lp.base.viewmodel.LifecycleViewModel] 交互逻辑
 * 同[com.lp.base.fragment.BaseViewModelFragment]
 */
open class BaseViewModelActivity : BaseActivity(){

    internal var vm:ViewModel? = null

    open fun initViewModel(viewModel: ViewModel?) {
        bindLifecycler(viewModel)
        Log.i("BaseViewModelActivity","initViewModel")
    }

    /**
     * 如果需要，绑定生命周期
     */
    open fun bindLifecycler(viewModel: ViewModel?) {
        viewModel?.let {
            this.vm = viewModel
            if (it is LifecycleObserver) {
                lifecycle.addObserver(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm?.let {
            if (it is LifecycleObserver) {
                lifecycle.removeObserver(it)
            }
        }
    }

    /**
     * hook 下创建流程
     * 主要的目的是在viewModel创建完之后自动初始化
     * 否则 要么写模板方法，破坏了原来的代码结构，要么用户自己初始化，要写多余代码，增加学习和出错成本
     */
    private var hookFactory: DefaultFactoryWrapper? = null
    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        val defaultViewModelProviderFactory = super.getDefaultViewModelProviderFactory()
        if (hookFactory == null) {
            hookFactory = DefaultFactoryWrapper(defaultViewModelProviderFactory)
            return hookFactory!!
        }
        return hookFactory!!
    }

    internal inner class DefaultFactoryWrapper(private var factory: ViewModelProvider.Factory) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val viewModel = factory.create(modelClass)
            initViewModel(viewModel)
            return viewModel
        }

    }
}