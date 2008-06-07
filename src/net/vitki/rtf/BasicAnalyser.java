package net.vitki.rtf;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BasicAnalyser extends RtfParser
{
    public static void main (String[] args) throws Exception
    {
        String name = "d:/java/rtf/test/sps";
        if (args.length > 0)
            name = args[0];
        BasicAnalyser aa;
        aa = new BasicAnalyser();
        aa.setOutputEncoding("windows-1251");
        aa.setDebugging(true);
        aa.setTracing(true);
        aa.setSkipTracing(true);
        aa.setAllFiles(name);
        long t = System.currentTimeMillis();
        aa.parse();
        t = System.currentTimeMillis() - t;
        aa.flushStreams();
        System.out.println(name+" done in "+t+"ms with "+aa.getGcCount()+"gc");
    }
    
    public static final int LINE_SIZE = 90;

    private String  encoding;
    private int     indent;
    private String  public_dtd;
    private String  system_dtd;
    private boolean debugging;
    private boolean flush_tracing;
    
    private PrintWriter     dbg;
    private XmlWriter       xml;
    private OutputStream    out;
    private ZipOutputStream zip;
    private OutputStream    zos;
    private String          zip_name;
    private int             zip_count;
    
    private boolean in_parse;
    private boolean in_body;
    private int     undo_count;
    private int     dbg_off;
    
    public BasicAnalyser()  throws RtfException  {
        super();
        encoding = "ISO-8859-1";
        indent = 1;
        public_dtd = null;
        system_dtd = null;
        dbg = null;
        out = null;
        xml = null;
        zip = null;
        zos = null;
        zip_name = null;
        zip_count = 0;
        flush_tracing = false;
        dbg_off = 0;
    }
    
    public void setAllFiles (String prefix)  throws RtfException {
        setInputFile (prefix+".rtf");
        setXmlFile (prefix+".xml");
        setDebugFile (prefix+".txt");
        setZipFile (prefix+".zip");
    }
    
    public void setInputFile (String name)  throws RtfException {
        FileInputStream fis = null;
        if (name != null)  {
            try {
                fis = new FileInputStream(name);
            } catch (FileNotFoundException e) {
                generateException(e);
            }
        }
        setInputStream(fis);
    }
    
    public void setXmlFile (String name)  throws RtfException {
        FileOutputStream fos = null;
        if (name != null)  {
            try {
                fos = new FileOutputStream(name);
            } catch (FileNotFoundException e) {
                generateException(e);
            }
        }
        setXmlStream(fos);
    }
    
    public void setDebugFile (String name)  throws RtfException {
        FileOutputStream fos = null;
        if (name != null && debugging)  {
            try {
                fos = new FileOutputStream(name);
            } catch (FileNotFoundException e) {
                generateException(e);
            }
        }
        setDebugStream(fos);
    }
    
    public void setZipFile (String name)  throws RtfException {
        FileOutputStream fos = null;
        if (name != null)  {
            try {
                fos = new FileOutputStream(name);
            } catch (FileNotFoundException e) {
                generateException(e);
            }
        }
        setZipStream(fos);
        if (zip != null)
            zip_name = name;
    }
    
    public void setInputStream (InputStream is)  throws RtfException {
        super.setInputStream (is);
    }
    
    public void setXmlStream (OutputStream os)  throws RtfException {
        out = null;
        xml = null;
        if (os == null)
            return;
        try {
            out = new BufferedOutputStream(os,CACHE_SIZE);
            xml = new XmlWriter(out, encoding, indent,
                                public_dtd, system_dtd, null, null);
        } catch (Exception e) {
            out = null;
            xml = null;
            generateException(e);
        }
    }
    
    public void setDebugStream (OutputStream os)  throws RtfException {
        dbg = null;
        dbg_off = 0;
        if (os == null || !debugging)
            return;
        BufferedOutputStream bos = new BufferedOutputStream(os,CACHE_SIZE);
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(bos,encoding);
        } catch (UnsupportedEncodingException e) {
            generateException("encoding ["+encoding+"] unsupported");
        }
        dbg = new PrintWriter(osw);
    }
    
    public void setZipStream (OutputStream os)  throws RtfException {
        zip = null;
        zos = null;
        zip_count = 0;
        if (os == null)
            return;
        BufferedOutputStream bos = new BufferedOutputStream(os,CACHE_SIZE);
        zip = new ZipOutputStream(bos);
        zos = os;
    }
    
    public void flushStreams()   {
        flushStreams(true);
    }
    
    public void flushStreams (boolean finish)  {
        if (out != null)  {
            try { out.flush(); } catch (Exception e) {}
            if (finish)  {
                try { out.close(); } catch (Exception e) {}
                out = null;
                xml = null;
            }
        }
        if (dbg != null)  {
            try { dbg.flush(); } catch (Exception e) {}
            if (finish)  {
                try { dbg.close(); } catch (Exception e) {}
                dbg = null;
            }
        }
        if (zip != null)  {
            try { zip.flush(); } catch (Exception e) {}
            if (finish)  {
                try { zip.finish(); } catch (Exception e) {}
                try { zip.close(); } catch (Exception e) {}
                try { zos.close(); } catch (Exception e) {}
                zip = null;
                zos = null;
                if (zip_name != null && zip_count == 0)
                    (new File(zip_name)).delete();
            }
        }
    }
    
    public void setOutputEncoding (String encoding)  {
        this.encoding = encoding;
    }
    
    public void setPublicDtd (String public_dtd) {
        this.public_dtd = Util.nullify(public_dtd);
    }
    
    public void setSystemDtd (String system_dtd) {
        this.system_dtd = Util.nullify(system_dtd);
    }

    public void setDebugging (boolean flag)  { debugging = flag; }
    public void setFlushTracing (boolean flag)  { flush_tracing = flag; }
    public void setIndent (int indent) { this.indent = indent; }
    public PrintWriter getDebug()  { return dbg; }
    public boolean isDebug()  { return dbg != null; }
    public XmlWriter getXml()  { return xml; }

    public void parse()  throws RtfException {
        beginParse();
        try  {
            while (in_parse)  {
                RtfToken tok = nextToken();
                if (tok.tag == Tag.END)
                    break;
                if (in_body)
                    processBody(tok);
                else
                    processHeader(tok);
            }
        } catch (RtfException e) {
            try { endParse(); } catch (Exception ee) {}
            throw e;
        }
        endParse();
    }
    
    public void processHeader(RtfToken tok)  throws RtfException {}
    public void processBody(RtfToken tok)  throws RtfException {}
    
    public String writeToZip (String type, String suffix,
                              ByteArrayOutputStream baos)
    throws RtfException
    {
        type = type.trim().toLowerCase();
        if (suffix == null || "".equals(suffix))
            suffix = "_";
        String ext = "dat";
        if ("png".equals(type) || "jpg".equals(type)
            || "gif".equals(type) || "eqn".equals(type)
            || "wmf".equals(type) || "ppt".equals(type)
            || "emf".equals(type)
            )
            ext = type;
        String name = ""+(++zip_count);
        while (name.length() < 4)  name = "0" + name;
        name = name + suffix + "." + ext;
        if (zip != null)  {
            if (zip_count > 0)  {
                try { zip.closeEntry(); } catch (Exception e) {}
            }
            ZipEntry entry = new ZipEntry(name);
            try {
                zip.putNextEntry(entry);
                if (baos != null)
                    baos.writeTo(zip);
            } catch (IOException e) {
                generateException(e);
            }
        }
        return "zip:" + name;
    }
    
    public void setFinishParse()  {
        this.in_parse = false;
    }

    public void setStartBody() throws RtfException {
        in_body = true;
        beginBody();
    }
    
    public void beginParse()  throws RtfException {
        super.init();
        setNewline('\0');
        in_parse = true;
        in_body = false;
        undo_count = 0;
        if (!debugging)  {
            flush_tracing = false;
            setTracing(false);
            setSkipTracing(false);
        }
    }
    
    public void endParse()  throws RtfException {
        flushStreams(false);
    }
    
    public void beginBody()  throws RtfException {
    }
    
    public RtfToken nextToken()  throws RtfException {
        RtfToken tok = super.nextToken();
        if (getTracing())  {
            if (undo_count > 0)
                undo_count--;
            else {
                prints (tok);
                if (flush_tracing)
                    flushStreams(false);
            }
        }
        return tok;
    }
    
    public void undoToken()  throws RtfException {
        super.undoToken();
        if (getTracing())
            undo_count++;
    }
    
    public void print (String s)  {
        if (dbg == null)   return;
        dbg.print(s);
        dbg_off += s.length();
    }
    
    public void println (String s)  {
        if (dbg == null)   return;
        dbg.println(s);
        dbg_off = 0;
    }
    
    public void prints (String s)  {
        if (dbg == null)  return;
        if (dbg_off > LINE_SIZE)  {
            dbg.println("");
            dbg_off = 0;
        }
        if (dbg_off == 0)  {
            int n = getDepth();
            while (s.startsWith("\b"))  {
                n--;
                s = s.substring(1);
            }
            for (int i=0; i<n; i++)  dbg.print(' ');
            dbg_off += n;
        }
        dbg.print(s);
        dbg_off += s.length();
    }
    
    public void prints (RtfToken tok)  {
        String s = tok.str;
        switch (tok.tag)  {
            case Tag.OPEN:
                if (dbg_off > 0)
                    println("");
                prints("\b{");
                break;
            case Tag.CLOSE:
                if (dbg_off > 0)
                    println("");
                prints("}");
                println("");
                break;
            case Tag.CHARS:
                if (s.length() > LINE_SIZE)
                    s = s.substring(0,LINE_SIZE)+"...";
                prints("["+s+"].");
                break;
            default:
                String ignore = tok.ignore ? "*" : "";
                if (tok.tag < 100)
                    break;
                if (s == null)
                    prints(ignore+Tag.getName(tok.tag)+".");
                else
                    prints(ignore+Tag.getName(tok.tag)+"="+s+".");
                break;
        }
    }

}