package net.vitki.rtf;

import java.util.Vector;
import java.io.PrintWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StyleSheet
{
    private Vector para_styles;
    private Vector char_styles;
    private Vector colors;
    private Vector fonts;
    private Vector lists;
    private Vector overs;
    private InfoProps info_props;
    private SectProps sect_props;
    private PageProps page_props;
    
    public StyleSheet()  {
        para_styles = new Vector();
        char_styles = new Vector();
        colors = new Vector();
        fonts = new Vector();
        lists = new Vector();
        overs = new Vector();
        info_props = new InfoProps();
        sect_props = new SectProps();
        page_props = new PageProps();
    }
    
    /* === global properties === */
    
    public void setPageProps (PageProps props)  { page_props = props; }
    public void setSectProps (SectProps props)  { sect_props = props; }
    public void setInfoProps (InfoProps props)  { info_props = props; }
    public PageProps getPageProps()  { return page_props; }
    public SectProps getSectProps()  { return sect_props; }
    public InfoProps getInfoProps()  { return info_props; }
    
    /* === colors === */

    public void addColor (ColorProps color)
    {
        colors.add(color);
    }
    
    public int getColor (int no) throws RtfException
    {
        if (no < 0 || no > colors.size())
            throw new RtfException ("no such color: "+no);
        if (no == 0)
            return 0;
        return ((ColorProps)colors.get(no-1)).getValue();
    }
    
    public void printColors (PrintWriter out)  throws RtfException
    {
        out.println("<colors>");
        for (int no=0; no<colors.size(); no++)
            ((ColorProps)colors.get(no)).print(out, no);
        out.println("</colors>");
    }

    /* === fonts === */
    
    public void addFont (FontProps font)
    {
        while (fonts.size() <= font.no)   fonts.add(new FontProps());
        fonts.set(font.no, font);
    }
    
    public int fontNum()  { return fonts.size(); }
    
    public FontProps getFont (int no)  throws RtfException
    {
        if (no < 0 || no >= fonts.size())
            throw new RtfException("bad font number "+no);
        return (FontProps)fonts.get(no);
    }
    
    public void printFonts (PrintWriter out)  throws RtfException
    {
        out.println("<fonts>");
        for (int no=0; no < fontNum(); no++)
            getFont(no).print(out);;
        out.println("</fonts>");
    }
    
    /* === lists === */
    
    public void addList (ListProps list)  {
        lists.add (list);
    }

    public void printLists(PrintWriter out)  throws RtfException
    {
        out.println("<lists>");
        for (int no=0; no < lists.size(); no++)
            ((ListProps)lists.get(no)).print(out, "list");
        out.println("</lists>");
    }

    public ListProps getListById (int id)  throws RtfException
    {
        for (int no=0; no < lists.size(); no++)  {
            ListProps lip = (ListProps)lists.get(no);
            if (lip.id == id)
                return lip;
        }
        throw new RtfException ("not found list with id: "+id);
    }

    /* === overrides === */
    
    public void addOver (ListProps over)  {
        if (over.no < 1)
            over.no = overs.size();
        if (over.no < 1)
            over.no = 1;
        while (overs.size() <= over.no)
            overs.add( new ListProps() );
        overs.set(over.no, over);
    }

    public void printOvers(PrintWriter out)  throws RtfException
    {
        out.println("<overs>");
        for (int no=1; no < overs.size(); no++)
            ((ListProps)overs.get(no)).print(out, "over");
        out.println("</overs>");
    }
    
    public LevelProps getLevel (int no, int level)  throws RtfException
    {
        if (no < 1 || no >= overs.size())
            throw new RtfException ("list overide with id "+no+" not found");
        ListProps lp = (ListProps)overs.get(no);
        if (level < 0 || level >= lp.level_count || level >= lp.level_count)
            throw new RtfException ("list override "+no+" does not have level "+level);
        LevelProps llp = lp.levels[level];
        if (llp == null)
            throw new RtfException ("list override "+no+" level "+level+" is null");
        return llp;
    }
    
    /* === styles === */
    
    public void addStyle (TextProps style)  throws RtfException
    {
        if (style.no == -1)
            style.no = 0;
        if (style.no < 0 || style.no > 2000)
            throw new RtfException ("undefined style number "+style.no);
        switch (style.type)
        {
            case TextProps.T_PARA:
                while (para_styles.size() <= style.no)
                    para_styles.add( new TextProps(TextProps.T_PARA) );
                if (getParaStyle(style.no).no != -1)
                    throw new RtfException ("duplicate paragraph style number "+style.no);
                para_styles.set( style.no, style );
                break;
            case TextProps.T_CHAR:
                while (char_styles.size() <= style.no)
                    char_styles.add( new TextProps(TextProps.T_CHAR) );
                if (getCharStyle(style.no).no != -1)
                    throw new RtfException ("duplicate character style number "+style.no);
                char_styles.set( style.no, style );
                break;
            default:
                throw new RtfException("wrong style type");
        }
    }

    public TextProps getParaStyle (int no)  throws RtfException
    {
        if (no < 0 || no >= para_styles.size())
            throw new RtfException ("not found paragraph style with number "+no);
        return (TextProps)para_styles.get(no);
    }

    public TextProps getCharStyle (int no)  throws RtfException
    {
        if (no < 0 || no >= char_styles.size())
            throw new RtfException ("not found character style with number "+no);
        return (TextProps)char_styles.get(no);
    }

    public int paraStyleNum()   { return para_styles.size(); }

    public int charStyleNum()   { return char_styles.size(); }

    public void printStyles (PrintWriter out, boolean all)  throws RtfException
    {
        int no;
        TextProps style;
        out.println("<styles>");
        for (no = 0; no < paraStyleNum(); no++)  {
            style = getParaStyle(no);
            if (style.no == -1 || (!all && style.usage_count == 0))
                continue;
            style.print(out, -1);
        }
        for (no = 0; no < charStyleNum(); no++)  {
            style = getCharStyle(no);
            if (style.no == -1 || (!all && style.usage_count == 0))
                continue;
            style.print(out, style.mask);
        }
        out.println("</styles>");
    }

    public void dumpStyles (XmlWriter out, boolean all)  throws RtfException, SAXException
    {
        int no;
        TextProps style;
        out.startElement("rtf-styles");
        for (no = 0; no < paraStyleNum(); no++)  {
            style = getParaStyle(no);
            if (style.no == -1 || (!all && style.usage_count == 0))
                continue;
            style.dump (out, "rtf-para-style", -2);
        }
        for (no = 0; no < charStyleNum(); no++)  {
            style = getCharStyle(no);
            if (style.no == -1 || (!all && style.usage_count == 0))
                continue;
            style.dump (out, "rtf-char-style", style.mask|TextProps.NO|TextProps.NAME);
        }
        out.endElement("rtf-styles");
    }

}
