package com.xck.agent;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.xck.agent.controller.ServerController;
import com.xck.agent.util.PluginClassLoader;
import com.xck.annotation.RequestMapping;

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
    public static Map<String, ObjExecutor> originPluginMap = new HashMap<>();
    //外部插件
    public static Map<String, ObjExecutor> controllerMap = new HashMap<>();

    /**
     * 注解扫描
     *
     */
    public static void scan() throws Exception {
        scanOriginPlugin();
    }

    public static void scanOriginPlugin() throws InstantiationException, IllegalAccessException {
        Map<String, ObjExecutor> neworiginPluginMap = new HashMap<>();
        scanController(ServerController.class, neworiginPluginMap);
        originPluginMap = neworiginPluginMap;
    }

    /**
     * @RequestMapping注解扫描注册到controllerMap
     * @throws Exception
     */
    public static void scanPlugin(String scanPath) throws Exception{
        System.out.println("扫描插件: " + scanPath);

        Map<String, ObjExecutor> newPluginMap = new HashMap<>();

        if (!FileUtil.exist(scanPath)) {
            System.out.println("未扫描到插件");
            return;
        }

        File[] files = FileUtil.ls(scanPath);
        URL[] urls = new URL[files.length];
        for (int i=0; i<files.length; i++) {
            urls[i] = new URL("file:" + files[i].getPath());
        }
        PluginClassLoader pluginClassLoader = new PluginClassLoader(urls);
        Thread.currentThread().setContextClassLoader(pluginClassLoader);

        //扫描对应的类
        Set<Class<?>> requests = ClassUtil.scanPackageByAnnotation("com", RequestMapping.class);

        for (Class<?> clzz : requests) {
            scanController(clzz, newPluginMap);
        }

        pluginClassLoader.close();
        Thread.currentThread().setContextClassLoader(null);

        controllerMap = newPluginMap;
    }

    private static void scanController(Class<?> clzz, Map<String, ObjExecutor> newPluginMap)
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
            newPluginMap.put(basePath + methodPath, new ObjExecutor(instance, method));
            System.out.println("注册接口: " + (basePath + methodPath));
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