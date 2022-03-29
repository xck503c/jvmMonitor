package com.xck;

import com.xck.model.ServerService;
import com.xck.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 交互终端
 *
 * @author xuchengkun
 * @date 2021/09/06 10:07
 **/
@SpringBootApplication
public class JvmMonitorTerminal {

    public static void main(String[] args) throws Exception {

        ServerService.nettyServer  = new NettyServer();
        ServerService.nettyServer.start();

        /**
         * --Dserver.port=xxxx
         */
        SpringApplication.run(JvmMonitorTerminal.class, args);

//        consoleMode();
    }

//    public static void consoleMode() throws Exception {
//        SysConstants.setHomePath(System.getProperty("user.dir"));
//
//        //1. 读取端口
//        //jps列表
//        List<String> jpsList = RuntimeUtil.execForLines("jps");
//        for (int i = 0; i < jpsList.size(); i++) {
//            System.out.println((i + 1) + ". " + jpsList.get(i));
//        }
//
//
//        AggregateCompleter monitorCompleter = new AggregateCompleter(Command.getCompleter());
//        ConsoleReader lineReader = new ConsoleReader();
//        lineReader.addCompleter(monitorCompleter);
//
//        String prompt = "monitor> ";
//
//        int targetPid = -1;
//        while (true) {
//            System.out.println("请选择序号: ");
//            String line = null;
//            try {
//                line = lineReader.readLine(prompt);
//                if ("quit".equals(line)) {
//                    System.out.println("退出");
//                    System.exit(0);
//                }
//
//                int order = Integer.parseInt(line);
//                targetPid = Integer.parseInt(jpsList.get(order - 1).split(" ")[0]);
//
//                ServerService.nettyServer = new NettyServer();
//                ServerService.nettyServer.start();
//                ServerService.startConnectTimeout();
//
//                String os = System.getProperty("os.name");
//                String result = "";
//                String javaHome = System.getProperty("java.home") + "/../lib/tools.jar";
//                if (javaHome.contains(" ")) {
//                    String[] paths = javaHome.split(" ");
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < paths.length; i++) {
//                        sb.append(paths[i]);
//                        if (i != paths.length - 1) {
//                            sb.append("\" \"");
//                        }
//                    }
//                    javaHome = sb.toString();
//                }
//                if (os.toLowerCase().startsWith("win")) {
//                    result = RuntimeUtil.execForStr("java -Xbootclasspath/a:" + javaHome + " -cp .;./* com.xck.boot.BootStrap "
//                            + targetPid + " " + ServerService.nettyServer.getPort());
//                } else {
//                    result = RuntimeUtil.execForStr("nohup java -Xbootclasspath/a:" + javaHome + " -cp '.:hutool-all-4.6.3.jar:jvmMonitor-boot.jar:' com.xck.boot.BootStrap "
//                            + targetPid + " " + ServerService.nettyServer.getPort() + " &");
//                }
//
//                if (!result.contains("错误") && !result.contains("Exception") && !result.contains("Error")) {
//                    System.out.println("启动成功, " + result);
//                    break;
//                } else {
//                    System.out.println("启动失败: " + result);
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("选择参数非法！");
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
//
//        while (true) {
//            String line = null;
//            try {
//                line = lineReader.readLine(prompt);
//                if ("help".equals(line)) {
//                    JCommander.newBuilder().addObject(new Command()).build().usage();
//                } else if ("quit".equals(line)) {
//                    System.out.println("退出");
//                    System.exit(0);
//                } else {
//                    Command command = new Command();
//                    JCommander.newBuilder().addObject(command).build().parse(line.split(" "));
//                    ServerService.writeCommand(command);
//                    ServerService.commandRespSQ.take();
//                }
//            } catch (ParameterException e) {
//                System.err.println("参数非法: " + e);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
