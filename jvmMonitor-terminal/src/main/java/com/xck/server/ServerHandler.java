package com.xck.server;

import cn.hutool.json.JSONUtil;
import com.xck.command.PidCommand;
import com.xck.command.TestCommand;
import com.xck.common.http.ReqResponse;
import com.xck.model.JProcessonRegister;
import com.xck.model.ServerService;
import com.xck.common.util.LogUtil;
import io.netty.buffer.ByteBuf;
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
        ServerService.writeCommand(ctx, PidCommand.pidCommandDefault, true);
        System.out.println("获取pid");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        boolean result = JProcessonRegister.deRegister(ctx);
        System.out.println("断开成功？" + result);
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

        if (commandType != TestCommand.uri) {
            System.out.println("服务端收到, 命令类型:" + commandType + ", 包长度:" + packageLen
                    + ", content: " + content);
        }
        ReqResponse reqResponse = JSONUtil.toBean(content, ReqResponse.class);
        ServerService.readCommand(ctx, commandType, reqResponse);
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
        ServerService.writeCommand(ctx, TestCommand.testCommand, true);
    }
}
