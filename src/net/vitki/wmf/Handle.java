package net.vitki.wmf;

public interface Handle  {

    public String toString();
    public int    getType();

    public static final int PEN     = 1;
    public static final int BRUSH   = 2;
    public static final int FONT    = 3;
    public static final int PALETTE = 4;
    public static final int REGION  = 5;
    public static final int BITMAP  = 6;
    public static final int CLIP    = 7;

}
