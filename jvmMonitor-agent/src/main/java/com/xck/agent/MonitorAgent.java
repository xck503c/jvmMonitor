package com.xck.agent;

import com.xck.agent.netty.NettyServer;

import java.lang.instrument.Instrumentation;

/**
 * 监控agent主入口
 *
 * @author xuchengkun
 * @date 2021/04/30 12:21
 **/
public class MonitorAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        NettyServer nettyServer = new NettyServer();
        try {
            System.out.println("attach start");

            nettyServer.start();

            AnnotationScanner.scan("com.xck.agent");

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            nettyServer.shutdown();
        }
    }
}
