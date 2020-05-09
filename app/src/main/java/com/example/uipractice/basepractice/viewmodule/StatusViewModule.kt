package com.example.uipractice.basepractice.viewmodule

import android.util.Log
import com.base.custom.BaseViewModel
import com.base.rxjavalib.bindLifecycle
import com.example.uipractice.api.ApiRepository

/**
 * @date 2020-01-10
 * @Author luffy
 * @description
 */
class StatusViewModule : BaseViewModel() {

    override fun onCreate() {
        super.onCreate()
        ApiRepository.getPublishList()
            .bindLifecycle(this)
            .subscribe({ list->
                Log.e("BackPressActivity",list.size.toString())
            },{ e->
                e.printStackTrace()
            })
    }
}