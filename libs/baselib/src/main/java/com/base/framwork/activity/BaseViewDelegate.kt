package com.base.framwork.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.base.framwork.interfaces.AbStatusView
import com.base.framwork.interfaces.IStatusView
import com.base.framwork.view.IBaseView

/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
abstract class BaseViewDelegate : IBaseView {

    abstract var statusViewControl:StatusViewControl

    abstract var viewModule:ViewModel?

    abstract fun <T:ViewModel>createViewModel(activity: FragmentActivity, tClass: Class<T>?): ViewModel?

    abstract fun <T:ViewModel>createViewModel(activity: Fragment, tClass: Class<T>?): ViewModel?

    companion object {
        fun <R : ViewModel> create(activity: BaseActivity): BaseViewDelegate {
            return DefaultDelegateImpl(activity)
        }
    }

    inner class StatusViewControl:IStatusView {

        var dialog:IStatusView? = null
        var loaderSir:AbStatusView? = null

        /**
         *  type: 状态类型
         *      0:模态加载，弹窗loading
         *      1:状态View
         */
        override fun showLoading(message: String, type: Int) {
            when(type and IStatusView.STATU_MASK) {
                0 -> dialog?.showLoading(message,type)
                else -> loaderSir?.showLoading(message,type)
            }
        }

        override fun showNormal() {
            dialog?.showNormal()
            loaderSir?.showNormal()
        }

        override fun showError(message: String, type: Int) {
            when(type and IStatusView.STATU_MASK) {
                0 -> dialog?.showError(message,type)
                else -> loaderSir?.showError(message,type)
            }
        }

    }

}
