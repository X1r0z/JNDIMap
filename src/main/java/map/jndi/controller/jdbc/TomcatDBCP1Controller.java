package map.jndi.controller.jdbc;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.DatabaseController;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/TomcatDBCP1")
public class TomcatDBCP1Controller extends DatabaseController {
    public Object process(Properties props) {
        System.out.println("[Reference] Factory: TomcatDBCP1");

        Reference ref = new Reference("javax.sql.DataSource", "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("url", props.getProperty("url")));
        ref.add(new StringRefAddr("initialSize", "1"));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("connectionInitSqls", props.getProperty("sql")));
        }

        return ref;
    }
}
