package net.vitki.wmf;

import java.awt.image.ImageProducer;
import java.awt.Image;

public interface Renderer
{
    public int setWindowOrg (int x, int y);
    public int setWindowExt (int w, int h);
    public int setViewportOrg (int x, int y);
    public int setViewportExt (int w, int h);
    public int ofssetWindowOrg (int xdiff, int ydiff);
    public int scaleWindowExt (int xnum, int xdenom, int ynum, int ydenom);
    public int ofssetViewportOrg (int xdiff, int ydiff);
    public int scaleViewportExt (int xnum, int xdenom, int ynum, int ydenom);

    public int createPenIndirect (int style, int width, int color, int handle);
    public int createBrushIndirect (int style, int color, int hatch, int handle);
    public int createRectRgn (int left, int top, int right, int bottom, int handle);
    public int selectObject (int obj_no, boolean standard);
    public int deleteObject (int obj_no);

    public int setBkColor (int color);
    public int setBkMode (int mode);
    public int setMapMode (int mode);
    public int setRop2 (int rop2);
    public int setPolyFillMode (int mode);
    public int setStretchBltMode (int mode);

    public int lineTo (int x, int y);
    public int moveTo (int x, int y);
    public int setPixel (int x, int y, int color);
    public int rectangle (int left, int top, int right, int bottom);
    public int roundRect (int left, int top, int right, int bottom,
                          int ell_width, int ell_height);
    public int polygon (int num, int[] x, int[] y);
    public int polyline (int num, int[] x, int[] y);
    public int polyPolygon (int count, Poly[] polys);
    public int ellipse (int left, int top, int right, int bottom);
    public int arc (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend);
    public int pie (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend);
    public int chord (int left, int top, int right, int bottom,
                      int xstart, int ystart, int xend, int yend);
    public int floodFill (int x, int y, int color);
    public int extFloodFill (int x, int y, int color, int type);

    public int setMapperFlags (int flags);
    public int createFontIndirect (byte[] faceName, int charSet, int width, int height,
                                   int escapement, int orientation, int weight,
                                   boolean italic, boolean underline, boolean strikeOut,
                                   int outPrecision, int clipPresicion,
                                   int quality, int pitchAndFamily,
                                   int handle);
    public int setTextColor (int color);
    public int setTextCharacterExtra (int extra);
    public int setTextAlign (int align);
    public int textOut (byte[] str, int count, int x, int y);
    public int extTextOut (int x, int y, byte[] text, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           );
    public int extTextOutW (int x, int y, char[] text, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            );

    public int excludeClipRect (int left, int top, int right, int bottom);
    public int intersectClipRect (int left, int top, int right, int bottom);
    public int offsetClipRgn (int x, int y);
    public int frameRegion (int region_no, int brush_no, int width, int height);
    public int invertRegion (int region_no);
    public int paintRegion (int region_no);
    public int selectClipRegion (int region_no);
    public int fillRgn (int region_no, int brush_no);

    public int setDiBitsToDevice (int xdest, int ydest, long cx, long cy,
                                  int xsrc, int ysrc,
                                  long startscan, long lines, int bits,
                                  byte[] bitmapInfo,
                                  int coloruse);
    public int patBlt (int left, int top, int width, int height, int rop);
    public int bitBlt (int xdst, int ydst, int width, int height,
                       int xsrc, int ysrc, long rop);
    public int stretchBlt (int xdst, int ydst, int wdst, int hdst,
                           int xsrc, int ysrc, int wsrc, int hsrc, int rop);
    public int dibCreatePatternBrush (Image image, int handle);
    public int stretchDiBits (int rop,
                              int src_x, int src_y, int src_w, int src_h,
                              int dst_x, int dst_y, int dst_w, int dst_h,
                              Image image);

    public int saveDC ();
    public int restoreDC (int level);
    public int escape (byte[] data);

    public int setPlaceable (boolean flag, int inch);
    public boolean isLockPlaceable ();
    public void setLockPlaceable (boolean lock);

    public int render (Record rec);
    public int render (RecordTable table);

    public void log (String msg);
    public void setDebug (boolean flag);
    public void setTiming (boolean flag);
    public void setLogging (boolean flag);

    public boolean isProhibited (int func);
    public void setProhibited (String list);
    public boolean getIgnoreQuirks ();

    public static final int OK    = 0;
    public static final int ERROR = -1;

}

