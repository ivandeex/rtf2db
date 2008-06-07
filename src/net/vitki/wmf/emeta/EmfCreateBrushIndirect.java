package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfCreateBrushIndirect extends EmfRecord
{
    public int    handle;
    public int    style;
    public int    hatch;
    public int    color;
    public int    spare1;

    public int render (Renderer rr) {
        return rr.createBrushIndirect (style, color, hatch, handle);
    }

    public void read (RecordReader rd)  throws IOException {
        handle = rd.readLong();
        style = rd.readLong();
        color = rd.readColor();
        hatch = rd.readLong();
    }

    public String toString() {
        return ("CreateBrushIndirect("+
                styleName(style)+C+
                "#"+Integer.toHexString(color)+C+
                hatchName(hatch)+
                C+"handle="+handle+
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
