package com.example.uipractice.ui.viewPager2

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class ViewPagerActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("ViewPager1", ViewPager2Activity::class.java),
        ItemBean("ViewPager2", ViewPager2Activity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
