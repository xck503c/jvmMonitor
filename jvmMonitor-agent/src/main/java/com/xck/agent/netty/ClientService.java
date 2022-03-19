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
    public static void client(int port) {
        ClientService.port = port;

        NettyClient nettyClient = new NettyClient(port);
        nettyClient.start();
    }
}
