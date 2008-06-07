package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.regex.Pattern;

public class DumpHelper
{
    private XmlWriter        out;
    private DocAnalyser      analyser;
    private StyleSheet       stylesheet;
    private RuleConfig       rules;
    private StyleTransform[] para_trans;
    private StyleTransform[] char_trans;
    
    public DumpHelper (DocAnalyser aa)  throws RtfException
    {
        analyser =  aa;
        out = analyser.getXml();
        rules = analyser.getRules();
        stylesheet = analyser.getStylesheet();
        emphasis_stack = new String[32];
        emphasis_stack_top = 0;
        in_row = false;
        cur_row = null;
        list_stack = new StyleTransform[32];
        list_stack_top = 0;
        div_stack = new StyleTransform[32];
        div_stack_top = 0;
        createTransformations();
        debug_lists = rules.getFlag("debug-lists");
        debug_styles = rules.getFlag("debug-styles");
        auto_lists = rules.getFlag("auto-lists");
        guess_div_labels = rules.getFlag("guess-div-labels");
        div_label_pattern = Pattern.compile(rules.getOption("div-label-pattern"));
        dump_all_fields = rules.getFlag("dump-all-fields");
        trace_fonts = rules.getFlag("trace-fonts");
        dump_shapes = rules.getFlag("dump-shapes");
        draw_shapes = rules.getFlag("draw-shapes");
        plain_output = rules.getFlag("plain-output");
    }
    
    boolean debug_lists;
    boolean debug_styles;
    boolean auto_lists;
    boolean dump_all_fields;
    boolean guess_div_labels;
    boolean trace_fonts;
    boolean dump_shapes;
    boolean draw_shapes;
    boolean plain_output;
    
    private Pattern div_label_pattern;
    
    private void createTransformations()  throws RtfException
    {
        TextProps style;
        StyleTransform st;
        ListRule list;
        DivRule div;
        SubstRule subst;
        char_trans = new StyleTransform[stylesheet.charStyleNum()];
        for (int i=0; i<stylesheet.charStyleNum(); i++)
        {
            style = stylesheet.getCharStyle(i);
            if (style.no < 0)
                continue;
            st = new StyleTransform();
            st.name = style.name;
            st.canonic_name = rules.getName(st.name);
            if (st.canonic_name == null)
                st.canonic_name = Util.canonicName(st.name);
            subst = rules.getSubstRule(st.canonic_name);
            if (subst != null)
                st.copyFrom(subst);
            char_trans[i] = st;
        }
        para_trans = new StyleTransform[stylesheet.paraStyleNum()];
        for (int i=0; i<stylesheet.paraStyleNum(); i++)
        {
            style = stylesheet.getParaStyle(i);
            if (style.no < 0)
                continue;
            st = new StyleTransform();
            st.name = style.name;
            st.canonic_name = rules.getName(st.name);
            if (st.canonic_name == null)
                st.canonic_name = Util.canonicName(st.name);
            subst = rules.getSubstRule(st.canonic_name);
            if (subst != null)
                st.copyFrom(subst);
            list = rules.getListRule(st.canonic_name);
            if (list != null)  {
                st.copyFrom(list);
                st.list_no = i;
            }
            div = rules.getDivRule(st.canonic_name);
            if (div != null)
                st.copyFrom(div);
            para_trans[i] = st;
        }
    }
    
    public XmlWriter getXml()  { return out; }
    public DocAnalyser getAnalyser()  { return analyser; }
    public String getOption (String name)  { return rules.getOption(name); }
    public boolean getFlag (String name)  { return rules.getFlag(name); }
    public StyleSheet getStylesheet()  { return stylesheet; }
        
    /* === emphasis === */
    
    private String[] emphasis_stack;
    private int emphasis_stack_top;
    
    public void startEmphasis (String tag, String role)  throws SAXException
    {
        if (tag == null)
            tag = "emphasis";
        if (role == null)
            out.startElement(tag);
        else
            out.startElement(tag, "role", role);
        emphasis_stack[emphasis_stack_top++] = tag;
    }
    
    public void endEmphasis()  throws SAXException
    {
        for (int i=emphasis_stack_top-1; i>=0; i--)
            out.endElement(emphasis_stack[i]);
        emphasis_stack_top = 0;
    }
    
    /* === tables === */
    
    private boolean in_row;
    private RtfRow  cur_row;
    
