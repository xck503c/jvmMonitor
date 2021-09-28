package com.xck.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令
 *
 * @author xuchengkun
 * @date 2021/09/06 17:22
 **/
@Parameters(commandDescription = "命令格式: program -具体选项 选项所需的选项")
public class Command {

    @Parameter(names = {"-program"}, required = true, description = "-program sender|receiver|dealer")
    public String program;

    @Parameter(names = {"-userCache"}, converter = UserCache.Convert.class, description = "-userCache userId;[base|service|groupService|multiService|multiGroupService|sign|extInfo]")
    public UserCache userCache;

    @Parameter(names = {"-tdCache"}, converter = TdCache.Convert.class, description = "-tdCache tdCode;[base|sign|extInfo]")
    public TdCache tdCache;

    @Parameter(names = {"-gateConfig"}, description = "-gateConfig keyName")
    public String gateConfigName;

    @Parameter(names = {"-blackIsHit"}, converter = BlackMobileIsHitConvert.class, description = "-blackIsHit userId;mobile")
    public BlackMobileIsHit blackMobileIsHit;
}
