package map.jndi;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import map.jndi.server.LDAPSServer;
import map.jndi.server.RMIServer;
import map.jndi.server.WebServer;
import map.jndi.server.LDAPServer;

public class Main {
    public static Config config;

    public static void main(String[] args) throws Exception {
        // 解析配置参数
        config = new Config();
        CommandLine cmd = new CommandLine(config);
        ParseResult parseResult = cmd.parseArgs(args);

        if(CommandLine.printHelpIfRequested(parseResult)) {
            return;
        }

        config.codebase = "http://" + config.ip + ":" + config.httpPort + "/";

        RMIServer rmiServer = new RMIServer(Main.config.ip, Main.config.rmiPort);
        LDAPServer ldapServer = new LDAPServer(Main.config.ip, Main.config.ldapPort);
        LDAPSServer ldapsServer = new LDAPSServer(Main.config.ip, Main.config.ldapsPort);
        WebServer webServer = new WebServer(Main.config.ip, Main.config.httpPort);

        Thread rmiThread = new Thread(rmiServer);
        Thread ldapThread = new Thread(ldapServer);
        Thread ldapsThread = new Thread(ldapsServer);
        Thread webThread = new Thread(webServer);

        rmiThread.start();
        ldapThread.start();
        ldapsThread.start();
        webThread.start();
    }
}