package com.xck.agent.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xck.agent.methodMonitor.MethodInvokeListenerManager;
import com.xck.common.annotation.RequestMapping;
import com.xck.common.methodMonitor.MethodMonitorRuleGroup;
import com.xck.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端控制器
 *
 * @author xuchengkun
 * @date 2021/08/27 15:24
 **/
@RequestMapping("/methodMonitor")
public class MethodMonitorController {

    @RequestMapping("/rule/update")
    public String ruleUpdate(String json) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("args");
            List<MethodMonitorRuleGroup> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(jsonArray.getBean(i, MethodMonitorRuleGroup.class));
            }
            JSONObject result = new JSONObject();
            result.put("resp", MethodInvokeListenerManager.updateRule(list));
            return result.toJSONString(0);
        } catch (Throwable e) {
            //其他异常，可能是业务定义的异常，需要返回
            LogUtil.error("other error", e);
            return "{\"resp\":\"other error" + ", " + ExceptionUtil.getRootCauseMessage(e) + "\"}";
        }
    }

    @RequestMapping("/rule/query")
    public String ruleQuery(String json) throws Exception {
        try {
            JSONObject result = new JSONObject();
            result.put("resp", MethodInvokeListenerManager.queryRule());
            return result.toJSONString(0);
        } catch (Throwable e) {
            //其他异常，可能是业务定义的异常，需要返回
            LogUtil.error("other error", e);
            return "{\"resp\":\"other error" + ", " + ExceptionUtil.getRootCauseMessage(e) + "\"}";
        }
    }
}
