package com.example.uipractice.fragment.floatwindow

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import com.base.framwork.fragment.BaseFragment
import com.example.uipractice.databinding.FragmentFloatWindowBinding
import com.example.uipractice.databinding.LayoutFloatBarBinding
import com.example.uipractice.recyclerview.layoutmanger.FlowLayoutManager


class FloatWindowFragment: BaseFragment() {

  lateinit var binding: FragmentFloatWindowBinding
  lateinit var windowManager: WindowManager
  lateinit var layoutParams: WindowManager.LayoutParams
//  lateinit var floatView: ViewGroup

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentFloatWindowBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//    floatView = LayoutFloatBarBinding.inflate(layoutInflater, view as ViewGroup, false).root

    activity?.let {
      FloatViewManager.showFloatView(it)
    }
  }




}