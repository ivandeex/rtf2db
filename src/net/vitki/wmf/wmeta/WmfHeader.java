package net.vitki.wmf.wmeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class WmfHeader implements Record {

    public int   fileType;       // Type of metafile (0=memory, 1=disk)
    public int   headerSize;     // Size of header in WORDS (always 9)
    public int   version;        // Version of Microsoft Windows used
    public long  fileSize;       // Total size of the metafile in WORDs
    public int   numOfObjects;   // Number of objects in the file
    public long  maxRecordSize;  // The size of largest record in WORDs
    public int   numOfParams;    // Not Used (always 0)

    public int render (Renderer rr)  {
        if (rr.isLockPlaceable())
            return OK;
        return rr.setPlaceable (false, 0);
    }

    public boolean isValid()  {
        return (fileType == 1
                && headerSize == 9
                && numOfParams == 0
                );
    }

    public void read(RecordReader rd) throws IOException {
        fileType = rd.readWord();
        headerSize = rd.readWord();
        version = rd.readWord();
        fileSize = rd.readDWord();
        numOfObjects = rd.readWord();
        maxRecordSize = rd.readDWord();
        numOfParams = rd.readWord();
    }

    public String getVersion()  {
        int major = (version >> 8) & 0xff;
        int minor = version & 0xff;
        return ""+major+"."+minor;
    }

    public String toString() {
        return "WmfHeader"+paramString();
    }

    public String paramString() {
        return ("("+
                "type="+fileType+C+
                "hdrsize="+headerSize+C+
                "version="+getVersion()+C+
                "objnum="+numOfObjects+C+
                "maxrec="+maxRecordSize+C+
                "parnum="+numOfParams+
                ")");
    }

    public void init (int size, int func)   {}

    public int getSize()   { return 9; }
    public int getFunc()   { return -1; }

}

