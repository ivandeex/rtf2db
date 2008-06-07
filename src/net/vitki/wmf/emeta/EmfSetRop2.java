package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetRop2 extends EmfRecord
{
    int   rop2;

    public int render (Renderer rr) {
        return rr.setRop2 (rop2);
    }

    public void read (RecordReader rd)  throws IOException {
        rop2 = rd.readLong();
    }

    public String toString() {
        return "SetROP2("+rop2Name(rop2)+")";
    }

    private static Constants rop2s = null;

    public static String rop2Name (int no)  {
        if (rop2s == null)   rop2s = new Constants("R2_");
        return rop2s.get(no);
    }
}
