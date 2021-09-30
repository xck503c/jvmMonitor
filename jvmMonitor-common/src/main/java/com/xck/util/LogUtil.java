package com.xck.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.xck.SysConstants;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 日志工具
 *
 * @author xuchengkun
 * @date 2021/08/29 19:29
 **/
public class LogUtil {

    private final static String logFileName = "output.log";

    public static synchronized void log(String log, String level) {
        String threadName = Thread.currentThread().getName();
        String curData = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
        FileUtil.appendUtf8String(String.format("%s [%s] %s - %s\n", curData, threadName, level, log)
                , SysConstants.homePath + "/" + logFileName);
    }

    public static synchronized void error(String log, String level, Throwable e) {
        String threadName = Thread.currentThread().getName();
        String curData = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
        FileUtil.appendUtf8String(String.format("%s [%s] %s - %s\n", curData, threadName, level, log)
                , SysConstants.homePath + "/" + logFileName);
        try {
            e.printStackTrace(new PrintWriter(logFileName));
        } catch (FileNotFoundException e1) {
        }
    }

    public static void info(String log) {
        log(log, "INFO");
    }

    public static void error(String log, Throwable e) {
        error(log, "ERROR", e);
    }

    public static void warn(String log) {
        log(log, "WARN");
    }
}
