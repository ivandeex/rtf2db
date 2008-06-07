package net.vitki.rtf;

import net.vitki.charset.FontManager;
import net.vitki.charset.ManagedFont;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.util.Hashtable;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

public class RtfShape extends RtfContainer implements DrawConst
{
    public static final int MARGIN = 10;
    
    public static final String PARAGRAPH = "~";
    
    int left;
    int top;
    int right;
    int bottom;
    int order;
    int gx_p, gy_p, gx_m, gy_m;
    
    int     cur_x;
    int     cur_y;
    boolean in_group;
    
    String  text_string;
    String  text_face;
    int     text_size;
    int     text_color;
    
    private Vector iprops;
    private Hashtable props;
    
    public RtfShape()
    {
        super();
        left = top = right = bottom = 0;
        order = 0;
        cur_x = cur_y = 0;
        in_group = false;
        iprops = new Vector();
        props = new Hashtable();
        text_string = null;
        text_face = null;
        text_size = -1;
        text_color = -1;
        gx_p = gy_p = gx_m = gy_m = 0;
    }
    
    public RtfObject add (RtfObject obj)  throws RtfException
    {
        if (obj instanceof RtfDrawing)
            super.add(obj);
        else
            throw new RtfException ("RtfShape can only contain drawings");
        return obj;
    }
    
    void addProperty (DrawProp prop)  {
        iprops.add( prop );
        props.put( new Integer(prop.getTag()), prop );
    }
    
    public int propCount()  {
        return iprops.size();
    }

    public DrawProp getNthProperty (int no)  {
        return (DrawProp)iprops.get(no);
    }
    
    public String getNthPropertyName (int no)  {
        return getNthProperty(no).getName();
    }
    
    public String getNthPropertyValue (int no)  {
        return getNthProperty(no).getString();
    }
    
    public DrawProp getProperty (int tag)  {
        DrawProp prop = (DrawProp)props.get( new Integer(tag) );
        if (prop == null) {
            String def = DrawTag.getDefault(tag);
            if (def == null)
                return null;
            prop = new DrawProp (tag, def);
        }
        return prop;
    }
    
    public boolean isRelative()  {
        return left == 0 && top == 0 && right == 0 && bottom == 0;
    }

    public RtfDrawing getDrawing (int no)
    {
        return (RtfDrawing)get(no);
    }
    
    public int drawingCount()
    {
        return size();
    }
    
    protected void dumpText (XmlWriter out)  throws SAXException
    {
        if (text_string == null)
            return;
        Attributes atts = out.newAttr();
        if (text_face != null)
            out.addAttr(atts, "face", text_face);
        if (text_size != -1)
            out.addAttr(atts, "size", text_size);
        if (text_color != -1)
            out.addAttr(atts, "color", ColorProps.getName(text_color));
        out.startElement("rtf-shape-text", atts);
        out.characters(text_string);
        out.endElement("rtf-shape-text");
    }

    protected void dumpProperties (XmlWriter out)  throws SAXException
    {
        Attributes atts = out.newAttr();
        for (int i=0; i<propCount(); i++)  {
            out.clearAttr(atts);
            out.addAttr(atts, "name", getNthPropertyName(i));
            out.addAttr(atts, "value", getNthPropertyValue(i));
            out.emptyElement("rtf-shape-property", atts);
        }
    }
    
