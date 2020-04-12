package com.example.uipractice.basepractice

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.base.custom.XBaseActivity
import com.base.framwork.p.LifyCycleViewModel
import com.example.uipractice.R

class StatusActivity : XBaseActivity() {

    var viewModel:LifyCycleViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
    }

    override fun createViewModel(): ViewModel? {
        viewModel = ViewModelProviders.of(this).get(LifyCycleViewModel::class.java)
        return viewModel
    }
}
