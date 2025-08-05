package map.jndi;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "JNDIMap.jar",
        version = "0.0.3",
        description = "JNDI injection exploitation framework",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        showDefaultValues = true
)
public class Config {
    @Option(names = {"-i", "--ip"}, paramLabel = "<ip>", description = "IP address (codebase) to listen on")
    public String ip = "127.0.0.1";

    @Option(names = {"-r", "--rmi-port"}, paramLabel = "<rmi-port>", description = "RMI server bind port")
    public int rmiPort = 1099;

    @Option(names = {"-l", "--ldap-port"}, paramLabel = "<ldap-port>", description = "LDAP server bind port")
    public int ldapPort = 1389;

    @Option(names = {"-s", "--ldaps-port"}, paramLabel = "<ldaps-port>", description = "LDAPS server bind port")
    public int ldapsPort = 1636;

    @Option(names = {"-p", "--http-port"}, paramLabel = "<http-port>", description = "HTTP server bind port")
    public int httpPort = 3456;

    @Option(names = {"-u", "--url"}, description = "specify the JNDI route")
    public String url;

    @Option(names = {"-j", "--jks-path"}, paramLabel = "<jks-path>", description = "path to the JKS cert")
    public String jksPath;

    @Option(names = {"-k", "--jks-pin"}, paramLabel = "<jks-pin>", description = "pin of the JKS cert")
    public String jksPin;

    @Option(names = {"-f", "--file"}, description = "path to the custom JS script")
    public String file;

    @Option(names = {"--use-reference-only"}, description = "directly returns Reference object through LDAP related parameters")
    public boolean useReferenceOnly = false;

    @Option(names = {"--confusing-class-name"}, description = "use confusing class names when generating malicious Java classes")
    public boolean confusingClassName = false;

    @Option(names = {"--jshell"}, description = "use JShell to execute the payload instead of Nashorn JS engine")
    public boolean jshell = false;

    public String codebase;
}
