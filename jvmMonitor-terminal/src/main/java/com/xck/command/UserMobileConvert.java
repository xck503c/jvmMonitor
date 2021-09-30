package com.xck.command;

import com.beust.jcommander.IStringConverter;

/**
 * 黑名单号码命中
 *
 * @author xuchengkun
 * @date 2021/09/11 18:01
 **/
public class UserMobileConvert implements IStringConverter<UserMobile> {

    @Override
    public UserMobile convert(String s) {
        String[] args = s.split(";");
        UserMobile blackMobileIsHit = new UserMobile();
        blackMobileIsHit.setUserId(args[0]);
        blackMobileIsHit.setMobile(args[1]);
        return blackMobileIsHit;
    }
}
