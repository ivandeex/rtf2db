package net.vitki.rtf;

import net.vitki.charset.FontManager;
import net.vitki.charset.ManagedFont;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.Vector;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.StringTokenizer;

public class RtfDrawing extends RtfObject
{
    public static final int TYPE_NONE      = 0;
    public static final int TYPE_ARC       = 1;
    public static final int TYPE_CALLOUT   = 2;
    public static final int TYPE_ELLIPSE   = 3;
    public static final int TYPE_LINE      = 4;
    public static final int TYPE_POLYGON   = 5;
    public static final int TYPE_POLYLINE  = 6;
    public static final int TYPE_RECTANGLE = 7;
    public static final int TYPE_TEXTBOX   = 8;
    
    public static final int PATTERN_CLEAR = 0;
    public static final int PATTERN_SOLID = 1;

    public static final int FLOW_NORMAL       = 0;
    public static final int FLOW_FLIP_HOR     = 1;
    public static final int FLOW_FLIP_VERT    = 2;
    public static final int FLOW_ROTATE_LEFT  = 3;
    public static final int FLOW_ROTATE_RIGHT = 4;
    
    public static final String PARAGRAPH = "\n";

    int type;
    int left;
    int top;
    int width;
    int height;
    int penwidth;
    int pattern;
    ColorProps line_color;
    ColorProps fill_fore_color;
    ColorProps fill_back_color;
    boolean line_color_flag;
    boolean fill_fore_flag;
    boolean fill_back_flag;
    Vector poly_x;
    Vector poly_y;
    String text;
    int text_flow;
    String text_font;
    int text_size;
    int text_color;
    int order;
    
    public RtfDrawing()
    {
        super();
        type = 0;
        left = top = width = height = 0;
        penwidth = 0;
        pattern = 0;
        line_color = new ColorProps();
        fill_fore_color = new ColorProps();
        fill_back_color = new ColorProps();
        line_color_flag = fill_fore_flag = fill_back_flag = false;
        poly_x = new Vector();
        poly_y = new Vector();
        text = null;
        text_flow = 0;
        text_font = null;
        text_size = -1;
        text_color = -1;
        order = 0;
    }
    
    ColorProps processLineColor()  {
        line_color_flag = true;
        return line_color;
    }
    
    ColorProps processFillForeColor()  {
        fill_fore_flag = true;
        return fill_fore_color;
    }
    
    ColorProps processFillBackColor()  {
        fill_back_flag = true;
        return fill_back_color;
    }
    
    public boolean acceptPoly()  {
        return type == TYPE_POLYGON || type == TYPE_POLYLINE;
    }
    
    void addPolyPoint (int x, int y)  {
        poly_x.add( new Integer(x) );
        poly_y.add( new Integer(y) );
    }
    
    public int polyCount()  {
        return poly_x.size();
    }
    
    public int polyX (int i) {
        return ((Integer)poly_x.get(i)).intValue();
    }
    
    public int polyY (int i) {
        return ((Integer)poly_y.get(i)).intValue();
    }
    
