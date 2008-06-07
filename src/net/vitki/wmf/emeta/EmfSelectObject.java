package net.vitki.wmf.emeta;

import java.io.IOException;

import net.vitki.wmf.*;

public class EmfSelectObject extends EmfRecord
{
    public int      obj_no;
    public boolean  standard;

    public int render (Renderer rr) {
        return rr.selectObject (obj_no, standard);
    }

    public void read (RecordReader rd)  throws IOException {
        obj_no = rd.readLong();
        standard = (obj_no & 0x80000000) != 0;
        obj_no &= 0x7fffffff;
    }

    public String toString() {
        String s = Integer.toHexString(obj_no)+"h";
        if (standard)
            s = "stock:"+stockName(obj_no);
        return "SelectObject("+s+")";
    }

    private static Constants objs = null;

    public static String objName (int no)  {
        if (objs == null)   objs = new Constants("OBJ_");
        return objs.get(no);
    }

    private static Constants stocks = null;

    public static String stockName(int no)  {
        if (stocks == null)   stocks = new Constants("STOCK_");
        return stocks.get(no);
    }

}