    public void startRow (RtfRow row)  throws SAXException, RtfException
    {
        if (plain_output)
            return;
        if (in_row)
            throw new RtfException("rows cannot nest");
        in_row = true;
        if (cur_row == null) {
            out.startElement("informaltable", "frame", "all");
            out.startElement("tgroup");
            row.dumpGeometry(out);
            out.startElement(row.isHeader() ? "thead" : "tbody");
        } else if (row.isHeader() != cur_row.isHeader()) {
            if (row.isHeader())  {
                out.endElement("tbody");
                out.endElement("tgroup");
                out.startElement("tgroup");
                row.dumpGeometry(out);
                out.startElement("thead");
            } else {
                out.endElement("thead");
                out.startElement("tbody");
            }
        } else if (!row.geometryEquals(cur_row)) {
            out.endElement(cur_row.isHeader() ? "thead" : "tbody");
            out.endElement("tgroup");
            out.startElement("tgroup");
            row.dumpGeometry(out);
            out.startElement(row.isHeader() ? "thead" : "tbody");
        }
        cur_row = row;
    }
    
    public void endRow (RtfRow row)  throws SAXException
    {
        if (plain_output)
            return;
        in_row = false;
        cur_row = row;
    }
    
    public void endTable ()  throws SAXException
    {
        if (plain_output)
            return;
        if (in_row)
            return;
        if (cur_row == null)
            return;
        out.endElement(cur_row.isHeader() ? "thead" : "tbody");
        out.endElement("tgroup");
        out.endElement("informaltable");
        cur_row = null;
    }
    
    /* === lists === */
    
    private StyleTransform[] list_stack;
    private int list_stack_top;
    
    private void startListParent (StyleTransform st)
    throws SAXException
    {
        st.list_children = 0;
        if (st.list_parent_tag == null)
            return;
        Attributes atts = out.newAttr();
        if (st.list_parent_role != null)
            out.addAttr(atts, "role", st.list_parent_role);
        if (st.list_parent_role == null && st.list_auto)
            out.addAttr(atts, "role", "auto");
        //out.addAttr(atts, "spacing", "compact");
        String type = LevelProps.getXmlNumbering(st.list_type);
        if (type != null)  {
            if (st.list_ordered)
                out.addAttr(atts, "numeration", type);
            else
                out.addAttr(atts, "mark", type);
        }
        out.startElement(st.list_parent_tag, atts);
    }
    
    private void startListChild (StyleTransform st, String bullet)
    throws SAXException
    {
        if (st.list_child_tag == null)
            return;
        Attributes atts = out.newAttr();
        if (st.list_child_role != null)
            out.addAttr (atts, "role", st.list_child_role);
        if (st.list_ordered && bullet != null)
            out.addAttr (atts, "override", bullet);
        out.startElement (st.list_child_tag, atts);
    }
    
    private void endListChild (StyleTransform st)
    throws SAXException
    {
        if (st.list_child_tag != null)
            out.endElement (st.list_child_tag);
        st.list_children++;
    }
    
    private void endListParent (StyleTransform st)
    throws SAXException
    {
        if (st.list_parent_tag != null)
            out.endElement (st.list_parent_tag);
        st.list_children = 0;
    }
    
    private void putListBreak (StyleTransform st)
    throws SAXException
    {
        if (st.list_children == 0)
            return;
        if (st.list_child_tag == null && st.list_break_tag == null
            && st.list_item_tag == null)
            out.characters("\n");
        if (st.list_break_tag != null)
            out.emptyElement(st.list_break_tag);
    }
    
    private void startList (StyleTransform st, String bullet)
    throws RtfException, SAXException
    {
        while (list_stack_top > 0)  {
            StyleTransform cur = list_stack[list_stack_top-1];
            if (cur.list_level <= st.list_level)
                break;
            endListChild(cur);
            endListParent(cur);
            list_stack_top--;
        }
        if (list_stack_top > 0)  {
            StyleTransform cur = list_stack[list_stack_top-1];
            if (cur.list_level == st.list_level && cur.list_no == st.list_no)  {
                endListChild(cur);
                putListBreak(cur);
                startListChild(st,bullet);
                return;
            }
            if (cur.list_level == st.list_level && cur.list_no != st.list_no)  {
                endListChild(cur);
                endListParent(cur);
                list_stack_top--;
            }
        }
        startListParent(st);
        startListChild(st, bullet);
        list_stack[list_stack_top++] = st;
    }
    
