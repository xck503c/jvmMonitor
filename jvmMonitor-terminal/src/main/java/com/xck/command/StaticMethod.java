package com.xck.command;

import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.IStringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 静态方法
 *
 * @author xuchengkun
 * @date 2021/09/11 17:31
 **/
public class StaticMethod {

    private String className;

    private String methodName;

    private List<String> args;

    /**
     * 账户缓存转换器
     *
     * @author xuchengkun
     * @date 2021/09/11 17:32
     **/
    public static class Convert implements IStringConverter<StaticMethod> {

        @Override
        public StaticMethod convert(String s) {
            String[] args = s.split("#");
            StaticMethod staticMethod = new StaticMethod();
            staticMethod.setClassName(args[0]);
            staticMethod.setMethodName(args[1]);
            if (args.length > 2) {
                List<String> argsList = new ArrayList<>();
                for (int i = 2; i < args.length; i++) {
                    argsList.add(args[i]);
                }
                staticMethod.setArgs(argsList);
            }
            return staticMethod;
        }
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
