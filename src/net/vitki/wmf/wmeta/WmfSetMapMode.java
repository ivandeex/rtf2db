package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSetMapMode  extends WmfRecord  {

    int  map_mode;

    public int render (Renderer rr) {
        return rr.setMapMode (map_mode);
    }

    public void read (RecordReader rd)  throws IOException {
        map_mode = rd.readWord();
    }

    public String toString() {
        return "MapMode("+modeName(map_mode)+")";
    }

    private static Constants modes = null;

    public static String modeName (int no)  {
        if (modes == null)    modes = new Constants("MM_");
        return modes.get(no);
    }

}
