package com.xck.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xck.command.StaticMethodCommand;
import com.xck.common.util.StrUtils;
import com.xck.model.JProcessonRegister;
import com.xck.model.ServerService;
import com.xck.common.http.ReqResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 方法执行
 *
 * @author xuchengkun
 * @date 2022/03/20 10:44
 **/
@Controller
@RequestMapping("/method")
@CrossOrigin
public class MethodExecutorController {

    private final static String[] paramKeyArr = new String[]{"a", "b", "c", "d", "e", "f", "g"
            , "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "w", "x", "y", "z"};

    @PostMapping("/static")
    @ResponseBody
    public ReqResponse staticMethod(@RequestBody JSONObject reqJsonObj) throws Exception {
        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return ReqResponse.error("未注册");
        }

        String className = reqJsonObj.getStr("className");
        String method = reqJsonObj.getStr("method");

        List<String> args = new ArrayList<>();
        for (String key : paramKeyArr) {
            String value = reqJsonObj.getStr(key);
            if (StrUtil.isBlank(value)) continue;

            args.add(value);
        }
        ReqResponse reqResponse = ServerService.writeCommand(pid, new StaticMethodCommand(className, method, args), false);
        return reqResponse;
    }
}
