package com.example.uipractice.ui.nestedscroll

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import java.util.*


class NestedBaseActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean(
            "coordinglayout基本用法",
            NestedBaseUsedActivity::class.java
        ),
        ItemBean("自定义behavior", NestedBehaviorActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
