package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfDeleteObject  extends WmfRecord  {

    public int  obj_no;

    public int render (Renderer rr)  {
        return rr.deleteObject (obj_no);
    }

    public void read (RecordReader rd)  throws IOException {
        obj_no = rd.readWord();
    }

    public String toString() {
        return "DeleteObject("+obj_no+")";
    }

}
