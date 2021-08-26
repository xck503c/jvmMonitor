package com.xck.agent;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.xck.agent.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 注解扫描器
 *
 * @author xuchengkun
 * @date 2021/08/25 15:48
 **/
public class AnnotationScanner {

    public static Map<String, Executor> controllerMap = new HashMap<>();

    /**
     * 注解扫描
     *
     * @param basePackage 扫描基础包
     */
    public static void scan(String basePackage) throws Exception {
        requestMapHandler(basePackage);
    }

    /**
     * 请求映射处理器
     * @param basePackage
     * @throws Exception
     */
    public static void requestMapHandler(String basePackage) throws Exception{
        //扫描对应的类
        Set<Class<?>> requests = ClassUtil.scanPackageByAnnotation(basePackage, RequestMapping.class);

        for (Class clzz : requests) {
            String basePath = AnnotationUtil.getAnnotationValue(clzz, RequestMapping.class);
            //扫描对应类中的方法
            Method[] methods = ClassUtil.getDeclaredMethods(clzz);
            for (Method method : methods) {
                String methodPath = AnnotationUtil.getAnnotationValue(method, RequestMapping.class);
                if (StrUtil.isBlank(methodPath)) {
                    continue;
                }

                Object instance = clzz.newInstance();
                controllerMap.put(basePath + methodPath, new Executor(instance, method));
            }
        }
    }

    public static class Executor {
        private Object object; //执行对象
        private Method method; //执行方法

        public Executor(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        public Object getObject() {
            return object;
        }

        public Method getMethod() {
            return method;
        }
    }

    public static void main(String[] args) throws Exception {
        AnnotationScanner.scan("com.xck.agent");
    }
}
