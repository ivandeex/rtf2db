package net.vitki.charset;

import java.awt.Font;

public class ManagedFont
{

    private String  name;
    private String  path;
    private Font    font;
    private Font    base;
    private double  width;
    private double  height;
    private int     style;
    private int     charset;

    private Long    stamp = new Long(System.currentTimeMillis());

    Long touch()  {
        stamp = new Long (System.currentTimeMillis());
        return stamp;
    }

    Long getStamp()  { return stamp; }

    public static String getKey (String n, int s, double w, double h)  {
        return '['+n+'|'+s+'|'+w+'|'+h+']';
    }

    public String getKey()  {
        return getKey(name,style,width,height);
    }
    
    void setFont (Font font)  {
        this.font = this.base = font;
    }
    
    void setPath (String path)  {
        this.path = path;
    }

    ManagedFont (String name, String path, Font font, int style,
                 int charset, double width, double height)
    {
        this.name    = name;
        this.path    = path;
        this.style   = style;
        this.charset = charset;
        this.width   = width;
        this.height  = height;
        this.font    = this.base = font;
    }

    ManagedFont (ManagedFont cloned)  {
        this.name    = cloned.name;
        this.path    = cloned.path;
        this.font    = cloned.base;
        this.base    = cloned.base;
        this.width   = cloned.width;
        this.height  = cloned.height;
        this.style   = cloned.style;
        this.charset = cloned.charset;
    }

    public Font getFont()       { return font; }
    public String getName()     { return name; }
    public String getPath()     { return path; }
    public int  getCharset()    { return charset; }

    void  setProps (int style, double width, double height)  {
        if (this.width == width && this.height == height && this.style == style)
            return;
        if (height == 0.0)    height = width;
        if (width == 0.0)    width = height;
        this.width  = width;
        this.height = height;
        this.style  = style;
        if (base != null)
            font = base.deriveFont( style, getSize() );
    }

    public float getSize()  {
        return (float) Math.max( width, height );
    }

    public float getWidth()  {
        return (float)width;
    }

    public float getHeight()  {
        return (float)height;
    }

}

