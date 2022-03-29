package com.xck.model;

import cn.hutool.json.JSONObject;
import com.xck.command.Command;
import com.xck.command.PidCommand;
import com.xck.command.StaticMethodCommand;
import com.xck.command.TestCommand;
import com.xck.server.NettyServer;
import com.xck.util.StrUtils;
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

    public static Object writeCommand(ChannelHandlerContext ctx, Command command, boolean isAsync) throws InterruptedException{
        SynchronousQueue queue = JProcessonRegister.getCommandRespQueue(ctx);
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

    public static Object writeCommand(Integer pid, Command command, boolean isAsync) throws InterruptedException{
        ChannelHandlerContext ctx = JProcessonRegister.getCtx(pid);
        return writeCommand(ctx, command, isAsync);
    }

    public static void readCommand(ChannelHandlerContext ctx, int commandType, String content){
        JSONObject jsonObject = new JSONObject(content);
        JSONObject resp = new JSONObject();
        if (commandType == PidCommand.uri) {
            boolean result = JProcessonRegister.registerClient(jsonObject.getInt("resp"), ctx);
            resp.put("pid", jsonObject.getInt("resp"));
            resp.put("registerClient", result);
        } else if (commandType == StaticMethodCommand.uri) {
            resp.put("result", StrUtils.json2Obj(content));
        }
        if (commandType != TestCommand.uri) {
            JProcessonRegister.commandResp(ctx, resp);
        }
    }
}
