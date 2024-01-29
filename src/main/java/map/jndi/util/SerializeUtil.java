package map.jndi.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(arr)){
            output.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr.toByteArray();
    }
}