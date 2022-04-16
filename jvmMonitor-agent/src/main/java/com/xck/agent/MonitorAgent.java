package com.xck.agent;

import cn.hutool.json.JSONObject;
import com.xck.agent.methodMonitor.MethodInvokeListenerManager;
import com.xck.common.constant.SysConstants;
import com.xck.agent.controller.MethodMonitorController;
import com.xck.agent.controller.ServerController;
import com.xck.agent.netty.ClientService;
import com.xck.common.annotation.AnnotationScanner;
import com.xck.common.util.LogUtil;

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
            MethodInvokeListenerManager.inst = inst;
            LogUtil.info("attach start, args: " + agentArgs);
            ClientService.client(configJson.getInt("serverPort"));
            AnnotationScanner.scanOriginPlugin(ServerController.class);
            AnnotationScanner.scanOriginPlugin(MethodMonitorController.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
