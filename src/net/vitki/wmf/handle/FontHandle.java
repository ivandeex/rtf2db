package net.vitki.wmf.handle;

import java.awt.Font;

import net.vitki.wmf.Handle;

public class FontHandle implements Handle {

    public String  face;
    public int     size;
    public boolean bold;
    public boolean italic;
    public boolean underline;
    public boolean strikeout;
    public int     weight;
    public int     charset;
    public int     escapement;
    public int     orientation;

    public FontHandle (String face, int size,
                       boolean bold, boolean italic,
                       boolean underline, boolean strikeout,
                       int charset, int weight, int escapement, int orientation
                       )
    {
        this.face = face;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikeout = strikeout;
        this.charset = charset;
        this.weight = weight;
        this.escapement = escapement;
        this.orientation = orientation;
    }

    public FontHandle (String face, int size, int charset, int weight)
    {
        this(face,size,false,false,false,false,charset,weight,0,0);
    }
    
    public FontHandle (String face)
    {
        this(face,12,0,0);
    }
    
    public int getStyle()  {
        if (bold && italic)
            return Font.BOLD | Font.ITALIC;
        else if (bold)
            return Font.BOLD;
        else if (italic)
            return Font.ITALIC;
        else
            return Font.PLAIN;
    }

    public FontHandle()  {
        this("Dialog", 12,
             false, false, false, false,
             0, 400, 0, 0
             );
    }

    public String toString()  {
        return "FontHandle("+face+")";
    }

    public int getType()   { return FONT; }

}

