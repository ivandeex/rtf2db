package net.vitki.wmf;


import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.Properties;

import net.vitki.wmf.emeta.*;
import net.vitki.wmf.wmeta.*;

public class RecordReader  {

    private InputStream in;
    private int pos;
    private int dib_alpha;
    private boolean ignore_quirks;

    public RecordReader (InputStream in, Properties props) {
        this.in = new BufferedInputStream (in, 1024);
        pos = 0;
        dib_alpha = Bitmap.ALPHA_SOURCE;
        String val;
        if (props == null)
            props = new Properties();
        val = props.getProperty ("alpha");
        if (val != null)  {
            if ("source".equalsIgnoreCase(val))
                dib_alpha = Bitmap.ALPHA_SOURCE;
            else if ("white".equalsIgnoreCase(val))
                dib_alpha = Bitmap.ALPHA_WHITE;
            else if ("black".equalsIgnoreCase(val))
                dib_alpha = Bitmap.ALPHA_BLACK;
            else
                dib_alpha = Integer.decode(val).intValue();
        }
        val = props.getProperty("ignore-quirks");
        ignore_quirks = booleanOf(val);
    }

    public boolean getIgnoreQuirks() {
    	return ignore_quirks;
    }

    public static boolean booleanOf (String s)  {
        if (s == null)    return false;
        s = s.trim().toLowerCase();
        return "1".equals(s) || "y".equals(s) || "yes".equals(s) || "t".equals(s) || "true".equals(s) || "on".equals(s);
    }

    public int getDibAlpha()  {
        return dib_alpha;
    }

    public int getPosition()  {
        return pos;
    }

    public short readByte() throws IOException {
        int b = in.read();
        if (b < 0)    throw new EOFException();
        pos++;
        return (short)b;
    }

    public int readWord() throws IOException {
        int b2 = in.read();
        int b1 = in.read();
        if ((b1 | b2) < 0)
            throw new EOFException();
        pos += 2;
        short r = (short)((b1 << 8) + (b2 << 0));
        return (int)r;
    }

    public int readLong() throws IOException {
        int b4 = in.read();
        int b3 = in.read();
        int b2 = in.read();
        int b1 = in.read();
        if ((b1 | b2 | b3 | b4) < 0)
            throw new EOFException();
        pos += 4;
        return ((b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0));
    }

    public float readFloat() throws IOException {
        int bits = readLong();
        float val = Float.intBitsToFloat(bits);
        return val;
    }

    public int readColor() throws IOException {
        int red   = in.read();
        int green = in.read();
        int blue  = in.read();
        int extra = in.read();
        if ((red | green | blue | extra) < 0)
            throw new EOFException();
        pos += 4;
        extra &= 0x7f;
        return ((extra << 24) + (red << 16) + (green << 8) + blue);
    }

    public long readDWord() throws IOException {
        int b4 = in.read();
        int b3 = in.read();
        int b2 = in.read();
        int b1 = in.read();
        if ((b1 | b2 | b3 | b4) < 0)
            throw new EOFException();
        pos += 4;
        return (((long)b1 << 24) + ((long)b2 << 16) + ((long)b3 << 8) + ((long)b4 << 0));
    }

    public byte[] readBytes (int len, boolean check_nulls, boolean continue_after_nulls)
        throws IOException
    {
        if (len==0)    return null;
        byte[] ba = new byte[len];
        int i, n, b;
        if (!check_nulls)  {
            n = in.read(ba);
            if (n < len)    throw new EOFException();
            pos += len;
            return ba;
        }
        if (check_nulls && continue_after_nulls)  {
            n = in.read(ba);
            if (n < len)    throw new EOFException();
            pos += len;
            for (i=0; i<n; i++)
                if (ba[i]==0)
                    break;
            if (i == n)
                return ba;
            if (i == 0)  {
                ba = null;
                return null;
            }
            byte[] res = new byte[i];
            System.arraycopy (ba, 0, res, 0, i);
            ba = null;
            return res;
        }
        for (n=0; n<len; n++)  {
            b = in.read();
            if (b < 0)    throw new EOFException();
            pos++;
            if (b==0)   break;
            ba[n] = (byte)b;
        }
        if (n == len)
            return ba;
        byte[] res = new byte[n];
        System.arraycopy (ba, 0, res, 0, n);
        ba = null;
        return res;
    }

    public byte[] readBytes (int len, boolean check_nulls) throws IOException  {
        return readBytes (len, check_nulls, true);
    }

    public byte[] readBytes (int len) throws IOException  {
        return readBytes (len, false, false);
    }

