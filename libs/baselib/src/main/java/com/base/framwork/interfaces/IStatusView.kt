package com.base.framwork.interfaces

/**
 * @date 2020-01-05
 * @Author luffy
 * @description
 */
open interface IStatusView {

    companion object {
        var STATU_MASK           = 0xFF
        var DIALOG               = 0x00
        var STATUS_VIEW_LOADING  = 0x10
        var STATUS_VIEW_EMPTY    = 0x11
        var STATUS_VIEW_EORROR   = 0x13
        var STATUS_VIEW_NETWORK  = 0x15
    }

    fun showLoading(message: String,type: Int)
    fun showNormal()
    fun showError(message: String,type: Int)
}