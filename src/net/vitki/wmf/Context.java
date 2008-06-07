package net.vitki.wmf;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import net.vitki.wmf.handle.*;


public class Context  {

    public PenHandle     pen;
    public BrushHandle   brush;
    public FontHandle    font;
    public PaletteHandle palette;
    public RegionHandle  region;
    public BitmapHandle  bitmap;
    public ClipHandle    clip;
    public Stroke  null_stroke;
    public Color   txt_color;
    public Color   fg_color;
    public Color   bg_color;
    public int     text_align;
    public int     cur_x;
    public int     cur_y;

    public Context ()  {
        pen = new PenHandle();
        brush = new BrushHandle();
        font = new FontHandle();
        palette = new PaletteHandle();
        region = new RegionHandle();
        bitmap = new BitmapHandle();
        clip = new ClipHandle();
        null_stroke = new BasicStroke(0);
        txt_color = Color.black;
        fg_color = Color.black;
        bg_color = Color.white;
        text_align = 0;
        cur_x = cur_y;
    }

    public boolean select (Handle handle)  {
        if (handle == null)
            return false;
        switch (handle.getType())  {
            case Handle.PEN:
                this.pen = (PenHandle)handle;
                return true;
            case Handle.BRUSH:
                this.brush = (BrushHandle)handle;
                return true;
            case Handle.FONT:
                this.font = (FontHandle)handle;
                return true;
            case Handle.PALETTE:
                this.palette = (PaletteHandle)handle;
                return true;
            case Handle.REGION:
                this.region = (RegionHandle)handle;
                return true;
            default:
                return false;
        }
    }

}

