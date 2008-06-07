
package net.vitki.rtf;

import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.apache.xalan.templates.OutputProperties;

public class XmlWriter implements ContentHandler, ErrorHandler
{

    protected ContentHandler  out   = null;
    protected String handler_package = "";
    protected boolean quiet      = false;
    protected boolean exceptions = false;
    protected String  out_uri    = "";
    protected String  out_prefix = "";

    public static final int FATAL   = 4;
    public static final int ERROR   = 3;
    public static final int WARNING = 2;

    protected int     report_level = WARNING;
    protected int     throw_level  = ERROR;
    protected String  who = "SaxPipe";
    protected Locator cur_locator = null;

    protected static final String CDATA = "CDATA";

    /** setup **/

    public XmlWriter(ContentHandler handler)
    {
        out = handler;
        init();
    }

    public XmlWriter()
    {
        this(null);
    }

    public XmlWriter(OutputStream ostream, String encoding, int indent_spaces, String doctype_public, String doctype_system, String out_uri, String out_prefix) throws SAXException
    {
        this(null);
        setXmlOutput( ostream, encoding, indent_spaces, doctype_public, doctype_system );
        setOutputNamespace( out_uri, out_prefix );
    }

    protected void init()
    {
    }

    public void setXmlOutput (OutputStream ostream, String encoding, int indent_spaces)
        throws SAXException
    {
        setXmlOutput( ostream, encoding, indent_spaces, null, null );
    }

    public void setXmlOutput (OutputStream ostream, String encoding, int indent_spaces,
                              String doctype_public, String doctype_system)
        throws SAXException
    {
        Properties format = new Properties();
        if (doctype_public != null)    format.put (OutputKeys.DOCTYPE_PUBLIC, doctype_public);
        if (doctype_system != null)    format.put (OutputKeys.DOCTYPE_PUBLIC, doctype_system);
        if (encoding == null)
            encoding = "UTF-8";
        format.put (OutputKeys.ENCODING, encoding);
        format.put (OutputKeys.METHOD, "xml");
        if (indent_spaces < 0)   {
            format.put (OutputKeys.INDENT, "no");
        } else {
            format.put (OutputKeys.INDENT, "yes");
            format.put (OutputProperties.S_KEY_INDENT_AMOUNT, String.valueOf(indent_spaces));
        }
        SAXTransformerFactory factory;
        try {
            factory = new org.apache.xalan.processor.TransformerFactoryImpl();
        } catch (Exception e) {
            System.err.println("SaxPipe: xalan not present in classpath");
            factory = (SAXTransformerFactory)TransformerFactory.newInstance();
        }
        TransformerHandler thandler;
        try {
            thandler = factory.newTransformerHandler();
        } catch (TransformerConfigurationException tce) {
            throw new SAXException( tce.getMessage() );
        }
        if (ostream == null)    ostream = System.out;
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(ostream, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SAXException(e);
        }
        thandler.setResult( new StreamResult( osw ));
        thandler.getTransformer().setOutputProperties(format);
        addHandler (thandler);
    }

    public void setOutputNamespace (String uri, String prefix)
    {
        out_uri = uri == null ? "" : uri;
        if (prefix == null)
            out_prefix = "";
        else if ("".equals(prefix))
            out_prefix = "";
        else if (prefix.endsWith(":"))
            out_prefix = prefix;
        else
            out_prefix = prefix + ":";
    }

    public void addHandler (ContentHandler handler)
    {
        XmlWriter cur = this;
        while (cur.out != null && cur.out instanceof XmlWriter)
            cur = (XmlWriter)cur.out;
        cur.out = handler;
    }

    public void addPipe (XmlWriter pipe)
    {
        addHandler( (ContentHandler)pipe );
    }

    public void addPipe (String className)
        throws Exception
    {
        Object obj = Class.forName(className).newInstance();
        if (obj != null && obj instanceof XmlWriter)
            addPipe( (XmlWriter)obj );
        else
            throw new Exception ("wrong class "+className);
    }

