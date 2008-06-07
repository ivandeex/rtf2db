package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetBkMode extends EmfRecord
{
    int  mode;

    public int render (Renderer rr) {
        return rr.setBkMode (mode);
    }

    public void read (RecordReader rd)  throws IOException {
        mode = rd.readLong();
    }

    public String toString() {
        return "SetBkMode("+modeName(mode)+"{"+mode+"})";
    }

    private static Constants modes = null;

    public static String modeName (int no)  {
        if (modes == null)   modes = new Constants("BKM_");
        return modes.get(no);
    }
}
