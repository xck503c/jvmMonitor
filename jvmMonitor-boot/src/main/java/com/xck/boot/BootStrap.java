package com.xck.boot;

import cn.hutool.json.JSONObject;
import com.sun.tools.attach.VirtualMachine;

/**
 * 启动
 *
 * @author xuchengkun
 * @date 2021/04/30 12:23
 **/
public class BootStrap {

    public static void main(String[] args) throws Exception{
        String agentPath = System.getProperty("user.dir")
                + "/jvmMonitor-agent-V1.0.0-jar-with-dependencies.jar";
        VirtualMachine vm = VirtualMachine.attach(args[0]);

        JSONObject configJson = new JSONObject();
        configJson.put("homePath", System.getProperty("user.dir"));
        configJson.put("serverPort", Integer.parseInt(args[1]));
        vm.loadAgent(agentPath, configJson.toJSONString(0));
        vm.detach();

    }
}
