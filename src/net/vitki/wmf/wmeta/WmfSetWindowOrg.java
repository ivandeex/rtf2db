package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetWindowOrg  extends WmfRecord  {

    public int  x, y;

    public int render (Renderer rr) {
        return rr.setWindowOrg (x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        x = rd.readWord();
        y = rd.readWord();
    }

    public String toString() {
        return "SetWindowOrg("+x+C+y+")";
    }

}
