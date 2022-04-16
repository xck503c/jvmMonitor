package com.xck.model;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xck.command.*;
import com.xck.server.NettyServer;
import com.xck.common.util.StrUtils;
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
            JProcessonRegister.commandResp(ctx, resp);
        } else if (commandType == StaticMethodCommand.uri) {
            resp.put("result", StrUtils.json2Obj(content));
            JProcessonRegister.commandResp(ctx, resp);
        } else if (commandType == MethodMonitorRuleQueryCommand.uri) {
            JProcessonRegister.commandResp(ctx, jsonObject.getJSONArray("resp"));
        } else if (commandType == MethodMonitorRuleUpdateCommand.uri) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("resp");
                JProcessonRegister.commandResp(ctx, jsonArray);
            } catch (Exception e) {
                JProcessonRegister.commandResp(ctx, jsonObject.getJSONObject("resp"));
            }
        }
    }
}
