package com.kangkang.util;

//import com.taobao.android.dexposed.DexposedBridge;
//import com.taobao.android.dexposed.XC_MethodHook;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;

public class PowerMockito {
    private static final String TAG = "PowerMockito";
    public static class MockObject {
        public Class clazz;
        public Method[] methods;
        public HashMap<Method, MethodHookMock> methodCallMap = new HashMap<>();
        public int verify(String method, Class... paraTypes) {
            int result = 0;
            try {
                Method method1 = clazz.getDeclaredMethod(method, paraTypes);
                MethodHookMock mock = methodCallMap.get(method1);
                return mock.invokeTimes;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    public interface Answer {
       public void beforeCall(Object[] args);
       public void afterCall(Object[] args);
    }
    static class MethodHookWhen extends XC_MethodHook {
        Object mResult = null;
        MethodHookWhen(Object result) {
            mResult = result;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Object t = (Object) param.thisObject;
            System.out.println("MethodHookWhen - beforeHookedMethod - mResult:" + mResult);
            param.setResult(mResult);
        }
    }
    static class MethodHookDoAnswer extends XC_MethodHook {
        Answer mAnswer = null;

        public MethodHookDoAnswer(Answer answer) {
            mAnswer = answer;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            System.out.println("shizhikang - mock: " + param.method);
            mAnswer.beforeCall(param.args);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            mAnswer.afterCall(param.args);
        }
    }
    static class MethodHookWhenThrow extends XC_MethodHook {
        Throwable mThrowable = null;
        MethodHookWhenThrow(Throwable throwable) {
            mThrowable = throwable;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Object t = (Object) param.thisObject;
            param.setThrowable(mThrowable);
//            ReflectionUtils.setPrivateFieldAnyway(param, "returnEarly", false);
        }

    }
    static class MethodHookMock implements Answer {
        public int invokeTimes;
        public int invokeSuccessTimes;
        @Override
        public void beforeCall(Object[] args) {
            invokeTimes ++;
        }

        @Override
        public void afterCall(Object[] args) {
            invokeSuccessTimes ++;
        }
    }


//    public static Object whenNew(Class clazz, final Object returnResult) {
//        DexposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                System.out.println("shizhikang - beforeHookedMethod - param.thisObject: " + param.thisObject);
//                param.thisObject = returnResult;
//                param.setResult(returnResult);
//            }
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                System.out.println("shizhikang - afterHookedMethod - param: " + param.thisObject.hashCode());
//                System.out.println("shizhikang - afterHookedMethod - param: " + returnResult.hashCode());
////                param.thisObject = returnResult;
//
////                Thread thread = (Thread) param.thisObject;
////                Class<?> clazz = thread.getClass();
////                if (clazz != Thread.class) {
////                    Log.d(TAG, "found class extend Thread:" + clazz);
////                    DexposedBridge.findAndHookMethod(clazz, "run", new ThreadMethodHook());
////                }
////                Log.d(TAG, "Thread: " + thread.getName() + " class:" + thread.getClass() +  " is created.");
//            }
//        });
//        return null;
//    }

    public static synchronized void unHook() {
        DexposedBridge.unhookAllMethods();
    }
    public static synchronized void unHook(Class clazz) {
        DexposedBridge.unhookAllMethods(clazz);
    }
    public static synchronized void unHook(Class clazz, String method) {
        DexposedBridge.unhookAllMethods(clazz, method);
    }
    /**
     * return mock result when method invoke, orgin method will not run
     * Usage:
     * <pre>
     *    source
     *    public void when(String path) {
     *         System.out.println("shizhikang - when: " + path);
     *         File file = new File(path);
     *         if (file.exists()) {
     *             System.out.println("when - true");
     *         } else {
     *             System.out.println("when - false");
     *         }
     *     }
     * </pre>
     * <pre>
     *    &#064;Test
     *      public void test_when() {
     *         PowerMockito.whenThenReturn(File.class, "exists", true);
     *         mPowerMockitoActivity.when("111");
     *         PowerMockito.unHook();
     *
     *         PowerMockito.whenThenReturn(File.class, "exists", false);
     *         mPowerMockitoActivity.when("111");
     *         PowerMockito.unHook();
     *     }
     * </pre>
     */
    public static synchronized void whenThenReturn(Class clazz, String method, Object returnResult, Class... paraTypes) {
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        unHook(clazz, method);
        DexposedBridge.findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookWhen(returnResult), paraTypes));
    }
    private static Class checkIfClassContainMethod(Class clazz, String method, Class... paraTypes) {
        Class checkClazz = null;
        try {
            clazz.getDeclaredMethod(method, paraTypes);
            checkClazz = clazz;
        } catch (NoSuchMethodException e) {
            checkClazz = checkIfClassContainMethod(clazz.getSuperclass(), method, paraTypes);
        }
        return checkClazz;
    }

