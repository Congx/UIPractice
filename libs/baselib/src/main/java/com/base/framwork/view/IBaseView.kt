package com.base.framwork.view

/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
interface IBaseView {

    /**
     * 显示dialog
     *
     * @param message 消息内容
     * @param type    弹框类型
     */
    fun showLoadingDialog(message: String = "", type: Int = -1)

    /**
     * 隐藏dialog
     *
     */
    fun hindLoadingDialog()

    /**
     * 显示loading页面
     *
     * @param message 加载信息
     */
    fun showLoading(message: String = "", type: Int = -1)

    /**
     * 空页面
     */
    fun showEmpty(message: String = "", type: Int = -1)

    /**
     * 无网络
     */
    fun showNoNet(message: String = "", type: Int = -1)

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    fun showError(message: String = "", type: Int = -1)

    /**
     * 正常展示页面
     */
    fun showNormal()

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    fun showToast(message: String = "")

}