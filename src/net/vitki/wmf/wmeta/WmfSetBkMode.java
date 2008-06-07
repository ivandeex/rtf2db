package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetBkMode  extends WmfRecord  {

    int  mode;
    int  spare1;

    public int render (Renderer rr) {
        return rr.setBkMode (mode);
    }

    public void read (RecordReader rd)  throws IOException {
        mode = rd.readWord();
        spare1 = size==5 ? rd.readWord() : 0;
    }

    public String toString() {
        return "SetBkMode("+modeName(mode)+"{"+mode+"}"+C+spare1+")";
    }

    private static Constants modes = null;

    public static String modeName (int no)  {
        if (modes == null)   modes = new Constants("BKM_");
        return modes.get(no);
    }

}
