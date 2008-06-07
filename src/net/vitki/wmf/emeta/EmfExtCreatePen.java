package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfExtCreatePen extends EmfRecord
{
    public int    handle;
    public int    bitmap_off;
    public int    bitmap_size;
    public int    bits_off;
    public int    bits_size;
    public int    elp_pen_style;
    public int    elp_pen_width;
    public int    elp_brush_style;
    public int    elp_color;
    public int    elp_hatch; // big?
    public int    elp_num_entries;
    public int[]  elp_style_entry;

    public int render (Renderer rr)  {
        return rr.createPenIndirect (elp_pen_style, elp_pen_width, elp_color, handle);
    }

    public void read (RecordReader rd)  throws IOException {
        handle = rd.readLong();
        bitmap_off = rd.readLong();
        bitmap_size = rd.readLong();
        bits_off = rd.readLong();
        bits_size = rd.readLong();
        elp_pen_style = rd.readLong();
        elp_pen_width = rd.readLong();
        elp_brush_style = rd.readLong();
        elp_color = rd.readColor();
        elp_hatch = rd.readLong();
        elp_num_entries = rd.readLong();
        if (elp_num_entries == 0)
            elp_style_entry = null;
        else {
            elp_style_entry = new int[elp_num_entries];
            for (int i=0; i<elp_num_entries; i++)
                elp_style_entry[i] = rd.readLong();
        }
        if (bitmap_size != 0)
            throw new IOException("EMR_ExtCreatePen cannot accept bitmaps");
        if (bits_size != 0)
            throw new IOException("EMR_ExtCreatePen cannot accept brush bits");
    }

    public String toString() {
        return ("ExtCreatePen("+
                styleName(getStyle())+
                (getCap()==0?"":"+"+styleName(getCap()))+
                (getJoin()==0?"":"+"+styleName(getJoin()))+
                C+"width="+elp_pen_width+C+
                "#"+Integer.toHexString(elp_color)+
                C+"handle="+handle+
                C+"brush_style="+elp_brush_style+
                C+"hatch="+elp_hatch+
                C+"num_entries="+elp_num_entries+
                ")");
    }

    public int getStyle()  {
        return (elp_pen_style & Constants.PS_STYLE_MASK);
    }

    public int getCap() {
        return (elp_pen_style & Constants.PS_ENDCAP_MASK);
    }

    public int getJoin() {
        return (elp_pen_style & Constants.PS_JOIN_MASK);
    }

    private static Constants styles = null;

    public static String styleName (int no)  {
        if (styles == null)   styles = new Constants("PS_");
        return styles.get(no);
    }
}
