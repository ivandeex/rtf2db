package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfBitBlt extends EmfRecord
{
    int     box_left, box_top, box_right, box_bottom;
    int     dst_x;
    int     dst_y;
    int     dst_w;
    int     dst_h;
    int     rop;
    int     src_x;
    int     src_y;
    float   xfs_m11;
    float   xfs_m12;
    float   xfs_m21;
    float   xfs_m22;
    float   xfs_dx;
    float   xfs_dy;
    int     src_bg_color;
    int     bmi_colors;
    int     off_bmi;
    int     size_bmi;
    int     off_bits;
    int     size_bits;
    byte[]  bmi;
    byte[]  bits;

    public void read (RecordReader rd)  throws IOException
    {
        box_left = rd.readLong();
        box_top = rd.readLong();
        box_right = rd.readLong();
        box_bottom = rd.readLong();
        dst_x = rd.readLong();
        dst_y = rd.readLong();
        dst_w = rd.readLong();
        dst_h = rd.readLong();
        rop = rd.readLong();
        src_x = rd.readLong();
        src_y = rd.readLong();
        xfs_m11 = rd.readFloat();
        xfs_m12 = rd.readFloat();
        xfs_m21 = rd.readFloat();
        xfs_m22 = rd.readFloat();
        xfs_dx = rd.readFloat();
        xfs_dy = rd.readFloat();
        src_bg_color = rd.readColor();
        bmi_colors = rd.readLong();
        off_bmi = rd.readLong();
        size_bmi = rd.readLong();
        off_bits = rd.readLong();
        size_bits = rd.readLong();
        bmi = null;
        bits = null;
        if (size_bmi > 0)
            bmi = rd.readBytes(size_bmi);
        if (size_bits > 0)
            bits = rd.readBytes(size_bits);
    }

    public int render (Renderer rr)  {
        rr.patBlt (dst_x, dst_y, dst_w, dst_h, rop);
        return ERROR;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("BitBlt(");
        buf.append("box{"+box_left+C+box_top+C+box_right+C+box_bottom+"},");
        buf.append("dst{"+dst_x+C+dst_y+C+dst_w+C+dst_h+"},");
        buf.append("rop="+ropName(rop)+C);
        buf.append("src{"+src_x+C+src_y+"},");
        buf.append("xfs{{"+xfs_m11+C+xfs_m12+C+xfs_m21+C+xfs_m22+"},{"+xfs_dx+C+xfs_dy+"}},");
        buf.append("bgclr="+Integer.toHexString(src_bg_color)+C);
        buf.append("bmi_colors="+bmi_colors+C);
        buf.append("size_bmi="+size_bmi+",size_bits="+size_bits);
        buf.append(")");
        return buf.toString();
    }

    private static Constants rops = null;

    public static String ropName (int no)  {
        if (rops == null)   rops = new Constants("ROP_");
        return rops.get(no);
    }
}
