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

    @Parameter(names = {"-staticMethod"}, converter = StaticMethod.Convert.class, description = "-static com.xck.Test#methodName#args1#args2")
    public StaticMethod staticMethod;

    public static List<Completer> getCompleter() {
        List<Completer> list = new ArrayList<>();
        list.add(new EndsCompleter("help"));
        list.add(new EndsCompleter("-staticMethod"));
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
