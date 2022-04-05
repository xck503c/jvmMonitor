package methodmonitor;

import com.xck.asm.ClassMethod;
import com.xck.asm.MethodMonitorEnhancer;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

/**
 * 方法监控
 *
 * @author xuchengkun
 * @date 2022/04/01 16:22
 **/
public class MethodMonitorHandlerTest {

    public static void main(String[] args) throws Exception {

        TargetTestBean targetTestBean = new TargetTestBean();

        Instrumentation instrumentation = ByteBuddyAgent.install();
        instrumentation.addTransformer(MethodMonitorEnhancer.INSTANCE, true);

        Set<ClassMethod> classMethods = new HashSet<>();
        ClassMethod classMethod = new ClassMethod("methodmonitor.TargetTestBean");
        classMethod.addMethodName("returnMsg");
        classMethods.add(classMethod);

        MethodMonitorEnhancer.enhance(instrumentation, classMethods);
        MethodMonitorEnhancer.enhance(instrumentation, classMethods);

        targetTestBean.returnMsg(1, "fffff");
        MethodMonitorEnhancer.reset(instrumentation, classMethods);
        targetTestBean.returnMsg(1, "fffff");
        MethodMonitorEnhancer.reset(instrumentation, classMethods);
        targetTestBean.returnMsg(1, "fffff");
    }
}
