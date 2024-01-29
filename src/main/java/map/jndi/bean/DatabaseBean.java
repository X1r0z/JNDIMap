package map.jndi.bean;

public class DatabaseBean {
    private String driver;
    private String url;

    public DatabaseBean(String driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }
}
