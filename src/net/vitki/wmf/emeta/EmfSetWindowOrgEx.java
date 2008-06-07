package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetWindowOrgEx extends EmfRecord
{
    public int  x, y;

    public int render (Renderer rr) {
        int rc = OK;
        rc = rr.setWindowOrg (x, y);
        return rc;
    }

    public void read (RecordReader rd)  throws IOException {
        y = rd.readLong();
        x = rd.readLong();
    }

    public String toString() {
        return "SetWindowOrgEx("+x+C+y+")";
    }

}
