package net.vitki.rtf;

import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RtfContainer extends RtfObject
{
    private Vector items;
    
    public RtfContainer()
    {
        items = new Vector();
    }
    
    public RtfObject add (RtfObject obj)  throws RtfException
    {
        items.add(obj);
        return obj;
    }
    
    public int size()
    {
        return items.size();
    }
    
    public RtfObject get (int no)
    {
        return (RtfObject)items.get(no);
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        for (int i=0; i<size(); i++)
            get(i).dump(out, ctx);
    }
    
    public String getString()
    {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<size(); i++)
            sb.append( get(i).getString() );
        return sb.toString();
    }
}

