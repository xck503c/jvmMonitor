package com.xck.util;

import cn.hutool.core.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 类工具
 *
 * @author xuchengkun
 * @date 2021/08/26 09:03
 **/
public class ClassAgentUtil {

    /**
     * 静态方法调用（带参数）
     *
     * @param className  调用类名
     * @param methodName 方法名
     * @param param      参数名
     * @return
     */
    public static Object methodStaticInvoke(String className, String methodName, Object... param)
            throws InvocationTargetException, IllegalAccessException {

        Class clzz = ClassUtil.loadClass(className);

        Class[] classes = new Class[param.length];
        for (int i = 0; i < param.length; i++) {
            classes[i] = param[i].getClass();
        }
        Method method = ClassUtil.getDeclaredMethod(clzz, methodName, classes);
        method.setAccessible(true);
        return method.invoke(null, param);
    }

    public static Object methodStaticInvoke(String className, String methodName)
            throws InvocationTargetException, IllegalAccessException {

        Class clzz = ClassUtil.loadClass(className);

        Class[] classes = new Class[0];
        Method method = ClassUtil.getDeclaredMethod(clzz, methodName, classes);
        method.setAccessible(true);
        return method.invoke(null);
    }

    /**
     * 普通类方法调用（带参数）
     *
     * @param instance   调用对象名
     * @param methodName 方法名
     * @param param      参数名
     * @return
     */
    public static Object methodObjInvoke(Object instance, String methodName, Object... param)
            throws InvocationTargetException, IllegalAccessException {

        Class[] classes = new Class[param.length];
        for (int i = 0; i < param.length; i++) {
            classes[i] = param[i].getClass();
        }
        Method method = ClassUtil.getDeclaredMethod(instance.getClass(), methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, param);
    }

    /**
     * 根据类名创建对象
     * @param className
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object newObj(String className)
            throws IllegalAccessException, InstantiationException {
        Class clzz = ClassUtil.loadClass(className);
        return clzz.newInstance();
    }

    /**
     * 根据类名创建对象并针对传参赋值
     * @param className
     * @param valueMap
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object newObjAndSetValue(String className, Map<String, Object> valueMap)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object newObj = newObj(className);
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            methodObjInvoke(newObj, entry.getKey(), entry.getValue());
        }
        return newObj;
    }

    public static void main(String[] args) throws Exception{
        methodStaticInvoke("com.xck.agent.util.ClassAgentUtil", "a", 1);
    }
}
