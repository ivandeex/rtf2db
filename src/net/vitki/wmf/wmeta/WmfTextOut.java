package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfTextOut extends WmfRecord {

    int    x;
    int    y;
    byte[] text;

    public int render (Renderer rr)  {
        return rr.textOut (text, text.length, x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        int  count = 0;
        count = rd.readWord();
        int wc = 4;
        text = rd.readBytes(count, false);
        if (count % 2 == 1)    rd.skipBytes(1);
        wc += (count+1)/2;
        y = rd.readWord();
        x = rd.readWord();
        wc += 2;
        wc = size - wc;
        if (wc != 0)
            throw new IOException("bad textOut");
    }

    public String toString() {
        String s = "textOut("+x+C+y+")";
        return s;
    }

}
