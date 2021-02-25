package com.kangkang.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * hook servicemanager，实现替换framework各个service的功能
 */

public class ServiceManagerUtils {
    private static final String CLASS_ActivityManagerNative = "android.app.ActivityManagerNative";
    private static final String CLASS_ServiceManager = "android.os.ServiceManager";

    public interface IServiceInvokeCallBack {
        public boolean isHook(Object proxy, Method method, Object[] args);
        public Object hookMethod(Object proxy, Method method, Object[] args) throws Throwable;
    }
    //hook 系统service
    //1.反射拿到service对象所有方法实现
    //2. 创建动态代理，实现invoke方法
    //3. 将动态代理创建的service 反射传入ServiceManager.sCache中
    //4. 将ActivityManagerNative中的gDefault单例重置
    public static class ServiceBinderHook implements InvocationHandler {
        private IBinder iBinder;
        private Object mIServiceManagerHook;

        public ServiceBinderHook(IBinder binder,Object iServiceManagerHook) {
            iBinder = binder;
            mIServiceManagerHook = iServiceManagerHook;

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("ServiceHook - begin " + method.toString());
            if (method.toString().contains("toString")) {
                return this.toString();
            }
            if (method.toString().contains("queryLocalInterface")) {
                //传出代理类

                return mIServiceManagerHook;
            }
            //拦截传递给framework中 service的通信
//            if (method.toString().contains("boolean android.os.IBinder.transact")) {
//                if (iServiceInvokeCallBack.isHook((int) args[0], args)) {
//                    return iServiceInvokeCallBack.hookMethod(proxy, method, args);
//                }
//            }
            Object invoke = method.invoke(iBinder, args);
            System.out.println("ServiceHook - end");
            return invoke;
        }
    }
    //IActivityManger，IPackageManager等的代理
    public static class ServiceProxyHook implements InvocationHandler {
        Object mServiceProxy;
        private IServiceInvokeCallBack iServiceInvokeCallBack;
        public ServiceProxyHook(Object serviceProxy, IServiceInvokeCallBack callBack) {
            mServiceProxy = serviceProxy;
            iServiceInvokeCallBack = callBack;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("ServiceProxyHook - begin " + method.toString());
            System.out.println("ServiceProxyHook - end " + mServiceProxy);
            if (iServiceInvokeCallBack.isHook(proxy, method, args)) {
                    return iServiceInvokeCallBack.hookMethod(proxy, method, args);
            }
            return method.invoke(mServiceProxy, args);
        }
    }
    public static Object hookWallpaperManager(Context context, IServiceInvokeCallBack iServiceInvokeCallBack) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        //1.反射拿到service对象所有方法实现
        Object IWallpaperManager = ReflectionUtils.invokePrivateMethod(WallpaperManager.getInstance(context), "getIWallpaperManager", null, null);
        //2.1 创建IServiceManager动态代理，实现invoke方法
        InvocationHandler serviceProxyHook = new ServiceProxyHook(IWallpaperManager, iServiceInvokeCallBack);
        Object iServiceManagerHook =  Proxy.newProxyInstance(serviceProxyHook.getClass().getClassLoader(), IWallpaperManager.getClass().getInterfaces(), serviceProxyHook);
        //2.2 创建Ibinder动态代理，实现invoke方法
//        IBinder iBinder = (IBinder) ReflectionUtils.invokePrivateMethod(IWallpaperManager, "asBinder", null, null);
//        InvocationHandler serviceBinderHook = new ServiceBinderHook(iBinder, iServiceManagerHook);
//        IBinder iBinderHook = (IBinder) Proxy.newProxyInstance(serviceBinderHook.getClass().getClassLoader(), new Class[]{IBinder.class}, serviceBinderHook);
//        HashMap<String, IBinder> sCache = (HashMap<String, IBinder>) ReflectionUtils.getStaticField(Class.forName(CLASS_ServiceManager), "sCache");
        //3. 将动态代理创建的service 反射传入ServiceManager.sCache中
//        sCache.put(Context.WALLPAPER_SERVICE, iBinderHook);
//        ReflectionUtils.setStaticField(Class.forName(CLASS_ServiceManager), "sCache", sCache);

        //4. 将WallpaperManager中的sGlobals.mService单例 动态代理
        Object object = ReflectionUtils.getStaticField(WallpaperManager.class, "sGlobals");

        ReflectionUtils.setPrivateField(object, "mService", iServiceManagerHook);
        return IWallpaperManager;
    }

        /**
         * hook ActivityManager
         * @param iServiceInvokeCallBack 方法回调，用于把系统调用返回成自己需要的
         *
         * */
    public static IBinder hookActivityManager(IServiceInvokeCallBack iServiceInvokeCallBack) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        //1.反射拿到service对象所有方法实现
        Object iActivityManager = ReflectionUtils.invokeStaticMethod(CLASS_ActivityManagerNative, "getDefault", null, null);
        //2.1 创建IServiceManager动态代理，实现invoke方法
        InvocationHandler serviceProxyHook = new ServiceProxyHook(iActivityManager, iServiceInvokeCallBack);
        Object iServiceManagerHook =  Proxy.newProxyInstance(serviceProxyHook.getClass().getClassLoader(), iActivityManager.getClass().getInterfaces(), serviceProxyHook);
        //2.2 创建Ibinder动态代理，实现invoke方法
        IBinder iBinder = (IBinder) ReflectionUtils.invokePrivateMethod(iActivityManager, "asBinder", null, null);
        InvocationHandler serviceBinderHook = new ServiceBinderHook(iBinder, iServiceManagerHook);
        IBinder iBinderHook = (IBinder) Proxy.newProxyInstance(serviceBinderHook.getClass().getClassLoader(), new Class[]{IBinder.class}, serviceBinderHook);
        HashMap<String, IBinder> sCache = (HashMap<String, IBinder>) ReflectionUtils.getStaticField(Class.forName(CLASS_ServiceManager), "sCache");
        sCache.put(Context.ACTIVITY_SERVICE, iBinderHook);
        //3. 将动态代理创建的service 反射传入ServiceManager.sCache中
        ReflectionUtils.setStaticField(Class.forName(CLASS_ServiceManager), "sCache", sCache);
        //4. 将ActivityManagerNative中的gDefault单例重置，使其重新去ServiceManager中获取
        Object singleton = ReflectionUtils.getStaticField(CLASS_ActivityManagerNative, "gDefault");
        ReflectionUtils.setPrivateFieldAnyway(singleton, "mInstance", null);
        return iBinder;
    }
    public static void resetActivityManager(IBinder iBinder) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        HashMap<String, IBinder> sCache = (HashMap<String, IBinder>) ReflectionUtils.getStaticField(Class.forName(CLASS_ServiceManager), "sCache");
        sCache.put(Context.ACTIVITY_SERVICE, iBinder);
        ReflectionUtils.setStaticField(Class.forName(CLASS_ServiceManager), "sCache", sCache);
        Object singleton = ReflectionUtils.getStaticField(CLASS_ActivityManagerNative, "gDefault");
        ReflectionUtils.setPrivateFieldAnyway(singleton, "mInstance", null);
    }
}
