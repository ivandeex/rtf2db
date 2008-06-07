package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfCreatePen extends EmfRecord
{
    public int    handle;
    public int    style;
    public int    width;
    public int    width_y_unused;
    public int    color;

    public int render (Renderer rr)  {
        return rr.createPenIndirect (style, width, color, handle);
    }

    public void read (RecordReader rd)  throws IOException {
        handle = rd.readLong();
        style = rd.readLong();
        width = rd.readLong();
        width_y_unused = rd.readLong();
        color = rd.readColor();
    }

    public String toString() {
        return ("CreatePen("+
                styleName(getStyle())+
                (getCap()==0?"":"+"+styleName(getCap()))+
                (getJoin()==0?"":"+"+styleName(getJoin()))+
                C+width+C+
                "#"+Integer.toHexString(color)+
                C+"handle="+handle+
                C+"unused="+width_y_unused+
                ")");
    }

    public int getStyle()  {
        return (style & Constants.PS_STYLE_MASK);
    }

    public int getCap() {
        return (style & Constants.PS_ENDCAP_MASK);
    }

    public int getJoin() {
        return (style & Constants.PS_JOIN_MASK);
    }

    private static Constants styles = null;

    public static String styleName (int no)  {
        if (styles == null)   styles = new Constants("PS_");
        return styles.get(no);
    }
}
