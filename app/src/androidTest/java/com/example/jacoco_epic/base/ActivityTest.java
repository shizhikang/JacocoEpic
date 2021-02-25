package com.example.jacoco_epic.base;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.view.Window;

import com.example.jacoco_epic.base.util.ServiceManagerUtils;
import com.example.jacoco_epic.base.util.ThreadUtils;
import com.kangkang.util.ReflectionUtils;

import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class ActivityTest {
    private Context mContext;
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    public ActivityTest(final Class<? extends Activity> clazz) throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
//        try {
//            mActivity = clazz.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("mActivity : " + mActivity);
        ThreadUtils.runMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mActivity = clazz.newInstance();
                    System.out.println("mActivity : " + mActivity);
//                    Method[] methods = Activity.class.getDeclaredMethods();
//                    for (Method method: methods) {
//                        System.out.println("Activity - method: " + method);
//                    }
                    Class clazz_ActivityThread = Class.forName("android.app.ActivityThread");
                    Object activityThread = ReflectionUtils.getPrivateFieldAnyway(mContext,"mMainThread");
                    Instrumentation instr = (Instrumentation) ReflectionUtils.invokePrivateMethodAnyway(activityThread, "getInstrumentation", null, null);
                    IBinder token = new IBinder() {
                        @Nullable
                        @Override
                        public String getInterfaceDescriptor() throws RemoteException {
                            return null;
                        }

                        @Override
                        public boolean pingBinder() {
                            return false;
                        }

                        @Override
                        public boolean isBinderAlive() {
                            return false;
                        }

                        @Nullable
                        @Override
                        public IInterface queryLocalInterface(@NonNull String descriptor) {
                            return null;
                        }

                        @Override
                        public void dump(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {

                        }

                        @Override
                        public void dumpAsync(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {

                        }

                        @Override
                        public boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
                            return false;
                        }

                        @Override
                        public void linkToDeath(@NonNull DeathRecipient recipient, int flags) throws RemoteException {

                        }

                        @Override
                        public boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags) {
                            return false;
                        }
                    };
                    int ident = 0;
                    Application application = (Application) ReflectionUtils.getPrivateFieldAnyway(activityThread, "mInitialApplication");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(mContext.getPackageName(), mActivity.getClass().getName()));
                    ActivityInfo info = new ActivityInfo();
                    info.theme = mContext.getApplicationInfo().theme;//AppCompat需要使用style,contextthemewrapper 与contextimpl 的mThemeResource不同

                    info.packageName = mContext.getPackageName();
//                    info.applicationInfo = new ApplicationInfo();
                    info.applicationInfo = mContext.getApplicationInfo();//AppCompat需要使用style
                    CharSequence title = "test";
                    Activity parent = null;
                    String id = mContext.getPackageName();
                    Class NonConfigurationInstances = Class.forName("android.app.Activity$NonConfigurationInstances");
                    Configuration config = null;
                    String referrer = null;
                    Class IVoiceInteractor = Class.forName("com.android.internal.app.IVoiceInteractor");
                    Class windowClass = Class.forName("com.android.internal.policy.PhoneWindow");
                    Constructor<?> localConstructor = windowClass.getConstructor(new Class[]{Context.class});
                    Window mWindow = (Window) localConstructor.newInstance(new Object[]{mContext});

//                    Method[] methods = Activity.class.getDeclaredMethods();
//                    for (Method method: methods) {
//                        System.out.println("shizhikang - method: " + method);
//                    }
                    //attach
                    if (Build.VERSION.SDK_INT < 24) {
                        ReflectionUtils.invokePrivateMethodAnyway(mActivity, "attach", new Class[] {Context.class, clazz_ActivityThread,
                                Instrumentation.class, IBinder.class, int.class,
                                Application.class, Intent.class, ActivityInfo.class,
                                CharSequence.class, Activity.class, String.class,
                                NonConfigurationInstances,
                                Configuration.class, String.class, IVoiceInteractor,
//                            Window.class
//                            , ActivityConfigCallback
                        }, new Object[] {mContext, activityThread, instr, token, ident, application, intent, info, title, parent, id, null, config,
                                referrer, null
//                            , mWindow
//                            , null
                        });
                    } else if (Build.VERSION.SDK_INT < 26) {
                        ReflectionUtils.invokePrivateMethodAnyway(mActivity, "attach", new Class[] {Context.class, clazz_ActivityThread,
                                Instrumentation.class, IBinder.class, int.class,
                                Application.class, Intent.class, ActivityInfo.class,
                                CharSequence.class, Activity.class, String.class,
                                NonConfigurationInstances,
                                Configuration.class, String.class, IVoiceInteractor,
                            Window.class
//                            , ActivityConfigCallback
                        }, new Object[] {mContext, activityThread, instr, token, ident, application, intent, info, title, parent, id, null, config,
                                referrer, null
                            , mWindow
//                            , null
                        });
                    } else {
                        Class ActivityConfigCallback = Class.forName("android.view.ViewRootImpl$ActivityConfigCallback");
                        ReflectionUtils.invokePrivateMethodAnyway(mActivity, "attach", new Class[] {Context.class, clazz_ActivityThread,
                                Instrumentation.class, IBinder.class, int.class,
                                Application.class, Intent.class, ActivityInfo.class,
                                CharSequence.class, Activity.class, String.class,
                                NonConfigurationInstances,
                                Configuration.class, String.class, IVoiceInteractor,
                                Window.class
                            , ActivityConfigCallback
                        }, new Object[] {mContext, activityThread, instr, token, ident, application, intent, info, title, parent, id, null, config,
                                referrer, null
                                , mWindow
                            , null
                        });
                    }

                    mActivity.setTheme(mContext.getApplicationInfo().theme);////AppCompat需要使用style,contextthemewrapper 与contextimpl 的mThemeResource不同

                    IBinder binder = ServiceManagerUtils.hookActivityManager(new ServiceManagerUtils.IServiceInvokeCallBack() {
                        @Override
                        public boolean isHook(Object proxy, Method method, Object[] args) {
                            String methodString = method.toString();
                            if (methodString.contains("isTopOfTask")) {//hook isTopOfTask, java.lang.IllegalArgumentException
                                return true;
                            }
                            if (methodString.contains("finishActivity")) {
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public Object hookMethod(Object proxy, Method method, Object[] args) throws Throwable {
                            String methodString = method.toString();
                            if (methodString.contains("isTopOfTask")) {
                                return false;
                            }
                            if (methodString.contains("finishActivity")) {
                                return true;
                            }
                            return null;
                        }
                    });
                    //onCreate
                    ReflectionUtils.invokePrivateMethodAnyway(mActivity, "onCreate", new Class[] {Bundle.class}, new Object[] {null});
                    //onResume
                    ReflectionUtils.invokePrivateMethodAnyway(mActivity, "onResume", null, null);
                    System.out.println("AndroidTest - new - end");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Activity getActivity() {
        return mActivity;
    }
    public Context getContext() {
        return mContext;
    }

}
