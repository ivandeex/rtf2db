package net.vitki.rtf;

import net.vitki.charset.Encoding;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.Vector;
import java.io.PrintWriter;

public class RtfField extends RtfObject
{
    private String text;
    private String result;
    private String name;
    private Vector values;
    private Vector flags;

    public RtfField(String str)
    {
        super();
        values = new Vector();
        flags = new Vector();
        result = null;
        name = "";
        text = "";
        setText(str);
    }
    
    void setResult (String str)   {
        result = str.trim();
    }
    
    public String getString()   {
        return text;
    }
    
    void setText (String str)   {
        str = str.trim();
        text = str;
        int pos = str.indexOf("\\*");
        if (pos >= 0)
            str = str.substring(0, pos).trim();
        parse(str);
    }
    
    public int size()  {
        return values.size();
    }
    
    public String getName()  {
        return name;
    }
    
    public String getValue (int no)  {
        return (String)values.get(no);
    }
    
    public boolean isCommand (int no)  {
        return ((Boolean)flags.get(no)).booleanValue();
    }
    
    public boolean hasCommand (String cmd)  {
        for (int i=0; i<size(); i++)  {
            if (isCommand(i) && getValue(i).equals(cmd))
                return true;
        }
        return false;
    }
    
    public String getOption (String cmd)  {
        for (int i=0; i<size(); i++)  {
            if (isCommand(i) && getValue(i).equals(cmd))  {
                if (i+1 < size() && !isCommand(i+1))
                    return getValue(i+1);
                else
                    return "";
            }
        }
        return null;
    }
    
    public String getFirstString()  {
        for (int i=0; i<size(); i++)  {
            if (!isCommand(i))
                return getValue(i);
        }
        return null;
    }
    
    public boolean isSkipResult()  {
        return "toc".equals(name);
    }
    
    public boolean isEmbedResult()  {
        return "ref".equals(name);
    }
    
    private void parse (String str)
    {
        char ca[] = str.trim().toCharArray();
        values.clear();
        flags.clear();

        StringBuffer str_buf = new StringBuffer();
        StringBuffer cmd_buf = new StringBuffer();
        boolean in_str = false;
        boolean in_cmd = false;
        
        for (int i = 0; i < ca.length; i++)
        {
            char ch = ca[i];
            if (in_str) {
                if (ch == '"')  {
                    in_str = false;
                    values.add(str_buf.toString());
                    flags.add(new Boolean(false));
                    str_buf.setLength(0);
                } else {
                    str_buf.append(ch);
                }
            } else if (in_cmd) {
                if (Character.isSpaceChar(ch) || ch == '"') {
                    values.add(cmd_buf.toString());
                    flags.add(new Boolean(true));
                    cmd_buf.setLength(0);
                    in_cmd = false;
                    if (ch == '"')  {
                        in_str = true;
                        //str_buf.append(ch);
                    }
                } else {
                    cmd_buf.append(ch);
                }
            } else {
                if (Character.isSpaceChar(ch)) {
                    // nothing to do
                    if (str_buf.length() > 0)  {
                        values.add(str_buf.toString());
                        flags.add(new Boolean(false));
                        str_buf.setLength(0);
                    }
                } else if (ch == '"') {
                    in_str = true;
                    //str_buf.append(ch);
                    str_buf.setLength(0);
                } else if (ch == '\\') {
                    //buf_cmd.append(ch);
                    in_cmd = true;
                } else {
                    // should not happen unless the user typed the
                    // hyperlink target manually in the field
                    str_buf.append(ch);
                }
            }
        }
        if (in_cmd)  {
            values.add(cmd_buf.toString());
            flags.add(new Boolean(true));
        }
        if (in_str || str_buf.length() > 0)  {
            values.add(str_buf.toString());
            flags.add(new Boolean(false));
        }
        name = "";
        if (size() > 0 && !isCommand(0))  {
            name = getValue(0).trim().toLowerCase();
            values.remove(0);
            flags.remove(0);
        }
    }

