package com.xck.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

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
     * @param param      调用所需的参数信息，json格式
     * @return
     */
    public static Object methodStaticInvoke(String className, String methodName, JSONArray param)
            throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

        if (param == null) {
            param = new JSONArray();
        }

        Class clzz = ClassUtil.loadClass(className);
        Method[] methodNames = ClassUtil.getDeclaredMethods(clzz);
        for (Method setMethod : methodNames) {
            if (setMethod.getName().equals(methodName)) {
                setMethod.setAccessible(true);

                LogUtil.info("找到同名方法, 核对...");

                Class[] parameterTypes = setMethod.getParameterTypes();
                if (parameterTypes.length != param.size()) {
                    continue;
                }

                Object[] args = new Object[parameterTypes.length];
                try {
                    for (int i = 0; i < parameterTypes.length; i++) {
                        args[i] = json2Obj(param.getStr(i), parameterTypes[i]);
                    }
                    return setMethod.invoke(null, args);
                } catch (Exception e) {
                    //说明不符合
                    LogUtil.error("调用异常, 跳过", e);
                }
            }
        }

        throw new NoSuchMethodException();
    }

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
     *
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
     *
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

    public static String obj2Json(Object o) {
        if (o == null) return "null";

        String result = "";
        if (o instanceof Number || o instanceof CharSequence) {
            result = o.toString();
        } else {
            result = new JSONObject(o).toJSONString(0);
        }

        return result;
    }

    public static Object json2Obj(String str, Class clzz) throws NoSuchFieldException {
        try {
            return JSONUtil.toBean(str, clzz);
        } catch (JSONException e) {
            if (clzz == Object.class || clzz == String.class) {
                return str;
            } else if (clzz == Integer.class) {
                return Integer.parseInt(str);
            } else if (clzz == Long.class) {
                return Long.parseLong(str);
            } else if (clzz == Byte.class) {
                return Byte.parseByte(str);
            }
        }

        throw new NoSuchFieldException("非法值=" + str + ", clzz=" + clzz);
    }

    public static void main(String[] args) throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("10");
        System.out.println(methodStaticInvoke("com.xck.util.ClassAgentUtil", "obj2Json"
                , jsonArray));
    }
}
