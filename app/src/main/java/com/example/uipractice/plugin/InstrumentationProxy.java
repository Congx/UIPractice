package com.example.uipractice.plugin;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import static com.example.uipractice.plugin.HookHelper.TARGET_INTENT_CLASS_NAME;

class InstrumentationProxy extends Instrumentation {

    Instrumentation instrumentation;
    PackageManager packageManager;

    public InstrumentationProxy(Instrumentation instrumentation, PackageManager packageManager) {
        this.packageManager = packageManager;
        this.instrumentation = instrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        if (resolveInfos == null || resolveInfos.isEmpty()) {
            intent.putExtra(TARGET_INTENT_CLASS_NAME,intent.getComponent().getClassName());
            intent.setClassName(who,HookHelper.SUB_ACTIVITY_NAME);
        }

        try {
            Method execMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",Context.class,
                    IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class);

            Log.d("InstrumentationProxy","execStartActivity success");
            return (ActivityResult)execMethod.invoke(instrumentation,who,contextThread,token,target,intent,requestCode,options);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String stringExtra = intent.getStringExtra(TARGET_INTENT_CLASS_NAME);
        if (!TextUtils.isEmpty(stringExtra)) {
            Log.d("InstrumentationProxy","newActivity success");
            return super.newActivity(cl, stringExtra, intent);
        }
        return super.newActivity(cl, className, intent);
    }
}
