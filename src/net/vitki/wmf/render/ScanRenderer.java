package net.vitki.wmf.render;

import java.awt.Image;
import java.awt.Rectangle;

import net.vitki.wmf.Constants;
import net.vitki.wmf.Poly;
import net.vitki.wmf.Renderer;

public class ScanRenderer extends DebugRenderer
{
    private boolean acct_log = false;
    private int     win_x, win_y, win_w, win_h;
    private double  vip_x, vip_y, vip_w, vip_h;
    private double  pix_w, pix_h;
    private int     map_mode;
    private boolean placeable;
    private double  dpi;
    private boolean vip_org_ok;
    private boolean vip_ext_ok;
    public  double  resolution;
    private double  min_x, min_y, max_x, max_y;
    private boolean have_min_max;

    public int setWindowOrg (int x, int y)  {
        win_x = y;
        win_y = x;
        recalc();
        return OK;
    }

    public int setWindowExt (int w, int h)  {
        win_w = w;
        win_h = h;
        recalc();
        return OK;
    }

    public int setViewportOrg (int x, int y)  {
        vip_x = (double)x * pix_w;
        vip_y = (double)y * pix_h;
        vip_org_ok = true;
        recalc();
        return OK;
    }

    public int setViewportExt (int w, int h)  {
        vip_w = (double)w;
        vip_h = (double)h;
        vip_ext_ok = true;
        recalc();
        return OK;
    }

    public int ofssetWindowOrg (int xdiff, int ydiff)  {
        win_x += xdiff;
        win_y += ydiff;
        recalc();
        return OK;
    }

    public int scaleWindowExt (int xnum, int xdenom, int ynum, int ydenom)  {
        win_w = (int)((double)win_w * (double)xnum / (double)xdenom);
        win_h = (int)((double)win_h * (double)ynum / (double)ydenom);
        recalc();
        return OK;
    }

    public int ofssetViewportOrg (int xdiff, int ydiff)  {
        vip_x += (double)xdiff * pix_w;
        vip_y += (double)ydiff * pix_h;
        recalc();
        return OK;
    }

    public int scaleViewportExt (int xnum, int xdenom, int ynum, int ydenom)  {
        vip_w = vip_w * (double)xnum / (double)xdenom;
        vip_h = vip_h * (double)ynum / (double)ydenom;
        recalc();
        return OK;
    }

    public int setMapMode (int map_mode)  {
        switch (map_mode)   {
        case Constants.MM_TEXT: // each unit is 1pt
            pix_w = 1.0;
            pix_h = 1.0;
            break;
        case Constants.MM_LOMETRIC: // each unit is 0.1mm
            pix_w = inchToPoint (mmToInch (0.1));
            pix_h = inchToPoint (mmToInch (0.1));
            break;
        case Constants.MM_HIMETRIC: // each unit is 0.01mm
            pix_w = inchToPoint (mmToInch (0.01));
            pix_h = inchToPoint (mmToInch (0.01));
            break;
        case Constants.MM_LOENGLISH: // each unit is 0.01 inch
            pix_w = inchToPoint (0.01);
            pix_h = inchToPoint (0.01);
            break;
        case Constants.MM_HIENGLISH: // each unit is 0.001 inch
            pix_w = inchToPoint (0.001);
            pix_h = inchToPoint (0.001);
            break;
        case Constants.MM_TWIPS: // each unit is 1/1440 inch
            pix_w = 0.05;
            pix_h = 0.05;
            break;
        case Constants.MM_ISOTROPIC:
        case Constants.MM_ANISOTROPIC:
            recalc();
            break;
        case Constants.MM_DPI:
            pix_w = inchToPoint (1.0 / dpi);
            pix_h = inchToPoint (1.0 / dpi);
            break;
        default:
            if (placeable)  {
                pix_w = inchToPoint (1.0 / dpi);
                pix_h = inchToPoint (1.0 / dpi);
                this.map_mode = Constants.MM_DPI;
                return ERROR;
            } else {
                pix_w = pix_h = 1.0;
                this.map_mode = Constants.MM_TEXT;
                return ERROR;
            }
        }
        this.map_mode = map_mode;
        return OK;
    }

