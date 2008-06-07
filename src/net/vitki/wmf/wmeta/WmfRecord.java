package net.vitki.wmf.wmeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class WmfRecord implements Record {
    int   size;         // Total size of the record in WORDs
    int   func;         // Function number (defined in WINDOWS.H)
    int   params[];     // Parameter values passed to function

    public WmfRecord() {
        init(0,0);
    }

    public int getFunc() {
        return func;
    }

    public int getSize() {
        return size;
    }

    public void init (int size, int func)  {
        this.size = size;
        this.func = func;
        this.params = null;
    }

    public void read(RecordReader rd) throws IOException {
        int paramCount = size - 3;
        params = new int[paramCount];
        for (int i=0; i<paramCount; i++)
            params[i] = rd.readWord();
    }

    public int render (Renderer rr) {
        rr.log ("WMF function "+funcName(func)+" not yet ready");
        return ERROR;
    }

    public String toString() {
        return funcName(func)+paramString();
    }

    public String paramString() {
        String s = "["+params.length+"](";
        for (int i=0; i<params.length; i++)  {
            s += ""+Integer.toHexString(params[i])+"h";
            if (i > 5) {
                s += ",...";
                break;
            }
            if (i < params.length-1)    s += C;
        }
        return s+")";
    }

    private static Constants funcs = null;

    public static String funcName(int no)  {
        if (funcs == null)   funcs = new Constants("F_");
        return funcs.get(no);
    }

}
