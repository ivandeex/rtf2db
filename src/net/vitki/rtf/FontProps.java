package net.vitki.rtf;

import java.io.PrintWriter;

import net.vitki.charset.Encoding;

public class FontProps  {
    public int     no;
    public String  name;
    public int     charset;
    public FontProps ()  {
        no = 0;
        name = "";
        charset = 0;
    }
    public void print (PrintWriter out)  {
        if ("".equals(name))
            return;
        out.println("  <font no="+no+" name=["+name+"] "+
                    "charset="+Encoding.getEncoding(charset)+"("+charset+") />"
                    );
    }
}
