package com.example.uipractice.ui

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.ivpacage.ImageViewActivity
import com.example.uipractice.ui.keybord.KeybordActivity
import com.example.uipractice.ui.nestedscroll.NestedBaseActivity
import com.example.uipractice.ui.recyclerview.RecyclerviewBaseActivity
import com.example.uipractice.ui.viewPager2.ViewPager2Activity

class UIActivity : BaseItemListActivity() {
    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("imageView相关", ImageViewActivity::class.java),
        ItemBean("嵌套滑动", NestedBaseActivity::class.java),
        ItemBean("recyclerview", RecyclerviewBaseActivity::class.java),
        ItemBean("软键盘", KeybordActivity::class.java),
        ItemBean("颜色-主题-属性", AttrStyleActivity::class.java),
        ItemBean("ViewPager-新懒加载", ViewPager2Activity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