    public static double DEFAULT_DPI = 1440.0 / 4.0;
    public static double DEFAULT_RESOLUTION = 96.0;

    public int setPlaceable (boolean flag, int inch)  {
        placeable = flag;
        if (placeable)  {
            dpi = inch > 0 ? (double)inch : DEFAULT_DPI;
            return setMapMode (Constants.MM_DPI);
        } else {
            dpi = DEFAULT_DPI;
            return setMapMode (Constants.MM_TEXT);
        }
    }

    public ScanRenderer()  {
        setPrefix ("scan");
        setDebug (false);
        setLogging (false);
        dpi = DEFAULT_DPI;
        resolution = DEFAULT_RESOLUTION;
        win_x = win_y = 0;
        vip_x = vip_y = 0.0;
        win_w = win_h = 1024;
        vip_w = vip_h = 1024.0;
        min_x = min_y = 0.0;
        max_x = max_y = 0.0;
        have_min_max = false;
        vip_org_ok = vip_ext_ok = false;
        setPlaceable (false, 0);
    }

    public Rectangle  getBounds()  {
        Rectangle box;
        int w = win_w;
        int h = win_h;
        if (w <= 0 || h <= 0)  {
            w = (int)(max_x - min_x);
            h = (int)(max_y - min_y);
            if (w < 1)  w = 1;
            if (h < 1)  h = 1;
            box = new Rectangle ((int)min_x, (int)min_y, w, h);
        } else {
            box = new Rectangle (win_x, win_y, w, h);
        }
        return box;
    }

    private void acct (int ux, int uy, String who)  {
        double dx = (((double)ux - win_x) * pix_w - vip_x);
        double dy = (((double)uy - win_y) * pix_h - vip_y);
        if (!have_min_max)  {
            min_x = max_x = dx;
            min_y = max_y = dy;
            have_min_max = true;
        }
        if (dx - pix_w < min_x)  min_x = dx - pix_w;
        if (dx + pix_w > max_x)  max_x = dx + pix_w;
        if (dy - pix_h < min_y)  min_y = dy - pix_h;
        if (dy + pix_h > max_y)  max_y = dy + pix_h;
        if (acct_log)
            log (who+": ("+ux+","+uy+")->("+dx+","+dy+")");
    }

    private void recalc ()   {
        // pixel width
        switch (map_mode)   {
            case Constants.MM_ISOTROPIC:    // scale here depends on window & viewport extents
            case Constants.MM_ANISOTROPIC:
                if (win_w==0)    break;
                pix_w  = vip_w  / (double)win_w;
                break;
		}
        // pixel height
        switch (map_mode)   {
            case Constants.MM_ISOTROPIC:    // scale here depends on window & viewport extents
            case Constants.MM_ANISOTROPIC:
                if (win_h==0)    break;
                pix_h  = vip_h  / (double)win_h;
                break;
		}
        final String C = ",";
        if (acct_log)
            log ("recalc: win=("+win_x+C+win_y+C+win_w+C+win_h+")"+C+
                          "pix=("+pix_w+C+pix_h+")"+C+
                          "vip=("+vip_x+C+vip_y+C+vip_w+C+vip_h+")"
                );
    }

    // scanned operations...

    public int lineTo (int x, int y)   {
        acct (x, y, "lintTo");
        return OK;
    }

    public int moveTo (int x, int y)   {
        acct (x, y, "moveTo");
        return OK;
    }

    public int setPixel (int x, int y, int color)   {
        acct (x, y, "setPixel");
        return OK;
    }

    public int rectangle (int left, int top, int right, int bottom)   {
        acct (left, top, "rectangleTL");
        acct (right, bottom, "rectangleBR");
        return OK;
    }

