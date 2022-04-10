package com.xck.command;

/**
 * 获取pid命令
 *
 * @author xuchengkun
 * @date 2022/03/20 10:11
 **/
public class TestCommand extends Command {

    public final static int uri = "/server/activeTest".hashCode();

    public static TestCommand testCommand = new TestCommand();

    @Override
    public byte[] bodyBytes() {
        return new byte[0];
    }

    @Override
    public int commandUri() {
        return uri;
    }
}
