package com.example.uipractice.ui.nestedscroll

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import java.util.*


class NestedBaseActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("coordinglayout基本用法", NestedBaseUsedActivity::class.java),
        ItemBean("自定义behavior", NestedBehaviorActivity::class.java),
        ItemBean("appbar behavior", AppbarBehaviorActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }

}
