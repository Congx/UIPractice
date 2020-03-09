package com.base.custom

import android.view.View
import com.base.custom.callback.ErrorCallback
import com.base.custom.callback.LoadingCallback
import com.base.framwork.interfaces.AbStatusView
import com.base.framwork.ui.statusview.core.LoadSir

/**
 * @date 2020-01-10
 * @Author luffy
 * @description
 */
class LoadSirDefault : AbStatusView() {

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