    String getPolyString()  {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<polyCount(); i++)  {
            if (i > 0)
                buf.append(' ');
            buf.append(polyX(i));
            buf.append(',');
            buf.append(polyY(i));
        }
        return buf.toString();
    }
    
    public String getTypeName()  {
        switch (type)  {
            case TYPE_ARC:       return "arc";
            case TYPE_CALLOUT:   return "callout";
            case TYPE_ELLIPSE:   return "ellipse";
            case TYPE_LINE:      return "line";
            case TYPE_POLYGON:   return "polygon";
            case TYPE_POLYLINE:  return "polyline";
            case TYPE_RECTANGLE: return "rectangle";
            case TYPE_TEXTBOX:   return "textbox";
            default:             return "none";
        }
    }
    
    public String getPatternName()  {
        switch (pattern)  {
            case  0:  return "clear";
            case  1:  return "solid";
            case  2:  return "5%";
            case  3:  return "10%";
            case  4:  return "20%";
            case  5:  return "25%";
            case  6:  return "30%";
            case  7:  return "40%";
            case  8:  return "50%";
            case  9:  return "60%";
            case 10:  return "70%";
            case 11:  return "75%";
            case 12:  return "80%";
            case 13:  return "90%";
            case 14:  return "hor";
            case 15:  return "vert";
            case 16:  return "bkslash";
            case 17:  return "slash";
            case 18:  return "grid";
            case 19:  return "trellis";
            case 20:  return "lthor";
            case 21:  return "ltvert";
            case 22:  return "ltbkslash";
            case 23:  return "ltslash";
            case 24:  return "ltgrid";
            case 25:  return "lttrellis";
            default:  return "none";
        }
    }
    
    public String getFlowName()  {
        switch (text_flow)  {
            case FLOW_NORMAL:       return "normal";
            case FLOW_FLIP_HOR:     return "flip-hor";
            case FLOW_FLIP_VERT:    return "flip-vert";
            case FLOW_ROTATE_LEFT:  return "rotate-left";
            case FLOW_ROTATE_RIGHT: return "rotate-right";
            default:                return "none";
        }
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        if (!ctx.dump_shapes)
            return;
        Attributes atts = out.newAttr();
        out.addAttr (atts, "type", getTypeName());
        out.addAttr (atts, "left", left);
        out.addAttr (atts, "top", top);
        out.addAttr (atts, "width", width);
        out.addAttr (atts, "height", height);
        out.addAttr (atts, "order", order);
        out.addAttr (atts, "penwidth", penwidth);
        out.addAttr (atts, "pattern", getPatternName());
        if (line_color_flag)
            out.addAttr (atts, "line-clr", line_color.getName());
        if (fill_fore_flag)
            out.addAttr (atts, "fore-clr", fill_fore_color.getName());
        if (fill_back_flag)
            out.addAttr (atts, "back-clr", fill_back_color.getName());
        if (text_flow != 0)
            out.addAttr (atts, "flow", getFlowName());
        if (text_font != null)
            out.addAttr (atts, "font", text_font);
        if (text_size != -1)
            out.addAttr (atts, "size", text_size);
        if (text_color != -1)
            out.addAttr (atts, "text-clr", ColorProps.getName(text_color));
        out.startElement("rtf-drawing", atts);
        if (acceptPoly())
            out.charAttrElement ("rtf-drawing-poly", "count", ""+polyCount(),
                                 getPolyString());
        if (text != null)
            out.charElement ("rtf-drawing-text", text);
        out.endElement("rtf-drawing");
    }
    
    void draw (ShapeContext sc)
    {
        int x = sc.getx(left);
        int y = sc.gety(top);
        int w = sc.getw(width);
        int h = sc.geth(height);
        switch (type)
        {
            case TYPE_ARC:
                drawArc (sc, x, y, w, h);
                break;
            case TYPE_CALLOUT:
                drawCallout (sc, x, y, w, h);
                break;
            case TYPE_ELLIPSE:
                drawEllipse (sc, x, y, w, h);
                break;
            case TYPE_LINE:
                drawLine (sc, x, y, w, h);
                break;
            case TYPE_POLYGON:
                drawPolygon (sc, x, y, w, h);
                break;
            case TYPE_POLYLINE:
                drawPolyline (sc, x, y, w, h);
                break;
            case TYPE_RECTANGLE:
                drawRectangle (sc, x, y, w, h);
                break;
            case TYPE_TEXTBOX:
                drawTextbox (sc, x, y, w, h);
                break;
        }
    }
    
    private void drawArc (ShapeContext sc, int x, int y, int w, int h)
    {
    }

    private void drawCallout (ShapeContext sc, int x, int y, int w, int h)
    {
    }

    private void drawEllipse (ShapeContext sc, int x, int y, int w, int h)
    {
        Shape s = new Ellipse2D.Double (x, y, w, h);
        drawFilledShape (sc, s);
    }

    private void drawLine (ShapeContext sc, int x, int y, int w, int h)
    {
        Shape s = new Line2D.Double (x, y, x+w, y+h);
        drawContourShape(sc, s);
    }
    
    private void drawPolygon (ShapeContext sc, int x, int y, int w, int h)
    {
        Polygon p = makePolygon(sc);
        drawFilledShape(sc, p);
    }
    
    private Polygon makePolygon (ShapeContext sc)
    {
        Polygon p = new Polygon();
        int cx, cy;
        cx = cy = 0;
        for (int i=0; i<polyCount(); i++)  {
            cx = polyX(i);
            cx += left;
            cx = sc.getx(cx);
            cy = polyY(i);
            cy += top;
            cy = sc.gety(cy);
            p.addPoint (cx, cy);
            cx = cy = 0;
        }
        return p;
    }

    private void drawPolyline (ShapeContext sc, int x, int y, int w, int h)
    {
        Polygon p = makePolygon(sc);
        drawContourShape(sc, p);
    }

    private void drawRectangle (ShapeContext sc, int x, int y, int w, int h)
    {
        Shape s = new Rectangle2D.Double(x, y, w, h);
        drawFilledShape(sc, s);
    }
    
    private void drawFilledShape (ShapeContext sc, Shape s)
    {
        if (pattern == PATTERN_SOLID)
            sc.setFillColor (fill_back_color.getValue());
        else
            sc.setFillColor (fill_fore_color.getValue());
        if ((fill_fore_flag || fill_back_flag) && pattern != PATTERN_CLEAR)
            sc.canvas.fill (s);
        if (line_color_flag)  {
            sc.setColor (line_color.getValue());
            sc.setPenWidth (penwidth);
            sc.canvas.draw (s);
        }
        if (line_color_flag)
            drawContourShape(sc, s);
    }

    private void drawContourShape (ShapeContext sc, Shape s)
    {
        sc.setColor (line_color.getValue());
        sc.setPenWidth (penwidth);
        sc.canvas.draw (s);
    }

    private void drawTextbox (ShapeContext sc, int x, int y, int w, int h)
    {
        if (text == null)
            return;
        StringTokenizer stt = new StringTokenizer (text, PARAGRAPH);
        String[] lines = new String[stt.countTokens()];
        for (int i=0; i<lines.length; i++)
            lines[i] = stt.nextToken();
        sc.setColor (line_color.getValue());
        int size = text_size;
        if (size < 0)
            size = 12;
        size = (int)(size * 1.5);
        String face = text_font;
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
        y += fm.getAscent();
        if ("wingdings".equalsIgnoreCase(face))  {
            String us = lines[0];
        }
        for (int i=0; i<lines.length; i++)  {
            sc.canvas.drawString (lines[i], x, y);
            GlyphVector gv = font.createGlyphVector (frc, lines[i]);
            Rectangle2D rect = gv.getVisualBounds();
            y += (int)rect.getHeight();
        }
    }

}
