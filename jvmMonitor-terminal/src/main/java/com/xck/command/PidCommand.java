package com.xck.command;

/**
 * 获取pid命令
 *
 * @author xuchengkun
 * @date 2022/03/20 10:11
 **/
public class PidCommand extends Command {

    public final static int uri = "/server/pid".hashCode();

    public static PidCommand pidCommandDefault = new PidCommand();

    @Override
    public byte[] bodyBytes() {
        return new byte[0];
    }

    @Override
    public int commandUri() {
        return uri;
    }
}
