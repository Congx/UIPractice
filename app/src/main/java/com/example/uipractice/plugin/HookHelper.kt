package com.example.uipractice.plugin

import android.app.Instrumentation
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import java.lang.reflect.Proxy

class HookHelper {

    companion object {

        const val TARGET_INTENT = "TARGET_INTENT"
        const val TARGET_SERVICE_INTENT = "TARGET_SERVICE_INTENT"
        const val SUB_ACTIVITY_NAME = "com.example.uipractice.plugin.StubActivity"
        const val TARGET_INTENT_CLASS_NAME = "TARGET_INTENT_CLASS_NAME"

        @JvmStatic
        fun hookAMS() {
            var defaultSingleton:Any?
            // 1.拿到sington对象
            if(Build.VERSION.SDK_INT in 26..28) {
                val amClass = Class.forName("android.app.ActivityManager")
                defaultSingleton = FieldUtils.getField(amClass, null, "IActivityManagerSingleton")
            }else {
                val amClass = Class.forName("android.app.ActivityManagerNative")
                defaultSingleton = FieldUtils.getField(amClass, null, "gDefault")
            }
            // 2.拿到sington 的IActivityManager对象中的mInstance属性，也就是IActivityManager在本地的AMS的代理
            val singletonClazz = Class.forName("android.util.Singleton")
            val iActivityManagerClazz = Class.forName("android.app.IActivityManager")
            val fieldInstance = singletonClazz.getDeclaredField("mInstance")
            fieldInstance.isAccessible = true
            val activityManager = fieldInstance.get(defaultSingleton)

            // 3.对IActivityManager进行动态代理，这样可以拿到activity的启动参数，进行替换,把插件的activity换成壳的activity
            var handler = IActivityManagerProxy(activityManager)
            val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                arrayOf(iActivityManagerClazz),
                handler
            )

            fieldInstance.set(defaultSingleton,proxy)

            hookHandle()
        }

        @JvmStatic
        fun hookHandle() {
            // hook activity 启动时候ActivityThread的启动流程，把intent启动参数换成插件Activity，这样就能启动插件的Activity
            var activityThreadClazz = Class.forName("android.app.ActivityThread")
            val activityThread = FieldUtils.getField(activityThreadClazz, null, "sCurrentActivityThread")
            val mHField = FieldUtils.getField(activityThreadClazz, "mH")
            mHField?.isAccessible = true
            var mH = mHField?.get(activityThread) as Handler
            var callback = HCallback(mH)
            FieldUtils.setField(Handler::class.java,mH,"mCallback",callback)

            Log.d("HookHelper","hookHandle")
        }

        @JvmStatic
        fun hookInstrumentation(context:Context) {
            var activityThreadClazz = Class.forName("android.app.ActivityThread")
            val activityThread = FieldUtils.getField(activityThreadClazz, null, "sCurrentActivityThread")
            val insField = FieldUtils.getField(activityThreadClazz, "mInstrumentation")
            val instrumentation = insField?.get(activityThread) as Instrumentation

            FieldUtils.setField(activityThreadClazz,activityThread!!,"mInstrumentation",InstrumentationProxy(instrumentation,context.packageManager))
            Log.d("HookHelper","hookInstrumentation")
        }
    }
}