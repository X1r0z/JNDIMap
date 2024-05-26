package map.jndi;

public class Config {
    public static String ip = "127.0.0.1";
    public static int rmiPort = 1099;
    public static int ldapPort = 1389;
    public static int httpPort = 3456;
    public static String url;
    public static String codebase;
    public static String file;
    public static boolean useReferenceOnly = false;

    public static void parse(String[] args) {
        // 解析命令行参数
        for (int i = 0; i < args.length; i ++ ) {
            switch (args[i]) {
                case "-h":
                    System.out.println("Usage: java -jar JNDIMap.jar [-i <ip>] [-r <rmiPort>] [-l <ldapPort>] [-p <httpPort>] [-u <url>] [-f <file>] [-useReferenceOnly] [-h]");
                    System.exit(-1);
                case "-i":
                    ip = args[i + 1];
                    break;
                case "-r":
                    rmiPort = Integer.parseInt(args[i + 1]);
                    break;
                case "-l":
                    ldapPort = Integer.parseInt(args[i + 1]);
                    break;
                case "-p":
                    httpPort = Integer.parseInt(args[i + 1]);
                    break;
                case "-u":
                    url = args[i + 1];
                    break;
                case "-f":
                    file = args[i + 1];
                    break;
                case "-useReferenceOnly":
                    useReferenceOnly = true;
                    break;
            }
        }

        codebase = "http://" + ip + ":" + httpPort + "/";
    }
}
