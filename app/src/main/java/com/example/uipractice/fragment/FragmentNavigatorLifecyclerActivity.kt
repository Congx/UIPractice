package com.example.uipractice.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.example.uipractice.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_fragment_live.*

/**
 * fragment 生命周期研究
 */
class FragmentNavigatorLifecyclerActivity : AppCompatActivity() {

    private var lastPostion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_live_navigator)
        fragmentManager()
    }


    private fun fragmentManager() {
        tablayout.addTab(tablayout.newTab().setText("0"))
        tablayout.addTab(tablayout.newTab().setText("1"))
//        tablayout.addTab(tablayout.newTab().setText("2"))
//        tablayout.addTab(tablayout.newTab().setText("3"))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {
                lastPostion = tab?.position!!
                showOrHide(tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                showOrHide(tab?.position)
            }

        })
    }

    fun showOrHide(position: Int?) {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        var bundle = Bundle()
        bundle.putString(AFragment.POSITION,position.toString())
        when(position) {
            0-> navController.navigate(R.id.AFragment,bundle)
            1-> navController.navigate(R.id.BFragment,bundle)
        }
    }

}
