package com.xck.command;

import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.IStringConverter;

/**
 * 账户缓存
 *
 * @author xuchengkun
 * @date 2021/09/11 17:31
 **/
public class UserCache {

    private String userId;
    private String option;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    /**
     * 账户缓存转换器
     *
     * @author xuchengkun
     * @date 2021/09/11 17:32
     **/
    public static class Convert implements IStringConverter<UserCache> {

        @Override
        public UserCache convert(String s) {
            String[] args = s.split(";");
            String userId = args[0];
            UserCache userCache = new UserCache();
            userCache.setUserId(userId);
            if (args.length == 2 && StrUtil.isNotBlank(args[1])) {
                userCache.setOption(args[1]);
            }
            return userCache;
        }
    }
}
