package com.xck.agent.methodMonitor;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xck.common.methodMonitor.MethodInvokeMsg;
import com.xck.common.methodMonitor.MethodMonitorRule;
import com.xck.common.methodMonitor.MethodMonitorRuleGroup;
import com.xck.common.util.LogUtil;

import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 * 方法调用监听器管理类
 *
 * @author xuchengkun
 * @date 2022/04/03 22:10
 **/
public class MethodInvokeListenerManager {

    /**
     * 存储同个线程处理结果
     * key -- 方法+类名
     * value -- 具有同组规则id的列表
     */
    private static ThreadLocal<Map<String, List<MethodInvokeMsg>>> msgThreadLocal = new ThreadLocal<Map<String, List<MethodInvokeMsg>>>(){
        @Override
        protected Map<String, List<MethodInvokeMsg>> initialValue() {
            return new HashMap<>();
        }
    };

    private static volatile List<MethodMonitorRuleGroup> ruleGroupList;

    public static Instrumentation inst;

    /**
     * 更新规则
     * @param newRuleGroupList
     * @return
     * @throws Exception
     */
    public synchronized static JSONArray updateRule(List<MethodMonitorRuleGroup> newRuleGroupList) throws Exception {
        //如果为空则清空
        if (newRuleGroupList.isEmpty()) {
            LogUtil.info("clear group rule");
            ruleGroupList = null;
            return queryRule();
        }

        Map<String, ClassMethod> tmpFilterMap = new HashMap<>();
        Iterator<MethodMonitorRuleGroup> it = newRuleGroupList.iterator();
        while (it.hasNext()) {
            MethodMonitorRuleGroup group = it.next();
            Map<String, MethodMonitorRule> ruleMap = group.getRuleMap();
            for (MethodMonitorRule rule : ruleMap.values()) {
                ClassMethod classMethod = tmpFilterMap.get(rule.getClassName());

                //判断是否存在
                try {
                    Class c = Class.forName(rule.getClassName());
                    Set<String> classMethods = ClassUtil.getDeclaredMethodNames(c);
                    //如果不包含改方法，则跳过
                    if (!classMethods.contains(rule.getMethodName())) continue;
                } catch (ClassNotFoundException e) {
                    LogUtil.error("updateRule error " + rule.getClassName() + " " + rule.getMethodName(), e);
                    it.remove();
                    continue;
                }

                if (classMethod == null) {
                    tmpFilterMap.put(rule.getClassName(), classMethod = new ClassMethod(rule.getClassName()));
                }

                classMethod.addMethodName(rule.getMethodName());
            }
        }

        if (tmpFilterMap.size() > 0) {
            MethodMonitorEnhancer.enhance(inst, new HashSet<>(tmpFilterMap.values()));
            ruleGroupList = newRuleGroupList;
            LogUtil.info("update rule " + JSONUtil.toJsonStr(newRuleGroupList));
        } else {
            LogUtil.info("no update rule " + JSONUtil.toJsonStr(newRuleGroupList));
        }

        return queryRule();
    }

