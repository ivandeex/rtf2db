package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfHeader extends EmfRecord {

    public int   boundsLeft;       // Left inclusive bounds
    public int   boundsRight;      // Right inclusive bounds
    public int   boundsTop;        // Top inclusive bounds
    public int   boundsBottom;     // Bottom inclusive bounds
    public int   frameLeft;        // Left side of inclusive picture frame
    public int   frameRight;       // Right side of inclusive picture frame
    public int   frameTop;         // Top side of inclusive picture frame
    public int   frameBottom;      // Bottom side of inclusive picture frame
    public long  signature;        // Signature ID (always 0x464D4520)
    public long  version;          // Version of the metafile
    public long  fileSize;         // Size of the metafile in bytes
    public long  numOfRecords;     // Number of records in the metafile
    public int   numOfHandles;     // Number of handles in the handle table
    public int   reserved;         // Not used (always 0)
    public long  sizeOfDescrip;    // Size of description string in WORDs
    public long  offsOfDescrip;    // Offset of description string in metafile
    public long  numPalEntries;    // Number of color palette entries
    public int   widthDevPixels;   // Width of reference device in pixels
    public int   heightDevPixels;  // Height of reference device in pixels
    public int   widthDevMM;       // Width of reference device in millimeters
    public int   heightDevMM;      // Height of reference device in millimeters

    public boolean isValid()  {
        return (func == Constants.EMR_Header
                && (size == 80 || size == 100)
                && signature == 0x464d4520
                && reserved == 0
                );
    }

    public void read(RecordReader rd) throws IOException
    {
        int startpos = rd.getPosition();
        int recordType     = (int)rd.readDWord();
        int recordSize     = (int)rd.readDWord();
        init (recordSize, recordType);
        boundsLeft     = rd.readLong();
        boundsTop      = rd.readLong();
        boundsRight    = rd.readLong();
        boundsBottom   = rd.readLong();
        frameLeft      = rd.readLong();
        frameRight     = rd.readLong();
        frameTop       = rd.readLong();
        frameBottom    = rd.readLong();
        signature      = rd.readDWord();
        version        = rd.readDWord();
        fileSize       = rd.readDWord();
        numOfRecords   = rd.readDWord();
        numOfHandles   = rd.readWord();
        reserved       = rd.readWord();
        sizeOfDescrip  = rd.readDWord();
        offsOfDescrip  = rd.readDWord();
        numPalEntries  = rd.readDWord();
        widthDevPixels = rd.readLong();
        heightDevPixels= rd.readLong();
        widthDevMM     = rd.readLong();
        heightDevMM    = rd.readLong();
        int endpos = rd.getPosition();
    }

    public String getVersion()  {
        int major = (int)(version >> 16) & 0xffff;
        int minor = (int)version & 0xffff;
        return ""+major+"."+minor;
    }

    public String paramString() {
        final String C = ",";
        return ("("+
                "bounds=("+boundsLeft+C+boundsTop+C+boundsRight+C+boundsBottom+"),"+
                "frame=("+frameLeft+C+frameTop+C+frameRight+C+frameBottom+"),"+
                "signature="+Long.toHexString(signature)+"h,"+
                "version="+getVersion()+","+
                "fileSize="+fileSize+C+
                "recnum="+numOfRecords+C+
                "handlenum="+numOfHandles+C+
                "reserved="+reserved+C+
                "descsize="+sizeOfDescrip+C+
                "descoff="+offsOfDescrip+C+
                "palentries="+numPalEntries+C+
                "devpix=("+widthDevPixels+C+heightDevPixels+"),"+
                "devmm=("+widthDevMM+C+heightDevMM+"),"+
                ")");
    }

    public String toString() {
        return "EmfHeader"+paramString();
    }

    public int render (Renderer rr)  {
        int rc = OK;
        if (rr.isLockPlaceable())
            return OK;
        //rc = rr.setPlaceable (false, 0);
        return rc;
    }

}

