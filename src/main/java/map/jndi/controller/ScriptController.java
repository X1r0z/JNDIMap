package map.jndi.controller;

import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;

@JNDIController
public class ScriptController implements Controller {
    public Object process(String args) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.put("args", args);
        return engine.eval(new FileReader(Config.file));
    }

    @JNDIMapping("/Script/{args}")
    public String script(String args) {
        System.out.println("[Script] File: " + Config.file + " Args: " + args);
        return args;
    }
}
