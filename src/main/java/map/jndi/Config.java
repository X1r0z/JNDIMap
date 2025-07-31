package map.jndi;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "JNDIMap.jar",
        version = "0.0.2",
        description = "JNDI injection exploitation framework",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        showDefaultValues = true
)
public class Config {
    @Option(names = {"-i", "--ip"}, description = "IP address (codebase) to listen on")
    public String ip = "127.0.0.1";

    @Option(names = {"-r", "--rmiPort"}, description = "RMI server bind port")
    public int rmiPort = 1099;

    @Option(names = {"-l", "--ldapPort"}, description = "LDAP server bind port")
    public int ldapPort = 1389;

    @Option(names = {"-s", "--ldapsPort"}, description = "LDAPS server bind port")
    public int ldapsPort = 1636;

    @Option(names = {"-p", "--httpPort"}, description = "HTTP server bind port")
    public int httpPort = 3456;

    @Option(names = {"-u", "--url"}, description = "specify the JNDI route")
    public String url;

    @Option(names = {"-j", "--jksPath"}, description = "path to the JKS cert")
    public String jksPath;

    @Option(names = {"-k", "--jksPin"}, description = "pin of the JKS cert")
    public String jksPin;

    @Option(names = {"-f", "--file"}, description = "path to the custom JS script")
    public String file;

    @Option(names = {"-useReferenceOnly"}, description = "directly returns Reference object through LDAP related parameters")
    public boolean useReferenceOnly = false;

    @Option(names = {"-fakeClassName"}, description = "use random fake class names when generating malicious Java classes")
    public boolean fakeClassName = false;

    public String codebase;
}
