package com.base.framwork.view

/**
 * @date 2020-01-03
 * @Author luffy
 * @description
 */
interface IBaseView {
    /**
     * 显示dialog默认展示type 0
     *
     * @param message 消息内容
     */
    fun showDialog(message: String)

    /**
     * 显示dialog
     *
     * @param message 消息内容
     * @param type    弹框类型
     */
    fun showDialog(message: String, type: Int)

    /**
     * 显示loading页面
     */
    fun showLoading()

    /**
     * 显示loading页面
     *
     * @param message 加载信息
     */
    fun showLoading(message: String?)

    /**
     * 隐藏loading页面
     */
    fun hideLoading()

    /**
     * 正常展示页面
     */
    fun showNormal()

    /**
     * 空页面
     */
    fun showEmpty()

    /**
     * 出现错误默认展示type 0
     *
     * @param message
     */
    fun showError(message: String)

    fun showError()

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    fun showError(message: String, type: Int)

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    fun showToast(message: String)

    /**
     * 跳转到登录界面
     */
    fun gotoLoginActivity()

}