    public void addPipes (String classes)
        throws Exception
    {
        StringTokenizer st = new StringTokenizer (classes, " \r\n\t,;/|", false);
        while (st.hasMoreTokens())
            addPipe (handler_package+st.nextToken());
    }

    /** miscellaneous **/

    public void setPackage (String pak)
    {
        if (pak == null || pak.equals(""))
            handler_package = "";
        else
            handler_package = pak+".";
    }

    public String getLocation() {
        StringBuffer sb = new StringBuffer();
        if (cur_locator != null)   {
            String id;
            if (cur_locator.getPublicId() != null)
                id = cur_locator.getPublicId();
            else if (cur_locator.getSystemId() != null)
                id = cur_locator.getSystemId();
            else
                id = "source";
            sb.append(id);
            sb.append('(');
            sb.append(cur_locator.getLineNumber());
            sb.append(':');
            sb.append(cur_locator.getColumnNumber());
            sb.append(')');
        }
        sb.append(": ");
        return sb.toString();
    }

    public void reportException (String who, Exception e)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Exception[");
        sb.append(who);
        sb.append("]");
        sb.append(getLocation());
        sb.append(e.toString());
        System.err.println(sb.toString());
    }

    /** basic attributes methods **/

    public static final Attributes newAttr()
    {
        return new AttributesImpl();
    }

    public static final void clearAttr (Attributes atts)
    {
        ((AttributesImpl)atts).clear();
    }

    public static final void addAttr (Attributes atts, String name, String value)
    {
        if (value == null)    value = "";
        ((AttributesImpl)atts).addAttribute ("", name, name, CDATA, value);
    }

    public static final void addAttrNotNull (Attributes atts, String name, String value)
    {
        if (value == null)    return;
        ((AttributesImpl)atts).addAttribute ("", name, name, CDATA, value);
    }

    public static final void setAttr (Attributes atts, int index, String value)
    {
        if (value == null)    value = "";
        ((AttributesImpl)atts).setValue (index, value);
    }

    public static final void setAttr (Attributes atts, String name, String value)
    {
        if (value == null)    value = "";
        AttributesImpl a = (AttributesImpl)atts;
        int index = a.getIndex ("", name);
        if (index == -1)
            a.addAttribute ("", name, name, CDATA, value);
        else
            a.setValue (index, value);
    }

    public static final boolean hasAttr (Attributes atts, String name)
    {
        return (-1 != ((AttributesImpl)atts).getIndex("",name));
    }

    public static final String getAttr (Attributes atts, String name)
    {
        return atts.getValue ("", name);
    }

    public static final String getStrAttr (Attributes atts, String name)
    {
        String value = atts.getValue ("", name);
        return (value == null ? "" : value);
    }

    public static final int getIntAttr (Attributes atts, String name)
    {
        String value = atts.getValue ("", name);
        return (value == null ? 0 : Integer.decode(value).intValue());
    }

    public static final boolean getBoolAttr (Attributes atts, String name)
    {
        String value = atts.getValue ("", name);
        if (value == null)
            return false;
        return (value.equalsIgnoreCase("on") ||
                value.equalsIgnoreCase("yes") ||
                value.equalsIgnoreCase("y") ||
                value.equalsIgnoreCase("true") ||
                value.equalsIgnoreCase("t") ||
                value.equalsIgnoreCase("1")
                );
    }

    /** convenience attributes methods */

    public static final void addAttr (Attributes atts, String name, long value)
    {
        addAttr (atts, name, Long.toString(value));
    }

    public static final void addAttr (Attributes atts, String name, int value)
    {
        addAttr (atts, name, Integer.toString(value));
    }

    public static final void addAttr (Attributes atts, String name, boolean flag)
    {
        addAttr (atts, name, (flag ? "yes" : "no"));
    }

    public static final void setAttr (Attributes atts, int index, int value)
    {
        setAttr (atts, index, Integer.toString(value));
    }

    public static final void setAttr (Attributes atts, String name, long value)
    {
        setAttr (atts, name, Long.toString(value));
    }

