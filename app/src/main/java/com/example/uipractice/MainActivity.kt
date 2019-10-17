package com.example.uipractice

import com.example.uipractice.ivpacage.ImageViewActivity
import com.example.uipractice.nestedscroll.NestedBaseActivity

class MainActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("imageView相关",ImageViewActivity::class.java),
        ItemBean("嵌套滑动", NestedBaseActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
