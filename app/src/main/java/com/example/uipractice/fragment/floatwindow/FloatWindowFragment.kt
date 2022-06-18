package com.example.uipractice.fragment.floatwindow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.base.framwork.fragment.BaseFragment
import com.example.uipractice.databinding.FragmentFloatWindowBinding

class FloatWindowFragment: BaseFragment() {

  lateinit var binding: FragmentFloatWindowBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentFloatWindowBinding.inflate(inflater)
    return binding.root
  }
}