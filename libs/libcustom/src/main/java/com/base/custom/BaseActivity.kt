package com.base.custom

import com.base.framwork.activity.BaseViewActivity
import com.base.framwork.activity.BaseViewDelegate
import com.base.framwork.interfaces.DialogLoading
import com.base.framwork.interfaces.IStatusView
import com.base.framwork.p.LifyCycleViewModel

/**
 * @date 2020-01-05
 * @Author luffy
 * @description app 相关的定制的一些东西
 */
abstract class BaseActivity<T : LifyCycleViewModel> : BaseViewActivity<T>() {


    override fun getViewDelegate(): BaseViewDelegate<T> {
        super.getViewDelegate()
        viewDelegate.statusViewControl.dialog = {
            object : DialogLoading(this@BaseActivity) {

                override fun hideLoading() {

                }

                override fun showLoading() {

                }

            }
        }

        viewDelegate.statusViewControl.loaderSir = {
            object : IStatusView {
                override fun showLoading(message: String, type: Int) {

                }

                override fun showNormal() {

                }

                override fun showError(message: String, type: Int) {

                }


            }
        }

        return viewDelegate
    }


}