    public static final void setAttr (Attributes atts, String name, int value)
    {
        setAttr (atts, name, Integer.toString(value));
    }

    public static final void setAttr (Attributes atts, String name, boolean flag)
    {
        setAttr (atts, name, (flag ? "yes" : "no"));
    }

    /** underscored output methods **/

    protected final void _setDocumentLocator (Locator locator)
    {
        if(out!=null)  out.setDocumentLocator (locator);
    }

    protected final void _startDocument ()  throws SAXException
    {
        try {
            if(out!=null)  out.startDocument();
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("startDocument", e);
        }
    }

    protected final void _endDocument()  throws SAXException
    {
        try {
            if(out!=null)  out.endDocument ();
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("endDocument", e);
        }
    }

    protected final void _startPrefixMapping (String prefix, String uri)  throws SAXException
    {
        try {
            if(out!=null)  out.startPrefixMapping (prefix, uri);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("startPrefixMapping", e);
        }
    }

    protected final void _endPrefixMapping (String prefix)  throws SAXException
    {
        try {
            if(out!=null)  out.endPrefixMapping (prefix);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("endPrefixMapping", e);
        }
    }

    protected final void _startElement (String ns, String name, String raw, Attributes atts)
        throws SAXException
    {
        try {
            if(out!=null)  out.startElement (ns, name, raw, atts);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("startElement", e);
        }
    }

    protected final void _endElement (String ns, String name, String raw)  throws SAXException
    {
        try {
            if(out!=null)  out.endElement (ns, name, raw);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("endElement", e);
        }
    }

    protected final void _characters (char[] ch, int start, int length)  throws SAXException
    {
        try {
            if(out!=null)  out.characters (ch, start, length);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("characters", e);
        }
    }

    protected final void _ignorableWhitespace (char[] ch, int start, int length)  throws SAXException
    {
        try {
            if(out!=null)  out.ignorableWhitespace (ch, start, length);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("characters", e);
        }
    }

    protected final void _processingInstruction (String target, String data)  throws SAXException
    {
        try {
            if(out!=null)  out.processingInstruction (target, data);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("processingInstruction", e);
        }
    }

    protected final void _skippedEntity (String name)  throws SAXException
    {
        try {
            if(out!=null)  out.skippedEntity (name);
        } catch (SAXException e) {
            if (exceptions)   throw e;
            else              reportException ("skippedEntity", e);
        }
    }

    /** convenience output methods **/

    protected final void _characters (String str)  throws SAXException
    {
        _characters (str.toCharArray(), 0, str.length());
    }

    protected final void _characters (StringBuffer buf)  throws SAXException
    {
        _characters (buf.toString().toCharArray(), 0, buf.length());
    }

    protected final void _startElement (String name, Attributes atts)  throws SAXException
    {
        _startElement (out_uri, name, out_prefix+name, atts);
    }

    protected final void _startElement (String name)  throws SAXException
    {
        _startElement (out_uri, name, out_prefix+name, new AttributesImpl());
    }

    protected final void _endElement (String name)  throws SAXException
    {
        _endElement (out_uri, name, out_prefix+name);
    }

    protected final void _emptyElement (String ns, String name, String raw, Attributes atts)
        throws SAXException
    {
        _startElement (ns, name, raw, atts);
        _endElement (ns, name, raw);
    }

    protected final void _emptyElement (String name, Attributes atts)  throws SAXException
    {
        _startElement (out_uri, name, out_prefix+name, atts);
        _endElement (out_uri, name, out_prefix+name);
    }

    protected final void _emptyElement (String name)  throws SAXException
    {
        _startElement (out_uri, name, out_prefix+name, new AttributesImpl());
        _endElement (out_uri, name, out_prefix+name);
    }

    /** redefinable SAX interface **/

    public void setDocumentLocator (Locator locator)
    {
        if (locator != null)    cur_locator = locator;
        if(!quiet)  _setDocumentLocator (locator);
    }

    public void startDocument ()  throws SAXException
    {
        cur_locator = null;
        if(!quiet)  _startDocument ();
    }

