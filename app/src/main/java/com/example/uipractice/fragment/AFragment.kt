package com.example.uipractice.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.base.framwork.p.BaseViewModel
import com.example.uipractice.R
import kotlinx.android.synthetic.main.layout_fragment_a.*

/**
 * @date 9/4/2020
 * @Author luffy
 * @description
 */
class AFragment : Fragment() {

    var position = "0"
    var TAG = "A"


    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getString(POSITION)?:""
        TAG = "$position-$TAG"
        Log.e(TAG,"onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
        ViewModelProvider(this).get(BaseViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"onActivityCreated")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG,"onCreateView")
//        lifecycle.addObserver(LifyCycleViewModel())
        return inflater.inflate(R.layout.layout_fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView.text = position
        var fragment = BFragment.newInstance(TAG)
        childFragmentManager.beginTransaction().add(R.id.container,fragment).commit()
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
        Log.e(TAG, "onHiddenChanged --> $hidden")

        // 生命周期只传递一次
        if (hidden) {
            // 这种方式不行
//            fragmentManager?.beginTransaction()?.setMaxLifecycle(this,Lifecycle.State.CREATED)?.commit()
            // 这种方式会分发给 这个lifyclerOwner的监听者
//            (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }else {
//            (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
//            fragmentManager?.beginTransaction()?.setMaxLifecycle(this,Lifecycle.State.RESUMED)?.commit()
        }
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


    companion object {

        var POSITION = "POSITION"

        fun newInstance(position: String): AFragment {
            val aFragment = AFragment()
            var bundle = Bundle()
            bundle.putString(POSITION,position)
            aFragment.arguments = bundle
            return aFragment
        }
    }
}