    public int roundRect (int left, int top, int right, int bottom,
                          int ell_width, int ell_height)   {
        acct (left, top, "roundRectTL");
        acct (right, bottom, "rouhdRectBR");
        return OK;
    }

    public int polygon (int num, int[] x, int[] y)   {
        for (int i=0; i<num; i++)
            acct (x[i], y[i], "polygon"+i);
        return OK;
    }

    public int polyline (int num, int[] x, int[] y)   {
        for (int i=0; i<num; i++)
            acct (x[i], y[i], "polyline"+i);
        return OK;
    }

    public int polyPolygon (int count, Poly[] polys)   {
        for (int i=0; i<count; i++)
            polygon (polys[i].n, polys[i].x, polys[i].y);
        return OK;
    }

    public int ellipse (int left, int top, int right, int bottom)   {
        acct (left, top, "ellipseTL");
        acct (right, bottom, "ellipseBR");
        return OK;
    }

    public int arc (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)   {
        acct (left, top, "arcTL");
        acct (right, bottom, "arcBR");
        return OK;
    }


    public int pie (int left, int top, int right, int bottom,
                    int xstart, int ystart, int xend, int yend)   {
        acct (left, top, "pieTL");
        acct (right, bottom, "pieBR");
        return OK;
    }

    public int chord (int left, int top, int right, int bottom,
                      int xstart, int ystart, int xend, int yend)   {
        acct (left, top, "chordTL");
        acct (right, bottom, "chordBR");
        return OK;
    }

    public int floodFill (int x, int y, int color)   {
        acct (x, y, "floodFill");
        return OK;
    }

    public int extFloodFill (int x, int y, int color, int type)   {
        acct (x, y, "extFloodFill");
        return OK;
    }


    public int textOut (byte[] str, int count, int x, int y)   {
        acct (x, y, "textOut");
        return OK;
    }

    public int extTextOut (int x, int y, byte[] text, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           )   {
        acct (x, y, "extTextOut");
        return OK;
    }

    public int extTextOutW (int x, int y, char[] text, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            )  {
        acct (x, y, "extTextOut");
        return OK;
    }

    public int excludeClipRect (int left, int top, int right, int bottom)   {
        acct (left, top, "excludeClipRectTL");
        acct (right, bottom, "excludeClipRectBR");
        return OK;
    }

    public int intersectClipRect (int left, int top, int right, int bottom)   {
        acct (left, top, "intersectClipRectTL");
        acct (right, bottom, "intersectClipRectBR");
        return OK;
    }

    public int setDiBitsToDevice (int xdest, int ydest, long cx, long cy,
                                  int xsrc, int ysrc,
                                  long startscan, long lines, int bits,
                                  byte[] bitmapInfo,
                                  int coloruse)   {
        acct (xdest, ydest, "setDiBitsToDevice");
        return OK;
    }

    public int patBlt (int left, int top, int width, int height, int rop)   {
        acct (left, top, "patBltTL");
        acct (left+width, top+height, "patBltBR");
        return OK;
    }

    public int bitBlt (int xdst, int ydst, int width, int height,
                       int xsrc, int ysrc, long rop)   {
        acct (xdst, ydst, "bitBltTL");
        acct (xdst+width, ydst+height, "bitBltBR");
        return OK;
    }

    public int stretchBlt (int xdst, int ydst, int wdst, int hdst,
                           int xsrc, int ysrc, int wsrc, int hsrc, int rop)   {
        acct (xdst, ydst, "stretchBltTL");
        acct (xdst+wdst, ydst+hdst, "stretchBltBR");
        return OK;
    }

    public int stretchDiBits (int rop,
                              int src_x, int src_y, int src_w, int src_h,
                              int dst_x, int dst_y, int dst_w, int dst_h,
                              Image image)   {
        acct (dst_x, dst_y, "stretchDiBitsTL");
        acct (dst_x+dst_w, dst_y+dst_h, "stretchDiBitsBR");
        return OK;
    }

}

