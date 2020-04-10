package com.example.uipractice.basepractice

import com.base.custom.BaseActivity
import com.base.framwork.p.LifyCycleViewModel
import com.example.uipractice.R
import com.example.uipractice.basepractice.viewmodule.StatusViewModule
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : BaseActivity<StatusViewModule>() {


    override fun generateIdLayout(): Int {
        return R.layout.activity_status
    }

    override fun getViewModelClass(): Class<StatusViewModule> {
        return StatusViewModule::class.java
    }

    override fun initView() {
        regist(content)
        viewDelegate.statusViewControl.loaderSir?.showError("",1)
    }

    override fun initEvent() {
        btnRegist.setOnClickListener {
            lifecycle.addObserver(LifyCycleViewModel())
        }
    }

    override fun initData() {

    }

}
