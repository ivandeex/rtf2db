package net.vitki.wmf.render;


import java.awt.image.ImageProducer;
import java.awt.Image;

import net.vitki.wmf.Poly;
import net.vitki.wmf.Record;
import net.vitki.wmf.RecordTable;
import net.vitki.wmf.Renderer;

public class NullRenderer implements Renderer
{
    public int setWindowOrg (int x, int y) { return OK; }
    public int setWindowExt (int w, int h)   { return OK; }
    public int setViewportOrg (int x, int y)   { return OK; }
    public int setViewportExt (int w, int h)   { return OK; }
    public int ofssetWindowOrg (int xdiff, int ydiff)   { return OK; }
    public int scaleWindowExt (int xnum, int xdenom, int ynum, int ydenom)   { return OK; }
    public int ofssetViewportOrg (int xdiff, int ydiff)   { return OK; }
    public int scaleViewportExt (int xnum, int xdenom, int ynum, int ydenom)   { return OK; }

    public int createPenIndirect (int style, int width, int color, int handle)   { return OK; }
    public int createBrushIndirect (int style, int color, int hatch, int handle)   { return OK; }
    public int createRectRgn (int left, int top, int right, int bottom, int handle)   { return OK; }
    public int selectObject (int obj_no, boolean standard)   { return OK; }
    public int deleteObject (int obj_no)   { return OK; }

    public int setBkColor (int color)   { return OK; }
    public int setBkMode (int mode)   { return OK; }
    public int setMapMode (int mode)   { return OK; }
    public int setRop2 (int rop2)   { return OK; }
    public int setPolyFillMode (int mode)   { return OK; }
    public int setStretchBltMode (int mode)   { return OK; }

    public int lineTo (int x, int y)   { return OK; }
    public int moveTo (int x, int y)   { return OK; }
    public int setPixel (int x, int y, int color)   { return OK; }
    public int rectangle (int left, int top, int right, int bottom)   { return OK; }
    public int roundRect (int left, int top, int right, int bottom,
                          int ell_width, int ell_height)   { return OK; }
    public int polygon (int num, int[] x, int[] y)   { return OK; }
    public int polyline (int num, int[] x, int[] y)   { return OK; }
    public int polyPolygon (int count, Poly[] polys)   { return OK; }
    public int ellipse (int left, int top, int right, int bottom)   { return OK; }
    public int arc (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)   { return OK; }
    public int pie (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)   { return OK; }
    public int chord (int left, int top, int right, int bottom,
                      int xstart, int ystart, int xend, int yend)   { return OK; }
    public int floodFill (int x, int y, int color)   { return OK; }
    public int extFloodFill (int x, int y, int color, int type)   { return OK; }

    public int setMapperFlags (int flags)   { return OK; }
    public int createFontIndirect (byte[] faceName, int charSet, int width, int height,
                                   int escapement, int orientation, int weight,
                                   boolean italic, boolean underline, boolean strikeOut,
                                   int outPrecision, int clipPresicion,
                                   int quality, int pitchAndFamily,
                                   int handle)   { return OK; }
    public int setTextColor (int color)   { return OK; }
    public int setTextCharacterExtra (int extra)   { return OK; }
    public int setTextAlign (int align)   { return OK; }
    public int textOut (byte[] str, int count, int x, int y)   { return OK; }
    public int extTextOut (int x, int y, byte[] text, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           )   { return OK; }
    public int extTextOutW (int x, int y, char[] text, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            )  { return OK; }

    public int excludeClipRect (int left, int top, int right, int bottom)   { return OK; }
    public int intersectClipRect (int left, int top, int right, int bottom)   { return OK; }
    public int offsetClipRgn (int x, int y)   { return OK; }
    public int frameRegion (int region_no, int brush_no, int width, int height)   { return OK; }
    public int invertRegion (int region_no)   { return OK; }
    public int paintRegion (int region_no)   { return OK; }
    public int selectClipRegion (int region_no)   { return OK; }
    public int fillRgn (int region_no, int brush_no)   { return OK; }

    public int setDiBitsToDevice (int xdest, int ydest, long cx, long cy,
                                  int xsrc, int ysrc,
                                  long startscan, long lines, int bits,
                                  byte[] bitmapInfo,
                                  int coloruse)   { return OK; }
    public int patBlt (int left, int top, int width, int height, int rop)   { return OK; }
    public int bitBlt (int xdst, int ydst, int width, int height,
                       int xsrc, int ysrc, long rop)   { return OK; }
    public int stretchBlt (int xdst, int ydst, int wdst, int hdst,
                           int xsrc, int ysrc, int wsrc, int hsrc, int rop)   { return OK; }
    public int dibCreatePatternBrush (Image image, int handle)   { return OK; }
    public int stretchDiBits (int rop,
                              int src_x, int src_y, int src_w, int src_h,
                              int dst_x, int dst_y, int dst_w, int dst_h,
                              Image image)   { return OK; }

    public int saveDC ()   { return OK; }
    public int restoreDC (int level)   { return OK; }
    public int escape (byte[] data)   { return OK; }
    public int setPlaceable (boolean flag, int inch)   { return OK; }

    public boolean isLockPlaceable ()  {
        return placeableLocked;
    }

    public void setLockPlaceable (boolean lock)  {
        placeableLocked = lock;
    }

    public int render (Record rec)  {
        return rec.render(this);
    }

    public int render (RecordTable table)  {
        int n = table.getSize();
        int err_qty = 0;
        for (int i=0; i<n; i++)  {
            int rc = table.get(i).render(this);
            if (rc != OK)    err_qty++;
        }
        return err_qty;
    }

    public void log (String msg)    {}
    public void setDebug (boolean flag)    {}
    public void setTiming (boolean flag)    {}
    public void setLogging (boolean flag)    {}

    public boolean isProhibited (int func)    { return false; }
    public void setProhibited (String list)    {}
    
    public boolean getIgnoreQuirks ()  { return true; }

    private boolean placeableLocked = false;
}

