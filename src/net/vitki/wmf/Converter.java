package net.vitki.wmf;

import java.util.Properties;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.vitki.wmf.render.ImageIoRenderer;
import net.vitki.wmf.wmeta.*;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class Converter implements ContentHandler {

    /** static conversions **/

    public static void convert (InputStream istream, OutputStream ostream, Properties props)
        throws IOException
    {
        RecordReader rd = new RecordReader (istream, props);
        RecordTable table = rd.readMetaFile();
        ImageIoRenderer iir = new ImageIoRenderer (props, table);
        iir.render (table);
        iir.write (ostream);
    }

    public static Properties getDefaultProperties()
    {
        Properties def_props = new Properties();
        def_props.setProperty ("dib", "white");
        def_props.setProperty ("debug", "0");
        def_props.setProperty ("timing", "0");
        def_props.setProperty ("logging", "0");
        def_props.setProperty ("mat", "#ffffff");
        def_props.setProperty ("white", "#ffffff");
        def_props.setProperty ("prefix", "");
        def_props.setProperty ("width", "800");
        def_props.setProperty ("height", "600");
        def_props.setProperty ("scale", "best");
        def_props.setProperty ("format", "png");
        def_props.setProperty ("prohibit", "F_AbortDoc");
        def_props.setProperty ("use-query-string", "1");
        def_props.setProperty ("text-antialias", "true");
        def_props.setProperty ("default-charset", "cp1250");
        return def_props;
    }

    /** SAX interface for Word Drawing Objects**/

    public void startElement (String ns, String name, String raw, Attributes atts) {
        if (name.equals("shape"))
            setShapeProps (atts);
        else if (name.equals("polygon"))
            drawPolygon (atts);
        else
            return;
    }

    public void startPrefixMapping (String prefix, String uri) {}
    public void endPrefixMapping (String prefix) {}

    public void setDocumentLocator (Locator locator) {}
    public void startDocument () {}
    public void endDocument () {}
    public void endElement (String ns, String name, String raw) {}
    public void characters (char[] ch, int start, int length) {}
    public void ignorableWhitespace (char[] ch, int start, int length) {}
    public void processingInstruction (String target, String data) {}
    public void skippedEntity (String name) {}

    /** Attribute Utilities **/

    protected static final String URI = "";

    private static String getStr (Attributes atts, String name, String def)  {
        String sval = atts.getValue (URI, name);
        if (sval == null)    sval = def;
        return sval;
    }

    private static int getInt (Attributes atts, String name, int def)  {
        String sval = atts.getValue (URI, name);
        if (sval == null)    return def;
        try  {
            return Integer.decode(sval).intValue();
        } catch (Exception e)  {
            return def;
        }
    }

    /** Drawing Primitives **/

    private RecordTable table;

    private void drawPolygon (Attributes atts)  {
        WmfCreatePenIndirect cpi = new WmfCreatePenIndirect();
        cpi.style = Constants.PS_SOLID;
        cpi.width = getInt(atts,"penw",0);
        cpi.color = getInt(atts,"cline",0);
        table.add( cpi );
        WmfSelectObject wso = new WmfSelectObject();
        wso.obj_no = 0;
        table.add( wso );
        WmfCreateBrushIndirect cbi = new WmfCreateBrushIndirect();
        int bfillfg = getInt(atts,"cfillfg",0);
        int bfillbg = getInt(atts,"cfillbg",0);
        String fillpat = getStr(atts,"fillpat","solid");
        cbi.style = Constants.BS_SOLID;
        cbi.color = bfillbg;
        cbi.hatch = 0;
        table.add( cbi );
        wso = new WmfSelectObject();
        wso.obj_no = 1;
        table.add( wso );
        StringTokenizer st = new StringTokenizer( getStr(atts,"points","") );
        WmfPolygon p = new WmfPolygon();
        p.num = st.countTokens();
        p.x = new int[p.num];
        p.y = new int[p.num];
        for (int i=0; i<p.num; i++)  {
            p.x[i] = p.y[i] = 0;
            String xy = st.nextToken();
            int pos = xy.indexOf(';');
            if (pos == -1)  continue;
            try { p.x[i] = Integer.parseInt(xy.substring(0,pos)); } catch (Exception e) {}
            try { p.y[i] = Integer.parseInt(xy.substring(pos+1)); } catch (Exception e) {}
        }
        table.add( p );
        WmfDeleteObject wdo = new WmfDeleteObject();
        wdo.obj_no = 1;
        table.add( wdo );
        wdo = new WmfDeleteObject();
        wdo.obj_no = 0;
        table.add( wdo );
    }

    public void setShapeProps (Attributes atts)  {
        WmfSetWindowOrg swo = new WmfSetWindowOrg();
        swo.x = swo.y = 0;
        table.add( swo );
        WmfSetWindowExt swe = new WmfSetWindowExt();
        int v;
        v = getInt(atts,"left",0);
        swe.w = getInt(atts,"right",v+10) - v;
        v = getInt(atts,"top",0);
        swe.h = getInt(atts,"bottom",v+10) - v;
        table.add( swe );
    }

    public void render (OutputStream ostream, Properties props)
        throws IOException
    {
        ImageIoRenderer iir = new ImageIoRenderer (props, table);
        iir.render (table);
        iir.write (ostream);
    }

    public Converter (Properties props)  {
        table = new RecordTable();
        table.addWmfHeader( new WmfHeader() );
    }

    public static void main (String[] arg)  throws Exception {
        String name = arg[0];
        Properties props = getDefaultProperties();
        Converter cvt = new Converter (props);
        XMLReader parser;
        parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        //parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
        //parser.setFeature(VALIDATION_FEATURE_ID, validation);
        parser.setContentHandler(cvt);
        parser.parse(name);
        FileOutputStream fos = new FileOutputStream (name+".png");
        cvt.render (fos, props);
        fos.close();
    }

}
