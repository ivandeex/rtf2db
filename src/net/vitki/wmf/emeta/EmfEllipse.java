package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfEllipse extends EmfRecord
{
    int  left, top, right, bottom;

    public int render (Renderer rr) {
        return rr.ellipse (left, top, right, bottom);
    }

    public void read (RecordReader rd)  throws IOException {
        left = rd.readLong();
        top = rd.readLong();
        right = rd.readLong();
        bottom = rd.readLong();
    }

    public String toString() {
        return "Ellipse("+left+C+top+C+right+C+bottom+")";
    }
}
