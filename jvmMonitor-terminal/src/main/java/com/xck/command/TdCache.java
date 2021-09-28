package com.xck.command;

import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.IStringConverter;

/**
 * tdCache
 *
 * @author xuchengkun
 * @date 2021/09/27 10:49
 **/
public class TdCache {

    private String tdCode;
    private String option;

    public String getTdCode() {
        return tdCode;
    }

    public void setTdCode(String tdCode) {
        this.tdCode = tdCode;
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
    public static class Convert implements IStringConverter<TdCache> {

        @Override
        public TdCache convert(String s) {
            String[] args = s.split(";");
            String tdCode = args[0];
            TdCache tdCache = new TdCache();
            tdCache.setTdCode(tdCode);
            if (args.length == 2 && StrUtil.isNotBlank(args[1])) {
                tdCache.setOption(args[1]);
            }
            return tdCache;
        }
    }
}
