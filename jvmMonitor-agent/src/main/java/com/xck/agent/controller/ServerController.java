package com.xck.agent.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xck.annotation.RequestMapping;
import com.xck.util.ClassAgentUtil;
import com.xck.util.LogUtil;

import java.lang.management.ManagementFactory;

/**
 * 服务端控制器
 *
 * @author xuchengkun
 * @date 2021/08/27 15:24
 **/
@RequestMapping("/server")
public class ServerController {

    @RequestMapping("/activeTest")
    public String activeTest(String json) throws Exception {
        return "{\"resp\":\"ok\"}";
    }

    @RequestMapping("/pid")
    public String pid(String json) throws Exception{
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return "{\"resp\":\"" + pid + "\"}";
    }

    @RequestMapping("/staticMethod")
    public String staticMethod(String json) throws Exception {

        JSONObject jsonObject = new JSONObject(json);
        String className = jsonObject.getStr("className");
        String methodName = jsonObject.getStr("methodName");
        JSONArray jsonArray = jsonObject.getJSONArray("args");

        String invokeResultStr = "";
        try {
            Object invokeResult = ClassAgentUtil.methodStaticInvoke(className, methodName, jsonArray);
            invokeResultStr = ClassAgentUtil.obj2Json(invokeResult);

            JSONObject result = new JSONObject();
            result.put("resp", invokeResultStr);
            return result.toJSONString(0);
        } catch (ClassNotFoundException e) {
            return "{\"resp\":\"class no found\"}";
        } catch (NoSuchMethodException e) {
            return "{\"resp\":\"method no found\"}";
        } catch (Throwable e) {
            LogUtil.error("other error", e);
            return "{\"resp\":\"other error\"}";
        } finally {
            LogUtil.info(String.format("invoke class=%s method=%s args=%s, result=%s"
                    , className, methodName, jsonArray, invokeResultStr));
        }
    }
}
