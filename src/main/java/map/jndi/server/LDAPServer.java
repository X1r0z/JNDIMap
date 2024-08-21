package map.jndi.server;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;

public class LDAPServer implements Runnable {
    public String ip;
    public int port;

    public LDAPServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=example,dc=com");
            config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("listen-ldap", this.port));
            config.addInMemoryOperationInterceptor(new OperationInterceptor("LDAP"));

            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            ds.startListening();

            System.out.println("[LDAP] Listening on " + this.ip + ":" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
