package com.base.custom

import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
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
 * @description app 相关通用的定制的一些东西
 */
abstract class BaseActivity<T : LifyCycleViewModel> : BaseViewActivity<T>() {

    /**
     * 在delegate创建完成之后，做一些默认初始化操作
     */
    override fun afterDedegateCreate(){
        viewDelegate.statusViewControl.dialog = DialogDefault(this)
        viewDelegate.statusViewControl.loaderSir = LoadSirDefault()
    }

    @CallSuper
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