    String getPropertyString()
    {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<propCount(); i++)  {
            if (i > 0)  buf.append(";");
            buf.append(getNthPropertyName(i));
            buf.append("=");
            buf.append(getNthPropertyValue(i));
        }
        return buf.toString();
    }
    
    protected void dumpDrawings (XmlWriter out, DumpHelper ctx)
    throws SAXException, RtfException
    {
        for (int i=0; i<drawingCount(); i++)
            getDrawing(i).dump(out, ctx);
    }
    
    protected void dumpCanvas (XmlWriter out, DumpHelper ctx, ShapeContext sc)
    throws SAXException, RtfException
    {
        if (sc == null)
            return;
        finishCanvas(sc, ctx.getAnalyser());
        int width = sc.width;
        int height = sc.height;
        out.startElement("inlinemediaobject");
        out.startElement("imageobject");
        Attributes atts = out.newAttr();
        out.setAttr(atts, "role", "autoshape");
        out.setAttr(atts, "fileref", sc.fileref);
        out.setAttr(atts, "width", Util.mmd2mm(right-left)); //""+width+"px");
        out.setAttr(atts, "height", Util.mmd2mm(bottom-top)); //""+height+"px");
        out.setAttr(atts, "format", "PNG");
        out.emptyElement("imagedata", atts);
        out.endElement("imageobject");
        out.endElement("inlinemediaobject");
    }
    
    protected void finishCanvas (ShapeContext sc, DocAnalyser analyser)
    throws RtfException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write (sc.image, "png", baos);
        } catch (Exception e) {
            throw new RtfException(e);
        }
        int len = baos.size();
        sc.fileref = analyser.writeToZip("png", "r", baos);
        sc.canvas = null;
        sc.image = null;
        baos = null;
        analyser.garbageCollect(len);
    }
    
    protected void dumpGeometry (XmlWriter out, Attributes atts)
    throws SAXException
    {
        out.addAttr (atts, "l", Util.mmd2mm(left));
        out.addAttr (atts, "t", Util.mmd2mm(top));
        out.addAttr (atts, "r", Util.mmd2mm(right));
        out.addAttr (atts, "b", Util.mmd2mm(bottom));
        out.addAttr (atts, "gx-p", Util.mmd2mm(gx_p));
        out.addAttr (atts, "gy-p", Util.mmd2mm(gy_p));
        out.addAttr (atts, "gx-m", Util.mmd2mm(gx_m));
        out.addAttr (atts, "gy-m", Util.mmd2mm(gy_m));
        out.addAttr (atts, "order", order);
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        dump (out, ctx, true);
    }

    public void dump (XmlWriter out, DumpHelper ctx, boolean can_draw)
    throws RtfException, SAXException
    {
        if (ctx.draw_shapes && can_draw)   {
            ShapeContext sc = draw(ctx);
            dumpCanvas (out, ctx, sc);
        }
        if (ctx.dump_shapes)   {
            Attributes atts = out.newAttr();
            dumpGeometry (out, atts);
            out.startElement ("rtf-shape", atts);
            dumpProperties(out);
            dumpText(out);
            dumpDrawings(out, ctx);
            out.endElement ("rtf-shape");
        }
    }

    ShapeContext draw (DumpHelper ctx) throws RtfException
    {
        FontManager.setup(this);
        DocAnalyser analyser = ctx.getAnalyser();
        ShapeContext sc = new ShapeContext(this, analyser.getPPI(), 0);
        if (sc.canvas == null)
            return null;
        draw (ctx, sc);
        return sc;
    }
    
    
    ShapeContext draw (DumpHelper ctx, ShapeContext sc)
    throws RtfException
    {
        if (canDrawWord97())
            drawWord97(ctx, sc);
        else
            drawWord95(ctx, sc);
        return sc;
    }
    
    void drawWord95 (DumpHelper ctx, ShapeContext sc)   throws RtfException
    {
        int num = drawingCount();
        int[] orders = new int[num];
        int i;
        for (i=0; i<num; i++)
            orders[i] = getDrawing(i).order;
        int[] pos = new int[num];
        int cur = 0;
        int cur_order = 0;
        while (cur < num)  {
            for (i=0; i<num; i++)
                if (orders[i] == cur_order)
                    pos[cur++] = i;
            cur_order++;
        }
        for (i=0; i<num; i++)
            getDrawing(pos[i]).draw(sc);
    }
    
    public boolean canDrawWord97()
    {
        if (propCount() == 0)
            return false;
        DrawProp type = getProperty(_ShapeType);
        if (type == null)
            return false;
        switch(type.getInt())
        {
            case D_FREEFORM:
            case D_RECTANGLE:
            case D_ELLIPSE:
            case D_ARC:
            case D_LINE:
            case D_TEXT_BOX:
                return true;
            default:
                return false;
        }
    }

    void drawWord97 (DumpHelper ctx, ShapeContext sc)
    throws RtfException
    {
        if (isRelative())
        {
            //String str = getPropertyString();
            int gx, gy, rl, rt, rr, rb;
            DrawProp prop;
            rl = getProperty(_relLeft).getInt();
            rt = getProperty(_relTop).getInt();
            rr = getProperty(_relRight).getInt();
            rb = getProperty(_relBottom).getInt();
            prop = getProperty(_groupLeft);
            prop = getProperty(_groupTop);
            gx = gx_p - gx_m;
            gy = gy_p - gy_m;
            left = rl + gx;
            top = rt + gy;
            right = rr + gx;
            bottom = rb + gy;
        }
        DrawProp type = getProperty(_ShapeType);
        if (type == null)
            return;
        int x = sc.getx(left);
        int y = sc.gety(top);
        int w = sc.getw(right-left);
        int h = sc.geth(bottom-top);
        switch(type.getInt())
        {
            case D_FREEFORM:
                drawPolygon(sc, x, y, w, h);
                break;
            case D_RECTANGLE:
                drawRectangle(sc, x, y, w, h);
                break;
            case D_ELLIPSE:
                drawEllipse(sc, x, y, w, h);
                break;
            case D_ARC:
                drawArc(sc, x, y, w, h);
                break;
            case D_LINE:
                drawLine(sc, x, y, w, h);
                break;
            case D_TEXT_BOX:
                drawTextbox(sc, x, y, w, h);
                break;
            default:
                break;
        }
    }
    
    private void drawEllipse (ShapeContext sc, int x, int y, int w, int h)
    {
        DrawProp prop;
        Shape s = new Ellipse2D.Double(x, y, w, h);
        prop = getProperty(_fFilled);
        if (prop != null && prop.getBool())  {
            prop = getProperty(_fillColor);
            sc.setFillColor( prop.getColor() );
            sc.canvas.fill(s);
        }
        prop = getProperty(_fLine);
        if (prop != null && prop.getBool())  {
            prop = getProperty(_lineColor);
            int color = prop != null ? prop.getColor() : 0;
            sc.setColor( color );
            prop = getProperty(_lineWidth);
            int width = prop != null ? prop.getInt() : 0;
            sc.setPenWidth(width);
            sc.canvas.draw(s);
        }
        drawTextbox(sc, x, y, w, h);
    }

    private void drawArc (ShapeContext sc, int x, int y, int w, int h)
    {
    }

    private void drawPolygon (ShapeContext sc, int x, int y, int w, int h)
    {
    }

    private void drawLine (ShapeContext sc, int x, int y, int w, int h)
    {
        Shape s = new Line2D.Double(x, y, x+w, y+h);
        DrawProp prop;
        prop = getProperty(_lineColor);
        int color = prop != null ? prop.getColor() : 0;
        sc.setColor( color );
        prop = getProperty(_lineWidth);
        int width = prop != null ? prop.getInt() : 0;
        sc.setPenWidth(width);
        sc.canvas.draw(s);
    }

    private void drawRectangle (ShapeContext sc, int x, int y, int w, int h)
    {
        DrawProp prop;
        Shape s = new Rectangle2D.Double(x, y, w, h);
        prop = getProperty(_fFilled);
        if (prop != null && prop.getBool())  {
            prop = getProperty(_fillColor);
            sc.setFillColor( prop.getColor() );
            sc.canvas.fill(s);
        }
        prop = getProperty(_fLine);
        if (prop != null && prop.getBool())  {
            prop = getProperty(_lineColor);
            int color = prop != null ? prop.getColor() : 0;
            sc.setColor( color );
            prop = getProperty(_lineWidth);
            int width = prop != null ? prop.getInt() : 0;
            sc.setPenWidth(width);
            sc.canvas.draw(s);
        }
        drawTextbox(sc, x, y, w, h);
    }

    private void drawTextbox (ShapeContext sc, int x, int y, int w, int h)
    {
        if (text_string == null)
            return;
        
        double tx = x;
        double ty = y;
        
        if (text_color != -1)
            sc.setColor (text_color);
        
        int size = text_size;
        if (size < 0)
            size = 12;
        size = (int)(size * 3.0);
        size = sc.getw(size);
        String face = text_face;
        if (face == null)
            face = "Arial";
        Font font = null;
        ManagedFont mf = FontManager.getFont(face, Font.PLAIN, size, size);
        if (mf != null)
            font = mf.getFont();
        if (font == null)
            font = new Font ("Dialog", Font.PLAIN, size);
        sc.canvas.setFont (font);
        
        FontMetrics fm = sc.canvas.getFontMetrics(font);
        FontRenderContext frc = sc.canvas.getFontRenderContext();
        
        StringTokenizer stt = new StringTokenizer (text_string, PARAGRAPH);
        String[] lines = new String[stt.countTokens()];
        Rectangle2D[] bounds = new Rectangle2D[lines.length];

        int height = 0;
        for (int i=0; i<lines.length; i++)  {
            lines[i] = stt.nextToken();
            GlyphVector gv = font.createGlyphVector (frc, lines[i]);
            bounds[i] = gv.getVisualBounds();
            height += bounds[i].getHeight();
            if (i > 0)   height += fm.getLeading() * 2;
        }

        AffineTransform saved_at = null;
        int rotation = getProperty(_Rotation).getInt();
        if (rotation != 0)  {
            double theta = rotation * (Math.PI / 180.0);
            saved_at = sc.canvas.getTransform();
            AffineTransform at;
            at = new AffineTransform(saved_at);
            double dx = w*Math.sin(theta)*0;
            double dy = h*Math.cos(theta)*0;
            double rx = x + w*0.5;
            double ry = y + h*0.5;
            at = new AffineTransform();
            at.setToRotation(theta, rx, ry);
            sc.canvas.transform(at);
        }

        ty = ty + (h - height + fm.getAscent()) * 0.5;
        for (int i=0; i<lines.length; i++)  {
            tx = x + (w - bounds[i].getWidth()) * 0.5;
            sc.canvas.drawString (lines[i], (float)tx, (float)ty);
            ty += bounds[i].getHeight() + fm.getLeading() * 2;
        }
        
        if (saved_at != null)
            sc.canvas.setTransform(saved_at);
        
        sc.setColor( 0xffff00 );
        sc.setPenWidth(0);
        //sc.canvas.draw(new Rectangle2D.Double(x, y, w, h));
    }
    
}
