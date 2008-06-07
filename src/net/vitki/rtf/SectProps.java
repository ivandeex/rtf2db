package net.vitki.rtf;

import java.io.PrintWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SectProps  {

    int  col_num;            // number of columns
    int  sect_break;         // section break type
    int  page_no_x;          // x position of page number in mmd
    int  page_no_y;          // y position of page number in mmd
    int  page_no_fmt;        // how the page number is formatted

    public SectProps()  {
        clear();
    }

    public void clear()  {
        col_num = 1;
        sect_break = SBK_NONE;
        page_no_x = 127;
        page_no_y = 127;
        page_no_fmt = PGN_DECIMAL;
    }

    public static String pageNoFormatAsString (int fmt)  {
        switch (fmt)  {
            case PGN_DECIMAL:   return "decimal";
            case PGN_UC_ROMAN:  return "uc-roman";
            case PGN_LC_ROMAN:  return "lc-roman";
            case PGN_UC_LETTER: return "uc-letter";
            case PGN_LC_LETTER: return "lc-letter";
            default:            return "unknown";
        }
    }

    public static String sectionBreakAsString (int sbk)  {
        switch (sbk)  {
            case SBK_NONE:    return "none";
            case SBK_COLUMN:  return "column";
            case SBK_EVEN:    return "even";
            case SBK_ODD:     return "odd";
            case SBK_PAGE:    return "page";
            default:          return "unknown";
        }
    }

    /** section break types **/
    public static final int SBK_NONE    = 0;
    public static final int SBK_COLUMN  = 1;
    public static final int SBK_EVEN    = 2;
    public static final int SBK_ODD     = 3;
    public static final int SBK_PAGE    = 4;

    /** page numbering types **/
    public static final int PGN_DECIMAL      = 0;
    public static final int PGN_UC_ROMAN  = 1;
    public static final int PGN_LC_ROMAN  = 2;
    public static final int PGN_UC_LETTER = 3;
    public static final int PGN_LC_LETTER = 4;

    public void print (PrintWriter out)
    {
        out.println ("<section col_num="+col_num
                     +" sect_break="+sect_break
                     +" page_no_xy=("+page_no_x+","+page_no_y+")"
                     +" page_no_fmt="+page_no_fmt
                     +" />");
    }
    
    public void dump (XmlWriter out)  throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "columns", col_num);
        out.addAttr (atts, "section-break", sectionBreakAsString (sect_break));
        out.addAttr (atts, "page-no-format", pageNoFormatAsString (page_no_fmt));
        out.emptyElement ("rtf-section-props", atts);
    }

}

