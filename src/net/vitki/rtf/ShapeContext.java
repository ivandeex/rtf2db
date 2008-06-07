package net.vitki.rtf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;

public class ShapeContext
{
    BufferedImage image;
    Graphics2D canvas;
    int left;
    int top;
    int ppi;
    int width;
    int height;
    String fileref;
    
    ShapeContext (RtfShape shape, int ppi, int margin)
    {
        fileref = null;
        this.left = shape.left;
        this.top = shape.top;
        this.ppi = ppi;
        this.width = getx(shape.right);
        this.height = gety(shape.bottom);
        image = null;
        canvas = null;
        if (width <= 0 || height <= 0)
            return;
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        canvas = image.createGraphics();
        canvas.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, 
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        setFillColor (0xf0f0f4);
        canvas.fill (new Rectangle2D.Double(0, 0, width, height));
    }
    
    void setColor (int color)  {
        canvas.setColor(makeColor(color));
    }
    
    void setFillColor (int color)  {
        canvas.setPaint (makeColor(color));
    }
    
    void setPenWidth (int width)  {
        canvas.setStroke( new BasicStroke( getw(width) ) );
    }
    
    Color makeColor (int color)  {
        return new Color(color & 0xffffff);
    }

    int getx (int x)
    {
        x -= left;
        //x = Util.twips2mmd(x);
        x = Util.mmd2px(x, ppi);
        return x;
    }

    int gety (int y)
    {
        y -= top;
        //y = Util.twips2mmd(y);
        y = Util.mmd2px(y, ppi);
        return y;
    }

    int getw (int w)
    {
        //w = Util.twips2mmd(w);
        w = Util.mmd2px(w, ppi);
        return w;
    }

    int geth (int h)
    {
        //h = Util.twips2mmd(h);
        h = Util.mmd2px(h, ppi);
        return h;
    }
}