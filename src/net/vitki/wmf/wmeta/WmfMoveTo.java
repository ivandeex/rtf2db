package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfMoveTo  extends WmfRecord  {

    int  x, y;

    public int render (Renderer rr) {
        return rr.moveTo (x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        y = rd.readWord();
        x = rd.readWord();
    }

    public String toString() {
        return "MoveTo("+x+C+y+")";
    }

}
