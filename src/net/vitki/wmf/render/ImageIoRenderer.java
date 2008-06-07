package net.vitki.wmf.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Properties;

import net.vitki.charset.FontManager;
import net.vitki.charset.Encoding;
import net.vitki.wmf.RecordTable;
import net.vitki.wmf.emeta.EmfHeader;


public class ImageIoRenderer extends Graphics2dRenderer
{
    private BufferedImage im;
    private String  default_format = "png";

    public ImageIoRenderer (Properties props, RecordTable table)
    {
        super();
        setPrefix ("ImageRenderer");
        FontManager.setup(this);
        String val;
        if (props == null)    props = new Properties();
        val = props.getProperty("text-antialias");
        boolean text_antialias = booleanOf(val);
        val = props.getProperty("debug");
        if (val != null)    setDebug( booleanOf(val) );
        val = props.getProperty("logging");
        if (val != null)    setLogging( booleanOf(val) );
        val = props.getProperty("timing");
        if (val != null)    setTiming( booleanOf(val) );
        val = props.getProperty("ignore-quirks");
        if (val != null)    setIgnoreQuirks( booleanOf(val) );
        val = props.getProperty("prefix");
        if (val != null)    setPrefix( val );
        val = props.getProperty("format");
        if (val != null)    default_format = val;
        val = props.getProperty("prohibit");
        if (val != null)    setProhibited( val );
        val = props.getProperty("mat");
        if (val != null)    mat_color = Integer.decode(val).intValue();
        val = props.getProperty("white");
        if (val != null)    white_color = Integer.decode(val).intValue();
        val = props.getProperty("default-charset");
        if (val != null)    def_charset = Encoding.getCharsetCode(val);
        double  scale    = 1.0;
        double  wscale   = scale;
        double  hscale   = scale;
        boolean auto_w   = false;
        boolean auto_h   = false;
        boolean best_fit = false;
        int     width    = -1024;
        int     height   = -768;
        int     org_x    = -1;
        int     org_y    = -1;
        int     max_size = -1;
        val = props.getProperty("orgx");
        if (val != null)
            org_x = Integer.decode(val.trim()).intValue();
        val = props.getProperty("orgy");
        log ("initially org_x="+org_x+",org_y="+org_y);
        if (val != null)
            org_y = Integer.decode(val.trim()).intValue();
        val = props.getProperty("width");
        if (val != null)
            width = Integer.decode(val.trim()).intValue();
        val = props.getProperty("height");
        if (val != null)
            height = Integer.decode(val.trim()).intValue();
        val = props.getProperty("scale");
        if (val != null)  {
            val = val.trim();
            if (val.equalsIgnoreCase("best"))
                best_fit = true;
            else if (val.endsWith("%"))
                wscale = hscale = scale = Double.parseDouble(val.substring(0,val.length()-1)) / 100.0;
            else
                wscale = hscale = scale = Double.parseDouble(val);
        }
        val = props.getProperty("wscale");
        if (val != null)    {
            val = val.trim();
            if (val.equalsIgnoreCase("auto"))
                auto_w = true;
            else if (val.endsWith("%"))
                wscale = Double.parseDouble(val.substring(0,val.length()-1)) / 100.0;
            else
                wscale = Double.parseDouble(val);
        }
        val = props.getProperty("hscale");
        if (val != null)    {
            val = val.trim();
            if (val.equalsIgnoreCase("auto"))
                auto_h = true;
            else if (val.endsWith("%"))
                hscale = Double.parseDouble(val.substring(0,val.length()-1)) / 100.0;
            else
                hscale = Double.parseDouble(val);
        }
        val = props.getProperty("maxsize");
        if (val != null)
        	max_size = Integer.decode(val.trim()).intValue();
        if (table != null)
        {
            int srx = 0;
            int sry = 0;
            int srw = 0;
            int srh = 0;
            boolean scan_needed = true;
            if (table.isEnhanced())  {
                EmfHeader emf = table.getEmfHeader();
                srx = emf.boundsLeft;
                sry = emf.boundsTop;
                srw = emf.boundsRight - emf.boundsLeft;
                srh = emf.boundsBottom - emf.boundsTop;
                log ("EMF showed: ("+srx+","+sry+","+srw+","+srh+")");
                if (srw > 0 && srh > 0)
                    scan_needed = false;
            }
            if (scan_needed)  {
                ScanRenderer sr = new ScanRenderer();
                sr.render(table);
                Rectangle srb = sr.getBounds();
                srx = srb.x;
                sry = srb.y;
                srw = srb.width;
                srh = srb.height;
                log ("scan showed: ("+srx+","+sry+","+srw+","+srh+")");
            }
            if (org_x < 0)    org_x = srx;
            if (org_y < 0)    org_y = sry;
            log ("after scan org_x="+org_x+",org_y="+org_y);
            if (best_fit)  {
                if (width < 0)    width = srw;
                if (height < 0)    height = srh;
                if (max_size > 0) {
                	if (width > max_size) width = max_size;
                	if (height > max_size) height = max_size;
                }
                double sc_x = (double)width / (double)srw;
                double sc_y = (double)height / (double)srh;
                double sc = Math.min(sc_x, sc_y);
                width = (int)(srw * sc);
                height = (int)(srh * sc);
                /*
                 *
                boolean calc_it = srw < width && srh < height;
                calc_it = true;
                if (calc_it)  {
                    double sx = (double)width / (double)srw;
                    double sy = (double)height / (double)srh;
                    if (sx < sy)  {
                        srh = srh * width / srw;
                        srw = width;
                    } else {
                        srw = srw * height / srh;
                        srh = height;
                    }
                }
                if (srw > width)  {
                    srh = srh * width / srw;
                    srw = width;
                }
                if (srh > height)  {
                    srw = srw * height / srh;
                    srh = height;
                }
                width = srw;
                height = srh;
                *
                */
            } else if (auto_h)  {
                if (width <= 0)
                    width = (int)(srw * wscale);
                if (height <= 0)
                    height = width * srh / srw;
            } else if (auto_w) {
                if (height <= 0)
                    height = (int)(srh * hscale);
                if (width <= 0)
                    width = height * srw / srh;
            } else {
                if (width <= 0)
                    width = (int)(srw * wscale);
                else
                    width = (int)(width * wscale);
                if (height <= 0)
                    height = (int)(srh * hscale);
                else
                    height = (int)(height * hscale);
            }
            wscale = (double)width / (double)srw;
            hscale = (double)height / (double)srh;
        }
        if (table == null)  {
            if (org_x < 0)    org_x = 0;
            if (org_y < 0)    org_y = 0;
        }
        if (width < 0)    width = -width;
        if (height < 0)    height = -height;
        Rectangle box = new Rectangle (org_x, org_y, width, height);
        im = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        log ("image width="+width+",height="+height);
        log ("finally org_x="+org_x+",org_y="+org_y);
        log ("size used: ("+org_x+","+org_y+","+width+","+height+")");
        log ("coef: ("+wscale+","+hscale+")");
        Graphics2D gr = im.createGraphics();
        if (text_antialias)
            gr.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, 
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        setup (gr, box, wscale, hscale);
    }

    public void write (String file, String format)  throws IOException {
        OutputStream ostream = new FileOutputStream(file);
        write (ostream, format);
        try { ostream.close(); } catch (IOException e) {}
    }

    public void write (String file)  throws IOException {
        write (file, null);
    }

    public void write (OutputStream outs, String format)  throws IOException {
        if (format == null)   format = default_format;
        javax.imageio.ImageIO.write (im, format, outs);
    }

    public void write (OutputStream outs)  throws IOException {
        write (outs, null);
    }
    
    public static boolean booleanOf (String s)  {
        if (s == null)    return false;
        s = s.trim().toLowerCase();
        return "1".equals(s) || "y".equals(s) || "yes".equals(s) || "t".equals(s) || "true".equals(s) || "on".equals(s);
    }

}

