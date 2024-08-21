package map.jndi.server;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.util.ssl.*;
import map.jndi.Config;

public class LDAPSServer implements Runnable {
    public String ip;
    public int port;

    public LDAPSServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            if (Config.jksPath == null) {
                System.out.println("[LDAPS] jks file is not specified, skipping to start LDAPS server");
                return;
            }

            SSLUtil serverSSLUtil = new SSLUtil(
                    new KeyStoreKeyManager(Config.jksPath, Config.jksPin != null ? Config.jksPin.toCharArray() : null),
                    new TrustAllTrustManager()
            );
            SSLUtil clientSSLUtil = new SSLUtil(new TrustAllTrustManager());

            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=example,dc=com");
            config.setListenerConfigs(InMemoryListenerConfig.createLDAPSConfig(
                    "listen-ldaps",
                    null,
                    this.port,
                    serverSSLUtil.createSSLServerSocketFactory(),
                    clientSSLUtil.createSSLSocketFactory()
            ));
            config.addInMemoryOperationInterceptor(new OperationInterceptor("LDAPS"));

            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            ds.startListening();

            System.out.println("[LDAPS] Listening on " + this.ip + ":" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
