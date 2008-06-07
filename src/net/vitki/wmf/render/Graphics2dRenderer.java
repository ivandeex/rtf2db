package net.vitki.wmf.render;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.font.GlyphVector;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;

import net.vitki.charset.*;
import net.vitki.wmf.*;
import net.vitki.wmf.handle.*;


public class Graphics2dRenderer extends DebugRenderer
{
    /** parameters **/
    protected int     mat_color   = 0;
    protected int     white_color = 0xffffff;
    protected boolean use_placeable = false;
    protected boolean debug_shapes  = false;
    protected boolean acct_log      = false;
    protected int     def_charset   = 0;

    /** coordinates **/
    private int     dev_x, dev_y, dev_w, dev_h;
    private int     win_x, win_y, win_w, win_h;
    private double  vip_x, vip_y, vip_w, vip_h;
    private double  min_x, min_y, max_x, max_y;
    private double  pix_w, pix_h;
    private int     map_mode;
    private boolean placeable;
    private double  dpi;
    private double  dws_w,  dws_h;
    private double  fin_w, fin_h;
    private boolean vip_org_ok;
    private boolean vip_ext_ok;
    private boolean have_min_max;

    /** context **/
    protected Graphics2D canvas;
    protected Handle[]   handles;
    protected Context[]  stack;
    protected int        depth;
    protected Context    context;

    /** transformations **/

    public int getWidth()   { return win_w; }
    public int getHeight()   { return win_h; }

    private void acct (int ux, int uy, String who)  {
        if (!acct_log)    return;
        double dx = (((double)ux - win_x) * pix_w - vip_x - dev_x) * dws_w;
        double dy = (((double)uy - win_y) * pix_h - vip_y - dev_y) * dws_h;
        if (!have_min_max)  {
            min_x = max_x = dx;
            min_y = max_y = dy;
            have_min_max = true;
        }
        if (dx - pix_w < min_x)  min_x = dx - pix_w;
        if (dx + pix_w > max_x)  max_x = dx + pix_w;
        if (dy - pix_h < min_y)  min_y = dy - pix_h;
        if (dy + pix_h > max_y)  max_y = dy + pix_h;
        log (who+": ("+ux+","+uy+")->("+(int)dx+","+(int)dy+")");
    }

    private void recalc ()   {
        switch (map_mode)   {
            case Constants.MM_ISOTROPIC:
            case Constants.MM_ANISOTROPIC:
                if (win_w==0)    break;
                pix_w  = vip_w  / (double)win_w;
                break;
		}
        switch (map_mode)   {
            case Constants.MM_ISOTROPIC:
            case Constants.MM_ANISOTROPIC:
                if (win_h==0)    break;
                pix_h  = vip_h  / (double)win_h;
                break;
		}
        fin_w = dws_w * pix_w;
        fin_h = dws_h * pix_h;
        if (acct_log)   {
            log ("recalc: win=("+win_x+C+win_y+C+win_w+C+win_h+")"+C+
                          "fin=("+fin_w+C+fin_h+")"+C+
                          "pix=("+pix_w+C+pix_h+")"+C+
                          "dws=("+dws_w+C+dws_h+")"+C+
                          "vip=("+vip_x+C+vip_y+C+vip_w+C+vip_h+")"+C+
                          "dev=("+dev_x+C+dev_y+C+dev_w+C+dev_h+")"
                );
        }
        // set transformation
        AffineTransform log2dev = new AffineTransform();
        log2dev.scale (dws_w, dws_h);
        log2dev.translate (-dev_x-vip_x, -dev_y-vip_y);
        log2dev.scale (pix_w, pix_h);
        log2dev.translate (-win_x, -win_y);
        canvas.setTransform (log2dev);
    }

    public int setWindowOrg (int x, int y)  {
        win_x = y;
        win_y = x;
        recalc();
        return OK;
    }

