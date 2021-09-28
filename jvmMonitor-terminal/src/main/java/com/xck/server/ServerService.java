package com.xck.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.xck.command.Command;
import com.xck.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

/**
 * 客户端服务类
 *
 * @author xuchengkun
 * @date 2021/09/07 10:22
 **/
public class ServerService {

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("program", command.program);

        if (command.userCache != null) {
            byteBuf.writeInt("/cache/user".hashCode());
            jsonObject.put("userId", command.userCache.getUserId());
            if (StrUtil.isNotBlank(command.userCache.getOption())) {
                jsonObject.put("option", command.userCache.getOption());
            }
        } else if (StrUtil.isNotBlank(command.gateConfigName)) {
            byteBuf.writeInt("/cache/gateConfig".hashCode());
            jsonObject.put("name", command.gateConfigName);
        } else if (command.blackMobileIsHit != null) {
            byteBuf.writeInt("/check/black".hashCode());
            jsonObject.put("userId", command.blackMobileIsHit.getUserId());
            jsonObject.put("mobile", command.blackMobileIsHit.getMobile());
        } else if (command.tdCache != null) {
            byteBuf.writeInt("/cache/td".hashCode());
            jsonObject.put("tdCode", command.tdCache.getTdCode());
            jsonObject.put("option", command.tdCache.getOption());
        }

        byte[] tmp = jsonObject.toJSONString(0).getBytes(Charset.forName("UTF-8"));
        byteBuf.writeInt(tmp.length);
        byteBuf.writeBytes(tmp);
        ctx.writeAndFlush(byteBuf);
    }
}
