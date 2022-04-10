package com.xck;

import java.lang.instrument.Instrumentation;

/**
 * 系统常量配置
 *
 * @author xuchengkun
 * @date 2021/08/29 19:32
 **/
public class SysConstants {

    public static String homePath = System.getProperty("user.dir");

    public static Instrumentation inst;

    public static void setHomePath(String homePath) {
        SysConstants.homePath = homePath;
    }
}
