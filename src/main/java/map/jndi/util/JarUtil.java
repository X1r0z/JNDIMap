package map.jndi.util;

import java.io.ByteArrayOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarUtil {
    public static byte[] create(String className, byte[] byteCode) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (JarOutputStream jos = new JarOutputStream(baos)) {
            // 写入恶意 class 文件
            JarEntry entry = new JarEntry(className.replace(".", "/") + ".class");
            jos.putNextEntry(entry);
            jos.write(byteCode);
            jos.closeEntry();
        }

        // 返回 jar 内容
        return baos.toByteArray();
    }
    public static byte[] createWithSPI(String spiClassName, String className, byte[] byteCode) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (JarOutputStream jos = new JarOutputStream(baos)) {
            // 写入恶意 class 文件
            JarEntry entry = new JarEntry(className.replace(".", "/") + ".class");
            jos.putNextEntry(entry);
            jos.write(byteCode);
            jos.closeEntry();

            // 写入 SPI 文件
            entry = new JarEntry("META-INF/services/" + spiClassName);
            jos.putNextEntry(entry);
            jos.write(className.getBytes());
            jos.closeEntry();
        }

        // 返回 jar 内容
        return baos.toByteArray();
    }
}