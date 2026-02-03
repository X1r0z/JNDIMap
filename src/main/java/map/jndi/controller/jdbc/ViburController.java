package map.jndi.controller.jdbc;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/Vibur")
public class ViburController extends SingleCommandController {
    public Object process(Properties props) {
        System.out.println("[Reference] Factory: Vibur");

        Reference ref = new Reference("javax.sql.DataSource", "org.vibur.dbcp.ViburDBCPObjectFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("jdbcUrl", props.getProperty("url")));
        ref.add(new StringRefAddr("username", "test"));
        ref.add(new StringRefAddr("password", "test"));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("initSQL", props.getProperty("sql")));
        }

        return ref;
    }
}
