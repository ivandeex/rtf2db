package net.vitki.wmf.render;


import java.awt.image.ImageProducer;
import java.awt.Image;
import java.util.StringTokenizer;

import net.vitki.wmf.*;

public class DebugRenderer implements Renderer
{

    public int setWindowOrg (int x, int y)  {
        return log ("setWindowOrg","");
    }

    public int setWindowExt (int w, int h)  {
        return log ("setWindowExt","");
    }

    public int setViewportOrg (int x, int y)  {
        return log ("setViewportOrg","");
    }

    public int setViewportExt (int w, int h)  {
        return log ("setViewportExt","");
    }

    public int ofssetWindowOrg (int xdiff, int ydiff)  {
        return log ("OffsetWindowOrg","");
    }

    public int scaleWindowExt (int xnum, int xdenom, int ynum, int ydenom)  {
        return log ("scaleWindowExt","");
    }

    public int ofssetViewportOrg (int xdiff, int ydiff)  {
        return log ("offsetViewportOrg","");
    }

    public int scaleViewportExt (int xnum, int xdenom, int ynum, int ydenom)  {
        return log ("scaleViewportExt","");
    }


    public int createPenIndirect (int style, int width, int color, int handle)  {
        return log ("createPenIndirect","");
    }

    public int createBrushIndirect (int style, int color, int hatch, int handle)  {
        return log ("createBrushIndirect","");
    }

    public int createRectRgn (int left, int top, int right, int bottom, int handle)  {
        return log ("createRectRgn","");
    }

    public int selectObject (int obj_no, boolean standard)  {
        return log ("selectObject","");
    }

    public int deleteObject (int obj_no)  {
        return log ("deleteObject","");
    }

    public int setBkColor (int color)  {
        return log ("setBkColor","");
    }

    public int setBkMode (int mode)  {
        return log ("setBkMode","");
    }

    public int setMapMode (int mode)  {
        return log ("setMapMode","");
    }

    public int setRop2 (int rop2)  {
        return log ("setRop2","");
    }

    public int setPolyFillMode (int mode)  {
        return log ("setPolyFillMode","");
    }

    public int setStretchBltMode (int mode)  {
        return log ("setStretchBltMode","");
    }

    public int lineTo (int x, int y)  {
        return log ("lineTo","");
    }

    public int moveTo (int x, int y)  {
        return log ("moveTo","");
    }

    public int setPixel (int x, int y, int color)  {
        return log ("setPixel","");
    }

    public int rectangle (int left, int top, int right, int bottom)  {
        return log ("rectange;","");
    }

    public int roundRect (int left, int top, int right, int bottom,
                          int ell_width, int ell_height)  {
        return log ("roundRect","");
    }

    public int polygon (int num, int[] x, int[] y)  {
        return log ("polygon","");
    }

    public int polyline (int num, int[] x, int[] y)  {
        return log ("polyline","");
    }

    public int polyPolygon (int count, Poly[] polys)  {
        return log ("polyPolygon","");
    }

    public int ellipse (int left, int top, int right, int bottom)  {
        return log ("ellipse","");
    }

    public int arc (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)  {
        return log ("arc","");
    }

    public int pie (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)  {
        return log ("pie","");
    }

    public int chord (int left, int top, int right, int bottom,
                      int xstart, int ystart, int xend, int yend)  {
        return log ("chord","");
    }

    public int floodFill (int x, int y, int color)  {
        return log ("floodFill","");
    }

    public int extFloodFill (int x, int y, int color, int type)  {
        return log ("extFloodFill","");
    }


    public int setMapperFlags (int flags)  {
        return log ("setMapperFlags","");
    }

    public int createFontIndirect (byte[] faceName, int charSet, int width, int height,
                                   int escapement, int orientation, int weight,
                                   boolean italic, boolean underline, boolean strikeOut,
                                   int outPrecision, int clipPresicion,
                                   int quality, int pitchAndFamily,
                                   int handle)  {
        return log ("createFontIndirect","");
    }

    public int setTextColor (int color)  {
        return log ("setTextColor","");
    }

    public int setTextCharacterExtra (int extra)  {
        return log ("setTextCharacterExtra","");
    }

    public int setTextAlign (int align)  {
        return log ("setTextAlign","");
    }

    public int textOut (byte[] str, int count, int x, int y)  {
        return log ("textOut","");
    }

    public int extTextOut (int x, int y, byte[] text, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           )  {
        return log ("extTextOut","");
    }

    public int extTextOutW (int x, int y, char[] text, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            )  {
        return log ("extTextOutW","");
    }

    public int excludeClipRect (int left, int top, int right, int bottom)  {
        return log ("excludeClipRect","");
    }

    public int intersectClipRect (int left, int top, int right, int bottom)  {
        return log ("intersectClipRect","");
    }

    public int offsetClipRgn (int x, int y)  {
        return log ("offsetClipRect","");
    }

    public int frameRegion (int region_no, int brush_no, int width, int height)  {
        return log ("frameRegion","");
    }

    public int invertRegion (int region_no)  {
        return log ("invertRegion","");
    }

