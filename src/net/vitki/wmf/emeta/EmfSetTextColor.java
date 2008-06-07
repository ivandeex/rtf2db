package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetTextColor extends EmfRecord
{
    int   color;

    public int render (Renderer rr) {
        return rr.setTextColor (color);
    }

    public void read (RecordReader rd)  throws IOException {
        color = rd.readColor();
    }

    public String toString() {
        return ("SetTextColor("+
                "#"+Integer.toHexString(color)+
                ")");
    }
}
