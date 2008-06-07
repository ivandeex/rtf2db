package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StyleRetriever extends DocAnalyser
{
    public static void main (String[] args) throws Exception
    {
        String prefix = "d:/java/rtf/test/sps";
        String rules = "d:/java/rtf/test/options.opt";
        if (args.length > 0)
            prefix = args[0];
        if (args.length > 1)
            rules = args[1];
        StyleRetriever aa;
        aa = new StyleRetriever(rules);
        aa.setAllFiles(prefix);
        aa.parse();
        aa.dump();
        aa.flushStreams();
    }
    
    public StyleRetriever(String file_name)  throws RtfException
    {
        super(file_name);
    }
    
    protected void setRules (RuleConfig cfg)  throws RtfException
    {
        cfg.setOption("debugging", "false");
        cfg.setOption("tracing", "false");
        cfg.setOption("skip-tracing", "false");
        cfg.setOption("flush-tracing", "false");
        cfg.setOption("debug-rtf-tables", "false");
        cfg.setOption("auto-pictures", "false");
        super.setRules(cfg);
    }
    
    public void setAllFiles (String prefix)  throws RtfException
    {
        setInputFile (prefix+".rtf");
        setXmlFile (prefix+".rtv");
        setDebugFile (null);
        setZipFile (null);
    }
    
    public void endParse()  throws RtfException
    {
        //props_stack_top = 0;
        //cont_stack_top = 1;
        super.endParse();
    }
    
    public void beginBody()  throws RtfException
    {
        super.beginBody();
        //setFinishParse();
    }
    
    public void processBody (RtfToken tok)  throws RtfException
    {
        switch (tok.tag)
        {
            case Tag._pard:
                getStylesheet().getParaStyle(0).usage_count++;
                break;
            case Tag._s:
                getStylesheet().getParaStyle(tok.val).usage_count++;
                break;
            case Tag._cs:
                getStylesheet().getCharStyle(tok.val).usage_count++;
                break;
            case Tag._bkmkstart:
            case Tag._bkmkend:
            case Tag._field:
            case Tag._pntext:
            case Tag._listtext:
            case Tag._pn:
            case Tag._shp:
            case Tag._shpgrp:
            case Tag._object:
            case Tag._shppict:
            case Tag._nonshppict:
            case Tag._pict:
            case Tag._header:
            case Tag._footer:
            case Tag._footnote:
            case Tag._background:
                skipGroup();
                break;
            default:
                defToken(tok);
                break;
        }
    }
    
    public void dump()  throws RtfException
    {
        try {
            dumpConfig(getXml(), getStylesheet());
        } catch (SAXException e) {
            throw new RtfException(e);
        }
    }
    
    private void dumpConfig (XmlWriter out, StyleSheet stylesheet)
    throws SAXException, RtfException
    {
        boolean dump_all_styles = false;
        out.startDocument();
        out.startElement("rules");
        out.startElement("info");
        out.charElement("title", stylesheet.getInfoProps().title);
        stylesheet.getInfoProps().dump(out);
        out.endElement("info");
        stylesheet.dumpStyles(out, dump_all_styles);
        out.startElement("config");
        out.startElement("options");
        dumpOption(out, "encoding", getRules().getOption("encoding"));
        out.endElement("options");
        out.startElement("style-aliases");
        for (int i=0; i<stylesheet.paraStyleNum(); i++)  {
            TextProps style = stylesheet.getParaStyle(i);
            if (style.no < 0 || "".equals(style.name))
                continue;
            if (style.name.equals("Normal"))
                continue;
            if (style.name.startsWith("heading "))
                continue;
            dumpAlias(out, style.name, Util.canonicName(style.name));
        }
        for (int i=0; i<stylesheet.charStyleNum(); i++)  {
            TextProps style = stylesheet.getCharStyle(i);
            if (style.no < 0 || "".equals(style.name))
                continue;
            if (style.usage_count >= 0 && !dump_all_styles)
                continue;
            dumpAlias(out, style.name, Util.canonicName(style.name));
        }
        dumpAlias(out, "Normal", "s-para");
        dumpAlias(out, "heading 1", "d-head1");
        dumpAlias(out, "heading 2", "d-head2");
        dumpAlias(out, "heading 3", "d-head3");
        dumpAlias(out, "heading 4", "d-head4");
        dumpAlias(out, "heading 5", "d-head5");
        dumpAlias(out, "heading 6", "d-head6");
        dumpAlias(out, "heading 7", "d-head7");
        dumpAlias(out, "heading 8", "d-head8");
        dumpAlias(out, "heading 9", "d-head9");
        out.endElement("style-aliases");
        out.startElement("subst-rules");
        dumpSubstRule(out, "p-center", "para", "centered");
        out.endElement("subst-rules");
        out.startElement("list-rules");
        dumpListRule(out, "l-bullet", 1, "itemizedlist", "l-bullet", "listitem", "simpara", null, null, null, "no");
        out.endElement("list-rules");
        out.startElement("div-rules");
        dumpDivRule(out, "d-head1", 1, "sect1", null, "num");
        dumpDivRule(out, "d-head2", 2, "sect2", null, "num");
        dumpDivRule(out, "d-head3", 3, "sect3", null, "num");
        dumpDivRule(out, "d-head4", 4, "sect4", null, "num");
        dumpDivRule(out, "d-head5", 5, "sect5", null, "num");
        dumpDivRule(out, "d-head6", 6, "sect", "sect6", "num");
        dumpDivRule(out, "d-head7", 7, "sect", "sect7", "num");
        dumpDivRule(out, "d-head8", 8, "sect", "sect8", "num");
        out.endElement("div-rules");
        out.endElement("config");
        out.endElement("rules");
        out.endDocument();
    }
    
    private void dumpAlias (XmlWriter out, String alias, String name)
    throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "alias", alias);
        out.emptyElement("style", atts);
    }
    
    private void dumpSubstRule(XmlWriter out, String name,
                               String subst_tag, String subst_role)
    throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "tag", subst_tag);
        if (subst_role != null)
            out.addAttr (atts, "role", subst_role);
        out.emptyElement("subst", atts);
    }
    
    private void dumpListRule (XmlWriter out, String name, int list_level,
                               String list_parent_tag, String list_parent_role,
                               String list_child_tag, String list_child_role,
                               String list_item_tag, String list_item_role,
                               String list_break_tag, String list_num)
    throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "level", list_level);
        if (list_parent_tag != null)
            out.addAttr (atts, "parent", list_parent_tag);
        if (list_parent_role != null)
            out.addAttr (atts, "parent-role", list_parent_role);
        if (list_child_tag != null)
            out.addAttr (atts, "child", list_child_tag);
        if (list_child_role != null)
            out.addAttr (atts, "child-role", list_child_role);
        if (list_child_tag != null)
            out.addAttr (atts, "item", list_item_tag);
        if (list_child_role != null)
            out.addAttr (atts, "item-role", list_item_role);
        if (list_break_tag != null)
            out.addAttr (atts, "break", list_break_tag);
        out.addAttr (atts, "num", list_num);
        out.emptyElement("list", atts);
    }

    private void dumpDivRule (XmlWriter out, String name, int div_level,
                              String div_tag, String div_role,
                              String div_num)
    throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "level", div_level);
        if (div_tag != null)
            out.addAttr (atts, "tag", div_tag);
        if (div_role != null)
            out.addAttr(atts, "role", div_role);
        if (div_num != null)
            out.addAttr (atts, "num", div_num);
        out.emptyElement("div", atts);
    }
    
    private void dumpOption (XmlWriter out, String name, String value)
    throws SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "value", value);
        out.emptyElement("option", atts);
    }
    
}