package com.xck.asm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 方法调用规则
 *
 * @author xuchengkun
 * @date 2022/04/07 14:01
 **/
public class MethodMonitorRuleGroup {

    private String id;
    private String name;
    private Map<String, MethodMonitorRule> ruleMap = new LinkedHashMap<>();

    public MethodMonitorRule getRule(String className, String method) {
        String key = className + method;
        return ruleMap.get(key);
    }

    public void addRule(MethodMonitorRule rule) {
        ruleMap.put(rule.getClassName() + rule.getMethodName(), rule);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return ruleMap.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, MethodMonitorRule> getRuleMap() {
        return ruleMap;
    }

    public void setRuleMap(Map<String, MethodMonitorRule> ruleMap) {
        this.ruleMap = ruleMap;
    }
}
