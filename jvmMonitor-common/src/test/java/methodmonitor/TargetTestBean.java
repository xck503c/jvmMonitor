package methodmonitor;

/**
 * 测试bean
 *
 * @author xuchengkun
 * @date 2022/04/01 16:41
 **/
public class TargetTestBean {

    public String returnMsg(int a, String o) {
        System.out.println("return msg hhh...");
        ++a;
        return "success";
    }
}
