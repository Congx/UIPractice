package com.example.uipractice.fragment

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.base.framwork.ui.recyclerview.utils.ScaleInTransformer
import com.example.uipractice.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_fragment_viewpager2.*

/**
 * fragment 生命周期研究
 */
class FragmentViewpager2Activity : AppCompatActivity() {

    var fragments:MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_viewpager2)
        for (i in 0..20) {
            fragments.add(AFragment.newInstance(i.toString()))
        }
        initView()
    }


    private fun initView() {
        viewPager.adapter = Myadapter()
        //
        viewPager.orientation = ORIENTATION_HORIZONTAL
//        val compositePageTransformer = CompositePageTransformer()
//        val marginPageTransformer = MarginPageTransformer(10)
//        viewPager.setPageTransformer(marginPageTransformer)
//        val scaleInTransformer = ScaleInTransformer()
//        compositePageTransformer.also {
//            it.addTransformer(scaleInTransformer)
//            it.addTransformer(marginPageTransformer)
//        }
//        viewPager.setPageTransformer(compositePageTransformer)
//        viewPager.isUserInputEnabled = false
//        viewPager.offscreenPageLimit = 1

        for (i in 0..20) {
            tablayoutx.addTab(tablayoutx.newTab().also { it.text = i.toString() })
        }

        tablayoutx.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position!!
            }

        })
//        (viewPager.getChildAt(0) as ViewGroup).clipToPadding = false
    }


    inner class Myadapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int {
            return 20
        }

        override fun createFragment(position: Int): Fragment {
            Log.e("Myadapter","createFragment -> $position")
            return fragments[position]
        }

    }

}
