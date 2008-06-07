package net.vitki.wmf.handle;

import java.awt.Rectangle;

import net.vitki.wmf.Handle;

public class PaletteHandle implements Handle {

    int color;

    public PaletteHandle ()  {
        this.color = 0;
    }

    public String toString()  {
        return "PaletteHandle()";
    }

    public int getType()  { return PALETTE; }

}

