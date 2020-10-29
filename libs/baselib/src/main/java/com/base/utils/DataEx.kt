package com.base.utils

import android.util.DisplayMetrics
import com.base.context.ContextProvider

/**
 *@author Levi
 *@date 2019-10-14
 *@desc Int Double Long 转px、转sp
 */
/** Int 转PX */
fun Int.toPX(): Int = (this * getDisplayMetrics().density + 0.5f).toInt()

/** Int转sp */
fun Int.toSP(): Int = (this * getDisplayMetrics().scaledDensity).toInt()

/** Float转PX */
fun Float.toPX(): Float = this * getDisplayMetrics().density + 0.5f

/** Float转sp */
fun Float.toSP(): Float = this * getDisplayMetrics().scaledDensity

/** Double转PX */
fun Double.toPX(): Double = this * getDisplayMetrics().density + 0.5f

/** Double转sp */
fun Double.toSP(): Double = this * getDisplayMetrics().scaledDensity

/** 获取 displayMetrics */
fun getDisplayMetrics(): DisplayMetrics = ContextProvider.getContext()!!.resources.displayMetrics
