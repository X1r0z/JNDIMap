package map.jndi.controller;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import map.jndi.Config;
import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.util.MiscUtil;

import java.io.File;

@JNDIController
public class CustomController implements Controller {
    public Object process(String args) throws Exception {
        File file = new File(Config.file);
        String rootPath = file.getParentFile() != null ? file.getParentFile().getCanonicalPath() : new File("").getCanonicalPath();
        String fileName = file.getName();
        GroovyScriptEngine engine = new GroovyScriptEngine(rootPath);
        Binding binding = new Binding();
        binding.setVariable("args", args);
        Object result = engine.run(fileName, binding);
        return result;
    }

    @JNDIMapping("/Custom/{args}")
    public String Custom(String args) {
        args = MiscUtil.tryBase64UrlDecode(args);
        System.out.println("[Custom] File: " + Config.file + " Args: " + args);
        return args;
    }
}
