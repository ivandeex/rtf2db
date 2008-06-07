package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfDeleteObject extends EmfRecord
{
    public int  obj_no;

    public int render (Renderer rr)  {
        return rr.deleteObject (obj_no);
    }

    public void read (RecordReader rd)  throws IOException {
        obj_no = rd.readLong();
    }

    public String toString() {
        return "DeleteObject("+obj_no+")";
    }
}
