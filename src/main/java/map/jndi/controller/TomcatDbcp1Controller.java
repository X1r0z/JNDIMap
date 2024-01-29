package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.DatabaseBean;

import javax.naming.Reference;
import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/TomcatDbcp1")
public class TomcatDbcp1Controller extends DatabaseController {
    public Object process(DatabaseBean databaseBean) {
        Reference ref = new Reference("javax.sql.DataSource", "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", databaseBean.getDriver()));
        ref.add(new StringRefAddr("url", databaseBean.getUrl()));
        ref.add(new StringRefAddr("initialSize","1"));
        return ref;
    }
}
