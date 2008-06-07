package net.vitki.wmf.handle;

import java.awt.Rectangle;

import net.vitki.wmf.Handle;

public class RegionHandle implements Handle {

    Rectangle rect;

    public RegionHandle ()  {
        this.rect = null;
    }

    public String toString()  {
        return "RegionHandle()";
    }

    public int getType()  { return REGION; }

}

