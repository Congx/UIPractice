package com.example.uipractice.net

import android.os.Bundle
import androidx.activity.viewModels
import com.base.framwork.activity.XBaseActivity
import com.base.net.RetrofitServer
import com.base.rxjavalib.iOtransformer
import com.base.rxjavalib.transformer
import com.example.uipractice.R
import com.example.uipractice.api.AppApi
import com.example.uipractice.basepractice.viewmodule.StatusViewModule

class RetrofitActivity : XBaseActivity() {

    val viewmodle by viewModels<StatusViewModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
//        viewmodle.loaddata()
        RetrofitServer.defaultRetrofitClient.create(AppApi::class.java)
            .getPublishList()
            .iOtransformer()
            .subscribe {

        }
    }
}
