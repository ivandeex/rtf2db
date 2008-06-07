package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfEOF extends EmfRecord
{
    int      pal_num;
    int      pal_off;
    int      eof_off;
    short[]  pal_red;
    short[]  pal_green;
    short[]  pal_blue;
    short[]  pal_flags;
    
    public int render (Renderer rr)  {
        return OK;
    }

    public void read (RecordReader rd)  throws IOException {
        pal_num = rd.readLong();
        pal_off = rd.readLong();
        if (pal_num == 0)  {
            pal_red = pal_green = pal_blue = pal_flags = null;
        } else {
            pal_red = new short[pal_num];
            pal_green = new short[pal_num];
            pal_blue = new short[pal_num];
            pal_flags = new short[pal_num];
            for (int i=0; i<pal_num; i++)  {
                pal_red[i] = rd.readByte();
                pal_green[i] = rd.readByte();
                pal_blue[i] = rd.readByte();
                pal_flags[i] = rd.readByte();
            }
        }
        eof_off = rd.readLong();
    }

    public String toString() {
        return "EOF(eof_off="+eof_off+",pal_num="+pal_num+")";
    }
}
