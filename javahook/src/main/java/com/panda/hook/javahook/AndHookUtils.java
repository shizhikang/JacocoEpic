package com.panda.hook.javahook;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.dx.DexMaker;
import com.android.dx.TypeId;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kangkang.util.PowerMockito;
import com.kangkang.util.PowerMockito.Answer;
import com.kangkang.util.PowerMockito.MockObject;
import com.kangkang.util.PowerMockito.MethodHookMock;
//import junit.framework.TestMine;

/**
 * Created by panda on 17/8/8.
 */

public class AndHookUtils {
    private static final String TAG = "AndHookUtils";
    static private HashMap<String, BackMethod> hooked = new HashMap();//所有已hook方法
    static private Application context = null;
    private static final Map<Class<?>, String> PRIMITIVE_TO_SIGNATURE;

    static {
        PRIMITIVE_TO_SIGNATURE = new HashMap<Class<?>, String>(9);
        PRIMITIVE_TO_SIGNATURE.put(byte.class, "B");
        PRIMITIVE_TO_SIGNATURE.put(char.class, "C");
        PRIMITIVE_TO_SIGNATURE.put(short.class, "S");
        PRIMITIVE_TO_SIGNATURE.put(int.class, "I");
        PRIMITIVE_TO_SIGNATURE.put(long.class, "J");
        PRIMITIVE_TO_SIGNATURE.put(float.class, "F");
        PRIMITIVE_TO_SIGNATURE.put(double.class, "D");
        PRIMITIVE_TO_SIGNATURE.put(void.class, "V");
        PRIMITIVE_TO_SIGNATURE.put(boolean.class, "Z");
    }