    public void endLists()  throws SAXException
    {
        while (list_stack_top > 0)  {
            StyleTransform cur = list_stack[list_stack_top-1];
            endListChild(cur);
            endListParent(cur);
            list_stack_top--;
        }
    }
    
    public StyleTransform checkList (RtfPara para)
    throws RtfException, SAXException
    {
        if (plain_output)
            return null;
        ParaProps props = para.getProps();
        StyleTransform st;
        if (props.no != -1)
        {
            st = para_trans[props.no];
            if (st != null && st.list_level != 0)
            {
                startList(st, props.bullet);
                return st;
            }
        }
        if (auto_lists && props.list_no != -1)
        {
            if (props.list_level == -1)
                props.list_level = 0;
            LevelProps lp = stylesheet.getLevel(props.list_no, props.list_level);
            if (props.list_type == -1)
                props.list_type = lp.numbering;
            st = new StyleTransform();
            st.list_no = props.list_no;
            st.list_level = props.list_level + 1;
            st.list_type = props.list_type;
            st.list_ordered = LevelProps.isOrdered(st.list_type);
            if (st.list_ordered)
                st.list_parent_tag = "orderedlist";
            else
                st.list_parent_tag = "itemizedlist";
            st.list_auto = true;
            st.list_child_tag = "listitem";
            st.list_item_tag = "simpara";
            startList(st, props.bullet);
            return st;
        }
        return null;
    }

    public boolean isList (RtfPara para) throws RtfException
    {
        if (plain_output)
            return false;
        ParaProps props = para.getProps();
        StyleTransform st;
        if (props.no != -1)
        {
            st = para_trans[props.no];
            if (st != null && st.list_level != 0)
                return true;
        }
        if (auto_lists && props.list_no != -1)
            return true;
        return false;
    }
    
    /* === character substitutions === */
    
    public StyleTransform getCharTransform (TextProps props)
    {
        if (plain_output)
            return null;
        int no = props.no;
        if (no >= 0 && no < char_trans.length && char_trans[no] != null)
            return char_trans[no];
        return null;
    }

    public StyleTransform getParaSubst (ParaProps props)
    {
        if (plain_output)
            return null;
        int no = props.no;
        if (no >= 0 && no < para_trans.length && para_trans[no] != null
            && para_trans[no].subst_tag != null)
            return para_trans[no];
        return null;
    }

    /* === divisions === */
    
    private StyleTransform[] div_stack;
    private int div_stack_top;
    
    private void startDivision (StyleTransform st, RtfPara para)
    throws RtfException, SAXException
    {
        endDivisions(st.div_level);
        if (para.getProps().page_break)  {
            out.emptyElement("beginpage");
            para.getProps().page_break = false;
        }
        Attributes atts = out.newAttr();
        if (st.div_role != null)
            out.addAttr (atts, "role", st.div_role);
        String label = para.getProps().bullet;
        if (label == null && guess_div_labels)
            label = para.guessDivLabel(div_label_pattern);
        if (label != null)
            out.addAttr (atts, "label", label);
        out.startElement (st.div_tag, atts);
        incrementParaCount();
        div_stack[div_stack_top++] = st;
        st.div_para_count = 0;
    }
    
    public void endDivisions (int level)  throws SAXException
    {
        endLists();
        endTable();
        while (div_stack_top > 0)  {
            StyleTransform cur = div_stack[div_stack_top-1];
            if (level != -1 && cur.div_level < level)
                break;
            if (cur.div_para_count <= 1)
                out.attrElement ("para", "role", "filler");
            //out.emptyElement("div_para_count_"+cur.div_para_count);
            out.endElement(cur.div_tag);
            div_stack_top--;
        }
    }
    
    public StyleTransform checkDivision (RtfPara para)
    throws RtfException, SAXException
    {
        if (plain_output)
            return null;
        ParaProps props = para.getProps();
        if (in_row)
            return null;
        if (props.no != -1)     {
            StyleTransform st = para_trans[props.no];
            if (st != null && st.div_level != 0)   {
                startDivision(st, para);
                return st;
            }
        }
        return null;
    }

    public boolean isDivision (RtfPara para) throws RtfException
    {
        if (plain_output)
            return false;
        ParaProps props = para.getProps();
        if (in_row)
            return false;
        if (props.no != -1)     {
            StyleTransform st = para_trans[props.no];
            if (st != null && st.div_level != 0)
                return true;
        }
        return false;
    }
    
    public void incrementParaCount()
    {
        if (div_stack_top > 0)
            div_stack[div_stack_top-1].div_para_count++;
    }
    
}

