package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfEOF  extends WmfRecord  {

    public int render (Renderer rr)  {
        return OK;
    }

    public void read (RecordReader rd)  throws IOException {
    }

    public String toString() {
        return "EOF()";
    }

}
