package com.example.uipractice.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import com.example.uipractice.R
import com.lp.base.viewmodel.LifecycleViewModel
import kotlinx.android.synthetic.main.layout_fragment_a.*
import kotlinx.android.synthetic.main.layout_fragment_viewpager.*

/**
 * @date 9/4/2020
 * @Author luffy
 * @description
 */
class ViewPagerFragment : Fragment() {

    var position = ""
    var TAG = this.javaClass.simpleName

    var hidden = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getString(POSITION)!!
        TAG = "$position-$TAG"
        Log.e(TAG,"onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"onActivityCreated")
//        var viewModel = LifyCycleViewModel()
        lifecycle.addObserver(LifecycleViewModel())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG,"onCreateView")
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var adapter = MyAdapter()
        viewPager.adapter = adapter
    }



    override fun onStart() {
        super.onStart()
        Log.e(TAG,"onStart")

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e(TAG, "setUserVisibleHint --> $isVisibleToUser")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden

        Log.e(TAG, "onHiddenChanged --> $hidden")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG,"onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG,"onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"onStop")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG,"onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG,"onDestroy")
    }

    inner class MyAdapter : FragmentPagerAdapter(childFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return AFragment.newInstance("$TAG $position")
        }

        override fun getCount(): Int {
            return 4
        }

    }

    companion object {

        var POSITION = "POSITION"

        fun newInstance(position: String): ViewPagerFragment {
            val aFragment = ViewPagerFragment()
            var bundle = Bundle()
            bundle.putString(POSITION,position)
            aFragment.arguments = bundle
            return aFragment
        }
    }
}