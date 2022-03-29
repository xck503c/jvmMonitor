package com.xck.controller;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xck.model.JProcessonRegister;
import com.xck.model.ServerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * java进程控制接口
 * 1. jps查看进程列表
 * 2. 选择指定进程进行agent操作
 * 3. 发出静态方法查询命令
 *
 * @author xuchengkun
 * @date 2022/03/20 09:26
 **/
@Controller
@RequestMapping("/jp")
@CrossOrigin
public class JProcessonController {

    @GetMapping("/list")
    @ResponseBody
    public String list() {
        List<String> jpsList = RuntimeUtil.execForLines("jps");
        JSONObject resp = new JSONObject(true);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < jpsList.size(); i++) {
            String[] lineArr = jpsList.get(i).split(" ");
            if (lineArr.length < 2) {
                continue;
            }
            JSONObject jsonObject = new JSONObject(true);
            Integer pid = Integer.parseInt(lineArr[0]);
            jsonObject.put("pid", pid);
            String processonName = lineArr[1];
            jsonObject.put("processonName", processonName);
            boolean isRegister = JProcessonRegister.isRegister(pid);
            jsonObject.put("status", isRegister ? "已注册" : "未注册");
            jsonArray.add(jsonObject);
        }

        resp.put("resp", jsonArray);

        return resp.toJSONString(0);
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(HttpServletRequest request) {
        Integer targetPid = Integer.parseInt(request.getParameter("pid"));
        if (JProcessonRegister.isRegister(targetPid)) {
            return "{\"resp\":\"已经注册\"}";
        }

        String processoName = "";
        List<String> jpsList = RuntimeUtil.execForLines("jps");
        for (int i = 0; i < jpsList.size(); i++) {
            if (Integer.parseInt(jpsList.get(i).split(" ")[0]) == targetPid) {
                processoName = jpsList.get(i).split(" ")[1];
            }
        }

        if (StrUtil.isBlank(processoName)) {
            return "{\"resp\":\"不存在该程序\"}";
        }

        String os = System.getProperty("os.name");
        String result = "成功";
        String javaHome = System.getProperty("java.home") + "/../lib/tools.jar";
        if (javaHome.contains(" ")) {
            String[] paths = javaHome.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paths.length; i++) {
                sb.append(paths[i]);
                if (i != paths.length - 1) {
                    sb.append("\" \"");
                }
            }
            javaHome = sb.toString();
        }
        if (os.toLowerCase().startsWith("win")) {
            result = RuntimeUtil.execForStr("java -Xbootclasspath/a:" + javaHome + " -cp .;./* com.xck.boot.BootStrap "
                    + targetPid + " " + ServerService.nettyServer.getPort());
        } else {
            result = RuntimeUtil.execForStr("nohup java -Xbootclasspath/a:" + javaHome + " -cp '.:hutool-all-4.6.3.jar:jvmMonitor-boot.jar:' com.xck.boot.BootStrap "
                    + targetPid + " " + ServerService.nettyServer.getPort() + " &");
        }

        if (!result.contains("错误") && !result.contains("Exception") && !result.contains("Error")) {
            JProcessonRegister.registerPid(targetPid, processoName);
        }

        return "{\"resp\":\"" + result + "\"}";
    }

    @GetMapping("/register/list")
    @ResponseBody
    public String registerList() {
        return "{\"resp\":\"" + JProcessonRegister.pidRegisterMapToString() + "\"}";
    }
}
