package com.xck.command;

import cn.hutool.json.JSONUtil;
import com.xck.common.methodMonitor.MethodMonitorRuleGroup;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 静态方法
 *
 * @author xuchengkun
 * @date 2021/09/11 17:31
 **/
public class MethodMonitorRuleUpdateCommand extends Command {

    public final static int uri = "/methodMonitor/rule/update".hashCode();

    private List<MethodMonitorRuleGroup> args;

    public MethodMonitorRuleUpdateCommand(List<MethodMonitorRuleGroup> args) {
        this.args = args;
    }

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

    public List<MethodMonitorRuleGroup> getArgs() {
        return args;
    }

    public void setArgs(List<MethodMonitorRuleGroup> args) {
        this.args = args;
    }
}
