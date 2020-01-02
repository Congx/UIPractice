package com.example.uipractice.ui.ivpacage

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import java.util.*

class ImageViewActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("imageView hint 练习", ImageViewHintActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
