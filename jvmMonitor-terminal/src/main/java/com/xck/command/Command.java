package com.xck.command;

import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import jline.console.completer.Completer;

import java.util.*;

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

    @Parameter(names = {"-blackIsHit"}, converter = UserMobileConvert.class, description = "-blackIsHit userId;mobile")
    public UserMobile blackMobileIsHit;

    @Parameter(names = {"-netSwitch"}, description = "-netSwitch mobile")
    public String netSwitch;

    @Parameter(names = {"-sendTd"}, converter = UserMobileConvert.class, description = "-sendTd userId;mobile")
    public UserMobile sendTd;

    @Parameter(names = {"-location"}, description = "-location mobile")
    public String location;

    public static List<Completer> getCompleter() {
        List<Completer> list = new ArrayList<>();
        list.add(new EndsCompleter("help"));
        list.add(new EndsCompleter("-program"));
        list.add(new EndsCompleter("-userCache"));
        list.add(new EndsCompleter("-tdCache"));
        list.add(new EndsCompleter("-gateConfig"));
        list.add(new EndsCompleter("-blackIsHit"));
        list.add(new EndsCompleter("-netSwitch"));
        list.add(new EndsCompleter("-sendTd"));
        list.add(new EndsCompleter("-location"));
        list.add(new EndsCompleter("sender"));
        list.add(new EndsCompleter("receiver"));
        list.add(new EndsCompleter("dealer"));
        list.add(new EndsCompleter("base"));
        list.add(new EndsCompleter("service"));
        list.add(new EndsCompleter("groupService"));
        list.add(new EndsCompleter("multiService"));
        list.add(new EndsCompleter("multiGroupService"));
        list.add(new EndsCompleter("sign"));
        list.add(new EndsCompleter("extInfo"));
        return list;
    }

    /**
     * 根据空格分割，末尾补全
     */
    public static class EndsCompleter implements Completer {
        private SortedSet<String> strings = new TreeSet<>();

        public EndsCompleter(Collection<String> strings) {
            this.strings.addAll(strings);
        }

        public EndsCompleter(String... strings) {
            this(Arrays.asList(strings));
        }

        /**
         * @param buffer     输入字符串
         * @param cursor     当前所处的游标
         * @param candidates 候选集合
         * @return
         */
        @Override
        public int complete(String buffer, int cursor, List<CharSequence> candidates) {

            if (StrUtil.isBlank(buffer)) {
                candidates.addAll(strings);
                return candidates.isEmpty() ? -1 : 0;
            }

            //往前判断空格
            int lastBlank = buffer.lastIndexOf(" ");
            String commandParam = buffer;
            if (lastBlank != -1) {
                commandParam = buffer.substring(lastBlank + 1, cursor);
            }
            for (String match : strings.tailSet(commandParam)) {
                if (match.startsWith(commandParam)) {
                    candidates.add(match);
                }
            }

            if (lastBlank != -1) {
                return candidates.isEmpty() ? -1 : lastBlank + 1; //游标重新定位
            }

            return candidates.isEmpty() ? -1 : 0;
        }
    }
}
