package net.vitki.rtf;

import net.vitki.charset.FontManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.util.Hashtable;

public class RtfShapeGroup extends RtfShape
{
    private Vector shapes;
    
    public RtfShapeGroup()
    {
        super();
        shapes = new Vector();
    }
    
    public RtfObject add (RtfObject obj)  throws RtfException
    {
        if (obj instanceof RtfDrawing)
            super.add(obj);
        else if (obj instanceof RtfShape)
            addShape( (RtfShape)obj );
        else
            throw new RtfException ("RtfShapeGroup can only contain shapes and drawings");
        return obj;
    }
    
    private void addShape (RtfShape shape)
    {
        shapes.add(shape);
        shape.gx_p = left;
        shape.gx_m = getProperty(_groupLeft).getInt();
        shape.gy_p = top;
        shape.gy_m = getProperty(_groupTop).getInt();
    }
    
    public RtfShape getShape (int no)
    {
        return (RtfShape)shapes.get(no);
    }
    
    public int shapeCount()  {
        return shapes.size();
    }
    
    protected void dumpShapes (XmlWriter out, DumpHelper ctx)
    throws SAXException, RtfException
    {
        for (int i=0; i<shapeCount(); i++)
            getShape(i).dump(out, ctx, false);
    }
    
    void recalculateBounds()
    {
        RtfShape shape;
        if (shapeCount() > 0)  {
            shape = getShape(0);
            left = shape.left;
            top = shape.top;
            right = shape.right;
            bottom = shape.bottom;
        }
        for (int i=0; i<shapeCount(); i++)  {
            shape = getShape(i);
            if (shape.isRelative())
                continue;
            shape = getShape(i);
            left = Math.min(left, shape.left);
            top = Math.min(top, shape.top);
            right = Math.max(right, shape.right);
            bottom = Math.max(bottom, shape.bottom);
        }
    }
    
    public void dump (XmlWriter out, DumpHelper ctx, boolean can_draw)
    throws RtfException, SAXException
    {
        if (isRelative())
            recalculateBounds();
        if (ctx.draw_shapes && can_draw)  {
            ShapeContext sc = draw(ctx);
            dumpCanvas (out, ctx, sc);
        }
        if (ctx.dump_shapes)  {
            Attributes atts = out.newAttr();
            dumpGeometry (out, atts);
            out.startElement ("rtf-shape-group", atts);
            dumpProperties (out);
            dumpText (out);
            dumpShapes(out, ctx);
            dumpDrawings(out, ctx);
            out.endElement ("rtf-shape-group");
        }
    }
    
    ShapeContext draw (DumpHelper ctx, ShapeContext sc)
    throws RtfException
    {
        int num = shapeCount();
        int[] orders = new int[num];
        int i;
        for (i=0; i<num; i++)
            orders[i] = getShape(i).order;
        int[] pos = new int[num];
        int cur = 0;
        int cur_order = 0;
        while (cur < num)  {
            for (i=0; i<num; i++)
                if (orders[i] == cur_order)
                    pos[cur++] = i;
            cur_order++;
        }
        int dx = getProperty(_relLeft).getInt();
        int dy = getProperty(_relTop).getInt();
        dx += gx_p - gx_m;
        dy += gy_p - gy_m;
        sc.left -= dx;
        sc.top -= dy;
        for (i=0; i < num; i++)
            getShape(pos[i]).draw(ctx, sc);
        sc.left += dx;
        sc.top += dy;
        return sc;
    }
    

}
