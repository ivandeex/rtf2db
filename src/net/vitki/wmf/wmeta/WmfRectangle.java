package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfRectangle  extends WmfRecord  {

    int  left, top, right, bottom;

    public int render (Renderer rr) {
        return rr.rectangle (left, top, right, bottom);
    }

    public void read (RecordReader rd)  throws IOException {
        bottom = rd.readWord();
        right = rd.readWord();
        top = rd.readWord();
        left = rd.readWord();
    }

    public String toString() {
        return "Rectangle("+left+C+top+C+(right-left)+C+(bottom-top)+")";
    }

}
