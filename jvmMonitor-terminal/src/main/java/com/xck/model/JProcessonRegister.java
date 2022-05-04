package com.xck.model;

import cn.hutool.json.JSONUtil;
import com.xck.common.http.ReqResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * java程序注册
 *
 * @author xuchengkun
 * @date 2022/03/20 09:12
 **/
public class JProcessonRegister {

    /* key=processonName, value=channel */
    private static Map<Integer, ChannelHandlerContext> clientRegisterMap = new HashMap<>();
    /* key=pid, value=processonName */
    private static Map<Integer, String> pidRegisterMap = new HashMap<>();
    private static Map<ChannelHandlerContext, SynchronousQueue<ReqResponse>> commandSyncQueue = new HashMap<>();

    public static synchronized ChannelHandlerContext getCtx(Integer targetPid) {
        return clientRegisterMap.get(targetPid);
    }

    public static synchronized boolean isRegister(Integer pid) {
        boolean isPid = pidRegisterMap.containsKey(pid);
        if (isPid) {
            boolean isClient = clientRegisterMap.containsKey(pid);
            if (isClient) {
                return true;
            } else {
                System.out.println("no has client " + clientRegisterMap);
                pidRegisterMap.remove(pid);
            }
        }
        return false;
    }

    public static synchronized boolean registerPid(Integer pid, String processonName) {
        String old = pidRegisterMap.putIfAbsent(pid, processonName);
        return old == null;
    }

    public static synchronized boolean registerClient(Integer pid, ChannelHandlerContext channelHandlerContext) {
        ChannelHandlerContext old = clientRegisterMap.putIfAbsent(pid, channelHandlerContext);
        boolean result = old == null;
        System.out.println("注册: " + pid + ", connect: " + channelHandlerContext + ", result=" + result);
        return result;
    }

    public static synchronized boolean deRegister(ChannelHandlerContext channelHandlerContext) {
        Iterator<Map.Entry<Integer, ChannelHandlerContext>> it = clientRegisterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ChannelHandlerContext> entry = it.next();
            if (entry.getValue() == channelHandlerContext) {
                Integer pid = entry.getKey();
                pidRegisterMap.remove(pid);
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static synchronized String pidRegisterMapToString() {
        return JSONUtil.toJsonStr(pidRegisterMap);
    }

    public static synchronized SynchronousQueue getCommandRespQueue(ChannelHandlerContext ctx) {
        SynchronousQueue queue = commandSyncQueue.get(ctx);
        if (queue == null) {
            commandSyncQueue.put(ctx, queue = new SynchronousQueue<>(true));
        }
        return queue;
    }

    public static synchronized void commandResp(ChannelHandlerContext ctx, Object resp) {
        SynchronousQueue queue = commandSyncQueue.get(ctx);
        if (queue == null) {
            commandSyncQueue.put(ctx, new SynchronousQueue(true));
        }
        System.out.println("push to sync queue : " + resp.toString());
        queue.offer(resp);
    }
}
