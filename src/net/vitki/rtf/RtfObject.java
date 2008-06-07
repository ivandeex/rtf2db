package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RtfObject
{
    public RtfObject()
    {
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        out.emptyElement("you_should_never_encounter_me");
    }
    
    public String getString()
    {
        return "";
    }
}
