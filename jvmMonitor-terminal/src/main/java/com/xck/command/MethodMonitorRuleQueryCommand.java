package com.xck.command;

import cn.hutool.json.JSONUtil;

import java.io.UnsupportedEncodingException;

/**
 * 静态方法
 *
 * @author xuchengkun
 * @date 2021/09/11 17:31
 **/
public class MethodMonitorRuleQueryCommand extends Command {

    public final static int uri = "/methodMonitor/rule/query".hashCode();

    @Override
    public byte[] bodyBytes() {
        try {
            return JSONUtil.toJsonStr(this).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int commandUri() {
        return uri;
    }
}
