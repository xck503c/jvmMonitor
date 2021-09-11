package com.xck;

import cn.hutool.core.util.RuntimeUtil;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.xck.command.Command;
import com.xck.server.NettyServer;
import com.xck.server.ServerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.List;
import java.util.Scanner;

/**
 * 交互终端
 *
 * @author xuchengkun
 * @date 2021/09/06 10:07
 **/
public class JvmMonitorTerminal {

    public static void main(String[] args) throws Exception{

        SysConstants.setHomePath(System.getProperty("user.dir"));

        //1. 读取端口
        //jps列表
        List<String> jpsList = RuntimeUtil.execForLines("jps");
        for (int i = 0; i < jpsList.size(); i++) {
            System.out.println((i + 1) + ". " + jpsList.get(i));
        }

        Scanner scanner = new Scanner(System.in);
        int targetPid = -1;
        while (true) {
            System.out.print("请选择序号: ");
            String line = scanner.nextLine();
            try {
                int order = Integer.parseInt(line);
                targetPid = Integer.parseInt(jpsList.get(order-1).split(" ")[0]);

                ServerService.nettyServer = new NettyServer();
                ServerService.nettyServer.start();
                ServerService.startConnectTimeout();

                String os = System.getProperty("os.name");
                String result = "";
                if (os.toLowerCase().startsWith("win")) {
                    result = RuntimeUtil.execForStr("java -cp .;hutool-all-4.6.3.jar;jvmMonitor-boot-V1.0.0.jar;tools.jar com.xck.boot.BootStrap "
                            + targetPid + " " + ServerService.nettyServer.getPort());
                } else {
                    result = RuntimeUtil.execForStr("nohup java -cp '.:hutool-all-4.6.3.jar:jvmMonitor-boot-V1.0.0.jar:tools.jar' com.xck.boot.BootStrap "
                            + targetPid + " " + ServerService.nettyServer.getPort() + " &");
                }

                if (!result.contains("错误") && !result.contains("Exception")) {
                    System.out.println("启动成功");
                    break;
                } else {
                    System.out.println("启动失败: " + result);
                }
            } catch (NumberFormatException e) {
                System.out.print("选择参数非法！");
                System.out.print("请选择序号: ");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        while (true) {
            String line = scanner.nextLine();
            try {
                if ("help".equals(line)) {
                    JCommander.newBuilder().addObject(new Command()).build().usage();
                }else {
                    System.out.println("echo: " + line);
                    Command command = new Command();
                    JCommander.newBuilder().addObject(command).build().parse(line.split(" "));
                    ServerService.writeCommand(command);
                }
            } catch (NumberFormatException e) {
                System.out.print("请选择序号: ");
            } catch (ParameterException e) {
                System.err.println("参数非法: " + e);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
