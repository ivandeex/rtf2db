package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfLineTo  extends WmfRecord  {

    int  x, y;

    public int render (Renderer rr)  {
        return rr.lineTo (x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        y = rd.readWord();
        x = rd.readWord();
    }

    public String toString() {
        return "LineTo("+x+C+y+")";
    }

}
