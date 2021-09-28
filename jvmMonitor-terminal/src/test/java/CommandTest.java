import com.beust.jcommander.JCommander;
import com.xck.command.Command;

/**
 * 命令测试
 *
 * @author xuchengkun
 * @date 2021/09/27 13:30
 **/
public class CommandTest {

    public static void main(String[] args) {
        Command command = new Command();
        JCommander.newBuilder().addObject(command).build().parse("-program", "a", "-userCache", "1");
        System.out.println(command.userCache.getUserId());
        JCommander.newBuilder().addObject(command).build().parse("-program", "a", "-tdCache", "1");
        System.out.println(command.tdCache.getTdCode());
    }
}
