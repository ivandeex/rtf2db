package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetRop2  extends WmfRecord {

    int   rop2;
    int   spare1;

    public int render (Renderer rr) {
        return rr.setRop2 (rop2);
    }

    public void read (RecordReader rd)  throws IOException {
        rop2 = rd.readWord();
        spare1 = size==5 ? rd.readWord() : 0;
    }

    public String toString() {
        return "SetROP2("+rop2Name(rop2)+C+spare1+")";
    }

    private static Constants rop2s = null;

    public static String rop2Name (int no)  {
        if (rop2s == null)   rop2s = new Constants("R2_");
        return rop2s.get(no);
    }

}
