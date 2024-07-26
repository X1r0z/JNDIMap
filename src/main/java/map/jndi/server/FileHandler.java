package map.jndi.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

class FileHandler implements HttpHandler {
    private byte[] data;

    public FileHandler(byte[] data) {
        this.data = data;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("[HTTP] Receive request: " + exchange.getRequestURI());
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }
}
