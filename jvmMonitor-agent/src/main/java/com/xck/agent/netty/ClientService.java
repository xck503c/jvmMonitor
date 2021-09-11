package com.xck.agent.netty;

import com.xck.SysConstants;
import com.xck.annotation.AnnotationScanner;
import com.xck.util.LogUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端服务类
 *
 * @author xuchengkun
 * @date 2021/09/07 10:22
 **/
public class ClientService {

    public static ChannelHandlerContext ctx;
    public static int port;

    /**
     * 控制服务端启停
     *
     * @param port 服务端监听端口
     * @return
     */
    public static void client(int port) throws InterruptedException {
        ClientService.port = port;

        NettyClient nettyClient = new NettyClient(port);
        nettyClient.start();

        ScanPluginThread scanPluginThread = new ScanPluginThread();
        scanPluginThread.setDaemon(true);
        scanPluginThread.setName("scan-plugin-thread");
        scanPluginThread.start();
    }

    public static class ScanPluginThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    AnnotationScanner.scanPlugin(SysConstants.homePath + "/plugin");
                } catch (Exception e) {
                    LogUtil.error("scan plugin error " + e);
                } finally {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
