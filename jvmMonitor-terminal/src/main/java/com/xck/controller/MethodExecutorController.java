package com.xck.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xck.command.StaticMethodCommand;
import com.xck.model.JProcessonRegister;
import com.xck.model.ServerService;
import com.xck.util.HttpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    public Object staticMethod(HttpServletRequest request) throws Exception {
        String json = HttpUtils.readJson(request);

        JSONObject reqJsonObj = JSONUtil.parseObj(json);

        Integer pid = reqJsonObj.getInt("pid");
        if (!JProcessonRegister.isRegister(pid)) {
            return JSONUtil.parseObj("{\"resp\":\"未注册\"}");
        }

        String className = reqJsonObj.getStr("className");
        String method = reqJsonObj.getStr("method");

        List<String> args = new ArrayList<>();
        for (String key : paramKeyArr) {
            String value = reqJsonObj.getStr(key);
            if (StrUtil.isBlank(value)) continue;

            args.add(value);
        }

        JSONObject result = (JSONObject) ServerService.writeCommand(pid, new StaticMethodCommand(className, method, args), false);
        return JSONUtil.toBean(result, HashMap.class);
    }
}
