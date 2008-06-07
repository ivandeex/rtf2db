package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetPolyFillMode  extends WmfRecord  {

    int  mode;
    int  spare1;

    public int render (Renderer rr) {
        return rr.setPolyFillMode (mode);
    }

    public void read (RecordReader rd)  throws IOException {
        mode = rd.readWord();
        spare1 = size==5 ? rd.readWord() : 0;
    }

    public String toString() {
        return "SetPolyFillMode("+modeName(mode)+")";
    }

    private static Constants modes = null;

    public static String modeName (int no)  {
        if (modes == null)  modes = new Constants("PFM_");
        return modes.get(no);
    }

}
