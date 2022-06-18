package com.example.uipractice.architecture

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class ArchitectureActivity : BaseItemListActivity<Activity>() {
    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("liveData", LiveActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
