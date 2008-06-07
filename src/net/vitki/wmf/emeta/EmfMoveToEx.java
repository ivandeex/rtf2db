package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfMoveToEx extends EmfRecord
{
    int  x, y;

    public int render (Renderer rr) {
        return rr.moveTo (x, y);
    }

    public void read (RecordReader rd)  throws IOException {
        y = rd.readLong();
        x = rd.readLong();
    }

    public String toString() {
        return "MoveToEx("+x+C+y+")";
    }
}
