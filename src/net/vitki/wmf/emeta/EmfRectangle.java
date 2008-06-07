package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfRectangle extends EmfRecord
{
    int  left, top, right, bottom;

    public int render (Renderer rr) {
        return rr.rectangle (left, top, right, bottom);
    }

    public void read (RecordReader rd)  throws IOException {
        left = rd.readLong();
        top = rd.readLong();
        right = rd.readLong();
        bottom = rd.readLong();
    }

    public String toString() {
        return "Rectangle("+left+C+top+C+right+C+bottom+")";
    }
}
