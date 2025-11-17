package map.jndi.util;

import map.jndi.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.util.Map;

public class SerializeUtil {
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();

        try (ObjectOutputStream output = new ObjectOutputStream(arr)) {
            output.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Main.config.overlongEncoding) {
            return new UTF8BytesMix(arr.toByteArray()).mix();
        } else {
            return arr.toByteArray();
        }
    }

    public static byte[] serialize(Object obj, Map<Class<?>, Long> map) {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();

        try(ObjectOutputStream output = new ObjectOutputStream(arr) {
            @Override
            protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
                Class<?> clazz = desc.forClass();

                if (map.containsKey(clazz)) {
                    ObjectStreamClass c = ObjectStreamClass.lookupAny(clazz);
                    try {
                        Field suid = ObjectStreamClass.class.getDeclaredField("suid");
                        suid.setAccessible(true);
                        suid.set(c, map.get(clazz));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                super.writeClassDescriptor(desc);
            }
        }) {
            output.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Main.config.overlongEncoding) {
            return new UTF8BytesMix(arr.toByteArray()).mix();
        } else {
            return arr.toByteArray();
        }
    }
}