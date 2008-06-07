package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetWindowExtEx extends EmfRecord
{
    public int  x, y;

    public int render (Renderer rr) {
        int rc = OK;
        rc = rr.setWindowExt (x, y);
        return rc;
    }

    public void read (RecordReader rd)  throws IOException {
        x = rd.readLong();
        y = rd.readLong();
    }

    public String toString() {
        return "SetWindowExtEx("+x+C+y+")";
    }

}
