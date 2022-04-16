package com.xck.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import com.xck.common.constant.SysConstants;

import java.util.Date;

/**
 * 日志工具
 *
 * @author xuchengkun
 * @date 2021/08/29 19:29
 **/
public class LogUtil {

    private final static String logFileName = "output.log";
    private final static String debugFileName = "debug.log";

    public static void log(String fileName, String log, String level) {
        synchronized (fileName) {
            String threadName = Thread.currentThread().getName();
            String curData = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
            FileUtil.appendUtf8String(String.format("%s [%s] %s - %s\n", curData, threadName, level, log)
                    , SysConstants.homePath + "/" + fileName);
        }
    }

    public static void error(String fileName, String log, String level, Throwable e) {
        synchronized (fileName) {
            String threadName = Thread.currentThread().getName();
            String curData = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
            FileUtil.appendUtf8String(String.format("%s [%s] %s - %s, case=%s\n", curData, threadName, level, log, ExceptionUtil.getRootCauseMessage(e))
                    , SysConstants.homePath + "/" + fileName);
        }
    }

    public static void methodDebug(String log) {
        log(debugFileName, log, "INFO");
    }

    public static void info(String log) {
        log(logFileName, log, "INFO");
    }

    public static void error(String log, Throwable e) {
        error(logFileName, log, "ERROR", e);
    }

    public static void warn(String log) {
        log(logFileName, log, "WARN");
    }
}
