package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfCreateFontIndirect  extends WmfRecord {

    // Long
    int       height;
    int       width;
    int       escapement;
    int       orientation;
    int       weight;
    // Byte
    boolean   italic;
    boolean   underline;
    boolean   strikeOut;
    int       charSet;
    int       outPrecision;
    int       clipPrecision;
    int       quality;
    int       pitchAndFamily;
    byte[]    faceName;

    public static final int FACE_SIZE = 32;

    public int render (Renderer rr)  {
        return rr.createFontIndirect (faceName, charSet, width, height,
                                      escapement, orientation, weight,
                                      italic, underline, strikeOut,
                                      outPrecision, clipPrecision,
                                      quality, pitchAndFamily,
                                      -1);
    }

    public void read (RecordReader rd)  throws IOException {
        height = rd.readWord();
        width = rd.readWord();
        escapement = rd.readWord();
        orientation = rd.readWord();
        weight = rd.readWord();
        italic = rd.readByte() != 0;
        underline = rd.readByte() != 0;
        strikeOut = rd.readByte() != 0;
        charSet = rd.readByte();
        outPrecision = rd.readByte();
        clipPrecision = rd.readByte();
        quality = rd.readByte();
        pitchAndFamily = rd.readByte();
        faceName = rd.readBytes(FACE_SIZE, true, false);
        rd.skipBytes (size*2 - (faceName==null ? 0 : faceName.length) - 25);
    }

    public String toString() {
        return ("CreateFontIndirect("+
                "["+(faceName==null ? "" : new String(faceName))+"],"+
                "w="+width+C+
                "h="+height+C+
                "esc="+escapement+C+
                "ori="+orientation+C+
                "wei="+weight+C+
                "i="+italic+C+
                "u="+underline+C+
                "s="+strikeOut+C+
                "cs="+charSet+C+
                "out="+outPrecision+C+
                "clip="+clipPrecision+C+
                "q="+quality+C+
                "pnf="+pitchAndFamily+
                ")");
    }

}
