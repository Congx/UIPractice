package com.base.custom

import android.os.Bundle
import androidx.lifecycle.Observer
import com.base.custom.BaseViewModel.UILiveData.TYPE.*
import com.base.framwork.activity.AbstractActivity
import com.base.framwork.activity.BaseActivity

/**
 * @date 2020-01-05
 * @Author luffy
 * @description  App相关的Activity基类，不同的app自己拷贝一份
 *
 * 这里 暂时空实现，便于应对基类需要扩展的情况 而不需要扩展[BaseActivity]
 * 作用等于[AbstractActivity]
 *
 * 否则由于基类的一些扩展或者一些改动，必须去更改基本类，影响太大
 */
open class XBaseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = getViewModel()
        if (viewModel is BaseViewModel) {
            // 简单的UI交互，复杂的自己扩展
            viewModel.ui.liveData.observe(this, Observer{
                when(it) {
                    SHOWLOADINGDIALOG -> hindLoadingDialog()
                    HIDELOADINGDIALOG -> hindLoadingDialog()
                    SHOWLOADING       -> showLoading()
                    SHOWCONTENT       -> showNormal()
                    SHOWERROR         -> showError()
                    SHOWNONETWORK     -> showNoNet()
                    FINISH            -> finish()
                }
            })
        }
    }

    /**
     * 显示dialog
     *
     * @param message 消息内容
     * @param type    弹框类型
     */
    @JvmOverloads
    fun showLoadingDialog(message: String = "", type: Int = -1) {

    }

    /**
     * 隐藏dialog
     *
     */
    fun hindLoadingDialog() {}

    /**
     * 显示loading页面
     *
     * @param message 加载信息
     */
    @JvmOverloads
    fun showLoading(message: String = "", type: Int = -1) {}

    /**
     * 空页面
     */
    @JvmOverloads
    fun showEmpty(message: String = "", type: Int = -1) {}

    /**
     * 无网络
     */
    @JvmOverloads
    fun showNoNet(message: String = "", type: Int = -1) {}

    /**
     * 出现错误
     *
     * @param message
     * @param type
     */
    @JvmOverloads
    fun showError(message: String = "", type: Int = -1) {}

    /**
     * 正常展示页面
     */
    fun showNormal() {}

    /**
     * 信息Toast提示
     *
     * @param message 提示信息
     */
    @JvmOverloads
    fun showToast(message: String = "") {}

}
