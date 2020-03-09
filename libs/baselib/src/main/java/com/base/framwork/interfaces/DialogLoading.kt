package com.base.framwork.interfaces

/**
 * @date 2020-01-05
 * @Author luffy
 * @description
 */
open abstract class DialogLoading:IStatusView {

    override fun showNormal() {
        hideLoading()
    }

    override fun showError(message: String, type: Int) {
        hideLoading()
    }

    override fun showLoading(message: String, type: Int) {
        showLoading()
    }

    abstract fun hideLoading()

    abstract fun showLoading()

}