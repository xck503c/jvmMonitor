package com.xck.asm;

/**
 * 方法调用规则
 *
 * @author xuchengkun
 * @date 2022/04/07 14:01
 **/
public class MethodMonitorRule {

    private String name;
    private String className;
    private String methodName;
    private boolean isBefore;
    private boolean isAfter;
    private boolean isTryCatch;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isBefore() {
        return isBefore;
    }

    public void setBefore(boolean before) {
        isBefore = before;
    }

    public boolean isAfter() {
        return isAfter;
    }

    public void setAfter(boolean after) {
        isAfter = after;
    }

    public boolean isTryCatch() {
        return isTryCatch;
    }

    public void setTryCatch(boolean tryCatch) {
        isTryCatch = tryCatch;
    }
}
