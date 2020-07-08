package com.base.framwork.activity

import android.os.Message
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.base.framwork.p.BaseViewModel

/**
 * @date 2019-12-08
 * @Author luffy
 * 0、封装和UI相关逻辑，不需要，自己继承子类或者其他BaseActivity
 * 1、封装和BaseViewModel UI交互的逻辑主要是 showloadingdialog,还有加载状态等
 * 2、封装其他UI控制逻辑，如状态栏等
 */
open class XBaseActivity : BaseViewModelActivity() {

    //------- XBaseFragment 自己调用UI控制逻辑start -------

    open fun showLoading() {
        var message = Message()
        message.what = BaseViewModel.UILiveData.TYPE.SHOWLOADING
        showLoading(message)
    }

    open fun showContent() {
        var message = Message()
        message.what = BaseViewModel.UILiveData.TYPE.SHOWCONTENT
        showContent(message)
    }

    open fun showError() {
        var message = Message()
        message.what = BaseViewModel.UILiveData.TYPE.SHOWERROR
        showError(message)
    }

    open fun showEmpty(messageFactory: (() -> Message)? = null) {
        var message =  Message()
        message.what = BaseViewModel.UILiveData.TYPE.SHOWEMPTY
        showEmpty(message)
    }

    open fun showNoNetwork() {
        var message = Message()
        message.what = BaseViewModel.UILiveData.TYPE.SHOWNONETWORK
        showNoNet(message)
    }

    open fun showToast(content:String?,type: Int = 0) {
        var message = Message()
        message.what = BaseViewModel.UILiveData.TYPE.TOAST
        message.obj = content
        message.arg1 = type
        showToast(message)
    }
    //------- XBaseFragment 自己调用UI控制逻辑end ------


    //------- 和BaseViewModel的交互 start -------

    override fun initViewModel(viewModel: ViewModel?) {
        super.initViewModel(viewModel)
        viewModel?.let {
            initUI(it)
        }
    }

    /**
     * 和[BaseViewModel] 分装的一些UI交互，如果没有使用[BaseViewModel]
     * 一下的UI交互功能无法使用，但是对正常的功能无影响
     */
    open fun initUI(viewModel: ViewModel) {
        // 重新赋值下，这里可能是上层调用
        if (viewModel is BaseViewModel) {
            // 简单的UI交互，复杂的自己扩展
            viewModel.ui?.statusLiveData?.observe(this, Observer {
                when (it.what) {
                    BaseViewModel.UILiveData.TYPE.SHOWLOADINGDIALOG -> TODO()
                    BaseViewModel.UILiveData.TYPE.HIDELOADINGDIALOG -> TODO()
                    BaseViewModel.UILiveData.TYPE.SHOWLOADING -> showLoading(it)
                    BaseViewModel.UILiveData.TYPE.SHOWCONTENT -> showContent(it)
                    BaseViewModel.UILiveData.TYPE.SHOWEMPTY -> showEmpty(it)
                    BaseViewModel.UILiveData.TYPE.SHOWERROR -> showError(it)
                    BaseViewModel.UILiveData.TYPE.SHOWNONETWORK -> showNoNet(it)
                    BaseViewModel.UILiveData.TYPE.BACKPRESS -> backPressed(it)
                    BaseViewModel.UILiveData.TYPE.FINISH -> finish()
                    BaseViewModel.UILiveData.TYPE.TOAST -> showToast(it)
                    else -> onCustomMessage(it)
                }
            })
        }
    }

    /**
     * 显示loading页面 一般为 [com.android.base.list.StatusViewLayout]
     * 下同
     * @param message 加载信息
     */
    open fun showLoading(message: Message) {
//        getStatusViewLayout()?.showLoading()
    }

    /**
     * 空页面
     */
    open fun showEmpty(message: Message) {
//        getStatusViewLayout()?.showEmpty("")
    }

    /**
     * 无网络
     */
    open fun showNoNet(message: Message) {
//        getStatusViewLayout()?.showNetWork()
    }

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    open fun showError(message: Message) {
//        getStatusViewLayout()?.showError()
    }

    /**
     * 正常展示页面
     */
    open fun showContent(message: Message) {
//        getStatusViewLayout()?.showContent()
    }

    /**
     * 返回键按下
     */
    open fun backPressed(it: Message) {
        onBackPressed()
    }

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    open fun showToast(message: Message) {
//        ToastAlertUtil.showByType(this, message.arg1, (message.obj ?: "").toString())
    }

    /**
     * 自定义消息，出了上述的消息外需要的自定义消息
     * 在[BaseViewModel.UILiveData.customMessage] 方法调用发送的消息的话，在这里接收
     * @param message
     */
    open fun onCustomMessage(message: Message) {

    }

    /**
     * 重写这个方法 获取statusView
     */
    open fun getStatusViewLayout(): View? {
//        return findViewById(R.id.statusViewLayout)
        return null
    }

    //------- 和BaseViewModel的交互 end -------

}