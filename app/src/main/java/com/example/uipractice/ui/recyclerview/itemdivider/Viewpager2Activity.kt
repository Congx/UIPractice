package com.example.uipractice.ui.recyclerview.itemdivider

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.base.utils.toPX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.uipractice.R
import com.example.uipractice.ui.banner2.IndicatorView
import kotlinx.android.synthetic.main.activity_viewpager2.*

class Viewpager2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager2)

        val myAdapter = MyAdapter()
        myAdapter.setNewInstance(mutableListOf(0,1,1,1))
        viewPager2.adapter = myAdapter

        val compositePageTransformer = CompositePageTransformer()
        val marginPageTransformer = MarginPageTransformer(10)
        compositePageTransformer.addTransformer(marginPageTransformer)
        viewPager2.setPageTransformer(compositePageTransformer)



        (viewPager2.getChildAt(0) as ViewGroup).setPadding(0,0,200.toPX(),0)
        (viewPager2.getChildAt(0) as ViewGroup).clipChildren = false
        (viewPager2.getChildAt(0) as ViewGroup).clipToPadding = false

        val myAdapter2 = MyAdapter()
        myAdapter2.setNewInstance(mutableListOf(0,1,1,1))
        //使用内置Indicator
        var indicator = IndicatorView(this)
            .setIndicatorColor(Color.DKGRAY)
            .setIndicatorSelectorColor(Color.WHITE);


        banner.setAutoPlay(true)
            .setOffscreenPageLimit(2)
            .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            .setPagerScrollDuration(800)
            .setIndicator(indicator)
            .setAdapter(myAdapter2)

        banner.setPageMargin3(100.toPX(),10.toPX())
    }

    inner class MyAdapter: BaseQuickAdapter<Int,BaseViewHolder>(R.layout.item_viewpger2_layout) {

        override fun convert(holder: BaseViewHolder, item: Int) {

        }

    }

}
