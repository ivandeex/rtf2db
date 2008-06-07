package net.vitki.wmf.render;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Font;

import net.vitki.wmf.Constants;
import net.vitki.wmf.Poly;


public class GraphicsRenderer extends DebugRenderer
{

    private int     win_x, win_y, win_w, win_h;
    private double  vip_x, vip_y, vip_w, vip_h;
    private double  pix_w, pix_h;
    private int     map_mode;
    private boolean placeable;
    private double  dpi;
    private int     dev_w, dev_h;
    private double  ds_w,  ds_h;
    private double  fin_w, fin_h;

    public int getWidth()  {
        return win_w;
    }

    public int getHeight()  {
        return win_h;
    }

    protected double devToLogX (int dx)  {
        double x = dx;
        x += vip_x;
        x /= pix_w;
        x += win_x;
        return x;
    }

    protected double devToLogY (int dy)  {
        double y = dy;
        y += vip_y;
        y /= pix_h;
        y += win_y;
        return y;
    }

    protected int logToDevX (int lx)  {
        double x = lx;
        x -= win_x;
        x *= pix_w;
        x -= vip_x;
        x *= ds_w;
        return (int)x;
    }

    protected int logToDevY (int ly)  {
        double y = ly;
        y -= win_y;
        y *= pix_h;
        y -= vip_y;
        y *= ds_h;
        return (int)y;
    }

    protected int logToDevW (int lw)  {
        double w = lw;
        w *= pix_w;
        w *= ds_w;
        return (int)w;
    }

    protected int logToDevH (int lh)  {
        double h = lh;
        h *= pix_h;
        h *= ds_h;
        return (int)h;
    }

