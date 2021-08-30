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
     * showdoc
     * @catalog 测试文档/服务端操作
     * @title 插件重新加载
     * @description 插件重新加载
     * @method post
     * @url http://ip:port/server/plugin/reload
     * @param path 必选 string 插件所在目录的绝对路径
     * @return {"resp":"ok"}
     * @return_param resp String 请求描述
     * @remark 无备注
     * @number 1
     */
    @RequestMapping("/plugin/reload")
    public String reloadPlugin(String json) throws Exception{
        JSONObject jsonObject = new JSONObject(json);
        AnnotationScanner.scanPlugin(jsonObject.getStr("path"));
        return "{\"resp\":\"ok\"}";
    }

    /**
     * showdoc
     * @catalog 测试文档/服务端操作
     * @title 重启服务端
     * @description 重启服务端
     * @method post
     * @url http://ip:port/server/httpServer/restart
     * @param port 必选 int 服务端监听端口
     * @param status 必选 int 1-关闭不启动，2-重启
     * @return {"resp":"restart server true"}
     * @return_param resp String 启动是否成功
     * @remark 无备注
     * @number 5
     */
    @RequestMapping("/httpServer/restart")
    public String reStartServer(String json) throws InterruptedException{
        JSONObject jsonObject = new JSONObject(json);
        int port = jsonObject.getInt("port");
        int status = jsonObject.getInt("status");
        boolean result = AnnotationScanner.scanServer(port, status);
        return String.format("{\"resp\":\"restart server %s\"}", result);
    }
}
