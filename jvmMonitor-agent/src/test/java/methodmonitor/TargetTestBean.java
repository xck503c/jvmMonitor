package methodmonitor;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试bean
 *
 * @author xuchengkun
 * @date 2022/04/01 16:41
 **/
public class TargetTestBean {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("name", "xck防守打法");
    }

    public static String getMap(String key) {
        return map.get(key);
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            getMap("name");
            Thread.sleep(10000);
        }
    }
}
