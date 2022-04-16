package com.xck.model.exception;

import com.xck.common.util.LogUtil;
import com.xck.model.http.ReqResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author xuchengkun
 * @date 2022/04/15 14:44
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ReqResponse handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        LogUtil.error(String.format("请求地址'%s', 发生系统异常。", requestURI), e);
        return ReqResponse.error(e.getMessage());
    }
}