    public static Context getSystemContext() {
        if (context == null) {
            try {
                Class at = Class.forName("android.app.ActivityThread");
                Method method = at.getDeclaredMethod("currentApplication");
                method.setAccessible(true);
                Application current = (Application) method.invoke(null);
                context = current;
                return context;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return context;
        }
    }

    public static boolean isHooked(Member m) {
        String sigName = m.getDeclaringClass().getSimpleName() + "." + HookUtil.sign(m);
        if (hooked.get(sigName) != null) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCallerStr(Member old) {
        String sigName = old.getDeclaringClass().getSimpleName() + "." + HookUtil.sign(old);
        return sigName;
    }

    public static void beginHook(List<BackMethod> methods) {
        if (!HookUtil.isArt()) {
            startDavilkHook(methods);
        } else {
            startArtHook(methods);
        }
    }

    private static void startDavilkHook(List<BackMethod> methods) {
        for (BackMethod bak : methods) {
            HookUtil.generateMethodDavilk(bak);
        }
        return;
    }

    private static void startArtHook(List<BackMethod> methods) {
        try {
            Iterator<BackMethod> it = methods.iterator();
            while (it.hasNext()) {
                BackMethod m = it.next();
                if (isHooked(m.getOldMethod())) {
                    Log.d("panda", m.getOldMethod().getName() + " is hooked!");
                    it.remove();
                }
            }
            if (methods.size() <= 0) {
                return;
            }
            DexMaker dexMaker = new DexMaker();
            Map<String, TypeId> classes = new HashMap<>();
            for (BackMethod m : methods) {
//                m.getDeclaringClass().getName().equals()
                String name = m.getOldMethod().getDeclaringClass().getName().replace(".", "_");
                if (classes.get(name) == null) {
                    TypeId<?> cls = TypeId.get("L" + name + ";");
                    Class target = m.getOldMethod().getDeclaringClass();
                    //shizhikang fix final method
//                    dexMaker.declare(cls, "", Modifier.PUBLIC, TypeId.OBJECT);
//                    if (Modifier.isFinal(target.getModifiers()) || !HookUtil.isArt()) {
                        dexMaker.declare(cls, "", Modifier.PUBLIC, TypeId.OBJECT);
//                    } else {
//                        dexMaker.declare(cls, "", Modifier.PUBLIC, TypeId.get(target));
//                    }
                    MethodUtil.addDefaultInstanceField(dexMaker, cls);
                    MethodUtil.addDefaultConstructor(dexMaker, cls);
                    classes.put(name, cls);
                    if (m.getOldMethod() instanceof Method) {
                        MethodUtil.generateMethodFromMethod(dexMaker, cls, (Method) m.getOldMethod());
                        MethodUtil.generateInvokerFromMethod(dexMaker, cls, (Method) m.getOldMethod());
                    } else {
                        MethodUtil.generateMethodFromConstructor(dexMaker, cls, (Constructor) m.getOldMethod());
                        MethodUtil.generateInvokerFromConstructor(dexMaker, cls, (Constructor) m.getOldMethod());
                    }
                } else {
                    if (m.getOldMethod() instanceof Method) {
                        MethodUtil.generateMethodFromMethod(dexMaker, classes.get(name), (Method) m.getOldMethod());
                        MethodUtil.generateInvokerFromMethod(dexMaker, classes.get(name), (Method) m.getOldMethod());
                    } else {
                        MethodUtil.generateMethodFromConstructor(dexMaker, classes.get(name), (Constructor) m.getOldMethod());
                        MethodUtil.generateInvokerFromConstructor(dexMaker, classes.get(name), (Constructor) m.getOldMethod());
                    }
                }
            }

            if (context == null) {
                getSystemContext();
            }

            File outputDir = new File(context.getDir("path", Context.MODE_PRIVATE).getPath());
            if (outputDir.exists()) {
                File[] fs = outputDir.listFiles();
                for (File f : fs) {
                    f.delete();
                }
            }
//            dexMaker.generate();


            ClassLoader loader = dexMaker.generateAndLoad(context.getClassLoader(), outputDir);
            for (BackMethod bak : methods) {
                Member m = bak.getOldMethod();
                String name = m.getDeclaringClass().getName().replace(".", "_");
                Class<?> cls = loader.loadClass(name);
                Constructor con = cls.getDeclaredConstructor();
                con.newInstance();
                Member mem = null;
                Method invoker = null;
//                if( HookUtil.isArt()) {
//                    Field classLoaderField = Class.class.getDeclaredField("classLoader");
//                    classLoaderField.setAccessible(true);
//                    classLoaderField.set(cls, m.getDeclaringClass().getClassLoader());
//                }
                if (!HookUtil.setMadeClassSuper(cls)) {
                    throw new FileNotFoundException("found error!");
                }
                if (m instanceof Method) {
                    mem = cls.getDeclaredMethod(m.getName(), ((Method) m).getParameterTypes());
                    invoker = cls.getDeclaredMethod(m.getName() + "_Invoker", ((Method) m).getParameterTypes());
                } else {
                    mem = cls.getDeclaredConstructor(((Constructor) m).getParameterTypes());
                    invoker = cls.getDeclaredMethod("init_Invoker", ((Constructor) m).getParameterTypes());
                }
                if (mem == null || invoker == null)
                    throw new NullPointerException("mem is null");
                bak.setInvoker(invoker);
                if (m instanceof Method) {
                    String sig = getMethodSignature((Method) m);
                    sig = sig.replace(".", "/");
                    HookUtil.initMethod(m.getDeclaringClass(), m.getName(), sig, Modifier.isStatic(m.getModifiers()));
                } else {
                    String sig = getConstructorSignature((Constructor) m);
                    sig = sig.replace(".", "/");
                    HookUtil.initMethod(m.getDeclaringClass(), "<init>", sig, Modifier.isStatic(m.getModifiers()));
                }
                bak.setNewMethod(mem);
                HookUtil.generateMethodaArt(bak);
                hooked.put(mem.getDeclaringClass().getSimpleName() + "_" + HookUtil.sign(m), bak);
            }
        } catch (Exception e) {
            Log.e("panda", "", e);
        }
    }

    public static void findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) throws NoSuchMethodException {
        if (parameterTypesAndCallback.length == 0 || !(parameterTypesAndCallback[parameterTypesAndCallback.length - 1] instanceof MethodCallback))
            throw new IllegalArgumentException("no callback defined");
        MethodCallback callback = (MethodCallback) parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        Method m = clazz.getDeclaredMethod(methodName, getParameterClasses(clazz.getClassLoader(), parameterTypesAndCallback));
        //XposedBridge.hookMethod(m, callback);
        BackMethod back = new BackMethod();
        back.setOldMethod(m);
        back.setCallback(callback);
        addNeedsMethod(null, back, false);
    }

    public static void findAndHookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) throws NoSuchMethodException {
        if (parameterTypesAndCallback.length == 0 || !(parameterTypesAndCallback[parameterTypesAndCallback.length - 1] instanceof MethodCallback))
            throw new IllegalArgumentException("no callback defined");
        MethodCallback callback = (MethodCallback) parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        Constructor m = clazz.getDeclaredConstructor(getParameterClasses(clazz.getClassLoader(), parameterTypesAndCallback));
        //XposedBridge.hookMethod(m, callback);
        BackMethod back = new BackMethod();
        back.setOldMethod(m);
        back.setCallback(callback);
        addNeedsMethod(null, back, false);
    }