    public void print (PrintWriter out)
    {
        out.println("<field name=["+name+"] text=["+text+"]>");
        if (result != null)
            out.println ("  <result ["+result+"]/>");
        for (int i=0; i<size(); i++)  {
            out.println ("  <option value=["+getValue(i)+"] "
                         +" command="+(isCommand(i) ? "yes" : "no")+"/>");
        }
        out.println("</field>");
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        if ("ref".equals(name))
            dumpRef (out, ctx);
        else if ("symbol".equals(name))
            dumpSymbol (out, ctx);
        else if ("includepicture".equals(name))
            dumpIncludePicture (out, ctx);
        else if ("hyperlink".equals(name))
            dumpHyperlink (out, ctx);
        else if ("toc".equals(name))
            dumpToc (out, ctx);
        else if (ctx.dump_all_fields)
            dumpField (out, ctx, "rtf-field");
    }    
    
    public void dumpField (XmlWriter out, DumpHelper ctx, String tag)
    throws RtfException, SAXException
    {
        Attributes atts = out.newAttr();
        out.addAttr (atts, "name", name);
        out.addAttr (atts, "text", text);
        if (result != null)
            out.addAttr (atts, "result", result);
        out.startElement (tag, atts);
        for (int i=0; i<size(); i++)  {
            atts = out.newAttr();
            out.addAttr (atts, "value", getValue(i));
            out.addAttr (atts, "command", isCommand(i) ? "yes" : "no");
            out.emptyElement ("rtf-field-option", atts);
        }
        out.endElement (tag);
    }
    
    public void dumpToc (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        if (ctx.dump_all_fields)
            dumpField (out, ctx, "rtf-field-toc");
    }

    public void dumpHyperlink (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        String data = getFirstString();
        if (data == null)  {
            if (ctx.dump_all_fields)
                dumpField (out, ctx, "rtf-field-hyperlink");
            return;
        }
        String prefix = hasCommand("l") ? "#" : "";
        Attributes atts = out.newAttr();
        out.addAttr (atts, "role", "hyperlink");
        out.addAttr (atts, "url", prefix + data);
        out.startElement ("ulink", atts);
        out.characters(data);
        out.endElement ("ulink");
    }

    public void dumpIncludePicture (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        String data = getFirstString();
        if (data == null)  {
            if (ctx.dump_all_fields)
                dumpField (out, ctx, "rtf-field-includepicture");
            return;
        }
        out.startElement("inlinemediaobject");
        out.startElement("imageobject");
        Attributes atts = out.newAttr();
        out.setAttr (atts, "role", "includepicture");
        out.setAttr (atts, "fileref", data);
        out.emptyElement ("imagedata", atts);
        out.endElement("imageobject");
        out.endElement("inlinemediaobject");
    }

    public void dumpSymbol (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        String code = getFirstString();
        if (code == null)  {
            if (ctx.dump_all_fields)
                dumpField (out, ctx, "rtf-field-symbol");
            return;
        }
        int charset = Encoding.CHARSET_SYMBOL;
        String font = getOption("f");
        String size = getOption("s");
        StringBuffer buf = new StringBuffer();
        buf.append("symbol:true");
        if (font != null && !"".equals(font))  {
            buf.append(";");
            buf.append ("font:");
            buf.append (font);
            if (Encoding.getRequiredCodepage(font) != 0)
                charset = Encoding.getRequiredCodepage(font);
        }
        if (size != null && !"".equals(size))  {
            buf.append(";");
            buf.append ("size:");
            buf.append (size);
        }
        out.startElement("emphasis", "role", buf.toString());
        int val = Integer.parseInt(code);
        String str = "" + Encoding.decode(charset, val);
        out.characters(str);
        out.endElement("emphasis");
    }

    public void dumpRef (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        String id = getFirstString();
        if (id == null)  {
            if (ctx.dump_all_fields)
                dumpField (out, ctx, "rtf-field-ref");
            return;
        }
        if (!ctx.getAnalyser().isBookmark(id))  {
            if (ctx.dump_all_fields)
                dumpField (out, ctx, "rtf-field-ref-broken");
            else {
                out.startElement("emphasis", "role", "broken-ref");
                if (result != null)
                    out.characters(result);
                out.endElement("emphasis");
            }
            return;
        }
        Attributes atts = out.newAttr();
        out.addAttr (atts, "role", "ref");
        out.addAttr (atts, "linkend", id);
        out.startElement ("link", atts);
        if (result != null)
            out.characters(result);
        out.endElement ("link");
    }

}
