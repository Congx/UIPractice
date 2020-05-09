package com.base.framwork.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*

/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
open class DefaultDelegateImpl(var activity: BaseActivity) : BaseViewDelegate() {

    override var viewModule: ViewModel? = null
    override var statusViewControl:StatusViewControl = StatusViewControl()

    override fun showLoadingDialog(message: String, type: Int) {
        statusViewControl
    }

    override fun hindLoadingDialog() {
    }

    override fun showLoading(message: String, type: Int) {

    }

    override fun showEmpty(message: String, type: Int) {

    }

    override fun showNoNet(message: String, type: Int) {
    }

    override fun showError(message: String, type: Int) {

    }

    override fun showNormal() {

    }

    override fun showToast(message: String) {

    }

    override fun <T:ViewModel>createViewModel(activity: FragmentActivity, tClass: Class<T>?): ViewModel? {
        if (tClass == null) return null
        viewModule = ViewModelProvider(activity).get(tClass)
        if (viewModule is LifecycleObserver) {
            var owner = viewModule as LifecycleObserver
            activity.lifecycle.addObserver(owner)
        }
        return viewModule as T
    }

    override fun <T:ViewModel>createViewModel(fragment: Fragment, tClass: Class<T>?): ViewModel? {
        if (tClass == null) return null
        viewModule = ViewModelProvider(fragment).get(tClass)
        if (viewModule is LifecycleObserver) {
            var owner = viewModule as LifecycleObserver
            activity.lifecycle.addObserver(owner)
        }
        return viewModule
    }


}
