package com.example.uipractice.plugin;

import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.example.uipractice.plugin.HookHelper.TARGET_INTENT;
import static com.example.uipractice.plugin.HookHelper.TARGET_SERVICE_INTENT;

class IActivityManagerProxy implements InvocationHandler {

    private Object activityManager;
    String pkg = "com.example.uipractice";

    public IActivityManagerProxy(Object activityManager) {
        this.activityManager = activityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Log.d("IActivityManagerProxy","invoke");

        if (method.getName().equals("startActivity")) {
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            intent = (Intent) args[index];
            String className = intent.getComponent().getClassName();
            Log.d("IActivityManagerProxy",className);

            // hook Activity逻辑
            if (className.endsWith("PluginActivity")) {
                Intent subIntent = new Intent();
                subIntent.setClassName(pkg, pkg + ".plugin.StubActivity");
                subIntent.putExtra(TARGET_INTENT, intent);
                args[index] = subIntent;
            }
        }

        if (method.getName().equals("startService")) {
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            intent = (Intent) args[index];
            String className = intent.getComponent().getClassName();

            if (className.endsWith("PluginService")) {
                Log.d("IActivityManagerProxy",className);
                Intent subIntent = new Intent();
                subIntent.setClassName(pkg, pkg + ".plugin.service.ProxyService");
                subIntent.putExtra(TARGET_SERVICE_INTENT, className);
                args[index] = subIntent;
            }
        }

        return method.invoke(activityManager, args);
    }
}
