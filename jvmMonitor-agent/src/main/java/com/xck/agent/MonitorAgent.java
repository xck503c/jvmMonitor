package com.xck.agent;

import java.lang.instrument.Instrumentation;

/**
 * 监控agent主入口
 *
 * @author xuchengkun
 * @date 2021/04/30 12:21
 **/
public class MonitorAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            System.out.println("attach start");
            AnnotationScanner.scan();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
