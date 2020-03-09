package com.example.uipractice.basepractice

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class BaseLibActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("statuView-viewmodule", StatusActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
