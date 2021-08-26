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
        String agentPath = System.getProperty("user.dir")
                + "/jvmMonitor-agent-V1.0.0-jar-with-dependencies.jar";
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        vm.loadAgent(agentPath);
        vm.detach();
    }
}
