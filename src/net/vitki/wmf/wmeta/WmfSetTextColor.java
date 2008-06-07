package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetTextColor  extends WmfRecord {

    int   color;

    public int render (Renderer rr) {
        return rr.setTextColor (color);
    }

    public void read (RecordReader rd)  throws IOException {
        color = rd.readColor();
    }

    public String toString() {
        return ("SetTextColor("+
                "#"+Integer.toHexString(color)+
                ")");
    }

}
