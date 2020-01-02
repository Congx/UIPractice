package com.example.uipractice.architecture

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class ArchitectureActivity : BaseItemListActivity() {
    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("liveData", LiveActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
