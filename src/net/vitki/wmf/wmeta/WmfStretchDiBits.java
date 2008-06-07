package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

import net.vitki.wmf.*;

public class WmfStretchDiBits  extends WmfRecord  {

    int  rop;
    int  spare1;
    int  src_x, src_y, src_w, src_h;
    int  dst_x, dst_y, dst_w, dst_h;
    Bitmap bitmap;

    public void read (RecordReader rd)  throws IOException {
        rop = rd.readLong();
        spare1 = rd.readWord();
        src_h = rd.readWord();
        src_w = rd.readWord();
        src_y = rd.readWord();
        src_x = rd.readWord();
        dst_h = rd.readWord();
        dst_w = rd.readWord();
        dst_y = rd.readWord();
        dst_x = rd.readWord();
        bitmap = new Bitmap();
        switch (rop)   {
            case Constants.ROP_WHITENESS:  // dest = white
            case Constants.ROP_BLACKNESS:  // dest = black
                break;
            case Constants.ROP_SRCCOPY:     // dest = source
            case Constants.ROP_SRCINVERT:   // dest = source XOR dest
                int alpha = rd.getDibAlpha();
                bitmap.read(rd, false, -1, alpha);
                break;
            case Constants.ROP_PATCOPY:     // dest = pattern
            case Constants.ROP_PATPAINT:    // dest = DPSnoo
            case Constants.ROP_PATINVERT:   // dest = pattern XOR dest
            case Constants.ROP_DSTINVERT:   // dest = (NOT dest)
            case Constants.ROP_SRCPAINT:    // dest = source OR dest
            case Constants.ROP_SRCAND:      // dest = source AND dest
            case Constants.ROP_SRCERASE:    // dest = source AND (NOT dest )
            case Constants.ROP_NOTSRCCOPY:  // dest = (NOT source)
            case Constants.ROP_NOTSRCERASE: // dest = (NOT src) AND (NOT dest)
            case Constants.ROP_MERGECOPY:   // dest = (source AND pattern)
            case Constants.ROP_MERGEPAINT:  // dest = (NOT source) OR dest
            default:
                throw new IOException ("unsupported rop "+WmfPatBlt.ropName(rop));
        }
    }

    public int render (Renderer rr)  {
        return rr.stretchDiBits (
                                 rop,
                                 src_x, src_y, src_w, src_h,
                                 dst_x, dst_y, dst_w, dst_h,
                                 bitmap.image
                                 );
    }

    public String toString() {
        return ("StretchDiBits("+
                WmfPatBlt.ropName(rop)+C+
                "src=("+src_x+C+src_y+C+src_w+C+src_h+")"+C+
                "dst=("+dst_x+C+dst_y+C+dst_w+C+dst_h+")"+C+
                bitmap+
                ")");
    }

}
