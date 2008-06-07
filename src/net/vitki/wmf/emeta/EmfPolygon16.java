package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfPolygon16 extends EmfRecord
{
    int    box_left, box_top, box_right, box_bottom;
    int    num;
    int[]  x;
    int[]  y;

    public int render (Renderer rr) {
        return rr.polygon (num, x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        box_left = rd.readLong();
        box_top = rd.readLong();
        box_right = rd.readLong();
        box_bottom = rd.readLong();
        num = rd.readLong();
        x = new int[num];
        y = new int[num];
        for (int i=0; i<num; i++)  {
            x[i] = rd.readWord();
            y[i] = rd.readWord();
        }
    }

    public String toString() {
        String s = "Polygon16["+num+"](";
        for (int i=0; i<num; i++)  {
            s += "("+x[i]+C+y[i]+")";
            if (i>8)    { s+=",..."; break; }
            if (i<num-1)    s+=C;
        }
        s+=",box{"+box_left+C+box_top+C+box_right+C+box_bottom+"}";
        return s+")";
    }

}
