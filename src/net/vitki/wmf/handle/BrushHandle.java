package net.vitki.wmf.handle;

import java.awt.Paint;
import java.awt.Color;
import java.awt.TexturePaint;

import net.vitki.wmf.Handle;

public class BrushHandle implements Handle {

    public Paint  paint;

    public BrushHandle (Paint paint)  {
        this.paint = paint;
    }

    public BrushHandle ()  {
        this.paint = null;
    }

    public String toString()  {
        String s1;
        if (paint == null)
            s1 = "transparent";
        else if (paint instanceof Color)
            s1 = "#"+((Color)paint).toString();
        else if (paint instanceof TexturePaint)
            s1 = "texture";
        else
            s1 = "paint";
        return "BrushHandle("+s1+")";
    }

    public int getType()  { return BRUSH; }

}

