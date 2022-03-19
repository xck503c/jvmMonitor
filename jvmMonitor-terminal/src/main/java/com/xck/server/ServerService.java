package com.xck.server;

import cn.hutool.json.JSONObject;
import com.xck.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.concurrent.SynchronousQueue;

/**
 * 客户端服务类
 *
 * @author xuchengkun
 * @date 2021/09/07 10:22
 **/
public class ServerService {

    public static SynchronousQueue<String> commandRespSQ = new SynchronousQueue<>();
    public static NettyServer nettyServer;
    public static ChannelHandlerContext ctx;
    public static long lastTime;

    public static void startConnectTimeout() {
        Thread connectionTimeout = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (ctx == null || System.currentTimeMillis() - lastTime < 35000) {
                            Thread.sleep(3000);
                        } else {
                            System.out.println("连接超时, 关闭连接");
                            ctx.close();
                            ctx = null;
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        });
        connectionTimeout.setDaemon(true);
        connectionTimeout.setName("check-timeout-client");
        connectionTimeout.start();
    }

    public static void writeCommand(Command command) {
        ByteBuf byteBuf = Unpooled.buffer();
        if (command.staticMethod != null) {
            byteBuf.writeInt("/server/staticMethod".hashCode());
            byte[] tmp = new JSONObject(command.staticMethod).toJSONString(0)
                    .getBytes(Charset.forName("UTF-8"));
            byteBuf.writeInt(tmp.length);
            byteBuf.writeBytes(tmp);
        }

        ctx.writeAndFlush(byteBuf);
    }
}
