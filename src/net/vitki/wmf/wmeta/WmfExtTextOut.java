package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfExtTextOut  extends WmfRecord {

    int    x;
    int    y;
    int    left, top, right, bottom;
    int    flags;
    byte[] text;
    int[]  dx;

    public int render (Renderer rr)  {
        return rr.extTextOut (x, y, text, text.length, flags, dx,
                              left, top, right, bottom);
    }

    public void read (RecordReader rd)  throws IOException {
        int  count = 0;
        left = top = right = bottom = 0;
        y = rd.readWord();
        x = rd.readWord();
        count = rd.readWord();
        flags = rd.readWord();
        int wc = 4;
        if ((flags & Constants.ETO_CLIPPED) != 0)  {
            left = rd.readWord();
            top = rd.readWord();
            right = rd.readWord();
            bottom = rd.readWord();
            wc += 4;
        }
        text = rd.readBytes(count, false);
        if (count % 2 == 1)    rd.skipBytes(1);
        wc += (count+1)/2;
        wc = size - 3 - wc;
        if (wc < 0)
            throw new IOException("bad ExtTextOut");
        if (wc == 0)
            dx = null;
        else {
            dx = new int[wc];
            for (int i=0; i<wc; i++)
                dx[i] = rd.readWord();
        }
    }

    public String toString() {
        String s = "ExtTextOut("+x+C+y+C;
        if ((flags & Constants.ETO_CLIPPED) != 0)
            s += "clip{"+left+C+top+C+right+C+bottom+"},";
        s += "\""+(text==null ? "" : new String(text))+"\",";
        s += "dx{";
        if (dx != null)  {
            for (int i=0; i<dx.length; i++)
                s += ""+dx[i]+(i<dx.length-1?C:"");
        }
        s += "})";
        return s;
    }

}