    public void endDocument ()  throws SAXException
    {
        if(!quiet)  _endDocument ();
    }

    public void startPrefixMapping (String prefix, String uri)  throws SAXException
    {
        if(!quiet)  _startPrefixMapping (prefix, uri);
    }

    public void endPrefixMapping (String prefix)  throws SAXException
    {
        if(!quiet)  _endPrefixMapping (prefix);
    }

    public void startElement (String ns, String name, String raw, Attributes atts)  throws SAXException
    {
        if(!quiet)  _startElement (ns, name, raw, atts);
    }

    public void endElement (String ns, String name, String raw)  throws SAXException
    {
        if(!quiet)  _endElement (ns, name, raw);
    }

    public void characters (char[] ch, int start, int length)  throws SAXException
    {
        if(!quiet)  _characters (ch, start, length);
    }

    public void ignorableWhitespace (char[] ch, int start, int length)  throws SAXException
    {
        if(!quiet)  _ignorableWhitespace (ch, start, length);
    }

    public void processingInstruction (String target, String data)  throws SAXException
    {
        if(!quiet)  _processingInstruction (target, data);
    }

    public void skippedEntity (String name)  throws SAXException
    {
        if(!quiet)  _skippedEntity (name);
    }

    /** convenience interface methods **/

    public final void characters (String str)  throws SAXException
    {
        characters (str.toCharArray(), 0, str.length());
    }

    public final void characters (StringBuffer buf)  throws SAXException
    {
        characters (buf.toString().toCharArray(), 0, buf.length());
    }

    public final void startElement (String name, Attributes atts)  throws SAXException
    {
        startElement (out_uri, name, out_prefix+name, atts);
    }

    public final void startElement (String name)  throws SAXException
    {
        startElement (out_uri, name, out_prefix+name, new AttributesImpl());
    }

    public final void endElement (String name)  throws SAXException
    {
        endElement (out_uri, name, out_prefix+name);
    }

    public final void emptyElement (String ns, String name, String raw, Attributes atts)
        throws SAXException
    {
        startElement (ns, name, raw, atts);
        endElement (ns, name, raw);
    }

    public final void emptyElement (String name, Attributes atts)  throws SAXException
    {
        startElement (out_uri, name, out_prefix+name, atts);
        endElement (out_uri, name, out_prefix+name);
    }

    public final void emptyElement (String name)  throws SAXException
    {
        startElement (out_uri, name, out_prefix+name, new AttributesImpl());
        endElement (out_uri, name, out_prefix+name);
    }

    public final void attrElement (String name, String attr_name, String attr_value)
        throws SAXException
    {
        Attributes atts = newAttr();
        addAttr (atts, attr_name, attr_value);
        startElement (name, atts);
        endElement (name);
    }

    public final void startElement (String name, String attr_name, String attr_value)
        throws SAXException
    {
        Attributes atts = newAttr();
        addAttr (atts, attr_name, attr_value);
        startElement (name, atts);
    }

    public final void charElement (String name, String data)  throws SAXException
    {
        startElement (name);
        if (data != null)    characters (data);
        endElement (name);
    }

    public final void charAttrElement (String name, String at_name, String at_val, String data)
        throws SAXException
    {
        Attributes atts = newAttr();
        if (at_name != null && at_val != null)    addAttr (atts, at_name, at_val);
        startElement (name, atts);
        if (data != null)    characters (data);
        endElement (name);
    }

    /** ErrorHandler methods **/

    public void error(SAXParseException e) throws SAXParseException {
        if (report_level <= ERROR)    reportException(who+"/error", e);
        if (throw_level <= ERROR)   throw e;
    }

    public void warning(SAXParseException e) throws SAXParseException {
        if (report_level <= WARNING)    reportException(who+"/warning", e);
        if (throw_level <= WARNING)   throw e;
    }

    public void fatalError(SAXParseException e) throws SAXParseException {
        if (report_level <= FATAL)    reportException(who+"/fatal", e);
        if (throw_level <= FATAL)   throw e;
    }

}