    private static Object[] getParameterTypesAndCallback(Object callback, Class... paraTypes) {
        Object[] parameterTypesAndCallback = new Object[paraTypes.length + 1];
        for (int i = 0; i < paraTypes.length; i++) {
            parameterTypesAndCallback[i] = paraTypes[i];
        }
        parameterTypesAndCallback[paraTypes.length] = callback;
        return parameterTypesAndCallback;
    }
    /**
     * throw Exception when method invoke. orgin method will not run
     * Usage:
     * <pre>
     *    source
     *    public void whenThrow() {
     *         File dbFile = new File("test");
     *         if (!dbFile.exists()) {
     *             try {
     *                 System.out.println("whenThrow - begin");
     *                 boolean isFileCreateSuccess = dbFile.createNewFile();
     *                 System.out.println("whenThrow - end");
     *             } catch (IOException e) {
     *                 System.out.println("whenThrow - IOException");
     *             } finally {
     *             }
     *         }
     *     }
     * </pre>
     * <pre>
     *    &#064;Test
     *     public void test_whenThrow() {
     *         try {
     *             PowerMockito.whenThrow(File.class, "createNewFile", new IOException("TestIOException"));
     *             mPowerMockitoActivity.whenThrow();
     *             PowerMockito.unHook();
     *
     *             PowerMockito.whenThrow(File.class, "createNewFile", new JSONException("JSONException"));
     *             mPowerMockitoActivity.whenThrow();
     *         } catch (Throwable e) {
     *             e.printStackTrace();
     *         }
     *         PowerMockito.unHook();
     *         PowerMockito.whenThenReturn(File.class, "createNewFile", true);
     *         mPowerMockitoActivity.whenThrow();
     *         PowerMockito.unHook();
     *     }
     * </pre>
     * */
    public static synchronized void whenThrow(Class clazz, String method, Throwable throwable, Class... paraTypes) {
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        unHook(clazz, method);
        DexposedBridge.findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookWhenThrow(throwable), paraTypes));
    }

    /**
     * method invoke callback
     * Usage:
     * <pre>
     *    &#064;Test
     *     PowerMockito.doAnswer(File.class, " createNewFile ", new PowerMockito.Answer () {
     *             @Override
     *             public void beforeCall(Object[] args) {
     *
     *             }
     *
     *             @Override
     *             public void afterCall(Object[] args) {
     *             }
     *         });
     *         PowerMockito.unHook();
     * </pre>
     * */
    public static synchronized void doAnswer(Class clazz, String method, Answer answer, Class... paraTypes) {
        Log.e(TAG, "doAnswer - method: " + method);
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        DexposedBridge.findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookDoAnswer(answer), paraTypes));
    }

    //Methods cannot be nested, if mock verity(), testPrivate() can not mock
    /**
     * verify method invoke times
     * Usage:
     * <pre>
     *    source
     *    public void verify() {
     *         testPrivate();
     *     }
     * </pre>
     * <pre>
     *    &#064;Test
     *     public void test_verify() throws Exception {
     *         PowerMockito.MockObject object = PowerMockito.mock(PowerMockitoActivity.class, new Method[] {PowerMockitoActivity.class.getDeclaredMethod("testPrivate")});
     *         mPowerMockitoActivity.verify();
     *         Assert.assertEquals(object.verify("testPrivate"), 1);
     *         PowerMockito.unHook();
     *     }
     * </pre>
     * @see MockObject#verify(String, Class[])
     * */
    public static synchronized MockObject mock(Class clazz, Method... methods) {
        MockObject object = new MockObject();
//        Method[] declaredMethods = clazz.getDeclaredMethods();
//        if (methods == null || methods.length == 0) {
//            methods = declaredMethods;
//        }
        object.methods = methods;
        object.clazz = clazz;
        for (Method method : methods) {
            MethodHookMock answer = new MethodHookMock();
            object.methodCallMap.put(method, answer);
            doAnswer(clazz, method.getName(), answer, method.getParameterTypes());
        }

        return object;
    }

}