    private void recalcPixel ()   {
        // device coordinates
        ds_w = (win_w > 0 ? (double)dev_w / (double)win_w : 1.0);
        ds_h = (win_h > 0 ? (double)dev_h / (double)win_h : 1.0);
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
                pix_w  = vip_w  / (double)win_w;
                break;
		}
        fin_w = ds_w / pix_w;
        fin_h = ds_h / pix_h;
        log("dev("+dev_w+C+dev_h+"),"+
            "win("+win_x+C+win_y+C+win_w+C+win_h+"),"+
            "vip("+vip_x+C+vip_y+C+vip_w+C+vip_h+"),"+
            "pix("+pix_w+C+pix_h+"),"+
            "ds("+ds_w+C+ds_h+"),"+
            "fin("+fin_w+C+fin_h+")"
            );
    }

    public int setWindowOrg (int x, int y)  {
        win_x = x;
        win_y = y;
        recalcPixel();
        return OK;
    }

    public int setWindowExt (int w, int h)  {
        win_w = w;
        win_h = h;
        recalcPixel();
        return OK;
    }

    public int setViewportOrg (int x, int y)  {
        vip_x = (double)x * pix_w;
        vip_y = (double)y * pix_h;
        recalcPixel();
        return OK;
    }

    public int setViewportExt (int w, int h)  {
        vip_w = (double)w;
        vip_h = (double)h;
        recalcPixel();
        return OK;
    }

    public int ofssetWindowOrg (int xdiff, int ydiff)  {
        win_x += xdiff;
        win_y += ydiff;
        recalcPixel();
        return OK;
    }

    public int scaleWindowExt (int xnum, int xdenom, int ynum, int ydenom)  {
        win_w = (int)((double)win_w * (double)xnum / (double)xdenom);
        win_h = (int)((double)win_h * (double)ynum / (double)ydenom);
        recalcPixel();
        return OK;
    }

    public int ofssetViewportOrg (int xdiff, int ydiff)  {
        vip_x += (double)xdiff * pix_w;
        vip_y += (double)ydiff * pix_h;
        recalcPixel();
        return OK;
    }

    public int scaleViewportExt (int xnum, int xdenom, int ynum, int ydenom)  {
        vip_w = vip_w * (double)xnum / (double)xdenom;
        vip_h = vip_h * (double)ynum / (double)ydenom;
        recalcPixel();
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
            recalcPixel();
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
            } else {
                pix_w = pix_h = 1.0;
                this.map_mode = Constants.MM_TEXT;
            }
            recalcPixel();
            return ERROR;
        }
        this.map_mode = map_mode;
        recalcPixel();
        return OK;
    }

    public int setPlaceable (boolean flag, int inch)  {
        placeable = flag;
        if (placeable)  {
            if (inch <= 0)    inch = 1440;
            dpi = (double)inch;
            return setMapMode (Constants.MM_DPI);
        } else {
            dpi = 1440.0;
            return setMapMode (Constants.MM_TEXT);
        }
    }


    public int createPenIndirect (int style, int width, long color, int handle)  {
        return OK;
    }

    public int createBrushIndirect (int style, long color, int hatch, int handle)  {
        draw_filled = (style <= 0);
        log("filled is "+(draw_filled?"yes":"no"));
        g.setColor(makeColor(color));
        return OK;
    }

    public int selectObject (int obj_no, boolean standard)  {
        return OK;
    }

    public int deleteObject (int obj_no)  {
        return OK;
    }

    public int setBkColor (long color)  {
        g.setColor(makeColor(color));
        return OK;
    }

    public int lineTo (int x, int y)  {
        g.drawLine(old_x, old_y, logToDevX(x), logToDevY(y));
        return OK;
    }

    public int moveTo (int x, int y)  {
        old_x = logToDevX(x);
        old_y = logToDevY(y);
        return OK;
    }

    public int rectangle (int left, int top, int right, int bottom)  {
        int x1 = logToDevX(left);
        int y1 = logToDevY(top);
        int x2 = logToDevX(right);
        int y2 = logToDevY(bottom);
        if (draw_filled)
            g.fillRect (x1, y1, x2-x1, y2-y1);
        else
            g.drawRect (x1, y1, x2-x1, y2-y1);
        return OK;
    }

    public int polygon (int num, int[] x, int[] y)  {
        int[] wx = new int[num];
        int[] wy = new int[num];
        for (int i=0; i<num; i++)  {
            wx[i] = logToDevX(x[i]);
            wy[i] = logToDevY(y[i]);
        }
        if (draw_filled)
            g.fillPolygon(wx, wy, num);
        else
            g.drawPolygon(wx, wy, num);
        return OK;
    }

    public int polyline (int num, int[] x, int[] y)  {
        int[] wx = new int[num];
        int[] wy = new int[num];
        for (int i=0; i<num; i++)  {
            wx[i] = logToDevX(x[i]);
            wy[i] = logToDevY(y[i]);
        }
        g.drawPolyline(wx, wy, num);
        for (int i=0; i<num; i++)
            log("poly point ("+x[i]+C+y[i]+")->("+wx[i]+C+wy[i]+")");
        return OK;
    }

    public int polyPolygon (int count, Poly[] polys)  {
        for (int i=0; i<count; i++)
            polygon (polys[i].n, polys[i].x, polys[i].y);
        return OK;
    }

    public int ellipse (int left, int top, int right, int bottom)  {
        int x1 = logToDevX(left);
        int y1 = logToDevY(top);
        int x2 = logToDevX(right);
        int y2 = logToDevY(bottom);
        if (draw_filled)
            g.fillOval (x1, y1, x2-x1, y2-y1);
        else
            g.drawOval (x1, y1, x2-x1, y2-y1);
        return OK;
    }

    public int createFontIndirect (byte[] faceName, int charSet, int width, int height,
                                   int escapement, int orientation, int weight,
                                   boolean italic, boolean underline, boolean strikeout,
                                   int outPrecision, int clipPresicion,
                                   int quality, int pitchAndFamily,
                                   int handle)  {
        int size = height;
        if (size < 0) {
            size = logToDevH( -height );
            log("recalc "+(-height)+"->"+size);
        } else  {
            size = (int)((double)height * 72.0 / dpi);
            if (size < 5)    size = 9;
        }

        String face = faceName==null ? "Dialog" : new String(faceName);
        /*
        if (face.startsWith("Courier"))   face = "Courier";
        else if (face.startsWith("MS Sans Serif"))   face = "Dialog";
        else if (face.startsWith("Arial"))    face = "Helvetica";
        else if (face.startsWith("Arial Narrow"))    face = "Helvetica";
        else if (face.startsWith("Times New Roman"))   face = "TimesRoman";
        else if (face.startsWith("Wingdings"))   face = "ZapfDingbats";
        else face = "Dialog";
        */

        int  style;
        if (italic) {
            style = Font.ITALIC;
            if (weight >= 700)    style |= Font.BOLD;
        } else if (weight >= 700)
            style = Font.BOLD;
        else
            style = Font.PLAIN;

        log("font face=["+face+"] weight="+weight+" height="+height
             +" style="+style+" size="+size);
        Font font = new Font (face, style, size);
        g.setFont(font);
        return OK;
    }

    public int setTextColor (long color)  {
        text_color = makeColor(color);
        return OK;
    }

    public int textOut (byte[] text, int count, int x, int y)  {
        Color prevColor = g.getColor();
        g.setColor (text_color);
        String str = new String (text, 0, count);
        int wx = logToDevX(x);
        int wy = logToDevY(y);
        g.drawString(str, wx, wy);
        log("font point ("+x+C+y+")->("+wx+C+wy+")");
        g.setColor(prevColor);
        return OK;
    }

    public int extTextOut (int x, int y, byte[] text, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           )  {
        return textOut (text, count, x, y);
    }

    public int extTextOutW (int x, int y, char[] text, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            )  {
        log ("extTextOutW not implemented");
        return ERROR;
    }

    public int setTextAlign (int align)  {
        return OK;
    }

    public int setBkMode (int mode)  {
        return OK;
    }

    public int patBlt (int left, int top, int width, int height, int rop)  {
        if (draw_filled && rop == Constants.ROP_PATCOPY)  {
            g.fillRect (logToDevX(left), logToDevY(top),
                        logToDevW(width), logToDevH(height));
        }
        return OK;
    }

/*
    public int F_StretchDIBits ()  {
        Image image;
        bmpImage bmp = null;
        tempBuffer = new String(mRecord.getParm(),0);
        tempBuffer= tempBuffer.substring(22);
        bmp = new bmpImage(tempBuffer, 1);
        image = bmp.getImage();
        g.drawImage(image, 0, 0, this);
        return OK;
    }
*/
    public int escape (byte[] data)  {
        return OK;
    }

    protected Graphics g;
    protected boolean  draw_filled;
    protected Color    text_color;
    protected int      old_x, old_y;

    public void setup (Graphics graphics, int width, int height)  {
        g = graphics;
        dev_w = width;
        dev_h = height;
        win_x = win_y = 0;
        win_w = dev_w;
        win_h = dev_h;
        vip_x = vip_y = 0.0;
        vip_w = win_w;
        vip_h = win_h;
        draw_filled = false;
        text_color = Color.black;
        setPlaceable(false, 0);
        g.setColor(Color.white);
        g.fillRect(0, 0, dev_w, dev_h);
        g.setColor(Color.black);
    }

    private Color makeColor (long color)  {
        return new Color((int)color);
    }

    public GraphicsRenderer()  {
        super();
        setPrefix ("GraphicsRenderer");
    }

}

