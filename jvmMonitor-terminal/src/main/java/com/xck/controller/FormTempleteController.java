package com.xck.controller;

import com.xck.model.FormTempleteList;
import com.xck.common.http.ReqResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 表单模板控制器
 *
 * @author xuchengkun
 * @date 2022/03/26 16:11
 **/
@Controller
@RequestMapping("/ft")
@CrossOrigin
public class FormTempleteController {

    @Autowired
    private FormTempleteList formTempleteList;

    @GetMapping("/menuList")
    @ResponseBody
    public ReqResponse menuList(){
        return ReqResponse.success(formTempleteList.templateMenus());
    }

    @GetMapping("/templete")
    @ResponseBody
    public ReqResponse templete(HttpServletRequest request) {
        int templeteId = Integer.parseInt(request.getParameter("templeteId"));
        FormTempleteList.FormTemplate formTemplate = formTempleteList.templateByIndex(templeteId);
        if (formTemplate == null) {
            return ReqResponse.error("没有该模板");
        }

        return ReqResponse.success(formTemplate);
    }
}
