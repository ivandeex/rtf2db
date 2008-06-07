package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetBkColor  extends WmfRecord {

    int   color;

    public int render (Renderer rr)  {
        return rr.setBkColor (color);
    }

    public void read (RecordReader rd)  throws IOException {
        color = rd.readColor();
    }

    public String toString() {
        return ("SetBkColor("+
                "#"+Integer.toHexString(color)+
                ")");
    }

}
