package com.xck.common.util;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;

/**
 * 字符串工具
 *
 * @author xuchengkun
 * @date 2022/03/25 16:50
 **/
public class StrUtils {

    public static Object json2Obj(String jsonStr) {
        HashMap<String, Object> hashMap = JSONUtil.toBean(jsonStr, HashMap.class);
        return json2Obj(hashMap);
    }

    public static Object json2Obj(HashMap<String, Object> jsonValueHashMap) {

        HashMap<String, Object> result = new HashMap<>();
        for (String key : jsonValueHashMap.keySet()) {
            Object value = jsonValueHashMap.get(key);
            if (value == null) continue;

            try {
                HashMap<String, Object> valueHashMap = JSONUtil.toBean(value.toString(), HashMap.class);
                jsonValueHashMap.put(key, valueHashMap);
                result.put(key, json2Obj(valueHashMap));
            } catch (JSONException e) {
                result.put(key, value);
            }
        }

        return result;
    }
}
