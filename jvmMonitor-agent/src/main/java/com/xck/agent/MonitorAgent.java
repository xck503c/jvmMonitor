package com.xck.agent;

import cn.hutool.json.JSONObject;
import com.xck.SysConstants;
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
        try {
            JSONObject configJson = new JSONObject(agentArgs);
            SysConstants.setHomePath(configJson.getStr("homePath"));
            AnnotationScanner.scanServer(configJson.getInt("serverPort"), 2);
            AnnotationScanner.scan();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
