package map.jndi;

import map.jndi.server.RMIServer;
import map.jndi.server.WebServer;
import map.jndi.server.LDAPServer;

public class Main {
    public static void main(String[] args) {

        // 解析配置参数
        Config.parse(args);

        RMIServer rmiServer = new RMIServer(Config.ip, Config.rmiPort);
        LDAPServer ldapServer = new LDAPServer(Config.ip, Config.ldapPort);
        WebServer webServer = new WebServer(Config.ip, Config.httpPort);

        Thread rmiThread = new Thread(rmiServer);
        Thread ldapThread = new Thread(ldapServer);
        Thread webThread = new Thread(webServer);

        rmiThread.start();
        ldapThread.start();
        webThread.start();
    }
}