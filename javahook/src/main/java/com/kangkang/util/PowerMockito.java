package com.kangkang.util;


import android.util.Log;

import com.panda.hook.javahook.AndHookUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PowerMockito {
    private static boolean FLAG_2_0 = true;
    private static void set2_0Flag(boolean flag) {
        FLAG_2_0 = flag;
    }
    private static final String TAG = "PowerMockito";
    public static class MockObject {
        public Class clazz;
        public Method[] methods;
        public HashMap<Method, MethodHookMock> methodCallMap = new HashMap<>();
        public int verify(String method, Class... paraTypes) {
            int result = 0;
            for (Map.Entry<Method, MethodHookMock> entry : methodCallMap.entrySet()) {
                Method key = entry.getKey();
                if (key.getName().equals(method)
                        && checkParaTypesIsEquals(key.getParameterTypes(), paraTypes)) {
                    result = entry.getValue().invokeTimes;
                }
            }
            return result;
        }
    }

    private static boolean checkParaTypesIsEquals(Class[] para1, Class[] para2) {
        if (para1 == null && para2 == null) {
            return true;
        }
        if (para1.length != para2.length) {
            return false;
        }
        for (int i = 0; i < para1.length; i ++) {
            if (!para1[i].getName().equals(para2[i].getName())) {
                return false;
            }
        }
        return true;
    }

    public interface Answer {
       public void beforeCall(Object[] args);
       public void afterCall(Object[] args);
    }


    public static class MethodHookMock implements Answer {
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

    public static synchronized void unHook() {
        AndHookUtils.unHook();
    }
    public static synchronized void unHook(Class clazz) {
        AndHookUtils.unHook(clazz);
    }
    public static synchronized void unHook(Class clazz, String method) {
        AndHookUtils.unHook(clazz, method);
    }

    public static synchronized void unHook(Class clazz, String method, Class... paraTypes) {
        AndHookUtils.unHook(clazz, method);
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
        AndHookUtils.whenThenReturn(clazz, method, returnResult, paraTypes);
    }
    public static Class checkIfClassContainMethod(Class clazz, String method, Class... paraTypes) {
        Class checkClazz = null;
        try {
            clazz.getDeclaredMethod(method, paraTypes);
            checkClazz = clazz;
        } catch (NoSuchMethodException e) {
            checkClazz = checkIfClassContainMethod(clazz.getSuperclass(), method, paraTypes);
        }
        return checkClazz;
    }

    public static Object[] getParameterTypesAndCallback(Object callback, Class... paraTypes) {
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
        AndHookUtils.whenThrow(clazz, method, throwable, paraTypes);
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
        AndHookUtils.doAnswer(clazz, method, answer, paraTypes);
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
        return AndHookUtils.mock(clazz, methods);
    }

}
