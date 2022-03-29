import cn.hutool.core.util.RuntimeUtil;
import com.beust.jcommander.JCommander;
import com.xck.command.Command;
import com.xck.command.PidCommand;

/**
 * 命令测试
 *
 * @author xuchengkun
 * @date 2021/09/27 13:30
 **/
public class CommandTest {

    public static void main(String[] args) {
        System.out.println(PidCommand.uri);
//        Command command = new Command();
//        JCommander.newBuilder().addObject(command).build().parse("-staticMethod", "com.hskj.blackMobileSync.properties.PropertiesManager#getSystemConfigProStr#black.mobile.table.remove.destTableName#no");
//        System.out.println(command.staticMethod.getArgs());
//
//        RuntimeUtil.execForStr("java -Xbootclasspath/a:C:\\Program Files\\Java\\jre1.8.0_191/../lib/tools.jar"
//                + "-cp .;./* com.xck.boot.BootStrap 25152 62432");
    }
}
