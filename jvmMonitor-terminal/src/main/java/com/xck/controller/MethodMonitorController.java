package com.xck.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xck.asm.MethodMonitorRuleGroup;
import com.xck.command.MethodMonitorRuleQueryCommand;
import com.xck.command.MethodMonitorRuleUpdateCommand;
import com.xck.model.JProcessonRegister;
import com.xck.model.MethodMonitorRuleCenter;
import com.xck.model.ServerService;
import com.xck.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 方法执行
 *
 * @author xuchengkun
 * @date 2022/03/20 10:44
 **/
@Controller
@RequestMapping("/methodMonitor")
@CrossOrigin
public class MethodMonitorController {

    @Autowired
    private MethodMonitorRuleCenter methodMonitorRuleCenter;

    @PostMapping("/rule/list")
    @ResponseBody
    public Object ruleList(HttpServletRequest request) throws Exception {
        return methodMonitorRuleCenter.ruleListToJSONArray();
    }

    @PostMapping("/rule/group/list")
    @ResponseBody
    public Object ruleGroupList(HttpServletRequest request) throws Exception {

        String json = HttpUtils.readJson(request);
        JSONObject reqJsonObj = JSONUtil.parseObj(json);
        String groupId = reqJsonObj.getStr("id");

        return methodMonitorRuleCenter.ruleGroupListToJSONArray(groupId);
    }

    @PostMapping("/rule/group/save")
    @ResponseBody
    public Object ruleGroupSave(HttpServletRequest request) throws Exception {

        String json = HttpUtils.readJson(request);
        JSONObject reqJsonObj = JSONUtil.parseObj(json);
        String groupId = reqJsonObj.getStr("id");
        JSONArray data = reqJsonObj.getJSONArray("ruleGroup");

        methodMonitorRuleCenter.ruleGroupListSave(groupId, data);

        return JSONUtil.parseObj("{\"resp\":\"success\"}");
    }

    @PostMapping("/rule/jp/list")
    @ResponseBody
    public Object ruleJpList(HttpServletRequest request) throws Exception {
        String json = HttpUtils.readJson(request);

        JSONObject reqJsonObj = JSONUtil.parseObj(json);

        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return JSONUtil.parseObj("{\"resp\":\"no register\"}");
        }

        return ServerService.writeCommand(pid, new MethodMonitorRuleQueryCommand(), false);
    }

    @PostMapping("/rule/update")
    @ResponseBody
    public Object ruleUpdate(HttpServletRequest request) throws Exception {
        String json = HttpUtils.readJson(request);

        JSONObject reqJsonObj = JSONUtil.parseObj(json);

        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return JSONUtil.parseObj("{\"resp\":\"no register\"}");
        }

        JSONArray ids = reqJsonObj.getJSONArray("ids");
        List<MethodMonitorRuleGroup> ruleGroups = methodMonitorRuleCenter.queryRuleGroup(ids);
        return ServerService.writeCommand(pid, new MethodMonitorRuleUpdateCommand(ruleGroups), false);
    }

    @PostMapping("/rule/result/query")
    @ResponseBody
    public Object ruleResult(HttpServletRequest request) throws Exception {
        String json = HttpUtils.readJson(request);

        JSONObject reqJsonObj = JSONUtil.parseObj(json);

        String id = reqJsonObj.getStr("id");
        Integer size = reqJsonObj.getInt("size");

        return methodMonitorRuleCenter.resultQuery(id, size);
    }
}