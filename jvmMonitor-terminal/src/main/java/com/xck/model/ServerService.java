package com.xck.model;

import com.xck.command.*;
import com.xck.common.http.ReqResponse;
import com.xck.server.NettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.SynchronousQueue;

/**
 * 客户端服务类
 *
 * @author xuchengkun
 * @date 2021/09/07 10:22
 **/
public class ServerService {

    public static NettyServer nettyServer;

    public static ReqResponse writeCommand(ChannelHandlerContext ctx, Command command, boolean isAsync) throws InterruptedException{
        SynchronousQueue<ReqResponse> queue = JProcessonRegister.getCommandRespQueue(ctx);
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(command.commandUri());
        byte[] body = command.bodyBytes();
        byteBuf.writeInt(body.length);
        if (body.length > 0) {
            byteBuf.writeBytes(command.bodyBytes());
        }
        ctx.writeAndFlush(byteBuf);

        if (isAsync) {
            return queue.poll();
        } else {
            return queue.take();
        }
    }

    public static ReqResponse writeCommand(Integer pid, Command command, boolean isAsync) throws InterruptedException{
        ChannelHandlerContext ctx = JProcessonRegister.getCtx(pid);
        return writeCommand(ctx, command, isAsync);
    }

    public static void readCommand(ChannelHandlerContext ctx, int commandType, ReqResponse reqResponse){
        if (commandType == PidCommand.uri && reqResponse.isSuccess()) {
            JProcessonRegister.registerClient(Integer.parseInt((String) reqResponse.getData()), ctx);
        }
        JProcessonRegister.commandResp(ctx, reqResponse);
    }
}
