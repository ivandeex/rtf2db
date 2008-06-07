package net.vitki.wmf.emeta;


import java.util.Hashtable;
import java.io.IOException;

import net.vitki.wmf.*;

public class EmfRecord implements Record {

    int   func;         // Function number (defined in WINDOWS.H)
    int   size;         // Total size of the record in WORDs
    long  params[];     // Parameter values passed to function

    public EmfRecord() {
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
    
    public int getParamCount()  {
        return (size - 8) / 4;
    }
    
    public int getParamSize()  {
        return size - 8;
    }

    public void read(RecordReader rd) throws IOException {
        int paramCount = getParamCount();
        params = new long[paramCount];
        for (int i=0; i<paramCount; i++)
            params[i] = rd.readDWord();
    }

    public int render (Renderer rr) {
        rr.log ("EMF function "+funcName(func)+" not yet ready");
        return ERROR;
    }

    public String toString() {
        return funcName(func)+paramString();
    }

    public String paramString() {
        String s = "["+params.length+"](";
        for (int i=0; i<params.length; i++)  {
            s += ""+Long.toHexString(params[i])+"h";
            if (i > 45) {
                s += ",...";
                break;
            }
            if (i < params.length-1)    s += ",";
        }
        return s+")";
    }

    private static Constants funcs = null;

    public static String funcName(int no)  {
        if (funcs == null)   funcs = new Constants("EMR_");
        return funcs.get(no);
    }

}
