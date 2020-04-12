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

}
