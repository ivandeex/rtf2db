package net.vitki.rtf;

import java.io.PrintWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PageProps
{
    int     page_width;      // page width in mmd
    int     page_height;     // page height in mmd
    int     margin_left;     // left margin in mmd
    int     margin_top;      // top margin in mmd
    int     margin_right;    // right margin in mmd
    int     margin_bottom;   // bottom margin in mmd
    int     start_page_no;   // starting page number
    boolean facing_pages;    // facing pages enabled?
    boolean is_landscape;    // landscape or portrait?
    int     def_tab;         // tab size
    
    public PageProps()
    {
        page_width = page_height = 0;
        margin_left = margin_top = margin_right = margin_bottom = 0;
        start_page_no = 1;
        facing_pages = false;
        is_landscape = false;
    }
    
    public void print (PrintWriter out)
    {
        out.println ("<page size=("+page_width+","+page_height+")"+
                     " margins=("+margin_left+","+margin_top+","+
                     margin_right+","+margin_bottom+")"+
                     " facing="+facing_pages+" lscape="+is_landscape+
                     " tab="+def_tab+
                     " page1="+start_page_no+
                     " />");
    }

    public void dump (XmlWriter out)  throws SAXException
    {
        Attributes atts = out.newAttr();
        out.startElement ("rtf-page-props");
        out.clearAttr (atts);
        out.addAttr (atts, "width", page_width);
        out.addAttr (atts, "height", page_height);
        out.addAttr (atts, "orientation", is_landscape ? "landscape" : "portrait");
        out.addAttr (atts, "facing", facing_pages);
        out.emptyElement ("rtf-page-size", atts);
        out.clearAttr (atts);
        out.addAttr (atts, "start-from", start_page_no);
        out.addAttr (atts, "tab-size", def_tab);
        out.emptyElement ("rtf-page-properties", atts);
        out.clearAttr (atts);
        out.addAttr (atts, "left", margin_left);
        out.addAttr (atts, "top", margin_top);
        out.addAttr (atts, "right", margin_right);
        out.addAttr (atts, "bottom", margin_bottom);
        out.emptyElement ("rtf-page-margins", atts);
        out.endElement ("rtf-page-props");
    }

}

