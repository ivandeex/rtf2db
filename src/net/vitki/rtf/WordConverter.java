package net.vitki.rtf;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WordConverter
{

    public static final int PORT    = 3405;
    public static final int BUFLEN  = 8192;
    public static final int TIMEOUT = 300000;

    private static final int MAGIC = 0x1c3e57af;

    private String host;
    private int    port;
    private int    timeout;
    private static boolean verbose = false;

    private String stage = null;
    private IOException lastex = null;

    public WordConverter (String host)  {
        this(host, 0, 0);
    }

    public WordConverter (String host, int port, int timeout)  {
        this.host = host;
        this.port = port <= 0 ? PORT : port;
        this.timeout = timeout <= 0 ? TIMEOUT : timeout;
    }

    private static void println (String msg)  {
        if (verbose)  System.out.println (msg);
    }

    public byte[] convert (byte[] uba)  {
        if (uba == null)    return null;
        if (uba.length == 0)    return uba;
        Socket sock = null;
        OutputStream sos = null;
        InputStream sis = null;
        boolean rc = false;
        byte[] cba = null;
        byte[] rba = null;
        int clen = 0;
        int ulen = uba.length;
        long time1, delta;
        try {
            stage = "compression";
            ByteArrayOutputStream baos = new ByteArrayOutputStream(ulen+100);
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            time1 = System.currentTimeMillis();
            gos.write(uba, 0, ulen);
            gos.finish();
            uba = null;
            cba = baos.toByteArray();
            clen = cba.length;
            gos.close();
            baos = null;
            delta = System.currentTimeMillis() - time1;
            println("compressing ulen="+ulen+" to clen="+clen+" took "+delta+" ms");
            stage = "connection";
            sock = new Socket (host, port);
            sis = new BufferedInputStream(sock.getInputStream(), BUFLEN);
            sos = new BufferedOutputStream(sock.getOutputStream(), BUFLEN);
            stage = "send";
            time1 = System.currentTimeMillis();
            writeLength (sos, MAGIC);
            writeLength (sos, clen);
            writeLength (sos, ulen);
            writeLength (sos, clen+ulen+2);
            writeLength (sos, MAGIC);
            sos.write (cba, 0, clen);
            writeLength (sos, MAGIC);
            cba = null;
            sos.flush();
            delta = System.currentTimeMillis() - time1;
            println("sending clen="+clen+" took "+delta+" ms");
            rc = true;
            time1 = System.currentTimeMillis();
            stage = "wait";
            readMagic (sis, timeout, 500);
            delta = System.currentTimeMillis() - time1;
            println("result appeared in "+delta+" ms");
            stage = "receive";
            clen = (int)readLength(sis);
            ulen = (int)readLength(sis);
            if (clen < 0 || ulen < 0)
                throw new IOException ("wrong result length on read");
            int csum = (int)readLength(sis);
            if (csum != clen+ulen+2)  {
                stage = "checksum";
                throw new IOException ("wrong result checksum");
            }
            readMagic (sis, timeout, 100);
            if (clen == 0 && ulen == 0)  {
                stage = "format";
                throw new IOException ("wrong file format");
            }
            if (clen > 0)  {
                stage = "decompress";
                time1 = System.currentTimeMillis();
                cba = new byte[clen];
                uba = new byte[ulen];
                readFully (sis, cba, 0, clen);
                readMagic (sis, timeout, 100);
                delta = System.currentTimeMillis() - time1;
                println("reading clen="+clen+" took "+delta+" ms");
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(cba));
                time1 = System.currentTimeMillis();
                readFully (gis, uba, 0, ulen);
                gis.close();
                delta = System.currentTimeMillis() - time1;
                println("uncompressing clen="+clen+" to ulen="+ulen+" took "+delta+" ms");
                cba = null;
            } else {
                uba = new byte[ulen];
                readFully (sis, uba, 0, ulen);
                readMagic (sis, timeout, 100);
            }
            stage = null;
            rba = uba;
        } catch (IOException e) {
            lastex = e;
            println ("conversion error: "+e.getMessage());
        } finally {
            if (sos != null)  { try { sos.flush(); } catch (Exception e) {} }
            if (sos != null)  { try { sos.close(); } catch (Exception e) {} }
            if (sis != null)  { try { sis.close(); } catch (Exception e) {} }
            if (sock != null)  { try { sock.close(); } catch (Exception e) {} }
        }
        sos = null;
        sis = null;
        sock = null;
        cba = uba = null;
        return rba;
    }

    public String getStage()   { return stage; }
    public IOException getException()  { return lastex; }

    public boolean convert (String ifile, String ofile)
    {
        InputStream is;
        stage = "open";
        lastex = null;
        if (ofile == null)    ofile = ifile+".rtf";
        try {
            is = new BufferedInputStream(new FileInputStream(ifile));
        } catch (IOException e) {
            lastex = e;
            return false;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stage = "read";
        try {
            while (true)  {
                int b = is.read();
                if (b < 0)    break;
                baos.write(b);
            }
        } catch (IOException e) {
            lastex = e;
            try { is.close(); } catch (Exception ee) {}
            System.gc();
            return false;
        }
        byte[] ba = baos.toByteArray();
        baos = null;
        long time1 = System.currentTimeMillis();
        stage = "convert";
        ba = convert(ba);
        long time2 = System.currentTimeMillis();
        if (ba == null)  {
            println("result not returned");
            System.gc();
            return false;
        }
        stage = "write";
        try {
            FileOutputStream fos = new FileOutputStream(ofile);
            fos.write(ba);
            fos.flush();
            fos.close();
            stage = null;
        } catch (IOException e) {
            lastex = e;
            System.err.println("cannot write output file");
        }
        println("conversion done in "+(time2-time1)+" ms");
        lastex = null;
        System.gc();
        return true;
    }

    private static long readLength (InputStream is)  throws IOException  {
        int b1 = is.read();
        int b2 = is.read();
        int b3 = is.read();
        int b4 = is.read();
        if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1)
            throw new IOException ("wrong length on input");
        return (((long)b1 << 24) + ((long)b2 << 16) + ((long)b3 << 8) + (long)b4);
    }

    private static void writeLength (OutputStream os, long len)  throws IOException  {
        os.write( (int)((len>>24) & 0xff) );
        os.write( (int)((len>>16) & 0xff) );
        os.write( (int)((len>>8)  & 0xff) );
        os.write( (int)((len)     & 0xff) );
    }

    private static void readMagic (InputStream is, int timeout, int waiter)  throws IOException {
        long time1 = System.currentTimeMillis();
        while (is.available() == 0)  {
            msSleep(waiter);
            long delta = System.currentTimeMillis() - time1;
            if (delta > timeout)
                throw new IOException ("data wait timeout");
        }
        int magic = (int)readLength(is);
        if (magic != MAGIC)
            throw new IOException ("wrong magic data");
    }

    private static void readFully (InputStream is, byte[] buf, int off, int len)  throws IOException  {
        while (len > 0)  {
            int n = is.read(buf, off, len);
            if (n < 0)    throw new IOException ("stream reading failure");
            off += n;
            len -= n;
        }
    }

    private static void msSleep (int msec)  {
        try { Thread.sleep(msec); } catch (Exception e) {}
    }

    public void finish()  {
    }

    public static void main (String[] arg)  {
        WordConverter cc;
        cc = new WordConverter("localhost");
        long time1 = System.currentTimeMillis();
        boolean ok = cc.convert (arg[0], null);
        if (ok)
            System.out.println ("conversion OK");
        else
            System.out.println ("error ["+cc.getException()+"] "+
                                "at stage ["+cc.getStage());
        long delta = System.currentTimeMillis() - time1;
        System.out.println ("done in "+delta+" ms");
        cc.finish();
    }
}
