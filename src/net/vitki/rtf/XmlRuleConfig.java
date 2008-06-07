package net.vitki.rtf;

import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlRuleConfig extends RuleConfig
{
    public XmlRuleConfig()
    {
        super();
    }
    
    public XmlRuleConfig (String name)  throws RtfException
    {
        this();
        readFile (name);
    }
    
    public void readFile (String file_name)  throws RtfException
    {
        Document doc = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file_name);
            DocumentBuilderFactory factory;
            factory = DocumentBuilderFactory.newInstance();
            factory.setCoalescing(true);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            doc = builder.parse(fis);
        } catch (Exception e) {
            if (fis != null)  {
                try { fis.close(); } catch (Exception ee) {}
            }
            throw new RtfException(e);
        }
        if (fis != null)  {
            try { fis.close(); } catch (Exception ee) {}
        }
        Element[] ea;
        Element e;
        String name;
        int i;
        int level;
        ea = getSubtree(doc, "options", "option");
        for (i=0; i<ea.length; i++) {
            e = ea[i];
            name = getNullAttr(e,"name");
            if (name == null)
                throw new RtfException("option name not set");
            setOption(name, getAttr(e,"value"));
        }
        ea = getSubtree(doc, "style-aliases", "style");
        for (i=0; i<ea.length; i++) {
            e = ea[i];
            name = getNullAttr(e,"name");
            if (name == null || getNullAttr(e,"alias") == null)
                throw new RtfException("alias or name not set");
            setAlias (getNullAttr(e,"alias"), name);
        }
        ea = getSubtree(doc, "subst-rules", "subst");
        for (i=0; i<ea.length; i++) {
            e = ea[i];
            name = getNullAttr(e,"name");
            if (name == null)
                throw new RtfException("substitution name not set");
            setSubstRule (name, getAttr(e,"tag"), getAttr(e,"role"));
        }
        ea = getSubtree(doc, "list-rules", "list");
        for (i=0; i<ea.length; i++) {
            e = ea[i];
            name = getNullAttr(e,"name");
            if (name == null)
                throw new RtfException("list name not set");
            try {
                level = Integer.parseInt(getAttr(e,"level"));
            } catch (Exception ee) {
                level = 1;
            }
            setListRule (name, level,
                         getNullAttr(e,"parent"), getNullAttr(e,"parent-role"),
                         getNullAttr(e,"child"), getNullAttr(e,"child-role"),
                         getNullAttr(e,"item"), getNullAttr(e,"item-role"),
                         getNullAttr(e,"break"), getAttr(e,"num")
                         );
        }
        ea = getSubtree(doc, "div-rules", "div");
        for (i=0; i<ea.length; i++) {
            e = ea[i];
            name = getNullAttr(e,"name");
            if (name == null)
                throw new RtfException("division name not set");
            try {
                level = Integer.parseInt(getAttr(e,"level"));
            } catch (Exception ee) {
                level = 1;
            }
            setDivRule (name, level,
                        getNullAttr(e,"tag"), getNullAttr(e,"role"),
                        getAttr(e,"num")
                        );
        }
    }
    
    private static Element[] getSubtree (Document doc,
                                         String group_tag, String item_tag)
    throws RtfException
    {
        Element root = doc.getDocumentElement();
        NodeList group_nl = root.getElementsByTagName (group_tag);
        if (group_nl == null || group_nl.getLength() != 1)
            throw new RtfException("wrong configuration group ["+group_tag+"]");
        Element group = (Element)group_nl.item(0);
        NodeList item_nl = group.getElementsByTagName (item_tag);
        if (item_nl == null || item_nl.getLength() < 1)
            throw new RtfException("empty configuration group ["+group_tag+"]");
        Element[] elems = new Element[item_nl.getLength()];
        for (int i=0; i < elems.length; i++)
            elems[i] = (Element)item_nl.item(i);
        return elems;
    }
    
    private static String getAttr (Element elem, String name)
    {
        return elem.getAttribute(name);
    }

    private static String getNullAttr (Element elem, String name)
    {
        return Util.nullify(elem.getAttribute(name));
    }

}
