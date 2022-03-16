package com.base.utils

object MathUtils {

    /**
     * 辗转相除法，求两个数的最大公约数
     */
    @JvmStatic
    fun getGreastCommonDivisor(a:Int,b:Int):Int {
        return if (a > b) {
            gcb(a,b)
        }else {
            gcb(b,a)
        }
    }

    /**
     * a > b
     */
    @JvmStatic
    fun gcb(a:Int,b:Int):Int {
        var result = a % b
        return if (result == 0) {
            b
        }else {
            gcb(b,result)
        }
    }

}