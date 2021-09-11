package com.xck.command;

import com.beust.jcommander.IStringConverter;

/**
 * 账户缓存转换器
 *
 * @author xuchengkun
 * @date 2021/09/11 17:32
 **/
public class UserCacheConvert implements IStringConverter<UserCache> {

    @Override
    public UserCache convert(String s) {
        String[] args = s.split(";");
        String userId = args[0];
        UserCache userCache = new UserCache();
        userCache.setUserId(userId);
        return userCache;
    }
}
