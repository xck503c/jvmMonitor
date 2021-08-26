/**
 * 方法测试
 *
 * @author xuchengkun
 * @date 2021/08/25 10:04
 **/
public class TestMethod {

    public static void main(String[] args) throws Exception{
        Thread.currentThread().setContextClassLoader(null);
        while (true) {
            Thread.sleep(3000);
            System.out.println(get(System.currentTimeMillis()));
        }
    }

    public static String get(long i){
        return i+"";
    }
}
