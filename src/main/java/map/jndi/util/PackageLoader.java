package map.jndi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageLoader extends ClassLoader {
    private Map<String, Class> classMap = new HashMap<>();
    private String packageName;

    public PackageLoader(String packageName) {
        super();
        this.packageName = packageName;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = classMap.get(name);

        if (clazz != null) {
            return clazz;
        }

        try {
            clazz = findClass(name);

            if (clazz != null) {
                classMap.put(name, clazz);
            } else {
                clazz = super.loadClass(name, resolve);
            }
        } catch (ClassNotFoundException ex) {
            clazz = super.loadClass(name, resolve);
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        URL u = this.getResource(packageName);
        JarFile jarFile;

        try {
            // 使用 sun.net.www.protocol.jar.URLJarFile.getJarFile(java.net.URL) 内部 API 获取 JarFile
            // 目前没找到其它好的办法
            Method m = Class.forName("sun.net.www.protocol.jar.URLJarFile").getDeclaredMethod("getJarFile", URL.class);
            m.setAccessible(true);
            jarFile = (JarFile) m.invoke(null, u);
        } catch (Exception e) {
            throw new ClassNotFoundException("Jar not found: " + packageName, e);
        }

        String fileName = name.replace(".", "/") + ".class";
        JarEntry entry = jarFile.getJarEntry(fileName);
        byte[] data;

        try (InputStream input = jarFile.getInputStream(entry); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;

            while ((n = input.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }

            data = baos.toByteArray();
        } catch (IOException | NullPointerException e) {
            throw new ClassNotFoundException("Class not found: " + name, e);
        }

        return super.defineClass(name, data, 0, data.length);
    }
}
