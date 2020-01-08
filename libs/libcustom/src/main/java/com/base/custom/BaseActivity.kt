package com.base.custom

import android.view.View
import android.widget.Toast
import com.base.custom.callback.ErrorCallback
import com.base.custom.callback.LoadingCallback
import com.base.framwork.activity.BaseViewActivity
import com.base.framwork.activity.BaseViewDelegate
import com.base.framwork.interfaces.AbStatusView
import com.base.framwork.interfaces.DialogLoading
import com.base.framwork.p.LifyCycleViewModel
import com.base.framwork.ui.statusview.core.LoadSir

/**
 * @date 2020-01-05
 * @Author luffy
 * @description app 相关的定制的一些东西
 */
abstract class BaseActivity<T : LifyCycleViewModel> : BaseViewActivity<T>() {


    override fun getViewDelegate(): BaseViewDelegate<T> {
        super.getViewDelegate()

        viewDelegate.statusViewControl.dialog = object : DialogLoading() {

            override fun hideLoading() {
                Toast.makeText(this@BaseActivity, "hideLoading", Toast.LENGTH_LONG).show()
            }

            override fun showLoading() {
                Toast.makeText(this@BaseActivity, "showLoading", Toast.LENGTH_LONG).show()
            }

        }


        viewDelegate.statusViewControl.loaderSir = object : AbStatusView() {

            override fun register(target: View, callback: (View) -> Unit) {
                default = LoadSir.getDefault().register(target,callback)
            }

            override fun showLoading(message: String, type: Int) {
                default?.showCallback(LoadingCallback::class.java)

            }

            override fun showNormal() {
                default?.showSuccess()
            }

            override fun showError(message: String, type: Int) {
                default?.showCallback(ErrorCallback::class.java)
            }

        }

        return viewDelegate
    }

    fun retry(view: View) {
        Toast.makeText(this@BaseActivity, "retry", Toast.LENGTH_LONG).show()
        viewDelegate.statusViewControl.loaderSir?.showLoading("",1)
    }

    // 当然也可以自己重写方法，定制每个activity
//    override fun showError() {
//        super.showError()
//    }

    fun regist(target: View) {
        viewDelegate.statusViewControl.loaderSir?.register(target, this::retry)
    }


}
