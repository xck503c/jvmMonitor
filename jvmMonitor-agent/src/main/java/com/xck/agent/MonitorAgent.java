package com.xck.agent;

import cn.hutool.json.JSONObject;
import com.xck.SysConstants;
import com.xck.agent.controller.MethodMonitorController;
import com.xck.agent.controller.ServerController;
import com.xck.agent.netty.ClientService;
import com.xck.annotation.AnnotationScanner;
import com.xck.util.LogUtil;

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
            SysConstants.inst = inst;
            LogUtil.info("attach start, args: " + agentArgs);
            ClientService.client(configJson.getInt("serverPort"));
            AnnotationScanner.scanOriginPlugin(ServerController.class);
            AnnotationScanner.scanOriginPlugin(MethodMonitorController.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
