package com.xck.common.http;

import cn.hutool.http.HttpStatus;

import java.util.HashMap;

/**
 * 请求响应对象
 *
 * @author xuchengkun
 * @date 2022/04/15 14:46
 **/
public class ReqResponse extends HashMap<String, Object> {

    /* 状态码 */
    public static final String CODE = "code";
    /* 返回内容 */
    public static final String MSG = "msg";
    /* 数据对象 */
    public static final String DATA = "data";

    /* 初始化一个空消息 */
    public ReqResponse() {
    }

    /**
     * 初始化消息体
     *
     * @param code 状态码
     * @param msg  描述内容
     */
    public ReqResponse(int code, String msg) {
        super.put(CODE, code);
        super.put(MSG, msg);
    }

    /**
     * 初始化消息体
     *
     * @param code 状态码
     * @param msg  描述内容
     * @param data 返回数据对象
     */
    public ReqResponse(int code, String msg, Object data) {
        super.put(CODE, code);
        super.put(MSG, msg);
        if (data != null) {
            super.put(DATA, data);
        }
    }

    public static ReqResponse success() {
        return new ReqResponse(HttpStatus.HTTP_OK, "操作成功");
    }

    public static ReqResponse success(String msg) {
        return new ReqResponse(HttpStatus.HTTP_OK, msg);
    }

    public static ReqResponse success(String msg, Object data) {
        return new ReqResponse(HttpStatus.HTTP_OK, msg, data);
    }

    public static ReqResponse success(Object data) {
        return new ReqResponse(HttpStatus.HTTP_OK, "操作成功", data);
    }

    public static ReqResponse error(String msg) {
        return new ReqResponse(HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    public boolean isSuccess() {
        return super.get(CODE) != null && (Integer)super.get(CODE) == HttpStatus.HTTP_OK;
    }

    public Object getData() {
        return super.get(DATA);
    }
    public Object setData(Object data) {
        return super.put(DATA, data);
    }
}
