package com.xck.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件类加载器
 *
 * @author xuchengkun
 * @date 2021/08/26 16:45
 **/
public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader() {
        super(new URL[0], null);
    }

    public PluginClassLoader(URL[] urls) {
        super(urls, null);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            //默认向上抛
            return ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
        }

        return super.loadClass(name);
    }
}