    /**
     * 查询规则列表
     * @return
     */
    public synchronized static JSONArray queryRule() {
        JSONArray jsonArray = new JSONArray();
        if (ruleGroupList == null) {
            return jsonArray;
        }

        for (int i = 0; i < ruleGroupList.size(); i++) {
            MethodMonitorRuleGroup group = ruleGroupList.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", group.getId());
            jsonObject.put("name", group.getName());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public static String beforeListenerStr(String className, String methodName, boolean isStatic) {
        return beforeOrAfterListenerStr(className, methodName, isStatic, false);
    }

    public static String afterListenerStr(String className, String methodName, boolean isStatic) {
        return beforeOrAfterListenerStr(className, methodName, isStatic, true);
    }

    public static String registerTryCatchListenerStr(String className, String methodName, boolean isStatic) {
        StringBuilder sb = new StringBuilder();
        sb.append("com.xck.agent.methodMonitor.MethodInvokeListenerManager.tryCatchListener(");
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
            sb.append("com.xck.agent.methodMonitor.MethodInvokeListenerManager.beforeListener(");
        } else {
            sb.append("com.xck.agent.methodMonitor.MethodInvokeListenerManager.afterListener(");
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
        try {
            List<MethodMonitorRuleGroup> tmpRuleGroupList = ruleGroupList;
            if (tmpRuleGroupList == null) {
                msgThreadLocal.remove();
                return;
            }

            Map<String, List<MethodInvokeMsg>> map = msgThreadLocal.get();

            String key = className + methodName;
            List<MethodInvokeMsg> invokeMsgs = map.get(key);
            if (invokeMsgs == null) {
                map.put(key, invokeMsgs = new ArrayList<>());
            } else {
                invokeMsgs.clear(); //如果之前就有，则清空
            }

            for (int i = 0; i < tmpRuleGroupList.size(); i++) {
                MethodMonitorRuleGroup ruleGroup = tmpRuleGroupList.get(i);
                //组内的方法规则
                MethodMonitorRule rule = ruleGroup.getRule(className, methodName);
                if (rule == null || (!rule.isAfter() && !rule.isBefore() && rule.isTryCatch())) continue;
                //构建消息
                MethodInvokeMsg methodInvokeMsg = new MethodInvokeMsg(className, methodName, ruleGroup.getId());
                invokeMsgs.add(methodInvokeMsg);
                if (rule.isBefore()) {
                    methodInvokeMsg.setBeforeParams(Arrays.asList(params));
                }
            }
        } catch (Exception e) {
        }
    }

    public static void afterListener(String className, String methodName, boolean isStatic
            , long diff, Object returnValue, final Object... params) {
        try {
            List<MethodMonitorRuleGroup> tmpRuleGroupList = ruleGroupList;
            if (tmpRuleGroupList == null) {
                msgThreadLocal.remove();
                return;
            }

            Map<String, List<MethodInvokeMsg>> map = msgThreadLocal.get();

            String key = className + methodName;
            List<MethodInvokeMsg> invokeMsgs = map.get(key);
            if (invokeMsgs == null) return;

            if (invokeMsgs.size() != tmpRuleGroupList.size()) {
                LogUtil.info("规则id不一致，移除本次记录信息");
                map.remove(key);
                return;
            }

            for (int i = 0; i < tmpRuleGroupList.size(); i++) {
                MethodMonitorRuleGroup ruleGroup = tmpRuleGroupList.get(i);
                MethodMonitorRule rule = ruleGroup.getRule(className, methodName);
                if (rule == null || !rule.isAfter()) continue;

                MethodInvokeMsg methodInvokeMsg = invokeMsgs.get(i);
                if (!methodInvokeMsg.getId().equals(ruleGroup.getId())) {
                    LogUtil.info("规则id不一致，移除本次记录信息");
                    map.remove(key);
                    return;
                }

                methodInvokeMsg.setBeforeParams(Arrays.asList(params));
                methodInvokeMsg.setAfterParams(Arrays.asList(params));
                methodInvokeMsg.setReturnValue(returnValue);
                methodInvokeMsg.setUseTime(diff);
            }

            for (MethodInvokeMsg item : invokeMsgs) {
                LogUtil.methodDebug(JSONUtil.toJsonStr(item));
                map.remove(key);
            }
        } catch (Exception e) {
        }
    }

    public static void tryCatchListener(String className, String methodName, boolean isStatic, final Throwable e) {
        try {
            List<MethodMonitorRuleGroup> tmpRuleGroupList = ruleGroupList;
            if (tmpRuleGroupList == null) {
                msgThreadLocal.remove();
                return;
            }

            Map<String, List<MethodInvokeMsg>> map = msgThreadLocal.get();

            String key = className + methodName;
            List<MethodInvokeMsg> invokeMsgs = map.get(key);
            if (invokeMsgs == null) return;

            if (invokeMsgs.size() != tmpRuleGroupList.size()) {
                LogUtil.info("规则id不一致，移除本次记录信息");
                map.remove(key);
                return;
            }

            for (int i = 0; i < tmpRuleGroupList.size(); i++) {
                MethodMonitorRuleGroup ruleGroup = tmpRuleGroupList.get(i);
                MethodMonitorRule rule = ruleGroup.getRule(className, methodName);
                if (rule == null || !rule.isTryCatch()) continue;

                MethodInvokeMsg methodInvokeMsg = invokeMsgs.get(i);
                if (!methodInvokeMsg.getId().equals(ruleGroup.getId())) {
                    LogUtil.info("规则id不一致，移除本次记录信息");
                    map.remove(key);
                    return;
                }

                methodInvokeMsg.setTryCatchParams(e);
            }

            for (MethodInvokeMsg item : invokeMsgs) {
                LogUtil.methodDebug(JSONUtil.toJsonStr(item));
                map.remove(key);
            }
        } catch (Exception e1) {
        }
    }
}
