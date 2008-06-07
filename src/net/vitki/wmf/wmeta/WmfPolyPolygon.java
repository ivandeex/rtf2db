package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfPolyPolygon  extends WmfRecord  {

    Poly  poly[];
    int      num;

    public int render (Renderer rr) {
        return rr.polyPolygon (num, poly);
    }

    public void read (RecordReader rd)  throws IOException {
        int i, j, n;
        num = rd.readWord();
        poly = new Poly[num];
        for (i=0; i<num; i++) {
            n = rd.readWord();
            poly[i] = new Poly();
            poly[i].n = n;
            poly[i].x = new int[n];
            poly[i].y = new int[n];
        }
        for (i=0; i<num; i++)  {
            for (j=0; j<poly[i].n; j++)  {
                poly[i].x[j] = rd.readWord();
                poly[i].y[j] = rd.readWord();
            }
        }
    }

    public String toString() {
        String s = "PolyPolygon["+num+"](";
        int i, j;
        for (i=0; i<num; i++)  {
            int n = this.poly[i].n;
            int[] x = poly[i].x;
            int[] y = poly[i].y;
            s += "["+n+"]{";
            for (j=0; j<n; j++)  {
                s += "("+x[j]+C+y[j]+")";
                if (j>4)    { s+=",..."; break; }
                if (j<n-1)    s+=C;
            }
            s += "}";
            if (i>4)    { s+=",..."; break; }
            if (i<num-1)    s+=C;
        }
        return s+")";
    }

}
