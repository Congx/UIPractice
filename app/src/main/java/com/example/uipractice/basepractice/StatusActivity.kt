package com.example.uipractice.basepractice

import android.os.Bundle
import androidx.activity.viewModels
import com.base.framwork.activity.XBaseActivity
import com.example.uipractice.R
import com.lp.base.viewmodel.LifecycleViewModel

class StatusActivity : XBaseActivity() {

    val viewModel:LifecycleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
    }

}
