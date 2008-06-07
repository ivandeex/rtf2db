package net.vitki.wmf;

import java.io.IOException;

public interface Record {
    public void   read (RecordReader rd) throws IOException;
    public String toString();
    public String paramString();
    public int    getSize();
    public int    getFunc();
    public void   init (int size, int func);
    public int    render (Renderer renderer);

    public static final int OK    = 0;
    public static final int ERROR = -1;

    public static final String C = ",";
}
