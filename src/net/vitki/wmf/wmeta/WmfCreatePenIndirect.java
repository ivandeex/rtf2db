package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfCreatePenIndirect  extends WmfRecord {

    public int    style;
    public int    width;
    public int    color;

    public int render (Renderer rr)  {
        return rr.createPenIndirect (style, width, color, -1);
    }

    public void read (RecordReader rd)  throws IOException {
        style = rd.readWord();
        width = (int)rd.readDWord();
        color = rd.readColor();
        if (size == 9)
            rd.readWord();
    }

    public String toString() {
        return ("CreatePenIndirect("+
                styleName(getStyle())+
                (getCap()==0?"":"+"+styleName(getCap()))+
                (getJoin()==0?"":"+"+styleName(getJoin()))+
                C+width+C+
                "#"+Integer.toHexString(color)+
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
