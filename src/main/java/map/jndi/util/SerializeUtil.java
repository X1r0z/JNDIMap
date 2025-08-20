package map.jndi.util;

import map.jndi.Main;

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

        if (Main.config.overlongEncoding) {
            return new UTF8BytesMix(arr.toByteArray()).mix();
        } else {
            return arr.toByteArray();
        }
    }
}