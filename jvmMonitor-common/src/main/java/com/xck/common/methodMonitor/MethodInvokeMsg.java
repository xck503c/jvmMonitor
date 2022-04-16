package com.xck.common.methodMonitor;

import java.util.List;

/**
 * 方法调用信息
 *
 * @author xuchengkun
 * @date 2022/04/07 13:52
 **/
public class MethodInvokeMsg {

    private String id;
    private String className;
    private String methodName;
    private List<Object> beforeParams;
    private List<Object> afterParams;
    private Object returnValue;
    private Throwable tryCatchParams;
    private long useTime;

    public MethodInvokeMsg(String className, String methodName, String id) {
        this.className = className;
        this.methodName = methodName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Object> getBeforeParams() {
        return beforeParams;
    }

    public void setBeforeParams(List<Object> beforeParams) {
        this.beforeParams = beforeParams;
    }

    public List<Object> getAfterParams() {
        return afterParams;
    }

    public void setAfterParams(List<Object> afterParams) {
        this.afterParams = afterParams;
    }

    public Throwable getTryCatchParams() {
        return tryCatchParams;
    }

    public void setTryCatchParams(Throwable tryCatchParams) {
        this.tryCatchParams = tryCatchParams;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }
}
