package com.xck.boot;

import com.sun.tools.attach.VirtualMachine;

/**
 * 启动
 *
 * @author xuchengkun
 * @date 2021/04/30 12:23
 **/
public class BootStrap {

    public static void main(String[] args) throws Exception{
        String pid = args[0];
        String cacheType = args[1];

        String agentArgs = "";
        if("userCache".equals(cacheType)){
            String userId = args[2];
            agentArgs = "userCache#" + userId;
        }

        String agentPath = System.getProperty("user.dir")
                + "/jvmMonitor-agent-V1.0.0-jar-with-dependencies.jar";
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentPath, agentArgs);
        vm.detach();
    }
}
