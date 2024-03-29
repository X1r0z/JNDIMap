package map.jndi.util;

import javassist.CtClass;
import javassist.CtField;

import java.lang.reflect.Field;

public class ReflectUtil {
    public static void setFieldValue(Object obj, String name, Object val) throws Exception{
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, val);
    }

    public static void setCtField(CtClass clazz, String name, CtField.Initializer value) throws Exception {
        CtField ctField = clazz.getField(name);
        clazz.removeField(ctField);
        clazz.addField(ctField, value);
    }
}