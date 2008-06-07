package net.vitki.wmf.handle;

import java.awt.Rectangle;

import net.vitki.wmf.Handle;

public class ClipHandle implements Handle {

    int color;

    public ClipHandle ()  {
        this.color = 0;
    }

    public String toString()  {
        return "ClipHandle()";
    }

    public int getType()  { return PALETTE; }

}

