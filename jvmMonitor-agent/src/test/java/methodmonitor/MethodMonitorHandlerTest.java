package methodmonitor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sun.javafx.runtime.SystemProperties;
import com.xck.SysConstants;
import com.xck.agent.methodMonitor.MethodInvokeListenerManager;
import com.xck.agent.methodMonitor.MethodMonitorEnhancer;
import com.xck.asm.MethodMonitorRule;
import com.xck.asm.MethodMonitorRuleGroup;
import com.xck.util.ClassAgentUtil;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.*;

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

        SysConstants.inst = instrumentation;

        List<MethodMonitorRuleGroup> list = new ArrayList<>();
        MethodMonitorRuleGroup ruleGroup = new MethodMonitorRuleGroup();
        ruleGroup.setId("xxxxx");
        Map<String, MethodMonitorRule> map = new HashMap<>();

        MethodMonitorRule monitorRule = new MethodMonitorRule();
        monitorRule.setClassName("methodmonitor.TargetTestBean");
        monitorRule.setMethodName("getMap");
        monitorRule.setAfter(true);
        map.put("methodmonitor.TargetTestBeanreturnMsg", monitorRule);

        ruleGroup.setRuleMap(map);
        list.add(ruleGroup);
        MethodInvokeListenerManager.updateRule(list);

        while (true) {
            TargetTestBean.getMap("name");
            Thread.sleep(1000);
        }

//        targetTestBean.returnMsg(1, "fffff");
//        MethodInvokeListenerManager.cancelRule();
//        targetTestBean.returnMsg(1, "fffff");
//        MethodInvokeListenerManager.updateRule(list);
//        targetTestBean.returnMsg(1, "fffff");

//        System.out.println(JSONUtil.toJsonStr(monitorRule));
    }
}
