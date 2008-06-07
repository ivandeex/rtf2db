package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.Vector;

public class RtfRow extends RtfContainer
{
    private int[] widths;
    private boolean is_header;
    
    public RtfRow()
    {
        super();
        widths = null;
        is_header = false;
    }
    
    public void setCellLimits (Vector limits)
    {
        widths = new int[limits.size()];
        int pos = 0;
        for (int i=0; i<limits.size(); i++) {
            int cur = ((Integer)limits.get(i)).intValue();
            widths[i] = cur - pos;
            pos = cur;
        }
    }
    
    public boolean geometryEquals (RtfRow row)
    {
        if (widths == null)
            return row.widths == null;
        if (widths.length != row.widths.length)
            return false;
        for (int i=0; i<widths.length; i++)
            if (widths[i] != row.widths[i])
                return false;
        return true;
    }
    
    public void dumpGeometry (XmlWriter out)  throws SAXException
    {
        if (widths == null)
            return;
        for (int i=0; i<widths.length; i++)
            out.attrElement ("colspec", "colwidth", Util.mmd2mm(widths[i]));
    }
    
    public boolean isHeader()  { return is_header; }
    public void setHeader (boolean flag)  { is_header = flag; }

    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        ctx.endLists();
        ctx.startRow(this);
        out.startElement("row");
        super.dump(out, ctx);
        out.endElement("row");
        ctx.endRow(this);
    }

}