package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetViewportExtEx extends EmfRecord
{
    public int  x, y;

    public int render (Renderer rr) {
        return rr.setViewportExt (x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        x = rd.readLong();
        y = rd.readLong();
    }

    public String toString() {
        return "SetViewportExtEx("+x+C+y+")";
    }

}
