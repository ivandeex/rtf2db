package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RtfCell extends RtfContainer
{
    public RtfCell()
    {
        super();
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        Attributes atts = out.newAttr();
        int ntotal = 0;
        int ncenter = 0;
        int nright = 0;
        int njustify = 0;
        int nmax = 0;
        for (int i=0; i<size(); i++)  {
            RtfObject obj = get(i);
            if (!(obj instanceof RtfPara))
                continue;
            ntotal++;
            int al = ((RtfPara)obj).getProps().align;
            switch(al)  {
                case TextProps.ALIGN_CENTER:
                    if (++ncenter > nmax)
                        nmax = ncenter;
                    break;
                case TextProps.ALIGN_RIGHT:
                    if (++nright > nmax)
                        nmax = nright;
                    break;
                case TextProps.ALIGN_JUSTIFY:
                    if (++njustify > nmax)
                        nmax = njustify;
                    break;
            }
        }
        String align = null;
        if (nmax > ntotal/2) {
            if (nmax == ncenter)
                align = "center";
            else if (nmax == nright)
                align = "right";
            else if (nmax == njustify)
                align = "justify";
        }
        if (align != null)
            out.addAttr (atts, "align", align);
        out.startElement("entry", atts);
        super.dump(out, ctx);
        ctx.endLists();
        out.endElement("entry");
    }

}