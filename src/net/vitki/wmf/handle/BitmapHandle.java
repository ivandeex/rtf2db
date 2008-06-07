package net.vitki.wmf.handle;

import java.awt.Rectangle;

import net.vitki.wmf.Handle;

public class BitmapHandle implements Handle {

    int color;

    public BitmapHandle ()  {
        this.color = 0;
    }

    public String toString()  {
        return "BitmapHandle()";
    }

    public int getType()  { return BITMAP; }

}

