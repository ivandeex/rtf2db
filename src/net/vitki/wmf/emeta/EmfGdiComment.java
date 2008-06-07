package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfGdiComment extends EmfRecord
{
    String text = null;
    int    id1  = 0;
    int    id2  = 0;
    byte[] data = null;

    public int render (Renderer rr)   {
        //System.out.println(this);
        return OK;
    }

    public void read(RecordReader rd) throws IOException {
        int num_bytes = rd.readLong();
        if (num_bytes > 8)  {
            id1 = rd.readLong();
            id2 = rd.readLong();
            num_bytes -= 8;
        }
        byte[] data = rd.readBytes(num_bytes, false, false);
        if (id1 == 0x43494447)  {
            switch (id2)  {
                case 0x80000001:
                    text = "metafile";
                    break;
                case 0x00000002:
                    text = "begingroup";
                    break;
                case 0x00000003:
                    text = "endgroup";
                    break;
                case 0x40000004:
                    text = "multiformats";
                    break;
            }
        }
    }

    public String toString() {
        String s = text;
        if (s == null)
            s = "id1="+id1+",id2="+id2;
        return "GdiComment("+s+")";
    }
}
