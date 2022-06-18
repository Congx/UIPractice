package com.example.uipractice.basepractice

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class BaseLibActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("statuView-viewmodule", StatusActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
