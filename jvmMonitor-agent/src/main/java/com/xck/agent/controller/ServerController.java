package com.xck.agent.controller;

import com.xck.annotation.RequestMapping;

/**
 * 服务端控制器
 *
 * @author xuchengkun
 * @date 2021/08/27 15:24
 **/
@RequestMapping("/server")
public class ServerController {

    @RequestMapping("/activeTest")
    public String activeTest(String json) throws Exception{
        return "{\"resp\":\"ok\"}";
    }
}
