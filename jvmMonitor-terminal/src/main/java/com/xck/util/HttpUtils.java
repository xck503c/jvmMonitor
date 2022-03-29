package com.xck.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * http工具类
 *
 * @author xuchengkun
 * @date 2022/03/24 08:46
 **/
public class HttpUtils {

    /**
     * 读json请求信息
     * @param request
     * @return
     * @throws IOException
     */
    public static String readJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
        try {
            char[] buffer = new char[4096];
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
            int i = -1;
            while ((i = br.read(buffer)) != -1) {
                sb.append(buffer, 0, i);
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