    public void skipBytes (int n)  throws IOException {
        for (int i=0; i<n; i++)  {
            int b = in.read();
            if (b < 0)    throw new EOFException();
            pos++;
        }
    }

    public RecordTable readMetaFile() throws IOException {
        RecordTable table = new RecordTable();
        if (readHeader(table) == false)
            throw new IOException ("wrong metafile header");
        boolean is_emf = table.isEnhanced();
        if (is_emf)  {
            int n = (int)table.getEmfHeader().numOfRecords;
            for (int i=1; i < n; i++)  {
                Record rec = readEmfRecord();
                if (table.add(rec) == false)
                    throw new IOException("wrong metafile record");
            }
        } else {
            while(true) {
                Record rec = readWmfRecord();
                //System.out.println("rec "+table.getSize()+": "+rec);
                if (table.add(rec) == false)
                    throw new IOException("wrong metafile record");
                if (rec.getFunc() == 0)
                    break;
            }
        }
        return table;
    }

    private boolean readHeader(RecordTable table)  throws IOException {
        ApmHeader apm_header = new ApmHeader();
        in.mark(32);
        apm_header.init(11, -1);
        apm_header.read(this);
        if (apm_header.isValid()) {
            //System.err.println("got APM: "+apm_header);
            table.addApmHeader (apm_header);
        } else {
            //System.err.println("not APM: "+apm_header);
            in.reset();
            this.pos = 0;
        }
        WmfHeader wmf_header = new WmfHeader();
        in.mark(80);
        wmf_header.read(this);
        boolean is_wmf = wmf_header.isValid();
        in.reset();
        this.pos = 0;
        if (is_wmf)  {
            //System.err.println("got WMF: "+wmf_header);
            wmf_header.read(this);
            return table.addWmfHeader (wmf_header);
        } else {
            //System.err.println("not WMF: "+wmf_header);
        }
        EmfHeader emf_header = new EmfHeader();
        in.mark(128);
        emf_header.read(this);
        boolean is_emf = emf_header.isValid();
        in.reset();
        this.pos = 0;
        if (is_emf)  {
            //System.err.println("got EMF: "+emf_header);
            emf_header.read(this);
            int to_skip = emf_header.getSize() - getPosition();
            if (to_skip > 0)
                skipBytes(to_skip);
            return table.addEmfHeader (emf_header);
        } else {
            //System.err.println("not EMF: "+emf_header);
        }
        //System.err.println("this is not a metafile");
        return false;
    }

    public int countRestBytes ()  throws IOException {
        int bytes = 0;
        while (in.read() >= 0)    bytes++;
        return bytes;
    }

    public int restBytes()  throws IOException {
        int cur_pos = getPosition();
        int n = wanted_pos2 - cur_pos;
        if (n < 0)    throw new IOException ("excess read "+(-n));
        return n;
    }

    public Record readRecord (boolean as_emf) throws IOException  {
        return (as_emf ? (Record)readEmfRecord() : (Record)readWmfRecord());
    }

    int  actual_pos1 = 0;
    int  wanted_pos2 = 0;

