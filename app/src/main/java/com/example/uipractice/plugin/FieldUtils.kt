package com.example.uipractice.plugin

import java.lang.Exception
import java.lang.reflect.Field

class FieldUtils {

    companion object {

        @JvmStatic
        fun getField(clazz: Class<*>, target: Any?,name:String):Any? {
            try {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                return field.get(target)
            }catch (e:Exception) {}

            return null
        }

        @JvmStatic
        fun getField(clazz: Class<*>,name:String):Field? {
            try {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                return field
            }catch (e:Exception) {}

            return null
        }

        @JvmStatic
        fun setField(clazz: Class<*>, target: Any,name:String,value:Any) {
            try {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                field.set(target,value)
            }catch (e:Exception) {}
        }
    }

}