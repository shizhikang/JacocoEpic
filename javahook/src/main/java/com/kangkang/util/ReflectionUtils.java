package com.kangkang.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class ReflectionUtils {

    /**
     * 创建类或内部类
     * @param className 类名
     * @param params 构造函数中的参数，如果是非静态内部类，第一个参数一定是外部类对象
     * @return 类对象
     * */
    public static Object createPrivateInnerClass(String className, Class[] classes, Object[] params) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class clazz = Class.forName(className);
        Constructor constructor = clazz.getDeclaredConstructor(classes);
        constructor.setAccessible(true);
        Object object = constructor.newInstance(params);
        return object;
    }
    /**
     * 创建类或内部类
     * @param className 类名
     * @param params 构造函数中的参数，如果是非静态内部类，第一个参数一定是外部类对象
     * @return 类对象
     * */
    public static Object createPrivateInnerClass(String className, Object... params) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Class.forName(className);
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object object = constructor.newInstance(params);
        return object;
    }
    /**
     * 获取静态成员变量的值
     * @param className 类名
     * @param filedName 变量名
     * @return 变量值
     */
    public static Object getStaticField(String className, String filedName) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Class clazz = Class.forName(className);
        return getStaticField(clazz, filedName);
    }

    /**
     * 获取静态成员变量的值
     * @param clazz 类名
     * @param filedName 变量名
     * @return 变量值
     */
    public static Object getStaticField(Class clazz, String filedName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(null);
    }

    /**
     * 递归查找静态成员变量（如果本类找不到，会递归查询父类中的变量）
     * @throws NullPointerException 当递归查询到Object类也没有找到成员时会抛出异常
     * @param clazz 类名
     * @param filedName 变量名
     * @return 变量值
     */
    public static Object getStaticFieldAnyway(Class clazz, String filedName) throws IllegalAccessException {
        Field field = null;
        try {
            field = clazz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            field = getPrivateFieldAnyway(clazz.getSuperclass(), filedName);
        }
        field.setAccessible(true);
        return field.get(null);
    }
    /**
     * 获取成员变量
     * @param instance 对象
     * @param filedName 变量名
     * @return 变量值
     */
    public static Object getPrivateField(Object instance, String filedName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(instance);
    }
    /**
     * 查找父类成员变量
     * @param instance 本对象
     * @param filedName 变量名
     * @return 父类变量值
     */
    public static Object getSuperPrivateField(Object instance, String filedName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getSuperclass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * 递归查找成员变量（如果本类找不到，会递归查询父类中的变量）
     * @throws NullPointerException 当递归查询到Object类也没有找到成员时会抛出异常
     * @param instance 对象
     * @param filedName 变量名
     * @return 变量值
     */
    public static Object getPrivateFieldAnyway(Object instance, String filedName) throws IllegalAccessException {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            field = getPrivateFieldAnyway(instance.getClass().getSuperclass(), filedName);
        }
        field.setAccessible(true);
        return field.get(instance);
    }
    private static Field getPrivateFieldAnyway(Class clazz, String filedName) throws IllegalAccessException {
        Field field = null;
        try {
            field = clazz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            field = getPrivateFieldAnyway(clazz.getSuperclass(), filedName);
        }
        return field;
    }

    /**
     * 设置静态变量值
     * @param clazz 类名
     * @param fieldName 变量名
     * @param value 变量值
     */
    public static void setStaticField(Class clazz, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * 递归设置静态成员变量（如果本类找不到，会递归设置父类中的变量）
     * @throws NullPointerException 当递归查询到Object类也没有找到成员时会抛出异常
     * @param clazz 类名
     * @param fieldName 变量名
     * @param value 变量值
     */
    public static void setStaticFieldAnyway(Class clazz, String fieldName, Object value) throws IllegalAccessException {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = getPrivateFieldAnyway(clazz.getSuperclass(), fieldName);
        }
        field.setAccessible(true);
        field.set(null, value);
    }
    public static void setPrivateField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
    public static void setPrivateFieldAnyway(Object instance, String fieldName, Object value) throws IllegalAccessException {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = getPrivateFieldAnyway(instance.getClass().getSuperclass(), fieldName);
        }
        field.setAccessible(true);
        field.set(instance, value);
    }
    public static void setSuperPrivateField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Object invokeStaticMethod(String className, String methodName, Class[] classes, Object[] objects) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(null, objects);
    }
    /**
     * 访问私有方法
     * @param instance
     * @param methodName
     * @param classes
     * @param objects
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokePrivateMethod(Object instance, String methodName, Class[] classes, Object[] objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, objects);
    }
    public static Object invokeSuperPrivateMethod(Object instance, String methodName, Class[] classes, Object[] objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getSuperclass().getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, objects);
    }

    /*
     *
     * */
    public static Object invokePrivateMethodAnyway(Object instance, String methodName, Class[] classes, Object[] objects) throws InvocationTargetException, IllegalAccessException {
        Object result = null;
        Method method = null;
        try {
            method = instance.getClass().getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            method = getPrivateMethodAnyway(instance.getClass().getSuperclass(), methodName, classes, objects);
        }
        method.setAccessible(true);
        result = method.invoke(instance, objects);
        return result;
    }

    public static Method getPrivateMethodAnyway(Class clazz, String methodName, Class[] classes, Object[] objects) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            method = getPrivateMethodAnyway(clazz.getSuperclass(), methodName, classes, objects);
        }

        return method;
    }

    public static Field getFiled(Object instance, String name) throws NoSuchFieldException {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static Field getFiled(Class clazz, String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * 反射调用删除final修饰符，用以后续修改非基本类型final成员（android无须调用此方法，可以直接反射设置）
     *
     * */
    public static void removeFieldFinalModifier(Field field) {
        try {
            // DoallJREsimplementFieldwithaprivateivarcalled"modifiers"?
            //java写法
            final Field modifiersField=Field.class.getDeclaredField("modifiers");
            //android写法
//            final Field modifiersField= Field.class.getDeclaredField("accessFlags");
            final boolean doForceAccess = !modifiersField.isAccessible();
            if (doForceAccess) {
                modifiersField.setAccessible(true);
            }
            try {
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                modifiersField.setInt(field, field.getModifiers() | Modifier.PUBLIC);
            } finally {
                System.out.println("shizhikang - final " + Modifier.isFinal(field.getModifiers()));
                if (doForceAccess) {
                    modifiersField.setAccessible(false);
                }
                System.out.println("shizhikang - final modifiersField:" + modifiersField);
            }
        } catch (final NoSuchFieldException ignored) {
            ignored.printStackTrace();
            // Thefieldclasscontainsalwaysamodifiersfield
        } catch (final IllegalAccessException ignored) {
            // Themodifiersfieldismadeaccessible
            ignored.printStackTrace();
        }
    }

}