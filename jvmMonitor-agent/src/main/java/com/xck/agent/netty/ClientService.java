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
    public static ScanPluginThread scanPluginThread;

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

        if (scanPluginThread != null) {
            scanPluginThread.doStop();
        }
        scanPluginThread = new ScanPluginThread();
        scanPluginThread.setDaemon(true);
        scanPluginThread.setName("scan-plugin-thread");
        scanPluginThread.start();
    }

    public static class ScanPluginThread extends Thread {

        private volatile boolean isRunning;

        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                try {
                    AnnotationScanner.scanPlugin(SysConstants.homePath + "/plugin");
                } catch (Throwable e) {
                    LogUtil.error("scan plugin error ", e);
                } finally {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void doStop(){
            isRunning = false;
            interrupt();
        }
    }
}
