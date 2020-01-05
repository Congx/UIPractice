package com.example.uipractice.ui.viewPager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_view_pager2.*

class ViewPager2Activity : AppCompatActivity() {

    private var position : Int = 0

    val fragments:ArrayList<Fragment> by lazy {
        arrayListOf(
            TestFragment.instance(position++.toString()),
            TestFragment.instance(position++.toString()),
            TestFragment.instance(position++.toString()),
            TestFragment.instance(position++.toString()),
            TestFragment.instance(position++.toString()),
            TestFragment.instance(position++.toString())
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

        }
    }
}
