package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfPolyline  extends WmfRecord  {

    int    num;
    int[]  x;
    int[]  y;

    public int render (Renderer rr) {
        return rr.polyline (num, x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        num = rd.readWord();
        x = new int[num];
        y = new int[num];
        for (int i=0; i<num; i++)  {
            x[i] = rd.readWord();
            y[i] = rd.readWord();
        }
    }

    public String toString() {
        String s = "Polyline["+num+"](";
        for (int i=0; i<num; i++)  {
            s += "("+x[i]+C+y[i]+")";
            if (i>4)    { s+=",..."; break; }
            if (i<num-1)    s+=C;
        }
        return s+")";
    }

}
