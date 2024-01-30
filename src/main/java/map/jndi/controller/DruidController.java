package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.bean.DatabaseBean;

import javax.naming.Reference;
import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/Druid")
public class DruidController extends DatabaseController {
    public Object process(DatabaseBean databaseBean) {
        Reference ref = new Reference("javax.sql.DataSource", "com.alibaba.druid.pool.DruidDataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", databaseBean.getDriver()));
        ref.add(new StringRefAddr("url", databaseBean.getUrl()));
        ref.add(new StringRefAddr("initialSize", "1"));
        ref.add(new StringRefAddr("init", "true"));

        if (databaseBean.getSql() != null) {
            ref.add(new StringRefAddr("initConnectionSqls", databaseBean.getSql()));
        }

        return ref;
    }
}
