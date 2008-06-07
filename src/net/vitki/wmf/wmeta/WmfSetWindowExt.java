package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetWindowExt  extends WmfRecord  {

    public int  w, h;

    public int render (Renderer rr) {
        return rr.setWindowExt (w, h);
    }

    public void read (RecordReader rd)  throws IOException {
        h = rd.readWord();
        w = rd.readWord();
    }

    public String toString() {
        return "SetWindowExt("+w+C+h+")";
    }

}
