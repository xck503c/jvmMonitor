package com.xck.command;

import cn.hutool.json.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 静态方法
 *
 * @author xuchengkun
 * @date 2021/09/11 17:31
 **/
public class StaticMethodCommand extends Command {

    public final static int uri = "/server/staticMethod".hashCode();

    private String className;

    private String methodName;

    private List<String> args;

    public StaticMethodCommand(String className, String methodName, List<String> args) {
        this.className = className;
        this.methodName = methodName;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
