package com.example.uipractice.fragment.floatwindow

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.uipractice.databinding.LayoutFloatBarBinding

class FloatView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  var binding: LayoutFloatBarBinding = LayoutFloatBarBinding.inflate(LayoutInflater.from(getContext()), null, false)

  init {
    addView(binding.root)
  }
}