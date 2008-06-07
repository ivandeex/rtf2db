package net.vitki.wmf.handle;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;

import net.vitki.wmf.Handle;

public class PenHandle implements Handle {

    public Stroke stroke;
    public Color  color;

    public PenHandle (Stroke stroke, Color color)  {
        this.stroke = stroke;
        this.color = color;
    }

    public PenHandle ()  {
        this(null, null);
    }

    public String toString()  {
        String s1, s2;
        if (stroke == null)
            s1 = "no_stroke";
        else if (stroke instanceof BasicStroke)
            s1 = "stroke("+ ((BasicStroke)stroke).getLineWidth()+")";
        else
            s1 = "stroke()";
        s2 = color==null ? "no_color" : "#"+color.toString();
        return ("PenHandle("+s1+","+s2+")");
    }

    public int getType()  { return PEN; }

}