    public int paintRegion (int region_no)  {
        return log ("paintRegion","");
    }

    public int selectClipRegion (int region_no)  {
        return log ("selectClipRegion","");
    }

    public int fillRgn (int region_no, int brush_no)  {
        return log ("fillRgn","");
    }


    public int setDiBitsToDevice (int xdest, int ydest, long cx, long cy,
                                  int xsrc, int ysrc,
                                  long startscan, long lines, int bits,
                                  byte[] bitmapInfo,
                                  int coloruse)  {
        return log ("setDiBitsToDevice","");
    }

    public int patBlt (int left, int top, int width, int height, int rop)  {
        return log ("patBlt","");
    }

    public int bitBlt (int xdst, int ydst, int width, int height,
                       int xsrc, int ysrc, long rop)  {
        return log ("bitBlt","");
    }

    public int stretchBlt (int xdst, int ydst, int wdst, int hdst,
                           int xsrc, int ysrc, int wsrc, int hsrc, int rop)  {
        return log ("stretchBlt","");
    }

    public int dibCreatePatternBrush (Image image, int handle)   {
        return log ("dibCreatePatternBrush","");
    }

    public int stretchDiBits (int rop,
                              int src_x, int src_y, int src_w, int src_h,
                              int dst_x, int dst_y, int dst_w, int dst_h,
                              Image image)  {
        return log ("stretchDiBits","");
    }

    public int saveDC ()  {
        return log ("saveDC","");
    }

    public int restoreDC (int level)  {
        return log ("restoreDC","");
    }

    public int escape (byte[] data)  {
        return log ("escape","");
    }

    public int setPlaceable (boolean flag, int inch)  {
        return log ("setPlaceable",(flag?"placeable":"nominal")+C+inch);
    }

    public boolean isLockPlaceable ()  {
        return placeableLocked;
    }

    public void setLockPlaceable (boolean lock)  {
        placeableLocked = lock;
    }

    public int render (Record rec)  {
        if (isProhibited(rec.getFunc()))
            return OK;
        long start = System.currentTimeMillis();
        if (debugFlag && !timingFlag)  {
            log (rec.toString());
        }
        int rc = rec.render(this);
        if (debugFlag && timingFlag)  {
            long ms = System.currentTimeMillis() - start;
            log ("ms="+ms+C+"rc="+rc+" op: "+rec.toString());
        }
        return rc;
    }

    public int renderOnly (RecordTable table, int n)  {
        long start = System.currentTimeMillis();
        //int n = table.getSize();
        int err_qty = 0;
        for (int i=0; i<n; i++)  {
        	log_step = i;
            int rc = render(table.get(i));
            if (rc != OK)    err_qty++;
            log_step = -1;
        }
        if (debugFlag)  {
            long ms = System.currentTimeMillis() - start;
            log ("total time: "+ms+" ms");
            log ("total errors: "+err_qty);
        }
        return err_qty;
    }

    public int render (RecordTable table)  {
    	return renderOnly(table, table.getSize());
    }
    private int[] debug_prohibited = null;
    private Constants rev_funcs = null;

    public boolean isProhibited (int func)  {
        if (debug_prohibited == null)
            return false;
        for (int i=0; i<debug_prohibited.length; i++)
            if (debug_prohibited[i]==func)
                return true;
        return false;
    }

    public void setProhibited (String list)  {
        if (rev_funcs == null)
            rev_funcs = new Constants (null, null, null, true);
        StringTokenizer st = new StringTokenizer (list.trim(), ";, \t\n\r\f", false);
        int count = st.countTokens();
        if (count < 1)  {
            debug_prohibited = null;
            return;
        }
        debug_prohibited = new int[count];
        for (int i=0; i<count; i++)
            debug_prohibited[i] = rev_funcs.get(st.nextToken());
    }

    public void setDebug (boolean flag)  {
        debugFlag = flag;
    }

    public void setTiming (boolean flag)    {
        timingFlag = flag;
    }

    public void setLogging (boolean flag)  {
        logFlag = flag;
    }

    public void setPrefix (String prefix)  {
        logPrefix = prefix;
    }

    public void log (String msg)  {
        if (logFlag) {
        	String step_str = "";
        	if (log_step >= 0)
        		step_str = "(" + log_step + ")";
        	System.out.println(logPrefix+step_str+": "+msg);
        }
    }

    private final int log(String proc, String params)  {
        log(proc+"("+(params==null?"":params)+")");
        return OK;
    }

    public static double pointToInch (double x)   { return (x / 72.0);  }
    public static double inchToPoint (double x)   { return (x * 72.0);  }
    public static double mmToInch (double x)      { return (x / 25.4);  }

    public boolean getIgnoreQuirks()  { return ignoreQuirksFlag; }
    public void setIgnoreQuirks(boolean flag)  { ignoreQuirksFlag = flag; }

    protected String  logPrefix = "DebugRenderer";
    protected boolean debugFlag = false;
    protected boolean timingFlag = false;
    protected boolean logFlag   = true;
    protected boolean placeableLocked = false;
    protected boolean ignoreQuirksFlag = false;
    protected int log_step = -1;

    protected final static String C = ",";

}

