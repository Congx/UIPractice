package com.example.uipractice.anim

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.basepractice.BaseLibActivity

class AnimListActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("多个动画同时执行测试", AnimTestActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