    public int setWindowExt (int w, int h)  {
        win_w = w;
        win_h = h;
        if (!vip_ext_ok)  {
            vip_w = win_w;
            vip_h = win_h;
        }
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
            break;
        case Constants.MM_DPI:
            pix_w = inchToPoint (1.0 / dpi);
            pix_h = inchToPoint (1.0 / dpi);
            break;
        default:
            setMapMode (placeable ? Constants.MM_DPI : Constants.MM_TEXT);
            recalc();
            return ERROR;
        }
        this.map_mode = map_mode;
        recalc();
        return OK;
    }

    public int setPlaceable (boolean flag, int inch)  {
        placeable = flag;
        int  map_mode = Constants.MM_TEXT;
        if (placeable)  {
            if (inch <= 0)    inch = 1440;
            dpi = (double)inch;
            if (use_placeable)
                map_mode = Constants.MM_DPI;
        } else {
            dpi = 1440.0;
        }
        log ("setPlaceable: set mode...");
        return setMapMode (map_mode);
    }

    /** pens and brushes **/

    public int createPenIndirect (int style, int width, int color, int nhandle)  {
        Handle handle;
        switch (style & Constants.PS_STYLE_MASK)  {
            case Constants.PS_SOLID:
                handle = new PenHandle(new BasicStroke(width),
                                       makeColor(color));
                break;
            case Constants.PS_NULL:
                handle = new PenHandle(null, null);
                break;
            default:
                handle = new PenHandle(new BasicStroke(width),
                                       makeColor(color));
                break;
        }
        return (addHandle(handle, nhandle) < 0 ? ERROR : OK);
    }

    public int createBrushIndirect (int style, int color, int hatch, int nhandle)  {
        Handle handle;
        switch (style)  {
            case Constants.BS_SOLID:
                handle = new BrushHandle(makeColor(color));
                break;
            default:
                handle = new BrushHandle();
                break;
        }
        return (addHandle(handle, nhandle) < 0 ? ERROR : OK);
    }

    /** drawing **/

    public int setBkColor (int color)  {
        context.bg_color = makeColor(color);
        return OK;
    }

    public int lineTo (int x, int y)  {
        Shape shape = new Line2D.Double(context.cur_x, context.cur_y, x, y);
        place (shape, false);
        acct (x, y, "lineTo");
        context.cur_x = x;
        context.cur_y = y;
        return OK;
    }

    public int moveTo (int x, int y)  {
        acct (x, y, "moveTo");
        context.cur_x = x;
        context.cur_y = y;
        return OK;
    }

    public int rectangle (int left, int top, int right, int bottom)  {
        acct (left, top, "rectangleTL");
        acct (right, bottom, "rectangleBR");
        Shape shape = new Rectangle2D.Double(left,top,right-left,bottom-top);
        place (shape, true);
        return OK;
    }

    public int polygon (int num, int[] x, int[] y)  {
        GeneralPath path = new GeneralPath();
        path.moveTo (x[0], y[0]);
        acct (x[0], y[0], "polygon0");
        for (int i=1; i<num; i++)  {
            path.lineTo (x[i], y[i]);
            acct (x[i], y[i], "polygon"+i);
        }
        path.lineTo (x[0], y[0]);
        acct (x[0], y[0], "polygonE");
        place (path, true);
        return OK;
    }

    public int polyline (int num, int[] x, int[] y)  {
        GeneralPath path = new GeneralPath();
        path.moveTo (x[0], y[0]);
        acct (x[0], y[0], "polyline0");
        for (int i=1; i<num; i++)    {
            path.lineTo (x[i], y[i]);
            acct (x[i], y[i], "polyline"+i);
        }
        place (path, false);
        return OK;
    }

    public int polyPolygon (int count, Poly[] polys)  {
        GeneralPath path = new GeneralPath();
        for (int k = 0; k < count; k++) {
        	int num = polys[k].n;
        	int x[] = polys[k].x;
        	int y[] = polys[k].y;
        	String prefix = "polygon-"+k+".";
        	path.moveTo (x[0], y[0]);
        	acct (x[0], y[0], prefix+"0");
        	for (int i=1; i<num; i++)  {
        		path.lineTo (x[i], y[i]);
        		acct (x[i], y[i], prefix+i);
        	}
        	path.lineTo (x[0], y[0]);
        	acct (x[0], y[0], prefix+"E");
        }
        place (path, true);
        return OK;
    }

    public int ellipse (int left, int top, int right, int bottom)  {
        Shape shape = new Ellipse2D.Double(left, top, right-left, bottom-top);
        acct (left, top, "ellipseTL");
        acct (right, bottom, "ellipseBR");
        place (shape, true);
        return OK;
    }

    /** text output **/

    public int createFontIndirect (byte[] faceName, int charset, int width, int height,
                                   int escapement, int orientation, int weight,
                                   boolean italic, boolean underline, boolean strikeout,
                                   int outPrecision, int clipPresicion,
                                   int quality, int pitchAndFamily,
                                   int nhandle)
    {
        String face = faceName==null ? "Dialog" : new String(faceName);
        int size = -height;
        if (size < 0)   {
            double dsize = (double)height * 72.0 / dpi;
            if (dsize < 5)    dsize = 9;
            size = (int)(dsize / fin_h);
        }
        boolean bold = weight >= 700;
        Handle handle;
        handle = new FontHandle(face, size,
                                bold, italic, underline, strikeout,
                                charset,
                                weight, escapement, orientation);
        return (addHandle(handle, nhandle) < 0 ? ERROR : OK);
    }

    public int setTextColor (int color)  {
        context.txt_color = makeColor(color);
        return OK;
    }

    public int textOut (byte[] texta, int count, int x, int y)  {
        return extTextOut (x, y, texta, count, 0, null, 0,0,0,0);
    }

    public int extTextOut (int x, int y, byte[] texta, int count,
                           int flags, int[] dx,
                           int left, int top, int right, int bottom
                           )
    {
        String face = context.font.face;
        int cs = context.font.charset;
        int style   = context.font.getStyle();
        int fsize   = context.font.size;
        ManagedFont mf = FontManager.getFont (face, style, fsize, fsize);
        if (mf == null)  {
        	mf = FontManager.getFont (face, style, fsize, fsize, true);
        	if (mf == null) {
        		log ("Font ["+face+"] not found");
        		return ERROR;
        	}
        	log ("Font ["+face+"] substituted by "+mf.getName());
        }
        if (mf.getCharset() != 0)
            cs = mf.getCharset();
        if (cs == 0)
        	cs = def_charset;
        String str = Encoding.decode (cs, texta, 0, count);
        if (logFlag) {
        	String log_str = "";
        	for (int j = 0; j < count; j++) {
        		int ch = texta[j] < 0 ? (int)texta[j] + 256 : (int)texta[j];
       			log_str += "0x"+Integer.toHexString(ch)+",";
        	}
        	log_str += " = ";
        	for (int j = 0; j < count; j++) {
        		int ch = str.charAt(j);
       			log_str += "0x"+Integer.toHexString(ch)+",";
        	}
        	log ("text_out: "+log_str);
        }
        char[] textw = str.toCharArray();
        return extTextOutW (x, y, textw, count, flags, dx, left, top, right, bottom);
    }

    public int extTextOutW (int x, int y, char[] textw, int count,
                            int flags, int[] dx,
                            int left, int top, int right, int bottom
                            )
    {
        acct (x, y, "extTextOutW");

        int align = context.text_align;
        boolean update_cp = (align & Constants.TA_UPDATECP) == Constants.TA_UPDATECP;
        double tx = x;
        double ty = y;
        if (update_cp)   {
            tx = context.cur_x;
            ty = context.cur_y;
        }

        String face = context.font.face;
        int cs = context.font.charset;
        int style   = context.font.getStyle();
        int fsize   = context.font.size;
        int orient  = context.font.orientation;
        log ("face="+face+" size="+fsize+" text=["+(new String(textw))+"]");
        

        ManagedFont mf = FontManager.getFont (face, style, fsize, fsize);
        FontRenderContext frc = canvas.getFontRenderContext();

        if (mf == null)  {
            log ("Font ["+face+"] not found");
            return ERROR;
        }
        
        String str = new String(textw, 0, count);
        
        GlyphVector gv = mf.getFont().createGlyphVector (frc, str);
        Rectangle2D rect = gv.getVisualBounds();
        double width  = rect.getWidth();
        double height = rect.getHeight();

        if ("Dialog".equals(face) && fsize == 12 && bottom > 0)  {
            // we need to recalculate font size using outline box
            int given_width = right - left;
            int given_height = bottom - top;
            int fsize_x = (int)(fsize * given_width / width);
            int fsize_y = (int)(fsize * given_height / height);
            fsize = Math.min(fsize_x, fsize_y);
            if (fsize == 0 && orient == 0)  {
                // we will try to rotate the font
                int rotated_width = bottom -top;
                int rotated_height = right - left;
                int fsize_orig = 12;
                int fsize_x_rot = (int)(fsize_orig * rotated_width / width);
                int fsize_y_rot = (int)(fsize_orig * rotated_height / height);
                int fsize_rot = Math.min(fsize_x_rot, fsize_y_rot);
                log ("try rotation: given_box={"+rotated_width+","+rotated_height+"} "+
                     "our_box={"+width+","+height+"} fsizes={"+fsize_x_rot+","+fsize_y_rot+"}"+
                     " fsize_rot="+fsize_rot);
                if (fsize_rot > 0)  {
                    orient = 900;
                    fsize_x = fsize_x_rot;
                    fsize_y = fsize_y_rot;
                    fsize = fsize_rot;
                }
            }
            log ("box={"+width+","+height+"} given={"+given_width+","+given_height+"} "+
                 "fsizes={"+fsize_x+","+fsize_y+"} fsize="+fsize+" orient="+orient);
            mf = FontManager.getFont (face, style, fsize, fsize);
            gv = mf.getFont().createGlyphVector (frc, str);
            rect = gv.getVisualBounds();
            width = rect.getWidth();
            height = rect.getHeight();
            log ("now fsize="+fsize+" box={"+width+","+height+"}");
            dx = null; // now need in spans when we recalculated size
        }

        double theta;
        double th_cos, th_sin;
        double zz;

        if (context.font.escapement == 0)  {
            theta  = 0.0;
            th_sin = 0.0;
            th_cos = 1.0;
        } else {
            theta = context.font.escapement * (-1.0 * Math.PI / 180.0 / 10.0);
            th_cos = Math.cos(theta);
            th_sin = Math.sin(theta);
        }

        if ((align & Constants.TA_BASELINE) == 0)  {
            if ((align & Constants.TA_BOTTOM) != 0)  {
                zz = height / 3;
                tx += -zz * th_sin;
                ty +=  zz * th_cos;
            } else {
                // vertical alignment: top
                zz = height;
                tx += -zz * th_sin;
                ty +=  zz * th_cos;
            }
        }

        if ((align & Constants.TA_CENTER) != 0)  {
            zz = -width / 2.0;
            tx += zz * th_cos;
            ty += zz * th_sin;
        } else if ((align & Constants.TA_RIGHT) != 0)  {
            zz = -width;
            tx += zz * th_cos;
            ty += zz * th_sin;
        }

        int  cno;
        int  dxlen = (dx==null ? 0 : dx.length);

        canvas.setColor(context.txt_color);
        canvas.setFont(mf.getFont());
        
        AffineTransform old_at = null;
        if (orient != 0)   {
            theta = orient * (-Math.PI / 1800.0);
            old_at = canvas.getTransform();
            AffineTransform new_at = new AffineTransform();
            new_at.setToRotation(theta, tx, ty);
            //new_at.preConcatenate(old_at);
            canvas.transform(new_at);
        }
        
        for (cno=0; cno<count && cno<dxlen; cno++)  {
            canvas.drawString( str.substring(cno, cno+1), (float)tx, (float)ty );
            tx += dx[cno];
        }
        if (cno < count)
            canvas.drawString( str.substring(cno), (float)tx, (float)ty );
        
        if (old_at != null)
            canvas.setTransform (old_at);

        if (update_cp)   {
            context.cur_x = (int)tx;
            context.cur_y = (int)ty;
        }
        return OK;
    }

    public int setTextAlign (int align)  {
        context.text_align = align;
        return OK;
    }

    /** settings **/

    public int setBkMode (int mode)  {
        return OK;
    }

    public int setPolyFillMode (int mode)  {
        return OK;
    }

    /** bitmap operations **/

    public int patBlt (int left, int top, int width, int height, int rop)  {
        acct (left, top, "patBltTL");
        acct (left+width, top+height, "patBltBR");
        if (context.brush.paint != null && rop == Constants.ROP_PATCOPY)  {
            canvas.setPaint (context.brush.paint);
            canvas.fill (new Rectangle2D.Double(left, top, width, height));
        }
        return OK;
    }

    private Image scaleImage (Image image, double w, double h)  {
        int flags = Image.SCALE_SMOOTH;
        Image scaled = image.getScaledInstance ((int)w, (int)h, flags);
        return scaled;
    }

    public int dibCreatePatternBrush (Image image, int nhandle)   {
        double w = image.getWidth(null) / fin_w;
        double h = image.getHeight(null) / fin_h;
        Image si = scaleImage (image, w, h);
        BufferedImage bi = new BufferedImage ((int)w, (int)h, BufferedImage.TYPE_4BYTE_ABGR);
        bi.getGraphics().drawImage (si, 0, 0, null);
        Rectangle2D anchor = new Rectangle2D.Double (0, 0, w, h);
        Paint paint = new TexturePaint (bi, anchor);
        Handle handle = new BrushHandle (paint);
        return (addHandle(handle, nhandle) < 0 ? ERROR : OK);
    }

    public int stretchDiBits (int rop,
                              int src_x, int src_y, int src_w, int src_h,
                              int dst_x, int dst_y, int dst_w, int dst_h,
                              Image image)
    {
        acct (dst_x, dst_y, "stretchDiBitsTL");
        acct (dst_x+dst_w, dst_y+dst_h, "stretchDiBitsBR");
        Composite save = canvas.getComposite();
        Composite comp = null;
        switch (rop)  {
            case Constants.ROP_SRCCOPY:     // dest = source
                comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
                break;
            case Constants.ROP_SRCINVERT:   // dest = source XOR dest
                comp = AlphaComposite.getInstance(AlphaComposite.XOR);
                break;
            case Constants.ROP_SRCPAINT:    // dest = source OR dest
            case Constants.ROP_SRCAND:      // dest = source AND dest
            case Constants.ROP_SRCERASE:    // dest = source AND (NOT dest )
            case Constants.ROP_NOTSRCCOPY:  // dest = (NOT source)
            case Constants.ROP_NOTSRCERASE: // dest = (NOT src) AND (NOT dest)
            case Constants.ROP_MERGECOPY:   // dest = (source AND pattern)
            case Constants.ROP_MERGEPAINT:  // dest = (NOT source) OR dest
                break;
        }
        switch (rop)   {
            case Constants.ROP_WHITENESS:
                canvas.setPaint (Color.white);
                canvas.fill (new Rectangle2D.Double(dst_x,dst_y,dst_w,dst_h));
                break;
            case Constants.ROP_BLACKNESS:
                canvas.setPaint (Color.black);
                canvas.fill (new Rectangle2D.Double(dst_x,dst_y,dst_w,dst_h));
                break;
            case Constants.ROP_SRCCOPY:     // dest = source
            case Constants.ROP_SRCPAINT:    // dest = source OR dest
            case Constants.ROP_SRCAND:      // dest = source AND dest
            case Constants.ROP_SRCINVERT:   // dest = source XOR dest
            case Constants.ROP_SRCERASE:    // dest = source AND (NOT dest )
            case Constants.ROP_NOTSRCCOPY:  // dest = (NOT source)
            case Constants.ROP_NOTSRCERASE: // dest = (NOT src) AND (NOT dest)
            case Constants.ROP_MERGECOPY:   // dest = (source AND pattern)
            case Constants.ROP_MERGEPAINT:  // dest = (NOT source) OR dest
                AffineTransform trans = new AffineTransform();
                double sx = (double)dst_w / (double)src_w;
                double sy = (double)dst_h / (double)src_h;
                trans.scale (sx, sy);
                double tx = dst_x - src_x;
                double ty = dst_y - src_y;
                trans.translate (tx, ty);
                canvas.drawImage (image, trans, null);
                break;
            default:
                return ERROR;
        }
        if (comp != null)
            canvas.setComposite (save);
        return OK;
    }

    /** other operations **/

    public int escape (byte[] data)  {
        return OK;
    }

    /** object & context handling **/

    public int selectObject (int obj_no, boolean standard)  {
        if (standard)  {
            Handle handle = null;
            switch (obj_no)  {
                case Constants.STOCK_WHITE_BRUSH:
                    log ("select stock white brush");
                    handle = new BrushHandle(Color.white);
                    break;
                case Constants.STOCK_LTGRAY_BRUSH:
                    log ("select stock light gray brush");
                    handle = new BrushHandle(Color.lightGray);
                    break;
                case Constants.STOCK_GRAY_BRUSH:
                    log ("select stock gray brush");
                    handle = new BrushHandle(Color.gray);
                    break;
                case Constants.STOCK_DKGRAY_BRUSH:
                    log ("select stock dark gray brush");
                    handle = new BrushHandle(Color.darkGray);
                    break;
                case Constants.STOCK_BLACK_BRUSH:
                    log ("select stock black brush");
                    handle = new BrushHandle(Color.black);
                    break;
                case Constants.STOCK_NULL_BRUSH:
                    log ("select stock null brush");
                    handle = new BrushHandle();
                    break;
                case Constants.STOCK_WHITE_PEN:
                    log ("select stock white pen");
                    handle = new PenHandle(new BasicStroke(0), Color.white);
                    break;
                case Constants.STOCK_BLACK_PEN:
                    log ("select stock black pen");
                    handle = new PenHandle(new BasicStroke(0), Color.black);
                    break;
                case Constants.STOCK_NULL_PEN:
                    log ("select stock null pen");
                    handle = new PenHandle();
                    break;
                case Constants.STOCK_OEM_FIXED_FONT:
                    log ("select stock oem fixed font");
                    handle = new FontHandle("Courier");
                    break;
                case Constants.STOCK_ANSI_FIXED_FONT:
                    log ("select stock ansi fixed font");
                    handle = new FontHandle("Courier");
                    break;
                case Constants.STOCK_ANSI_VAR_FONT:
                    log ("select stock ansi var font");
                    handle = new FontHandle("Times");
                    break;
                case Constants.STOCK_SYSTEM_FONT:
                    log ("select stock system font");
                    handle = new FontHandle("Courier");
                    break;
                case Constants.STOCK_DEVICE_DEFAULT_FONT:
                    log ("select stock device default font");
                    handle = new FontHandle("Times");
                    break;
                case Constants.STOCK_DEFAULT_PALETTE:
                    log ("select stock default palette");
                    break;
                case Constants.STOCK_SYSTEM_FIXED_FONT:
                    log ("select stock system fixed font");
                    handle = new FontHandle("Courier");
                    break;
                default:
                    log ("bad stock object "+obj_no+" for select");
                    return ERROR;
            }
            if (handle != null)
                context.select(handle);
            return OK;
        }
        if (obj_no < 0 || obj_no >= handles.length)   {
            log ("bad number for select: "+obj_no);
            return ERROR;
        }
        if (handles[obj_no] == null)   {
            log ("object "+obj_no+" for select is null");
            return ERROR;
        }
        context.select (handles[obj_no]);
        log ("select("+obj_no+")=>"+handles[obj_no]);
        return OK;
    }

    public int deleteObject (int obj_no)  {
        if (obj_no < 0 || obj_no >= handles.length)   {
            log ("bad number for delete: "+obj_no);
            return ERROR;
        }
        if (handles[obj_no] == null)   {
            log ("object "+obj_no+" for delete is null");
            return ERROR;
        }
        log ("delete("+obj_no+")=>"+handles[obj_no]);
        handles[obj_no] = null;
        return OK;
    }

    protected int addHandle (Handle handle, int nhandle)  {
        if (nhandle == -1)  {
            for (int i=0; i<handles.length; i++)  {
                if (handles[i] == null)  {
                    nhandle = i;
                    log("handle("+i+"):="+handle);
                    break;
                }
            }
        }
        if (nhandle == -1)  {
            log ("no place for handle "+handle);
            return -1;
        }
        if (handles[nhandle] != null)
            log ("[warning] handle is busy"+handle);
        handles[nhandle] = handle;
        return nhandle;
    }

    public int saveDC ()  {
        stack[depth++] = context;
        return OK;
    }

    public int restoreDC (int level)  {
        context = stack[depth - level];
        depth -= level;
        return OK;
    }

    /** low-level primitives **/

    protected void place (Shape shape, boolean fill_allowed)  {
        //fill_allowed = false; // @@@@@@@@@@@@@@
        if (context.brush.paint != null && fill_allowed)  {
            canvas.setPaint(context.brush.paint);
            canvas.fill (shape);
            log ("++fill-shape("+context.brush+")");
        }
        if (context.pen.stroke != null)  {
            canvas.setStroke(context.pen.stroke);
            canvas.setColor(context.pen.color);
            canvas.draw (shape);
            log ("++draw-shape("+context.pen+")");
        }
        if (debug_shapes)   {
            canvas.setStroke(new BasicStroke(0));
            canvas.setColor(makeColor(0x000099));
            canvas.draw (shape);
            log ("++debug-shape");
        }
    }

    protected Color makeColor (int color)  {
        int index = (color >>> 24) & 0xff;
        color &= 0xffffff;
        if (color == 0xffffff)
            return new Color(white_color);
        return new Color(color);
    }

    /** setup **/

    public void setup (Graphics2D graphics, Rectangle bounds,
                       double wscale, double hscale)  {
        canvas = graphics;
        dev_x = bounds.x;
        dev_y = bounds.y;
        dev_w = bounds.width;
        dev_h = bounds.height;
        dws_w = wscale;
        dws_h = hscale;
        win_x = win_y = 0;
        win_w = dev_w;
        win_h = dev_h;
        vip_x = vip_y = 0.0;
        vip_w = win_w;
        vip_h = win_h;
        min_x = min_y = max_x = max_y;
        vip_org_ok = false;
        vip_ext_ok = false;
        have_min_max = false;
        setPlaceable(false, 0);
        handles = new Handle[100];
        for (int i=0; i<handles.length; i++)
            handles[i] = null;
        stack = new Context[64];
        for (int i=0; i<stack.length; i++)
            stack[i] = null;
        depth = 0;
        context = new Context();
        context.bg_color = makeColor(white_color);
        if (mat_color != 0)  {
            canvas.setPaint (makeColor(mat_color));
            acct(0, 0, "matTL");
            acct(dev_w, dev_h, "matBR");
            double big = 999999.0;
            canvas.fill (new Rectangle2D.Double(-big/2, -big/2, big, big));
        }
    }

    public Graphics2dRenderer()  {
        super();
        setPrefix ("Graphics2dRenderer");
    }

}