    public static void startHooks(Application context) {
        addNeedsMethod(context, null, true);
    }

    private static void addNeedsMethod(Application con, BackMethod m, boolean end) {
        List<BackMethod> list = new ArrayList<>();
        list.add(m);
        beginHook(list);
    }

    public static Class<?>[] getParameterClasses(ClassLoader classLoader, Object[] parameterTypesAndCallback) {
        Class<?>[] parameterClasses = null;
        for (int i = parameterTypesAndCallback.length - 1; i >= 0; i--) {
            Object type = parameterTypesAndCallback[i];
            if (type == null)
                throw new NullPointerException("parameter type must not be null");

            // ignore trailing callback
            if (type instanceof MethodCallback)
                continue;

            if (parameterClasses == null)
                parameterClasses = new Class<?>[i + 1];

            if (type instanceof Class)
                parameterClasses[i] = (Class<?>) type;
            else if (type instanceof String) {
                try {
                    parameterClasses[i] = classLoader.loadClass((String) type); //((String) type, classLoader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                throw new NullPointerException("parameter type must either be specified as Class or String");
        }

        // if there are no arguments for the method
        if (parameterClasses == null)
            parameterClasses = new Class<?>[0];

        return parameterClasses;
    }

    public static Object invoke(String method, Object thiz, Object[] args) throws Throwable {
        Member old = null;
//        Object res=null;
        MethodCallback callback = null;
        BackMethod back = (BackMethod) hooked.get(method);
        if (back == null) {
            throw new NullPointerException("find back null");
        }
        callback = (MethodCallback) back.getCallback();
        if (callback == null) {
            throw new NullPointerException("find old Method null");
        }
        old = HookUtil.repairMethod(back.getOldMethod(), back.getInvoker(), (long) back.getBackAddr());
//        old = back.getOldMethod();
        if (old == null) {
            throw new NullPointerException("find old Method null");
        }
        return invoke(old, callback, thiz, args);
    }

    public static Object invoke(Member old, MethodCallback callback, Object thiz, Object[] args) throws Throwable {
        if (old instanceof Method) {
            ((Method) old).setAccessible(true);
        } else {
            ((Constructor) old).setAccessible(true);
        }
        MethodHookParam param = new MethodHookParam();
        param.method = old;
        param.thisObject = thiz;
        param.args = args;
        try {
            callback.beforeHookedMethod(param);
        } catch (Throwable t) {
            // reset result (ignoring what the unexpectedly exiting callback did)
            t.printStackTrace();
            param.setResult(null);
            param.returnEarly = false;
        }
        if (param.getThrowable() != null) {
            throw param.getThrowable();
        }
        if (param.returnEarly) {
            return param.getResult();
        }
        if (HookUtil.isArt()) {
            Object res = ((Method) old).invoke(thiz, args);
            param.setResult(res);
            try {
                callback.afterHookedMethod(param);
            } catch (Throwable t) {
                param.setResult(null);
            }
        } else {
            Object res = HookUtil.invokeOriginalMethod(old, thiz, args);
            param.setResult(res);
            try {
                callback.afterHookedMethod(param);
            } catch (Throwable t) {
                param.setResult(null);
            }
        }
        if (param.getThrowable() != null) {
            throw param.getThrowable();
        }
        return param.getResult();
    }

    static String getMethodSignature(Method m) {
        StringBuilder result = new StringBuilder();

        result.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            result.append(getSignature(parameterType));
        }
        result.append(')');
        result.append(getSignature(m.getReturnType()));
        return result.toString();
    }

    public static String getSignature(Class<?> clazz) {
        String primitiveSignature = PRIMITIVE_TO_SIGNATURE.get(clazz);
        if (primitiveSignature != null) {
            return primitiveSignature;
        } else if (clazz.isArray()) {
            return "[" + getSignature(clazz.getComponentType());
        } else {
            // TODO: this separates packages with '.' rather than '/'
            return "L" + clazz.getName() + ";";
        }
    }

    static String getConstructorSignature(Constructor c) {
        StringBuilder result = new StringBuilder();

        result.append('(');
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            result.append(getSignature(parameterType));
        }
        result.append(")V");

        return result.toString();
    }

