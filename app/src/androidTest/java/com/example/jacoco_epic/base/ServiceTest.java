package com.example.jacoco_epic.base;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.test.InstrumentationRegistry;

import com.example.jacoco_epic.base.util.ThreadUtils;
import com.kangkang.util.ReflectionUtils;

public abstract class ServiceTest {
    private Context mContext;
    private Service mService;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    public ServiceTest(final Class<? extends Service> clazz) throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        ThreadUtils.runMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mService = clazz.newInstance();
                    Class clazz_ActivityThread = Class.forName("android.app.ActivityThread");
                    Object activityThread = ReflectionUtils.getPrivateFieldAnyway(mContext,"mMainThread");
                    Application application = (Application) ReflectionUtils.getPrivateFieldAnyway(activityThread, "mInitialApplication");

                    Object ActivityManagerNative_getDefault = ReflectionUtils.invokeStaticMethod("android.app.ActivityManagerNative", "getDefault", null, null);
                    //设置Service相关参数
                    ReflectionUtils.invokePrivateMethodAnyway(mService, "attach", new Class[] {Context.class, clazz_ActivityThread, String.class, IBinder.class, Application.class, Object.class}, new Object[] {mContext, activityThread, mService.getClass().toString(), null, application, ActivityManagerNative_getDefault});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mService.onCreate();
            }
        });
    }
    public Service getService() {
        return mService;
    }
    public Context getContext() {
        return mContext;
    }
}
