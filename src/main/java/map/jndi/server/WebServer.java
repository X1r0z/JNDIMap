package map.jndi.server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class WebServer implements Runnable {
    private String ip;
    private int port;
    public static HttpServer httpServer;
    public WebServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("[HTTP] Listening on " + this.ip + ":" + port);
        try {
            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serveFile(String path, byte[] data) {
        httpServer.createContext(path, exchange -> {
            System.out.println("[HTTP] Receive request: " + exchange.getRequestURI());
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.close();
        });
    }
}
