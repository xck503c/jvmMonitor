package com.xck.agent.controller;

import cn.hutool.json.JSONObject;
import com.xck.agent.annotation.RequestMapping;
import com.xck.agent.util.ClassAgentUtil;

/**
 * 审核控制器
 *
 * @author xuchengkun
 * @date 2021/08/25 15:45
 **/
@RequestMapping("/check")
public class CheckController {

    @RequestMapping("/black")
    public String checkBlack(String reqJson){
        System.out.println("接收 " + reqJson);
        try {
            Object apx = ClassAgentUtil.methodStaticInvoke("com.hskj.spring.Spring", "getApx");
            Object impDoCheckBlack = ClassAgentUtil.methodObjInvoke(apx, "getBean", "impDoCheck_Black");

            JSONObject jsonObject = new JSONObject(reqJson);
            String mobile = jsonObject.getStr("mobile");
            String userId = jsonObject.getStr("userId");

            Object sms = ClassAgentUtil.newObj("com.hskj.form.SmsMessage");
            ClassAgentUtil.methodObjInvoke(sms, "setMobile", mobile);
            ClassAgentUtil.methodObjInvoke(sms, "setUser_id", userId);
            System.out.println(String.format("获取参数: mobile=%s, userId=%s", mobile, userId));

            int result = (Integer) ClassAgentUtil.methodObjInvoke(impDoCheckBlack, "doCheck", sms);
            if (result == 1) {
                return "{\"resp\":\"no hit black\"}";
            }

            return "{\"resp\":\"hit black\"}";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"resp\":\"error\"}";
    }
}
