package net.vitki.rtf;

import net.vitki.wmf.Converter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Properties;

public class RtfImage extends RtfContainer
{
    public static final int GC_PERIOD = 32;
    public static int transform_count = 0;
    
    public RtfImage()
    {
        super();
    }
    
    public RtfPicture getPicture (int no)
    {
        return (RtfPicture)get(no);
    }
    
    public boolean needsAutoPicture()
    {
        for (int i=0; i<size(); i++)
            if ("png".equals(getPicture(i).type))
                return false;
        return true;
    }
    
    public void clearRawData (DocAnalyser analyser)
    {
        int len = 0;
        for (int i=0; i<size(); i++)  {
            RtfPicture pict = getPicture(i);
            if (pict.data != null)  {
                if (pict.data.size() > len)
                    len = pict.data.size();
                pict.data = null;
            }
        }
        analyser.garbageCollect(len);
    }
    
    public void makeAutoPicture (DocAnalyser analyser)  throws RtfException
    {
        if (!needsAutoPicture())  {
            clearRawData(analyser);
            return;
        }
        int len = 0;
        int wmf_no = -1;
        int emf_no = -1;
        int jpg_no = -1;
        RtfPicture pict = null;
        RtfPicture auto = null;
        ByteArrayInputStream bais;
        ByteArrayOutputStream baos;
        for (int i=0; i<size(); i++)  {
            pict = getPicture(i);
            if (pict.data == null)
                continue;
            if ("wmf".equals(pict.type))
                wmf_no = i;
            else if ("emf".equals(pict.type))
                emf_no = i;
            else if ("jpg".equals(pict.type))
                jpg_no = i;
        }
        if (jpg_no != -1)  {
            try {
                wmf_no = -1;
                pict = getPicture(jpg_no);
                if (pict.data.size() > len)
                    len = pict.data.size();
                bais = new ByteArrayInputStream(pict.data.toByteArray());
                BufferedImage image = ImageIO.read(bais);
                bais = null;
                pict.data = null;
                baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                if (baos.size() > len)
                    len = baos.size();
                String name = analyser.writeToZip("png", "a", baos);
                baos = null;
                auto = new RtfPicture(name, "png", pict.width, pict.height, null);
                add(auto);
            } catch (IOException e) {
                throw new RtfException(e);
            }
        }
        if (wmf_no == -1 && emf_no != -1)  {
            wmf_no = emf_no;
            emf_no = -1;
        }
        if (wmf_no != -1)  {
            try {
                pict = getPicture(wmf_no);
                if (pict.data.size() > len)
                    len = pict.data.size();
                bais = new ByteArrayInputStream(pict.data.toByteArray());
                pict.data = null;
                baos = new ByteArrayOutputStream();
                Properties props = Converter.getDefaultProperties();
                int ppi = analyser.getPPI();
                props.setProperty ("width", ""+Util.mmd2px(pict.width, ppi));
                props.setProperty ("height", ""+Util.mmd2px(pict.width, ppi));
                Converter.convert(bais, baos, props);
                bais = null;
                String name = analyser.writeToZip("png", "a", baos);
                baos = null;
                auto = new RtfPicture(name, "png", pict.width, pict.height, null);
                add(auto);
            } catch (IOException e) {
                throw new RtfException(e);
            }
        }
        analyser.garbageCollect(len);
        clearRawData(analyser);
        if (transform_count++ % GC_PERIOD == 0)
            analyser.garbageCollect(-1);
    }
    
    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        out.startElement("inlinemediaobject");
        super.dump(out, ctx);
        out.endElement("inlinemediaobject");
    }

}
