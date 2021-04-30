package com.xck.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 缓存查询agent
 *
 * @author xuchengkun
 * @date 2021/04/30 12:21
 **/
public class CacheAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        String[] args = agentArgs.split("#");
        if ("userCache".equals(args[0])) {
            String className = "com.hskj.model.cache.visit.CacheHolder";
            Class clzz = CacheAgent.class.getClassLoader().loadClass(className);
            Field userBeanInfosField = clzz.getDeclaredField("userBeanInfos");
            userBeanInfosField.setAccessible(true);
            Map userCacheMap = (Map)userBeanInfosField.get(null);
            if(userCacheMap == null || userCacheMap.isEmpty()) {
                System.out.println("user cache is empty");
                return;
            }
            Object userBeanHolder = userCacheMap.get(args[1]);
            if(userBeanHolder == null) {
                System.out.println(args[1] + " user cache is empty");
                return;
            }
            Field userBeanField = userBeanHolder.getClass().getDeclaredField("userBean");
            userBeanField.setAccessible(true);
            Object userBean = userBeanField.get(userBeanHolder);
            System.out.println(userBean);

            Field paramField = userBean.getClass().getDeclaredField("paramMap");
            paramField.setAccessible(true);
            Object paramMap = paramField.get(userBean);
            System.out.println(paramMap);
        }
    }
}
