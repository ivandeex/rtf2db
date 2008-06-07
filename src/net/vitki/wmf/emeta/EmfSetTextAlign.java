package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSetTextAlign extends EmfRecord
{
    int  align;

    public int render (Renderer rr) {
        return rr.setTextAlign (align);
    }

    public void read (RecordReader rd)  throws IOException {
        align = rd.readLong();
    }

    public String toString() {
        return ("SetTextAlign("+
                getUpdatePosAsString()+"+"+
                getHorAlignAsString()+"+"+
                getVertAlignAsString()+
                ")");
    }

    public boolean getUpdatePos()  {
        return (align & 1) == Constants.TA_UPDATECP;
    }

    public int getHorAlign()  {
        return (align & 6);
    }

    public String getUpdatePosAsString()  {
        return getUpdatePos() ? "UpdatePos" : "NoUpdatePos";
    }

    public String getHorAlignAsString()  {
        switch (getHorAlign()) {
            case Constants.TA_LEFT:    return "LEFT";
            case Constants.TA_RIGHT:   return "RIGHT";
            case Constants.TA_CENTER:  return "CENTER";
            default:         return "HOR?";
        }
    }

    public int getVertAlign()  {
        return (align & 24);
    }

    public String getVertAlignAsString()  {
        switch (getVertAlign())  {
            case Constants.TA_TOP:      return "TOP";
            case Constants.TA_BOTTOM:   return "BOTTOM";
            case Constants.TA_BASELINE: return "BASELINE";
            default:          return "VERT?";
        }
    }
}
