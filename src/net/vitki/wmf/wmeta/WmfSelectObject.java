package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfSelectObject  extends WmfRecord  {

    public int  obj_no;

    public int render (Renderer rr) {
        return rr.selectObject (obj_no, false);
    }

    public void read (RecordReader rd)  throws IOException {
        obj_no = rd.readWord();
    }

    public String toString() {
        return "SelectObject("+obj_no+")";
    }

    private static Constants objs = null;

    public static String objName (int no)  {
        if (objs == null)   objs = new Constants("OBJ_");
        return objs.get(no);
    }

}
