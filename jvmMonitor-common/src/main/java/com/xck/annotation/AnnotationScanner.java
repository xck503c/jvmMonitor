package com.xck.annotation;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.xck.util.LogUtil;
import com.xck.util.PluginClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
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

    //内置插件
    public static Map<Integer, ObjExecutor> originPluginMap = new HashMap<>();


    public static void scanOriginPlugin(Class clzz) throws InstantiationException, IllegalAccessException {
        Map<Integer, ObjExecutor> neworiginPluginMap = new HashMap<>();
        scanController(clzz, neworiginPluginMap);
        originPluginMap = neworiginPluginMap;
    }

    private static void scanController(Class<?> clzz, Map<Integer, ObjExecutor> newPluginMap)
            throws IllegalAccessException, InstantiationException {
        String basePath = AnnotationUtil.getAnnotationValue(clzz, RequestMapping.class);
        //扫描对应类中的方法
        Method[] methods = ClassUtil.getDeclaredMethods(clzz);
        for (Method method : methods) {
            String methodPath = AnnotationUtil.getAnnotationValue(method, RequestMapping.class);
            if (StrUtil.isBlank(methodPath)) {
                continue;
            }

            Object instance = clzz.newInstance();
            newPluginMap.put((basePath + methodPath).hashCode(), new ObjExecutor(instance, method));
        }
    }

    /**
     * 对象执行器，存储执行的方法和对象，便于反射执行
     */
    public static class ObjExecutor {
        private Object object; //执行对象
        private Method method; //执行方法

        public ObjExecutor(Object object, Method method) {
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

    }
}
