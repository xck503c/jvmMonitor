package com.xck.server;

import com.xck.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * http处理器
 *
 * @author xuchengkun
 * @date 2021/08/25 11:13
 **/
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerService.ctx = ctx;
        ServerService.lastTime = System.currentTimeMillis();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerService.ctx = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;
        int commandType = byteBuf.readInt();
        int packageLen = byteBuf.readInt();
        if (packageLen < 0) {
            throw new IllegalArgumentException("服务端读取包长度错误: " + packageLen);
        }

        String content = "";
        if (packageLen > 0) {
            content = byteBuf.readCharSequence(packageLen, Charset.forName("UTF-8")).toString();
        }
        if (commandType != "/server/activeTest".hashCode()) {
            ServerService.commandRespSQ.offer("1");
            System.out.println("服务端收到, 命令类型:"+commandType + ", 包长度:"+packageLen
                    + ", content: " + content);
        }
        ServerService.lastTime = System.currentTimeMillis();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("客户端断开连接: errMsg: " + cause.getCause().getMessage());
        LogUtil.error("客户端断开连接: errMsg: ", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        LogUtil.info("服务端发送心跳检测...");

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt("/server/activeTest".hashCode());
        byteBuf.writeInt(0);

        ctx.writeAndFlush(byteBuf);
    }
}
