package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetStretchBltMode  extends WmfRecord  {

    int  mode;

    public int render (Renderer rr) {
        return rr.setStretchBltMode (mode);
    }

    public void read (RecordReader rd)  throws IOException {
        mode = rd.readWord();
        rd.skipBytes( rd.restBytes() );
    }

    public String toString() {
        return "SetStretchBltMode("+modeName(mode)+")";
    }

    private static Constants modes = null;

    public static String modeName (int no)  {
        if (modes == null)   modes = new Constants("SBM_");
        return modes.get(no);
    }

}
