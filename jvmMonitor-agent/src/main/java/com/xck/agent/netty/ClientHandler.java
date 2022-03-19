package com.xck.agent.netty;

import com.xck.annotation.AnnotationScanner;
import com.xck.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * http处理器
 *
 * @author xuchengkun
 * @date 2021/08/25 11:13
 **/
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientService.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClientService.ctx = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        byte[] respContent = "{\"resp\":\"system error\"}".getBytes(Charset.forName("UTF-8"));

        int commandType = -1;
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            commandType = byteBuf.readInt();
            int packageLen = byteBuf.readInt();
            if (packageLen < 0) {
                throw new IllegalArgumentException("服务端读取包长度错误: " + packageLen);
            }

            AnnotationScanner.ObjExecutor objExecutor = AnnotationScanner.originPluginMap.get(commandType);
            if (objExecutor == null) {
                respContent = "{\"resp\":\"404 no found\"}".getBytes(Charset.forName("UTF-8"));
                return;
            }

            String content = "";
            if (packageLen > 0) {
                content = byteBuf.readCharSequence(packageLen, Charset.forName("UTF-8")).toString();
            }
            LogUtil.info("客户端收到, 命令类型:"+commandType + ", 包长度:"+packageLen
                    + ", content: " + content);

            String resp = (String)objExecutor.getMethod().invoke(objExecutor.getObject(), content);
            respContent = resp.getBytes(Charset.forName("UTF-8"));

        } catch (IllegalArgumentException e) {
            LogUtil.error("client read error: ", e);
        } catch (Throwable e) {
            LogUtil.error("client error: ", e);
        } finally {
            ByteBuf resp = Unpooled.buffer();
            resp.writeInt(commandType);
            resp.writeInt(respContent.length);
            resp.writeBytes(respContent);
            ctx.writeAndFlush(resp);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LogUtil.error("客户端系统错误: errMsg: ", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        LogUtil.info("客户端发送心跳检测...");

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(3);
        byteBuf.writeInt(0);

        ctx.writeAndFlush(byteBuf);
    }
}