    //shizhikang add
    private static BackMethod replaceNewOldMethod(BackMethod backMethod) {
        Member old = backMethod.getOldMethod();
        backMethod.setOldMethod(backMethod.getNewMethod());
        backMethod.setNewMethod(old);
        return backMethod;
    }

    public static synchronized void unHook() {
        Log.i(TAG, "unHook");
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            Log.i(TAG, "unHook - key = " + entry.getKey() + ", value = " + entry.getValue().getOldMethod().getName());
            BackMethod backMethod = entry.getValue();
//            System.out.println("shizhikang - getOldMethod: " + backMethod.getOldMethod());
//            System.out.println("shizhikang - getNewMethod: " + backMethod.getNewMethod());
//            System.out.println("shizhikang - getInvoker:" + backMethod.getInvoker());
//            System.out.println("shizhikang - unHook: " + backMethod.getOldMethod());

//            backMethod.setInvoker((Method) backMethod.getOldMethod());
//            backMethod.setNewMethod(backMethod.getOldMethod());
            HookUtil.repair(backMethod.getOldMethod(), backMethod.getBackAddr());
        }
        hooked.clear();
    }

    public static synchronized void unHook(Class clazz) {
        Log.i(TAG, "unHook - clazz :" + clazz);
        HashMap<String, BackMethod> hookedCloned = (HashMap<String, BackMethod>) hooked.clone();
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            if (oldMethod.getDeclaringClass().getName().equals(clazz.getName())) {
                Log.i(TAG, "unHook - clazz - key = " + entry.getKey() + ", value = " + entry.getValue().getOldMethod().getName());
                backMethod.setInvoker((Method) backMethod.getOldMethod());
                backMethod.setNewMethod(backMethod.getOldMethod());
                HookUtil.repair(backMethod.getOldMethod(), backMethod.getBackAddr());

                hookedCloned.remove(entry.getKey());
            }
        }
        hooked = hookedCloned;
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            Log.i(TAG, "unhookAllMethods - now left method :" + oldMethod.getName() + ", class: " + oldMethod.getDeclaringClass());
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
    public static synchronized void unHook(Class clazz, String method, Class... paraTypes) {
        Log.i(TAG, "unHook - clazz :" + clazz + ", method : " + method + ", paraTypes : " + paraTypes);
        HashMap<String, BackMethod> hookedCloned = (HashMap<String, BackMethod>) hooked.clone();
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            if (oldMethod.getDeclaringClass().getName().equals(clazz.getName())
                    && oldMethod.getName().equals(method)
                    && checkParaTypesIsEquals(oldMethod.getParameterTypes(), paraTypes)) {
                Log.i(TAG, "unHook - key = " + entry.getKey() + ", value = " + entry.getValue().getOldMethod().getName());
                backMethod.setInvoker((Method) backMethod.getOldMethod());
                backMethod.setNewMethod(backMethod.getOldMethod());
                HookUtil.repair(backMethod.getOldMethod(), backMethod.getBackAddr());
                hookedCloned.remove(entry.getKey());
            }
        }
        hooked = hookedCloned;
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            Log.i(TAG, "unhookAllMethods - now left method :" + oldMethod.getName() + ", class: " + oldMethod.getDeclaringClass());
        }
    }

    public static synchronized void unHook(Class clazz, String method) {
        Log.i(TAG, "unHook - clazz :" + clazz + ", method : " + method);
        HashMap<String, BackMethod> hookedCloned = (HashMap<String, BackMethod>) hooked.clone();
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            if (oldMethod.getDeclaringClass().getName().equals(clazz.getName())
                    && oldMethod.getName().equals(method)) {
                Log.i(TAG, "unHook - key = " + entry.getKey() + ", value = " + entry.getValue().getOldMethod().getName());
                backMethod.setInvoker((Method) backMethod.getOldMethod());
                backMethod.setNewMethod(backMethod.getOldMethod());
                HookUtil.repair(backMethod.getOldMethod(), backMethod.getBackAddr());
                hookedCloned.remove(entry.getKey());
            }
        }
        hooked = hookedCloned;
        for (Map.Entry<String, BackMethod> entry : hooked.entrySet()) {
            BackMethod backMethod = entry.getValue();
            Method oldMethod = (Method) backMethod.getOldMethod();
            Log.i(TAG, "unhookAllMethods - now left method :" + oldMethod.getName() + ", class: " + oldMethod.getDeclaringClass());
        }
    }

    static class MethodHookWhen extends MethodCallback {
        Object mResult = null;
        MethodHookWhen(Object result) {
            mResult = result;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Object t = (Object) param.thisObject;
            System.out.println("MethodHookWhen - method: "+ param.method+" - beforeHookedMethod- mResult:" + mResult);
            param.setResult(mResult);
        }
    }

    static class MethodHookDoAnswer extends MethodCallback {
        PowerMockito.Answer mAnswer = null;

        public MethodHookDoAnswer(Answer answer) {
            mAnswer = answer;
        }
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            System.out.println("shizhikang - MethodHookDoAnswer: " + param.method);
            mAnswer.beforeCall(param.args);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            mAnswer.afterCall(param.args);
        }
    }
    static class MethodHookWhenThrow extends MethodCallback {
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

    public static Object[] getParameterTypesAndCallback(Object callback, Class... paraTypes) {
        Object[] parameterTypesAndCallback = new Object[paraTypes.length + 1];
        for (int i = 0; i < paraTypes.length; i++) {
            parameterTypesAndCallback[i] = paraTypes[i];
        }
        parameterTypesAndCallback[paraTypes.length] = callback;
        return parameterTypesAndCallback;
    }

    public static synchronized void whenThenReturn(Class clazz, String method, Object returnResult, Class... paraTypes) {
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        unHook(clazz, method, paraTypes);
        try {
            findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookWhen(returnResult), paraTypes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static synchronized void doAnswer(Class clazz, String method, Answer answer, Class... paraTypes) {
        Log.e(TAG, "doAnswer - method: " + method);
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        unHook(clazz, method, paraTypes);
        try {
            findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookDoAnswer(answer), paraTypes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static synchronized void whenThrow(Class clazz, String method, Throwable throwable, Class... paraTypes) {
        clazz = checkIfClassContainMethod(clazz, method, paraTypes);
        unHook(clazz, method);
        try {
            findAndHookMethod(clazz, method, getParameterTypesAndCallback(new MethodHookWhenThrow(throwable), paraTypes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized PowerMockito.MockObject mock(Class clazz, Method... methods) {
        MockObject object = new MockObject();
//        Method[] declaredMethods = clazz.getDeclaredMethods();
//        if (methods == null || methods.length == 0) {
//            methods = declaredMethods;
//        }
        object.methods = methods;
        object.clazz = clazz;
        for (Method method : methods) {
            PowerMockito.MethodHookMock answer = new MethodHookMock();
            object.methodCallMap.put(method, answer);
            doAnswer(clazz, method.getName(), answer, method.getParameterTypes());
        }

        return object;
    }
}
