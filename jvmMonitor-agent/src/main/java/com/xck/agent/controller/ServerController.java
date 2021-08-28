package com.xck.agent.controller;

import cn.hutool.json.JSONObject;
import com.xck.agent.AnnotationScanner;
import com.xck.annotation.RequestMapping;

/**
 * 服务端控制器
 *
 * @author xuchengkun
 * @date 2021/08/27 15:24
 **/
@RequestMapping("/server")
public class ServerController {

    /**
     * 提供插件加载的接口
     * @param json
     * @return
     */
    @RequestMapping("/plugin/reload")
    public String reloadPlugin(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            AnnotationScanner.scanPlugin(jsonObject.getStr("path"));
            return "{\"resp\":\"ok\"}";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"resp\":\"error\"}";
    }
}
