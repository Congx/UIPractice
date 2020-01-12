package com.example.uipractice.basepractice

import com.base.custom.BaseActivity
import com.example.uipractice.R
import com.example.uipractice.basepractice.viewmodule.StatusViewModule

class StatusActivity : BaseActivity<StatusViewModule>() {


    override fun generateIdLayout(): Int {
        return R.layout.activity_status
    }

    override fun getViewModelClass(): Class<StatusViewModule> {
        return StatusViewModule::class.java
    }

    override fun initView() {

    }

    override fun initEvent() {

    }

    override fun initData() {

    }

}
