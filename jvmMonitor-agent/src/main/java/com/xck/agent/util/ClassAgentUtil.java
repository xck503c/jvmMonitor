package com.xck.agent.util;

import cn.hutool.core.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        Method method = cn.hutool.core.util.ClassUtil.getDeclaredMethod(instance.getClass(), methodName, classes);
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

    public static void main(String[] args) throws Exception{
        methodStaticInvoke("com.xck.agent.util.ClassAgentUtil", "a", 1);
    }
}
