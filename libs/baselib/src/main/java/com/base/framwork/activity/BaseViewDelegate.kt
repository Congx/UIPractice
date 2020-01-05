package com.base.framwork.activity

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.base.framwork.interfaces.DialogLoading

import com.base.framwork.interfaces.IStatusView
import com.base.framwork.p.LifyCycleViewModel
import com.base.framwork.view.IBaseView

/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
abstract class BaseViewDelegate<T : LifyCycleViewModel> : IBaseView {

    abstract var statusViewControl:StatusViewControl

    abstract var viewModule:T

//    abstract fun getViewModel(): T
//
//    abstract fun getStatusControl(): StatusViewControl

    abstract fun createViewModel(activity: FragmentActivity, tClass: Class<T>): T

    abstract fun createViewModel(activity: Fragment, tClass: Class<T>): T

    companion object {

        fun <R : LifyCycleViewModel> create(activity: BaseViewActivity<R>): BaseViewDelegate<R> {
            return DefaultDelegateImpl<R>(activity)
        }
    }

    inner class StatusViewControl(var activity: Activity):IStatusView {

        lateinit var dialog:()-> DialogLoading?
        lateinit var loaderSir:()-> IStatusView?

        /**
         *  type: 状态类型
         *      0:模态加载，弹窗loading
         *      1:状态View
         */
        override fun showLoading(message: String, type: Int) {
            when(type and IStatusView.STATU_MASK) {
                0 -> dialog()?.showLoading()
                else -> loaderSir()?.showLoading(message,type)
            }
        }

        override fun showNormal() {
            dialog()?.showNormal()
            loaderSir()?.showNormal()
        }

        override fun showError(message: String, type: Int) {
            when(type and IStatusView.STATU_MASK) {
                0 -> dialog()?.showError(message,type)
                else -> loaderSir()?.showError(message,type)
            }
        }

    }

}
