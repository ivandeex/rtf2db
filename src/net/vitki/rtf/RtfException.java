package net.vitki.rtf;

public class RtfException extends Exception
{
    public RtfException() {
        super();
    }
    public RtfException(String message) {
        this(message,0,0);
    }
    public RtfException(String message, int line, int pos) {
        super("["+line+":"+pos+"]: "+message);
    }
    public RtfException(Exception e, int line, int pos)  {
        this("Exception: "+e.getMessage(), line, pos);
    }
    public RtfException(Exception e)  {
        this(e,0,0);
    }
    static final long serialVersionUID = 0x12340005;
}
