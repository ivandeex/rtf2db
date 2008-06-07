package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfPatBlt  extends WmfRecord {

    int  rop;
    int  x, y, w, h;

    public int render (Renderer rr) {
        return rr.patBlt (x, y, w, h, rop);
    }

    public void read (RecordReader rd)  throws IOException {
        rop = (int)rd.readDWord();
        h = rd.readWord();
        w = rd.readWord();
        y = rd.readWord();
        x = rd.readWord();
    }

    public String toString() {
        return ("PatBlt("+
                ropName(rop)+C+
                x+C+y+C+
                w+C+h+
                ")");
    }

    private static Constants rops = null;

    public static String ropName (int no)  {
        if (rops == null)    rops = new Constants("ROP_");
        return rops.get(no);
    }

}
