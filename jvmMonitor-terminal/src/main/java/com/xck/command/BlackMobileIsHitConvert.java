package com.xck.command;

import com.beust.jcommander.IStringConverter;

/**
 * 黑名单号码命中
 *
 * @author xuchengkun
 * @date 2021/09/11 18:01
 **/
public class BlackMobileIsHitConvert implements IStringConverter<BlackMobileIsHit> {

    @Override
    public BlackMobileIsHit convert(String s) {
        String[] args = s.split(";");
        BlackMobileIsHit blackMobileIsHit = new BlackMobileIsHit();
        blackMobileIsHit.setUserId(args[0]);
        blackMobileIsHit.setMobile(args[1]);
        return blackMobileIsHit;
    }
}
