package map.jndi.bean;

public class ClassBean {
    private String name;
    private byte[] data;

    public ClassBean(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
