package com.xck.command;


/**
 * 命令
 *
 * @author xuchengkun
 * @date 2021/09/06 17:22
 **/
public abstract class Command {

    /* 命令体 */
    public abstract byte[] bodyBytes();

    /* 命令uri */
    public abstract int commandUri();
}
