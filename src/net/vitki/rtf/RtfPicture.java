package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.ByteArrayOutputStream;

public class RtfPicture extends RtfObject
{
    String name;
    String type;
    int    width;
    int    height;
    String oclass;
    String role;
    ByteArrayOutputStream data;
    
    public RtfPicture (String name, String type, int width, int height, String oclass)
    {
        super();
        this.name = name;
        this.type = type;
        this.width = width;
        this.height = height;
        this.oclass = oclass;
        this.data = null;
        this.role = null;
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        Attributes atts = out.newAttr();
        out.startElement("imageobject");
        out.addAttr (atts, "fileref", name);
        out.addAttr (atts, "format", type.toUpperCase());
        out.addAttr (atts, "width", Util.mmd2mm(width));
        out.addAttr (atts, "height", Util.mmd2mm(height));
        if (oclass != null)
            out.addAttr (atts, "srccredit", oclass);
        if (role != null)
            out.addAttr (atts, "role", role);
        out.emptyElement ("imagedata", atts);
        out.endElement("imageobject");
    }

}
