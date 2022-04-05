package com.xck.asm;

/**
 * 方法调用监听器管理类
 *
 * @author xuchengkun
 * @date 2022/04/03 22:10
 **/
public class MethodInvokeListenerManager {

    public static String beforeListenerStr(String className, String methodName, boolean isStatic) {
        return beforeOrAfterListenerStr(className, methodName, isStatic, false);
    }

    public static String afterListenerStr(String className, String methodName, boolean isStatic) {
        return beforeOrAfterListenerStr(className, methodName, isStatic, true);
    }

    public static String registerTryCatchListenerStr(String className, String methodName, boolean isStatic) {
        StringBuilder sb = new StringBuilder();
        sb.append("com.xck.asm.MethodInvokeListenerManager.tryCatchListener(");
        sb.append("\"").append(className).append("\"").append(",")
                .append("\"").append(methodName).append("\"").append(",")
                .append(isStatic).append(",");
        sb.append("$e").append(");");
        sb.append("throw $e;");

        return sb.toString();
    }

    public static String beforeOrAfterListenerStr(String className, String methodName, boolean isStatic, boolean isAfter) {
        //这里需要装箱操作，否则会出现verifyError异常
        StringBuilder sb = new StringBuilder();
        if (!isAfter) {
            sb.append("com.xck.asm.MethodInvokeListenerManager.beforeListener(");
        } else {
            sb.append("com.xck.asm.MethodInvokeListenerManager.afterListener(");
        }
        sb.append("\"").append(className).append("\"").append(",")
                .append("\"").append(methodName).append("\"").append(",")
                .append(isStatic).append(",");
        if (isAfter) {
            sb.append("_monitorDiff").append(","); //耗时统计
            sb.append("$_").append(","); //参数返回值
        }
        sb.append("$args");
        sb.append(");");

        return sb.toString();
    }

    public static void beforeListener(String className, String methodName, boolean isStatic, final Object... params) {

    }

    public static void afterListener(String className, String methodName, boolean isStatic
            , long diff, Object returnValue, final Object... params) {

    }

    public static void tryCatchListener(String className, String methodName, boolean isStatic, final Throwable e) {

    }
}
