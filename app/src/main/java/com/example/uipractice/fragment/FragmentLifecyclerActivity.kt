package com.example.uipractice.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.uipractice.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_fragment_live.*

/**
 * fragment 生命周期研究
 */
class FragmentLifecyclerActivity : AppCompatActivity() {

    var fragment0:AFragment? = null
    var fragment1:AFragment? = null
    var fragment2:AFragment? = null
    var fragment3:AFragment? = null

    private var lastPostion = 0
    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_live)

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            fragmentManager()
        }
        checkbox2.setOnCheckedChangeListener { buttonView, isChecked ->
            viewpager()
        }
    }

    private fun viewpager() {
        var fragment = ViewPagerFragment.newInstance("")
        supportFragmentManager.beginTransaction().replace(R.id.containerBottom, fragment).commit()
    }

    private fun fragmentManager() {
        tablayout.addTab(tablayout.newTab().setText("0"))
        tablayout.addTab(tablayout.newTab().setText("1"))
        tablayout.addTab(tablayout.newTab().setText("2"))
        tablayout.addTab(tablayout.newTab().setText("3"))
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
        fragment0 = AFragment.newInstance(0.toString())
        supportFragmentManager.beginTransaction().add(R.id.containerTop, fragment0!!).commit()
        //        supportFragmentManager.beginTransaction().commit()
        //        supportFragmentManager.beginTransaction().show(fragment0!!)
    }

    fun showOrHide(position: Int?) {
        var pos = position?:0
//        change(getFragment(pos),getFragment(lastPostion))
        showFragment(pos)
    }

    fun showFragment(position: Int) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        hide(lastPostion,beginTransaction)
        lastPostion = position
        when(position) {
            0-> {
                if (fragment0 == null) {
                    fragment0 = AFragment.newInstance(position.toString())
                    beginTransaction.add(R.id.containerTop, fragment0!!)
                }else {
                    beginTransaction.show(fragment0!!)
                }
            }
            1-> {
                if (fragment1 == null) {
                    fragment1 = AFragment.newInstance(position.toString())
                    beginTransaction.add(R.id.containerTop, fragment1!!)
                } else {
//                    beginTransaction.setMaxLifecycle(fragment1!!, Lifecycle.State.RESUMED)
                    beginTransaction.show(fragment1!!)
                }
            }
            2-> {
                if (fragment2 == null) {
                    fragment2 = AFragment.newInstance(position.toString())
                    beginTransaction.add(R.id.containerTop, fragment2!!)
                }else {
                    beginTransaction.show(fragment2!!)
                }
            }
            3-> {
                if (fragment3 == null) {
                    fragment3 = AFragment.newInstance(position.toString())
                    beginTransaction.add(R.id.containerTop, fragment3!!)
                }else {
                    beginTransaction.show(fragment3!!)
                }
            }

        }
//        beginTransaction?.setMaxLifecycle(this, Lifecycle.State.STARTED)
        beginTransaction.commit()
    }

    fun hide(position: Int, beginTransaction: FragmentTransaction) {
        when(position) {
            0-> fragment0?.let {
                beginTransaction.hide(fragment0!!)
            }
            1-> fragment1?.let {
//                beginTransaction.setMaxLifecycle(fragment1!!, Lifecycle.State.STARTED)
                beginTransaction.hide(fragment1!!)
            }
            2-> fragment2?.let {
                beginTransaction.hide(fragment2!!)
            }
            3-> fragment3?.let {
                beginTransaction.hide(fragment3!!)
            }

        }
    }

}
