package com.xck.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xck.common.methodMonitor.MethodInvokeMsg;
import com.xck.common.methodMonitor.MethodMonitorRule;
import com.xck.common.methodMonitor.MethodMonitorRuleGroup;
import com.xck.common.util.LogUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 表单模板管理
 *
 * @author xuchengkun
 * @date 2022/03/26 15:53
 **/
@Component
public class MethodMonitorRuleCenter {

    private final static String methodMonitorRuleList = System.getProperty("user.dir") + "/config/methodMonitorRule.json";
    private static String DEBUGLOG = System.getProperty("user.dir") + "/debug.log";

    private static Map<String, MethodMonitorRuleGroup> ruleGroupList = new HashMap<>();

    @PostConstruct
    public void init() {
        JSONArray gourpRuleList = JSONUtil.readJSONArray(new File(methodMonitorRuleList), Charset.forName("UTF-8"));

        for (int i = 0; i < gourpRuleList.size(); i++) {
            JSONObject ruleGroupJSON = gourpRuleList.getJSONObject(i);
            MethodMonitorRuleGroup ruleGroup = new MethodMonitorRuleGroup();
            ruleGroup.setId(ruleGroupJSON.getStr("id"));
            ruleGroup.setName(ruleGroupJSON.getStr("name"));

            JSONArray rulesJSON = ruleGroupJSON.getJSONArray("rules");
            for (int j = 0; j < rulesJSON.size(); j++) {
                MethodMonitorRule rule = rulesJSON.getBean(j, MethodMonitorRule.class);
                ruleGroup.addRule(rule);
            }

            ruleGroupList.put(ruleGroup.getId(), ruleGroup);
        }
        if (ruleGroupList.size() > 0) {
            LogUtil.info("load rule list, size=" + ruleGroupList.size());
        }
    }

    public synchronized JSONArray ruleListToJSONArray() {
        JSONArray groupJsonList = new JSONArray();
        for (MethodMonitorRuleGroup group : ruleGroupList.values()) {
            JSONObject groupObj = new JSONObject();
            groupObj.put("id", group.getId());
            groupObj.put("name", group.getName());
            groupJsonList.add(groupObj);
        }

        return groupJsonList;
    }

    public synchronized JSONArray ruleGroupListToJSONArray(String id) {
        MethodMonitorRuleGroup group = ruleGroupList.get(id);
        Collection<MethodMonitorRule> collection = group.getRuleMap().values();
        JSONArray groupJson = new JSONArray();
        for (MethodMonitorRule rule : collection) {
            JSONObject ruleObj = new JSONObject();
            ruleObj.put("name", rule.getName());
            ruleObj.put("className", rule.getClassName());
            ruleObj.put("methodName", rule.getMethodName());

            JSONArray conditionObj = new JSONArray();
            if (rule.isBefore()) conditionObj.add("1");
            if (rule.isAfter()) conditionObj.add("2");
            if (rule.isTryCatch()) conditionObj.add("3");
            ruleObj.put("condition", conditionObj);

            groupJson.add(ruleObj);
        }

        return groupJson;
    }

    public synchronized List<MethodMonitorRuleGroup> queryRuleGroup(JSONArray ids) {
        List<MethodMonitorRuleGroup> result = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            MethodMonitorRuleGroup ruleGroup = ruleGroupList.get(ids.getStr(i));
            if (ruleGroup == null) continue;
            result.add(ruleGroup);
        }

        return result;
    }

    /**
     * {"ruleGroup":[{"name":"业务选择入口","className":"com.hskj.model.valider.UserBusinessSelector"
     * ,"methodName":"getRealUserBusiness","condition":["1","2","3"]}],"id":"1"}
     *
     * @param id
     * @param datas
     * @return
     */
    public synchronized void ruleGroupListSave(String id, JSONArray datas) {
        MethodMonitorRuleGroup group = ruleGroupList.get(id);

        group.getRuleMap().clear();
        for (int i = 0; i < datas.size(); i++) {
            JSONObject item = datas.getJSONObject(i);
            MethodMonitorRule rule = new MethodMonitorRule();
            rule.setName(item.getStr("name"));
            rule.setClassName(item.getStr("className"));
            rule.setMethodName(item.getStr("methodName"));
            JSONArray condition = item.getJSONArray("condition");
            for (int j = 0; j < condition.size(); j++) {
                String value = condition.getStr(j);
                if (value.equals("1")) {
                    rule.setBefore(true);
                } else if (value.equals("2")) {
                    rule.setAfter(true);
                } else {
                    rule.setTryCatch(true);
                }
            }
            group.addRule(rule);
        }
    }

    public synchronized JSONArray resultQuery(final String id, final int size) {
        JSONArray jsonArray = new JSONArray();
        if (!FileUtil.exist(DEBUGLOG)) {
            return jsonArray;
        }

        JSONArray tmp = new JSONArray();

        //2022-04-10 17:30:45,513 [main] INFO - [{"beforeParams":["name"],"returnValue":"xck防守打法","afterParams":["name"],"methodName":"getMap","useTime":0,"className":"HttpInterfaceTest","id":"2"}]
        FileUtil.readLines(new File(DEBUGLOG), Charset.forName("UTF-8"), new LineHandler() {
            @Override
            public void handle(String line) {
                if (StrUtil.isBlank(line)) {
                    return;
                }
                String[] lineArr = line.split(" ");
                if (lineArr.length < 6) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 5; i < lineArr.length; i++) {
                    sb.append(lineArr[i]).append(" ");
                }
                sb.delete(sb.length() - 1, sb.length());
                MethodInvokeMsg methodInvokeMsg = JSONUtil.toBean(sb.toString(), MethodInvokeMsg.class);
                if (!methodInvokeMsg.getId().equals(id)) {
                    return;
                }

                JSONObject jsonObject = new JSONObject(true);
                jsonObject.put("time", lineArr[0] + " " + lineArr[1]);
                jsonObject.put("threadName", lineArr[2]);
                jsonObject.put("result", methodInvokeMsg);

                if (tmp.size() == size) {
                    for (int i = 0; i < tmp.size() - 1; i++) {
                        tmp.set(i, tmp.get(i + 1));
                    }
                    tmp.set(tmp.size() - 1, jsonObject);
                } else {
                    tmp.add(jsonObject);
                }
            }
        });

        for (int i = tmp.size() - 1; i >= 0; i--) {
            if (tmp.get(i) == null) continue;
            jsonArray.add(tmp.get(i));
        }

        return jsonArray;
    }
}
