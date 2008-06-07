package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfExtTextOutW extends EmfRecord
{
    int    box_left, box_top, box_right, box_bottom;
    int    gr_mode;
    float  x_scale;
    float  y_scale;
    int    x_ref;
    int    y_ref;
    int    n_chars;
    int    off_str;
    int    flags;
    int    clip_left, clip_top, clip_right, clip_bottom;
    int    dx_off;
    char[] text;
    int[]  dx;

    public int render (Renderer rr)  {
        return rr.extTextOutW (x_ref, y_ref, text, n_chars, flags, dx,
                               box_left, box_top, box_right, box_bottom);
    }

    public void read (RecordReader rd)  throws IOException
    {
        box_left = rd.readLong();
        box_top = rd.readLong();
        box_right = rd.readLong();
        box_bottom = rd.readLong();
        gr_mode = rd.readLong();
        x_scale = rd.readFloat();
        y_scale = rd.readFloat();
        x_ref = rd.readLong();
        y_ref = rd.readLong();
        n_chars = rd.readLong();
        off_str = rd.readLong();
        flags = rd.readLong();
        boolean read_clip = (flags & Constants.ETO_CLIPPED) != 0;
        read_clip = true;
        if (read_clip)  {
            clip_left = rd.readLong();
            clip_top = rd.readLong();
            clip_right = rd.readLong();
            clip_bottom = rd.readLong();
        }
        dx_off = rd.readLong();
        text = new char[n_chars];
        for (int i=0; i<n_chars; i++)
            text[i] = (char)rd.readWord();
        if (n_chars % 2 == 1)
            rd.readWord();
        dx = new int[n_chars];
        for (int i=0; i<n_chars; i++)
            dx[i] = rd.readLong();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("ExtTextOutW("+x_ref+C+y_ref+C);
        buf.append("\""+(new String(text))+"\",");
        buf.append("box{"+box_left+C+box_top+C+box_right+C+box_bottom+"},");
        buf.append("clip{"+clip_left+C+clip_top+C+clip_right+C+clip_bottom+"},");
        buf.append("scale{"+x_scale+C+y_scale+"},");
        buf.append("flags="+Integer.toHexString(flags)+"h,");
        buf.append("gr_mode="+gr_mode+C);
        buf.append("dx{");
        for (int i=0; i<dx.length; i++)
            buf.append(""+dx[i]+(i<dx.length-1?C:""));
        buf.append("})");
        return buf.toString();
    }
}
