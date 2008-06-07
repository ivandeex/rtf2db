package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;

import net.vitki.wmf.*;

public class WmfDibCreatePatternBrush  extends WmfRecord  {

    int  style;
    Bitmap bitmap;

    public void read (RecordReader rd)  throws IOException {
        style = rd.readLong();
        bitmap = new Bitmap();
        bitmap.read(rd, false, -1, 255);
    }

    public int render (Renderer rr)  {
        if (style != Constants.BS_DIBPATTERN)  {
            //System.err.println ("WmfDibCreatePatternBrush: unsupported style "+style);
            return OK;
        }
        return rr.dibCreatePatternBrush (bitmap.image, -1);
    }

    public String toString() {
        return ("DibCreatePatternBrush("+
                WmfCreateBrushIndirect.styleName(style)+
                ")");
    }


}
