package map.jndi.bean;

import java.util.List;

public class DatabaseBean {
    private String driver;
    private String url;
    private String sql;

    public DatabaseBean(String driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public DatabaseBean(String driver, String url, String sql) {
        this.driver = driver;
        this.url = url;
        this.sql = sql;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getSql() {
        return sql;
    }
}