    private WmfRecord readWmfRecord ()  throws IOException
    {
        int pos1 = getPosition();
        int size = (int)readDWord();
        int func = readWord();
        WmfRecord r;
        switch (func)
        {
            case Constants.F_DibBitblt:             r = new WmfDibBitBlt();             break;
            case Constants.F_DibCreatePatternBrush: r = new WmfDibCreatePatternBrush(); break;
            case Constants.F_StretchDiBits:         r = new WmfStretchDiBits();         break;
            case Constants.F_SetROP2:               r = new WmfSetRop2();               break;
            case Constants.F_PatBlt:                r = new WmfPatBlt();                break;
            case Constants.F_SelectObject:          r = new WmfSelectObject();          break;
            case Constants.F_DeleteObject:          r = new WmfDeleteObject();          break;
            case Constants.F_SetBkMode:             r = new WmfSetBkMode();             break;
            case Constants.F_Rectangle:             r = new WmfRectangle();             break;
            case Constants.F_Ellipse:               r = new WmfEllipse();               break;
            case Constants.F_Polygon:               r = new WmfPolygon();               break;
            case Constants.F_Polyline:              r = new WmfPolyline();              break;
            case Constants.F_PolyPolygon:           r = new WmfPolyPolygon();           break;
            case Constants.F_Escape:                r = new WmfEscape();                break;
            case Constants.F_MoveTo:                r = new WmfMoveTo();                break;
            case Constants.F_LineTo:                r = new WmfLineTo();                break;
            case Constants.F_SetWindowOrg:          r = new WmfSetWindowOrg();          break;
            case Constants.F_SetWindowExt:          r = new WmfSetWindowExt();          break;
            case Constants.F_SetTextAlign:          r = new WmfSetTextAlign();          break;
            case Constants.F_SetTextColor:          r = new WmfSetTextColor();          break;
            case Constants.F_SetMapMode:            r = new WmfSetMapMode();            break;
            case Constants.F_ExtTextOut:            r = new WmfExtTextOut();            break;
            case Constants.F_TextOut:               r = new WmfTextOut();               break;
            case Constants.F_SetBkColor:            r = new WmfSetBkColor();            break;
            case Constants.F_SetPolyFillMode:       r = new WmfSetPolyFillMode();       break;
            case Constants.F_SetStretchBltMode:     r = new WmfSetStretchBltMode();     break;
            case Constants.F_CreateBrushIndirect:   r = new WmfCreateBrushIndirect();   break;
            case Constants.F_CreatePenIndirect:     r = new WmfCreatePenIndirect();     break;
            case Constants.F_CreateFontIndirect:    r = new WmfCreateFontIndirect();    break;
            case 0:                                 r = new WmfEOF();                   break;
            default:                                r = new WmfRecord();                break;
        }
        r.init(size, func);
        actual_pos1 = pos1;
        wanted_pos2 = pos1+size*2;
        r.read(this);
        int pos2 = getPosition();
        int real_delta = pos2 - pos1;
        int unread = size*2 - real_delta;
        if (unread > 0) {
        	if (!ignore_quirks)
        		throw new IOException("still "+unread+" bytes unread for "+r.getClass().getName());
            skipBytes (unread);
        } else if (unread < 0) {
        	String who;
        	try {
        		who = r.toString();
        	} catch (Exception e) {
        		who = r.getClass().getName();
        	}
            throw new IOException("record ate "+(-unread)+" bytes more than "+(size*2)+" for "+who);
        }
        return r;
    }

    private EmfRecord readEmfRecord ()  throws IOException
    {
        int pos1 = getPosition();
        int func = (int)readDWord();
        int size = (int)readDWord();
        if (size % 4 != 0)
            throw new IOException ("EMF record size is not on DWORD boundary");
        EmfRecord r;
        switch (func)
        {
            case Constants.EMR_GdiComment:          r = new EmfGdiComment();            break;
            case Constants.EMR_EOF:                 r = new EmfEOF();                   break;
            case Constants.EMR_SetWindowOrgEx:      r = new EmfSetWindowOrgEx();        break;
            case Constants.EMR_SetWindowExtEx:      r = new EmfSetWindowExtEx();        break;
            case Constants.EMR_SetViewportOrgEx:    r = new EmfSetViewportOrgEx();      break;
            case Constants.EMR_SetViewportExtEx:    r = new EmfSetViewportExtEx();      break;
            case Constants.EMR_SetRop2:             r = new EmfSetRop2();               break;
            case Constants.EMR_SelectObject:        r = new EmfSelectObject();          break;
            case Constants.EMR_DeleteObject:        r = new EmfDeleteObject();          break;
            case Constants.EMR_SetBkColor:          r = new EmfSetBkColor();            break;
            case Constants.EMR_SetBkMode:           r = new EmfSetBkMode();             break;
            case Constants.EMR_SetTextColor:        r = new EmfSetTextColor();          break;
            case Constants.EMR_SetTextAlign:        r = new EmfSetTextAlign();          break;
            case Constants.EMR_MoveToEx:            r = new EmfMoveToEx();              break;
            case Constants.EMR_Rectangle:           r = new EmfRectangle();             break;
            case Constants.EMR_Ellipse:             r = new EmfEllipse();               break;
            case Constants.EMR_CreatePen:           r = new EmfCreatePen();             break;
            case Constants.EMR_ExtCreatePen:        r = new EmfExtCreatePen();          break;
            case Constants.EMR_CreateBrushIndirect: r = new EmfCreateBrushIndirect();   break;
            case Constants.EMR_ExtTextOutW:         r = new EmfExtTextOutW();           break;
            case Constants.EMR_PolyLine16:          r = new EmfPolyline16();            break;
            case Constants.EMR_PolyGon16:           r = new EmfPolygon16();             break;
            case Constants.EMR_BitBlt:              r = new EmfBitBlt();                break;
            default:                                r = new EmfRecord();                break;
        }
        r.init(size, func);
        actual_pos1 = pos1;
        wanted_pos2 = pos1+size;
        r.read(this);
        int pos2 = getPosition();
        int unread = size-(pos2-pos1);
        if (unread != 0)
            throw new IOException("still "+unread+" bytes unread for "+r.getClass().getName());
        return r;
    }

}

