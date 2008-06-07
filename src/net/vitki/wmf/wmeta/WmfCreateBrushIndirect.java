package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfCreateBrushIndirect  extends WmfRecord  {

    public int    style;
    public int    color;
    public int    hatch;

    public int render (Renderer rr) {
        return rr.createBrushIndirect (style, color, hatch, -1);
    }

    public void read (RecordReader rd)  throws IOException {
        style = rd.readWord();
        color = rd.readColor();
        hatch = rd.readWord();
    }

    public String toString() {
        return ("CreateBrushIndirect("+
                styleName(style)+C+
                "#"+Integer.toHexString(color)+C+
                hatchName(hatch)+
                ")");
    }

    private static Constants styles = null;

    public static String styleName (int no)  {
        if (styles == null)   styles = new Constants("BS_");
        return styles.get(no);
    }

    private static Constants hatches = null;

    public static String hatchName (int no)  {
        if (hatches == null)   hatches = new Constants("HS_");
        return hatches.get(no);
    }

}
