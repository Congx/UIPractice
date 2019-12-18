package com.example.uipractice

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ivpacage.ImageViewActivity
import com.example.uipractice.keybord.KeybordActivity
import com.example.uipractice.nestedscroll.NestedBaseActivity
import com.example.uipractice.recyclerview.RecyclerviewBaseActivity

class MainActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("imageView相关", ImageViewActivity::class.java),
        ItemBean("嵌套滑动", NestedBaseActivity::class.java),
        ItemBean("recyclerview", RecyclerviewBaseActivity::class.java),
        ItemBean("软键盘", KeybordActivity::class.java),
        ItemBean("颜色-主题-属性", AttrStyleActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
