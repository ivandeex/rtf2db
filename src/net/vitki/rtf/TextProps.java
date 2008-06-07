package net.vitki.rtf;

import java.io.PrintWriter;

import net.vitki.charset.Encoding;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TextProps
{

    public int     no;
    public int     mask;
    public boolean additive;
    public int     type;
    public String  name;
    public int     usage_count;

    public final static int T_NONE   = 0;
    public final static int T_PARA   = 1;
    public final static int T_CHAR   = 2;
    public final static int T_SECT   = 3;
    public final static int T_TABLE  = 4;

    public final static int NAME     = 0x00000001;
    public final static int ALIGN    = 0x00000002;
    public final static int FONT     = 0x00000004;
    public final static int SIZE     = 0x00000008;
    public final static int CHARSET  = 0x00000010;
    public final static int B        = 0x00000020;
    public final static int I        = 0x00000040;
    public final static int U        = 0x00000080;
    public final static int SUB      = 0x00000100;
    public final static int SUP      = 0x00000200;
    public final static int STRIKE   = 0x00000400;
    public final static int CAPS     = 0x00000800;
    public final static int SCAPS    = 0x00001000;
    public final static int COLOR    = 0x00002000;
    public final static int HIDDEN   = 0x00004000;
    public final static int LISTLEV  = 0x00008000;
    public final static int LINESPC  = 0x00010000;
    public final static int LINSPMUL = 0x00020000;
    public final static int LEFTIND  = 0x00040000;
    public final static int RIGHTIND = 0x00080000;
    public final static int FIRSTIND = 0x00100000;
    public final static int SPCBEFOR = 0x00200000;
    public final static int SPCAFTER = 0x00400000;
    public final static int NO       = 0x80000000;

    public final static int XMLMASK = LISTLEV | LINESPC | LINSPMUL | LEFTIND |
                                      RIGHTIND | FIRSTIND | SPCBEFOR | SPCAFTER |
                                      ALIGN;

    public int     align;
    public String  font;
    public int     size;
    public int     charset;
    public boolean bold;
    public boolean italic;
    public boolean underline;
    public boolean sub;
    public boolean sup;
    public boolean strike;
    public boolean caps;
    public boolean scaps;
    public boolean hidden;
    public int     color;
    public int     listlev;
    public int     linespc;
    public int     linspmul;
    public int     left_ind;
    public int     right_ind;
    public int     first_ind;
    public int     spc_befor;
    public int     spc_after;

    public static final int ALIGN_LEFT    = 1;
    public static final int ALIGN_RIGHT   = 2;
    public static final int ALIGN_CENTER  = 3;
    public static final int ALIGN_JUSTIFY = 4;

    public void copyFrom (TextProps style)
    {
        this.no        = style.no       ;
        this.mask      = style.mask     ;
        this.type      = style.type     ;
        this.additive  = style.additive ;
        this.name      = style.name     ;
        this.align     = style.align    ;
        this.font      = style.font     ;
        this.size      = style.size     ;
        this.charset   = style.charset  ;
        this.bold      = style.bold     ;
        this.italic    = style.italic   ;
        this.underline = style.underline;
        this.sub       = style.sub      ;
        this.sup       = style.sup      ;
        this.strike    = style.strike   ;
        this.caps      = style.caps     ;
        this.scaps     = style.scaps    ;
        this.hidden    = style.hidden   ;
        this.color     = style.color    ;
        this.listlev   = style.listlev  ;
        this.linespc   = style.linespc  ;
        this.linspmul  = style.linspmul ;
        this.left_ind  = style.left_ind ;
        this.right_ind = style.right_ind;
        this.first_ind = style.first_ind;
        this.spc_befor = style.spc_befor;
        this.spc_after = style.spc_after;
    }

    public void applyFrom (TextProps style)
    {
        if (style.mask == 0)    return;
        if (style.has(ALIGN   ))  { set(ALIGN   );  this.align     = style.align    ; }
        if (style.has(FONT    ))  { set(FONT    );  this.font      = style.font     ; }
        if (style.has(SIZE    ))  { set(SIZE    );  this.size      = style.size     ; }
        if (style.has(CHARSET ))  { set(CHARSET );  this.charset   = style.charset  ; }
        if (style.has(B       ))  { set(B       );  this.bold      = style.bold     ; }
        if (style.has(I       ))  { set(I       );  this.italic    = style.italic   ; }
        if (style.has(U       ))  { set(U       );  this.underline = style.underline; }
        if (style.has(SUB     ))  { set(SUB     );  this.sub       = style.sub      ; }
        if (style.has(SUP     ))  { set(SUP     );  this.sup       = style.sup      ; }
        if (style.has(STRIKE  ))  { set(STRIKE  );  this.strike    = style.strike   ; }
        if (style.has(CAPS    ))  { set(CAPS    );  this.caps      = style.caps     ; }
        if (style.has(SCAPS   ))  { set(SCAPS   );  this.scaps     = style.scaps    ; }
        if (style.has(HIDDEN  ))  { set(HIDDEN  );  this.hidden    = style.hidden   ; }
        if (style.has(COLOR   ))  { set(COLOR   );  this.color     = style.color    ; }
        if (style.has(LISTLEV ))  { set(LISTLEV );  this.listlev   = style.listlev  ; }
        if (style.has(LINESPC ))  { set(LINESPC );  this.linespc   = style.linespc  ; }
        if (style.has(LINSPMUL))  { set(LINSPMUL);  this.linspmul  = style.linspmul ; }
        if (style.has(LEFTIND ))  { set(LEFTIND );  this.left_ind  = style.left_ind ; }
        if (style.has(RIGHTIND))  { set(RIGHTIND);  this.right_ind = style.right_ind; }
        if (style.has(FIRSTIND))  { set(FIRSTIND);  this.first_ind = style.first_ind; }
        if (style.has(SPCBEFOR))  { set(SPCBEFOR);  this.spc_befor = style.spc_befor; }
        if (style.has(SPCAFTER))  { set(SPCAFTER);  this.spc_after = style.spc_after; }
    }

    public int diff (TextProps style)
    {
        int mask = 0;
        if (!name.equals(style.name))          mask |= NAME;
        if (!font.equals(style.font))          mask |= FONT;
        if (this.align != style.align)         mask |= ALIGN;
        if (this.size != style.size)           mask |= SIZE;
        if (this.charset != style.charset)     mask |= CHARSET;
        if (this.bold != style.bold)           mask |= B;
        if (this.italic != style.italic)       mask |= I;
        if (this.underline != style.underline) mask |= U;
        if (this.sub != style.sub)             mask |= SUB;
        if (this.sup != style.sup)             mask |= SUP;
        if (this.strike != style.strike)       mask |= STRIKE;
        if (this.caps != style.caps)           mask |= CAPS;
        if (this.scaps != style.scaps)         mask |= SCAPS;
        if (this.hidden != style.hidden)       mask |= HIDDEN;
        if (this.color != style.color)         mask |= COLOR;
        if (this.listlev != style.listlev)     mask |= LISTLEV;
        if (this.linespc != style.linespc)     mask |= LINESPC;
        if (this.linspmul != style.linspmul)   mask |= LINSPMUL;
        if (this.left_ind != style.left_ind)   mask |= LEFTIND;
        if (this.right_ind != style.right_ind) mask |= RIGHTIND;
        if (this.first_ind != style.first_ind) mask |= FIRSTIND;
        if (this.spc_befor != style.spc_befor) mask |= SPCBEFOR;
        if (this.spc_after != style.spc_after) mask |= SPCAFTER;
        return mask;
    }

    public boolean has (int mask)
    {
        return ((this.mask & mask) != 0);
    }

    public static boolean has (int base, int mask)
    {
        return ((base & mask) != 0);
    }

    public void set (int mask)
    {
        this.mask |= mask;
    }

    public void clear()
    {
        no       = -1;
        type     = T_CHAR;
        additive = true;
        mask     = 0;
        name     = "";
        align    = ALIGN_LEFT;
        font     = "";
        size     = 0;
        charset  = 0;
        bold = italic = underline = strike = false;
        sub = sup = caps = scaps = hidden = false;
        color = 0;
        listlev = -1;
        linespc = linspmul = 0;
        left_ind = right_ind = first_ind = 0;
        spc_befor = spc_after = 0;
        usage_count = 0;
    }

    public TextProps()
    {
        clear();
    }

    public TextProps(int type)
    {
        clear();
        this.type = type;
        switch (type)  {
            case T_PARA:
                additive = false;
                break;
            case T_CHAR:
                additive = true;
                break;
        }
    }

    public TextProps(TextProps cloned)
    {
        copyFrom (cloned);
    }
    
    public String getTagName()
    {
        switch (type)  {
            case T_PARA:  return "parastyle";
            case T_CHAR:  return "charstyle";
            case T_SECT:  return "sectstyle";
            case T_TABLE: return "tablestyle";
            default:      return "badstyle";
        }
    }
    
    public String getAlignName()
    {
        switch (align)  {
            case ALIGN_LEFT:     return "left";
            case ALIGN_RIGHT:    return "right";
            case ALIGN_CENTER:   return "center";
            case ALIGN_JUSTIFY:  return "justify";
            default:             return "badalign";
        }
    }
    
    public void print (PrintWriter out, int mask)
    {
        String tag = getTagName();
        String _charset = Encoding.getEncoding(this.charset)+"("+this.charset+")";
        String str = "<"+tag+" no="+no;
        if (has(mask, NAME    ))  { str += " name=["+name+"]"; }
        if (has(mask, ALIGN   ))  { str += " align="+getAlignName(); }
        if (has(mask, FONT    ))  { str += " font=["+font+"]"; }
        if (has(mask, SIZE    ))  { str += " size="+size; }
        if (has(mask, CHARSET ))  { str += " charset="+_charset; }
        if (has(mask, B       ))  { str += " b="+Util.yn(bold); }
        if (has(mask, I       ))  { str += " i="+Util.yn(italic); }
        if (has(mask, U       ))  { str += " u="+Util.yn(underline); }
        if (has(mask, SUB     ))  { str += " sub="+Util.yn(sub); }
        if (has(mask, SUP     ))  { str += " sup="+Util.yn(sup); }
        if (has(mask, STRIKE  ))  { str += " strike="+Util.yn(strike); }
        if (has(mask, CAPS    ))  { str += " caps="+Util.yn(caps); }
        if (has(mask, SCAPS   ))  { str += " scaps="+Util.yn(scaps); }
        if (has(mask, HIDDEN  ))  { str += " hidden="+Util.yn(hidden); }
        if (has(mask, COLOR   ))  { str += " color="+ColorProps.getName(color); }
        if (has(mask, LISTLEV ))  { str += " listlev="+listlev; }
        if (has(mask, LINESPC ))  { str += " linespc="+linespc; }
        if (has(mask, LINSPMUL))  { str += " linspmul="+linspmul; }
        if (has(mask, LEFTIND ))  { str += " left_ind="+left_ind; }
        if (has(mask, RIGHTIND))  { str += " right_ind="+right_ind; }
        if (has(mask, FIRSTIND))  { str += " first_ind="+first_ind; }
        if (has(mask, SPCBEFOR))  { str += " spc_befor="+spc_befor; }
        if (has(mask, SPCAFTER))  { str += " spc_after="+spc_after; }
        str += " add="+Util.yn(additive);
        str += " />";
        out.println(str);
    }
    
    public void styleAttr (XmlWriter out, Attributes atts, int mask)  throws SAXException
    {
        if (mask == -2)  {
            out.clearAttr (atts);
            mask = -1;
        }
        if (has(mask, NO))
            out.setAttr (atts, "no", no);
        if (has(mask, NAME))
            out.setAttr (atts, "name", name);
        if (has(mask, ALIGN))
            out.setAttr (atts, "align", getAlignName());
        if (has(mask, FONT))
            out.setAttr (atts, "font", font);
        if (has(mask, SIZE))
            out.setAttr (atts, "size", size);
        if (has(mask, CHARSET))
            out.setAttr (atts, "charset", Encoding.getEncoding(charset));
        if (has(mask, B))
            out.setAttr (atts, "b", bold);
        if (has(mask, I))
            out.setAttr (atts, "i", italic);
        if (has(mask, U))
            out.setAttr (atts, "u", underline);
        if (has(mask, STRIKE))
            out.setAttr (atts, "s", strike);
        if (has(mask, SUB))
            out.setAttr (atts, "sub", sub);
        if (has(mask, SUP))
            out.setAttr (atts, "sup", sup);
        if (has(mask, CAPS))
            out.setAttr (atts, "caps", caps);
        if (has(mask, SCAPS))
            out.setAttr (atts, "scaps", scaps);
        if (has(mask, HIDDEN))
            out.setAttr (atts, "hidden", hidden);
        if (has(mask, COLOR))
            out.setAttr (atts, "color", ColorProps.getName(color));
        if (has(mask, LISTLEV))
            out.setAttr (atts, "list-level", listlev);
        if (has(mask, LINESPC))
            out.setAttr (atts, "line-space", linespc);
        if (has(mask, LINSPMUL))
            out.setAttr (atts, "line-sp-mul", linspmul);
        if (has(mask, LEFTIND))
            out.setAttr (atts, "left-ind", left_ind);
        if (has(mask, RIGHTIND))
            out.setAttr (atts, "right-ind", right_ind);
        if (has(mask, FIRSTIND))
            out.setAttr (atts, "first-ind", first_ind);
        if (has(mask, SPCBEFOR))
            out.setAttr (atts, "spc_before", spc_befor);
        if (has(mask, SPCAFTER))
            out.setAttr (atts, "spc-after", spc_after);
        out.setAttr (atts, "additive", additive);
    }

    public void dump (XmlWriter out, String tag, int mask)  throws SAXException
    {
        Attributes atts = out.newAttr();
        styleAttr (out, atts, mask);
        out.emptyElement (tag, atts);
    }

    public void startEmphasis (DumpHelper ctx, int mask)  throws SAXException
    {
        if (has(mask, ALIGN))
            ctx.startEmphasis (null, "align:"+getAlignName());
        if (has(mask, FONT))
            ctx.startEmphasis (null, "font:"+font);
        if (has(mask, SIZE))
            ctx.startEmphasis (null, "size:"+size);
        if (has(mask, CHARSET))
            ctx.startEmphasis (null, "charset:"+Encoding.getEncoding(charset));
        if (has(mask, B) && bold)
            ctx.startEmphasis (null, null);
        if (has(mask, I) && italic)
            ctx.startEmphasis (null, "italic");
        if (has(mask, U) && underline)
            ctx.startEmphasis (null, "underline");
        if (has(mask, STRIKE) && strike)
            ctx.startEmphasis (null, "strike");
        if (has(mask, SUB) && sub)
            ctx.startEmphasis ("subscript", null);
        if (has(mask, SUP) && sup)
            ctx.startEmphasis ("superscript", null);
        if (has(mask, CAPS) && caps)
            ctx.startEmphasis (null, "caps");
        if (has(mask, SCAPS) && scaps)
            ctx.startEmphasis (null, "smallcaps");
        if (has(mask, HIDDEN) && hidden)
            ctx.startEmphasis (null, "hidden");
        if (has(mask, COLOR))
            ctx.startEmphasis (null, "color:"+ColorProps.getName(color));
        if (has(mask, LISTLEV))
            ctx.startEmphasis (null, "list-level:"+listlev);
        if (has(mask, LINESPC))
            ctx.startEmphasis (null, "line-space:"+linespc);
        if (has(mask, LINSPMUL))
            ctx.startEmphasis (null, "line-sp-mul:"+linspmul);
        if (has(mask, LEFTIND))
            ctx.startEmphasis (null, "left-ind:"+left_ind);
        if (has(mask, RIGHTIND))
            ctx.startEmphasis (null, "right-ind:"+right_ind);
        if (has(mask, FIRSTIND))
            ctx.startEmphasis (null, "first-ind:"+first_ind);
        if (has(mask, SPCBEFOR))
            ctx.startEmphasis (null, "spc_before:"+spc_befor);
        if (has(mask, SPCAFTER))
            ctx.startEmphasis (null, "spc-after:"+spc_after);
    }

}
