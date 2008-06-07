package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfEscape  extends WmfRecord  {

    byte[] data;

    public int render (Renderer rr)   {
        return rr.escape(data);
    }

    public void read(RecordReader rd) throws IOException {
        int bytes = (size - 3) * 2;
        data = rd.readBytes(bytes, false);
    }

    public String toString() {
        return "Escape(...)";
    }

}
