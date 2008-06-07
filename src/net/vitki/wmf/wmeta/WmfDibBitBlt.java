package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfDibBitBlt  extends WmfRecord {
    int     rasterOp;         // High-order word for the raster operation
    int     ySrcOrigin;       // Y-coordinate of the source origin
    int     xSrcOrigin;       // X-coordinate of the source origin
    int     yDest;            // Destination width
    int     xDest;            // Destination height
    int     yDestOrigin;      // Y-coordinate of the destination origin
    int     xDestOrigin;      // X-coordinate of the destination origin
    // DIB Bitmap
    long    width;            // Width of bitmap in pixels
    long    height;           // Height of bitmap in scan lines
    long    bytesPerLine;     // Number of bytes in each scan line
    int     numColorPlanes;   // Number of color planes in the bitmap
    int     bitsPerPixel;     // Number of bits in each pixel
    long    compression;      // Compression type
    long    sizeImage;        // Size of bitmap in bytes
    int     xPelsPerMeter;    // Width of image in pixels per meter
    int     yPelsPerMeter;    // Height of image in pixels per meter
    long    clrUsed;          // Number of colors used
    long    clrImportant;     // Number of important colors
    RGBQuad bitmap[];         // Bitmap data

    public int render (Renderer rr)  {
    	if (isRop())
    		return renderRop(rr);
        rr.log ("not yet supported: "+toString());
        //rr.patBlt (xDestOrigin, yDestOrigin, xDest, yDest, rasterOp);
        return ERROR;
    }

    public String toString() {
    	return funcName()+"("+paramString()+")";
    }
    
    public String funcName() {
    	if (isRop())
    		return funcNameRop();
    	return "DibBitBlt";
    }

    public String paramString() {
    	if (isRop())
    		return paramStringRop();
    	String s = "rop="+ropName(rasterOp);
    	s += C+"so("+xSrcOrigin+C+ySrcOrigin+")";
    	s += C+"d("+xDest+C+yDest+")";
    	s += C+"do("+xDestOrigin+C+yDestOrigin+")";
    	s += C+"wh("+width+C+height+")";
    	s += C+"bpl="+bytesPerLine + C+"ncp="+numColorPlanes;
    	s += C+"bpp="+bitsPerPixel + C+"c="+compression;
    	s += C+"si="+sizeImage;
    	return s;
    }

    public void read (RecordReader rd)  throws IOException {
    	if (isRop()) {
    		readRop(rd);
    		return;
    	}
        rasterOp = rd.readWord();
        ySrcOrigin = rd.readWord();
        xSrcOrigin = rd.readWord();
        yDest = rd.readWord();
        xDest = rd.readWord();
        yDestOrigin = rd.readWord();
        xDestOrigin = rd.readWord();
        width = rd.readDWord();
        height = rd.readDWord();
        bytesPerLine = rd.readDWord();
        numColorPlanes = rd.readWord();
        bitsPerPixel = rd.readWord();
        compression = rd.readDWord();
        sizeImage = rd.readDWord();
        xPelsPerMeter = rd.readLong();
        yPelsPerMeter = rd.readLong();
        clrUsed = rd.readDWord();
        clrImportant = rd.readDWord();
        if (sizeImage % 4 != 0) {
        	if (!rd.getIgnoreQuirks())
        		throw new IOException("wrong DIBBitBlt image size "+this.toString());
       		return;
        }
        int size = (int)sizeImage / 4;
        bitmap = new RGBQuad[size];
        for (int i=0; i<size; i++)  {
            bitmap[i].red = (byte)(rd.readByte() & 255);
            bitmap[i].green = (byte)(rd.readByte() & 255);
            bitmap[i].blue = (byte)(rd.readByte() & 255);
            bitmap[i].reserved = (byte)(rd.readByte() & 255);
        }
    }


    // Special Case

    public boolean isRop() {
    	return getSize() == 12;
    }

    public String funcNameRop() {
    	return "DibBitBltROP";
    }

    public String paramStringRop() {
    	String s = "rop="+ropName(rasterOp);
    	s += C+"d("+xDest+C+yDest+")";
    	s += C+"wh("+width+C+height+")";
    	return s;
    }

    public int renderRop (Renderer rr)  {
        return rr.patBlt (xDest, yDest, (int)width, (int)height, rasterOp);
    }

    public void readRop (RecordReader rd)  throws IOException {
        rasterOp = (int)rd.readDWord();
        rd.skipBytes(6);
        height = rd.readWord();
        width = rd.readWord();
        yDest = rd.readWord();
        xDest = rd.readWord();
        
    }


    private static Constants rops = null;

    public static String ropName (int no)  {
        if (rops == null)    rops = new Constants("ROP_");
        return rops.get(no);
    }
}
