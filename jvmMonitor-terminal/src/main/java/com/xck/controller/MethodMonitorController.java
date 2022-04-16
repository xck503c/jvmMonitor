package com.xck.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xck.common.methodMonitor.MethodMonitorRuleGroup;
import com.xck.command.MethodMonitorRuleQueryCommand;
import com.xck.command.MethodMonitorRuleUpdateCommand;
import com.xck.model.JProcessonRegister;
import com.xck.model.MethodMonitorRuleCenter;
import com.xck.model.ServerService;
import com.xck.model.http.ReqResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ReqResponse ruleList() throws Exception {
        return ReqResponse.success(methodMonitorRuleCenter.ruleListToJSONArray());
    }

    @PostMapping("/rule/group/list")
    @ResponseBody
    public ReqResponse ruleGroupList(@RequestBody JSONObject reqJsonObj) throws Exception {
        String groupId = reqJsonObj.getStr("id");
        return ReqResponse.success(methodMonitorRuleCenter.ruleGroupListToJSONArray(groupId));
    }

    @PostMapping("/rule/group/save")
    @ResponseBody
    public ReqResponse ruleGroupSave(@RequestBody JSONObject reqJsonObj) throws Exception {

        String groupId = reqJsonObj.getStr("id");
        JSONArray data = reqJsonObj.getJSONArray("ruleGroup");

        methodMonitorRuleCenter.ruleGroupListSave(groupId, data);

        return ReqResponse.success();
    }

    @PostMapping("/rule/jp/list")
    @ResponseBody
    public ReqResponse ruleJpList(@RequestBody JSONObject reqJsonObj) throws Exception {
        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return ReqResponse.error("未注册");
        }

        return ReqResponse.success(ServerService.writeCommand(pid, new MethodMonitorRuleQueryCommand(), false));
    }

    @PostMapping("/rule/update")
    @ResponseBody
    public ReqResponse ruleUpdate(@RequestBody JSONObject reqJsonObj) throws Exception {

        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return ReqResponse.error("未注册");
        }

        JSONArray ids = reqJsonObj.getJSONArray("ids");
        List<MethodMonitorRuleGroup> ruleGroups = methodMonitorRuleCenter.queryRuleGroup(ids);

        return ReqResponse.success(ServerService.writeCommand(pid, new MethodMonitorRuleUpdateCommand(ruleGroups), false));
    }

    @PostMapping("/rule/result/query")
    @ResponseBody
    public ReqResponse ruleResult(@RequestBody JSONObject reqJsonObj) throws Exception {

        String id = reqJsonObj.getStr("id");
        Integer size = reqJsonObj.getInt("size");

        return ReqResponse.success(methodMonitorRuleCenter.resultQuery(id, size));
    }
}