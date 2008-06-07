package net.vitki.wmf;

import java.io.IOException;

public class ApmHeader implements Record  {

    public long  key;           // Magic number (always 9AC6CDD7h)
    public int   handle;        // Metafile HANDLE number (always 0)
    public int   left;          // Left coordinate in metafile units
    public int   top;           // Top coordinate in metafile units
    public int   right;         // Right coordinate in metafile units
    public int   bottom;        // Bottom coordinate in metafile units
    public int   inch;          // Number of metafile units per inch
    public long  reserved;      // Reserved (always 0)
    public int   checksum;      // Checksum value for previous 10 WORDs

    public int render (Renderer rr)  {
        //rr.setLockPlaceable (true);
        //return rr.setPlaceable (true, inch);
        return rr.setPlaceable (false, 0);
    }

    public boolean isValid()  {
    	int cs = calcCheckSum();
        boolean valid = (key==0x9ac6cdd7l && handle==0 && reserved==0 && checksum==cs);
        return valid;
    }

    public int calcCheckSum()  {
        int sum = 0;
        sum ^= key & 0xffff;
        sum ^= (key >> 16) & 0xffff;
        sum ^= handle;
        sum ^= left;
        sum ^= top;
        sum ^= right;
        sum ^= bottom;
        sum ^= inch;
        sum ^= reserved & 0xffff;
        sum ^= (reserved >> 16) & 0xffff;
        sum &= 0xffff;
        return sum;
    }

    public void read(RecordReader rd) throws IOException {
        key = rd.readDWord();
        handle = rd.readWord();
        left = rd.readWord();
        top = rd.readWord();
        right = rd.readWord();
        bottom = rd.readWord();
        inch = rd.readWord();
        reserved = rd.readDWord();
        checksum = rd.readWord() & 0xffff;
    }

    public String toString() {
        return "PlaceableHeader"+paramString();
    }

    public String paramString() {
        return ("("+
                "left="+left+","+
                "top="+top+","+
                "right="+right+","+
                "bottom="+bottom+","+
                "inch="+inch+","+
                "key="+Long.toHexString(key)+"h,"+
                "handle="+handle+","+
                "reserved="+reserved+","+
                "checksum="+Integer.toHexString(checksum)+"h,"+
                "sum="+Integer.toHexString(calcCheckSum())+"h"+
                ")");
    }

    public void init (int size, int func)   {}

    public int getSize()   { return 9; }
    public int getFunc()   { return -1; }
}

