package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RtfPara extends RtfContainer
{
    private ParaProps para_props;
    private RtfText last_text;
    private String  bookmark;
    
    public RtfPara (ParaProps props)
    {
        super();
        setProps(props);
        last_text = null;
        bookmark = null;
    }
    
    void setBookmark (String data)
    {
        this.bookmark = data;
    }
    
    void setProps (ParaProps props)
    {
        para_props = new ParaProps(props);
    }
    
    ParaProps getProps()
    {
        return para_props;
    }
    
    public void addText (String str, TextProps text_props, StyleSheet stylesheet)
    throws RtfException
    {
        int diff;
        if (last_text != null)  {
            diff = last_text.getProps().diff(text_props);
            diff &= ~TextProps.XMLMASK;
            if (diff == 0)  {
                last_text.append(str);
                return;
            }
        }
        last_text = new RtfText(str, text_props);
        TextProps summ_props = new TextProps();
        TextProps para_style = null;
        TextProps char_style = null;
        if (para_props.no != -1)
            para_style = stylesheet.getParaStyle(para_props.no);
        if (text_props.no != -1)
            char_style = stylesheet.getCharStyle(text_props.no);
        if (para_style != null)
            summ_props.applyFrom(para_style);
        if (char_style != null)
            summ_props.applyFrom(char_style);
        diff = text_props.diff(summ_props);
        diff &= ~TextProps.XMLMASK;
        last_text.setMask(diff);
        super.add(last_text);
    }
    
    public RtfObject add (RtfObject obj)  throws RtfException
    {
        super.add(obj);
        last_text = null;
        return obj;
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        ctx.endTable();
        if (!ctx.isList(this))
            ctx.endLists();
        Attributes atts = out.newAttr();
        String tag = "para";
        String role = null;
        StyleTransform st;
        st = ctx.checkDivision(this);
        ctx.incrementParaCount();
        if (st != null) {
            tag = "title";
        } else {
            st = ctx.checkList(this);
            if (st != null) {
                tag = st.list_item_tag;
                role = st.list_item_role;
            } else {
                st = ctx.getParaSubst(this.para_props);
                if (st != null) {
                    tag = st.subst_tag;
                    role = st.subst_role;
                }
            }
        }
        if (ctx.debug_styles)
            out.addAttr (atts, "no", para_props.no);
        if (ctx.debug_lists)  {
            if (para_props.list_no != -1)
                out.addAttr(atts, "listno", para_props.list_no);
            if (para_props.list_level != -1)
                out.addAttr(atts, "listlvl", para_props.list_level);
            if (para_props.list_type != -1)
                out.addAttr(atts, "listtype", para_props.list_type);
            if (para_props.bullet != null)
                out.addAttr(atts, "bullet", para_props.bullet);            
        }
        if (role != null)
            out.setAttr (atts, "role", role);
        if (bookmark != null)
            out.setAttr (atts, "id", bookmark);
        if (para_props.page_break)
            out.emptyElement("beginpage");
        if (tag != null)
            out.startElement(tag, atts);
        if (tag == null && bookmark != null)  {
            Attributes atts2 = out.newAttr();
            out.addAttr (atts2, "role", "bookmark");
            out.addAttr (atts2, "id", bookmark);
            out.emptyElement ("anchor", atts2);
        }
        super.dump(out, ctx);
        if (tag != null)
            out.endElement(tag);
    }
    
    public String guessDivLabel (Pattern label_pattern)
    {
        RtfText chunk = null;
        for (int i=0; i < size(); i++)  {
            if (get(i) instanceof RtfText) {
                chunk = (RtfText)get(i);
                break;
            }
        }
        if (chunk == null)
            return null;
        Matcher match = label_pattern.matcher(chunk.getString());
        if (!match.matches())
            return null;
        chunk.getBuffer().delete(match.start(1), match.end(1));
        return match.group(1).trim();
    }

}
