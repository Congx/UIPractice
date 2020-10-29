package com.example.uipractice.ui.recyclerview

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.recyclerview.itemdivider.RvDividerActivity
import com.example.uipractice.ui.viewPager2.ViewPagerActivity

import java.util.ArrayList

class RecyclerviewBaseActivity : BaseItemListActivity() {


    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("recyclerview基本用法", RvDividerActivity::class.java),
        ItemBean("viewpager2", ViewPagerActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
