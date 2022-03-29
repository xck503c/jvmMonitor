import java.util.HashMap;
import java.util.Map;

/**
 * http接口测试
 *
 * @author xuchengkun
 * @date 2022/03/20 11:10
 **/
public class HttpInterfaceTest {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("name", "xck防守打法");
    }

    public static String getMap(String key) {
        return map.get(key);
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            Thread.sleep(10000);
        }
    }
}
