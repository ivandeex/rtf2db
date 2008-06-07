package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RtfText extends RtfObject
{
    private TextProps text_props;
    private StringBuffer text;
    private int mask;
    
    public RtfText(String str, TextProps props)
    {
        super();
        this.text = new StringBuffer();
        setProps(props);
        append (str);
    }
    
    void setProps (TextProps props)
    {
        text_props = new TextProps(props);
        mask = text_props.mask;
    }
    
    void setMask (int mask)   { this.mask = mask; }
    
    StringBuffer getBuffer()   { return text; }
    
    public TextProps getProps()   { return text_props; }
    
    public void append (String str)   { text.append(str); }
    
    public String getString()   { return text.toString(); }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        int out_mask = mask;
        if (!ctx.trace_fonts)
            out_mask &= ~(TextProps.FONT | TextProps.SIZE | TextProps.CHARSET);
        text_props.startEmphasis (ctx, out_mask);
        StyleTransform st = ctx.getCharTransform(text_props);
        if (st != null) {
            if (st.subst_tag != null) {
                Attributes atts = out.newAttr();
                if (st.subst_role != null)
                    out.addAttr (atts, "role", st.subst_role);
                if ("ulink".equals(st.subst_tag)) {
                    String url = getString();
                    if (url.indexOf(":/") < 1)
                        url = "http://"+url;
                    out.addAttr (atts, "url", url);
                }
                out.startElement (st.subst_tag, atts);
            }
        } else if (!"".equals(text_props.name)) {
            ctx.startEmphasis(null, "name:"+Util.canonicName(text_props.name));
        }
        out.characters(getString());
        if (st != null && st.subst_tag != null)
            out.endElement(st.subst_tag);
        ctx.endEmphasis();
    }

}
