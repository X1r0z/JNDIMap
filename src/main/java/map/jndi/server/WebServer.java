package map.jndi.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class WebServer implements Runnable {
    public String ip;
    public int port;
    private HttpServer httpServer;
    private static WebServer INSTANCE;
    public static WebServer getInstance() {
        return INSTANCE;
    }
    public WebServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("[HTTP] Listening on " + ip + ":" + port);
        try {
            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            httpServer.start();
            if (INSTANCE == null) {
                INSTANCE = this;
            } else {
                throw new RuntimeException("WebServer has already been started");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void serveFile(String path, byte[] data) {
        httpServer.createContext(path, new FileHandler(data));
    }
}