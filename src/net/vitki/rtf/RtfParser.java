package net.vitki.rtf;

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import net.vitki.charset.Encoding;


public class RtfParser
{

    /** constants **/

    // allocation sizes
    public static final int CACHE_SIZE   = 2000000;
    public static final int CHAR_SIZE    = 8192;
    public static final int KW_SIZE      = 64;
    public static final int PARAM_SIZE   = 128;
    public static final int STACK_SIZE   = 32;
    public static final int TAB_SIZE     = 8;
    public static final int QUEUE_SIZE   = 4;
    public static final int GC_THRESHOLD = 16240;

    // RTF destination state
    public static final int DS_NORM = 0;
    public static final int DS_SKIP = 1;
    public static final int DS_UPR  = 2;

    // RTF internal state
    public static final int IS_NORM = 0;
    
    // RTF character modes
    public static final int CM_NORMAL = 0;
    public static final int CM_SKIP   = 1;
    public static final int CM_STREAM = 2;

    /** initialization **/

    public RtfParser() throws RtfException   {
        initialized = false;
    }
    
    public void setInputStream (InputStream is) throws RtfException
    {
        try  {
            Reader isr = new InputStreamReader (is, "ISO-8859-1");
            this.in = new BufferedReader (isr, CACHE_SIZE);
        } catch (UnsupportedEncodingException e)  {
            throw new RtfException ("ISO charser is not supported", 0, 0);
        }
    }

    /** internal state **/

    private Reader in;
    private int    line_no;
    private int    pos_no;

    private boolean       is_ignorable;
    private int           uc_skip;

    private int           dest_state;

    private int           cur_cs;
    private StringBuffer  char_buf;

    private Context[]     stack_arr;
    private int           stack_top;

    private int  prev_ch;

    private StringBuffer  keyword_buf;
    private StringBuffer  param_buf;

    private boolean wrap_unknown;
    private boolean skip_asian;
    private boolean skip_until_linefeed;

    private int new_line_char;

    private int  cur_tag;
    private int  cur_state;
    private int  cur_target;
    private String cur_dest;
    private int  tok_depth;
    
    private int  char_mode;
    private int  hex_nibble;
    private ByteArrayOutputStream  hex_stream;
    
    private boolean skip_tracing;
    private boolean tracing;
    private int gc_count;

    private boolean initialized = false;

    /** token queue **/
    
    private RtfToken[] tok_queue;
    private int tok_head;
    private int tok_tail;
    private int tok_len;
    
    private void addToken (int tag, String str, int val, boolean flag, boolean ignore)
    throws RtfException
    {
        if (tok_len == QUEUE_SIZE)
            generateException ("token queue overflow");
        RtfToken tok = tok_queue[tok_head];
        if (++tok_head >= QUEUE_SIZE)
            tok_head = 0;
        tok_len++;
        tok.tag = tag;
        tok.str = str;
        tok.val = val;
        tok.flag = flag;
        tok.ignore = ignore;
    }
    
    public void undoToken()  throws RtfException
    {
        if (tok_len == QUEUE_SIZE)
            generateException ("token queue overflow for undo");
        tok_tail = tok_tail == 0 ? QUEUE_SIZE - 1 : tok_tail - 1;
        tok_len++;
        RtfToken tok = tok_queue[tok_tail];
        switch (tok.tag)  {
            case Tag.CLOSE:
                tok_depth++;
                pushProps();
                break;
            case Tag.OPEN:
                tok_depth--;
                popProps();
                break;
        }
    }
    
    /** state machine **/

    public void init()  throws RtfException
    {
        char_mode = CM_NORMAL;
        hex_nibble = -1;
        hex_stream = null;
        
        tok_depth = 0;
        tok_head = tok_tail = tok_len = 0;
        tok_queue = new RtfToken[QUEUE_SIZE];
        for (int i=0; i<QUEUE_SIZE; i++)   tok_queue[i] = new RtfToken();
        
        line_no = pos_no = 0;
        stack_top = 0;
        is_ignorable = false;
        wrap_unknown = false;
        skip_asian = false;
        skip_tracing = false;
        new_line_char = 0;
        uc_skip = 0;
        dest_state = DS_NORM;
        stack_arr = new Context[STACK_SIZE];
        for (int i=0; i<STACK_SIZE; i++)
            stack_arr[i] = new Context();
        prev_ch = -1;
        keyword_buf = new StringBuffer(KW_SIZE);
        param_buf = new StringBuffer(PARAM_SIZE);
        char_buf = new StringBuffer(CHAR_SIZE);
        cur_cs = 0;
        switchCharset(Encoding.CHARSET_ANSI);
        cur_tag    = 0;
        cur_state  = 0;
        cur_target = 0;
        cur_dest   = null;
        pushState();
        initialized = true;
        addToken (Tag.BEGIN, null, 0, false, false);
    }

    private void pushState()  throws RtfException
    {
        if (stack_top >= STACK_SIZE-1)
            generateException ("stack overflow");
        Context ctx = stack_arr[++stack_top];
        ctx.dest_state   = dest_state;
        ctx.uc_skip      = uc_skip;
        ctx.cs = cur_cs;
        ctx.tag = cur_tag;
        ctx.state = cur_state;
        ctx.target = cur_target;
        ctx.dest = cur_dest;
        if (initialized)  {
            flushText();
            if (dest_state == DS_NORM)
                addToken (Tag.OPEN, null, 0, false, false);
        }

    }

    private void popState()  throws RtfException
    {
        if (stack_top < 2)
            generateException ("stack underflow");
        Context ctx = stack_arr[stack_top--];
        if (initialized)  {
            flushText();
            if (ctx.target != cur_target)
                addToken (Tag.C_TARGET, null, cur_target, false, false);
            if (ctx.dest_state == DS_NORM)  {
                addToken (Tag.CLOSE, null, 0, false, false);
            }
        }
        dest_state   = ctx.dest_state;
        uc_skip      = ctx.uc_skip;
        switchCharset (ctx.cs);
        cur_tag    = ctx.tag;
        cur_state  = ctx.state;
        cur_target = ctx.target;
        cur_dest   = ctx.dest;
    }
    
    static class Context
    {
        String dest;
        int dest_state;
        int uc_skip;
        int cs;
        int tag;
        int state;
        int target;
        Context()    {
            dest_state = 0;
            uc_skip = 0;
            cs = 0;
            tag = 0;
            state = 0;
            target = 0;
            dest = null;
        }
    }
    
    /** parsing **/

    public RtfToken nextToken()  throws RtfException
    {
        while (tok_len == 0)
        {
            int ch = getc();
            if (ch == -1)
            {
                flushText();
                addToken(Tag.END, null, 0, false, false);
                if (stack_top < 1)    generateException("stack underflow");
                if (stack_top > 1)    generateException("unmatched brace");
                break;
            }
            switch (ch)
            {
                case '{':
                    pushState();
                    break;
                case '}':
                    popState();
                    break;
                case '\\':
                    parseKeyword();
                    break;
                case '\r':
                case '\n':
                    skip_until_linefeed = false;
                    break;
                case '\t':
                    kwTab();
                    break;
                default:
                    ansiChar(ch);
                    break;
            }
        }
        if (stack_top < 1)    generateException ("stack underflow");
        RtfToken tok = tok_queue[tok_tail];
        if (++tok_tail >= QUEUE_SIZE)
            tok_tail = 0;
        tok_len--;
        switch (tok.tag)  {
            case Tag.OPEN:
                tok_depth++;
                pushProps();
                break;
            case Tag.CLOSE:
                tok_depth--;
                popProps();
                break;
        }
        return tok;
    
    }

    /** internal methods **/

    private void parseKeyword()  throws RtfException
    {
        int ch;
        boolean has_param = false;
        int param = 0;
        String v_str;
        int  v_int;
        boolean v_flag;
        
        keyword_buf.setLength(0);
        param_buf.setLength(0);
        
        // read keyword
        ch = getc();
        if (ch == -1)
            generateException("premature end of file");
        switch (ch) {
            case '*':
                if (dest_state == DS_NORM)
                    is_ignorable = true;
                return;
            case ':':      // index entry
                flushText();
                if (dest_state == DS_NORM)
                    addToken(Tag.INDEX, null, 0, false, is_ignorable);
                is_ignorable = false;
                return;
            case '|':     // Mac Word 5.1 formula start
                flushText();
                if (dest_state == DS_NORM)
                    addToken(Tag.FORMULA, null, 0, false, is_ignorable);
                is_ignorable = false;
                return;
            case '\'':
                int c1 = getc(), c2 = getc();
                if (c1 >= '0' && c1 <= '9')       c1 -= '0';
                else if (c1 >= 'a' && c1 <= 'f')  c1 -= 'a' - 10;
                else if (c1 >= 'A' && c1 <= 'F')  c1 -= 'A' - 10;
                else    generateException("invalid hex");
                if (c2 >= '0' && c2 <= '9')       c2 -= '0';
                else if (c2 >= 'a' && c2 <= 'f')  c2 -= 'a' - 10;
                else if (c2 >= 'A' && c2 <= 'F')  c2 -= 'A' - 10;
                else    generateException("invalid hex");
                if (dest_state == DS_NORM)
                    ansiChar((c1 << 4) | c2);
                return;
            case '\\': ucChar('\\');     return;
            case '{':  ucChar('{');      return;
            case '}':  ucChar('}');      return;
            case '-':  ucChar(0x00ad);   return;  // optional hyphen
            case '_':  ucChar(0x2011);   return;  // no-break hyphen
            case '~':  ucChar(0x00a0);   return;  // no-break space
            case '\t': ucChar('\t');     return;
            case '\n': kwPar(1);  return;
            case '\r': kwPar(2);  return;
            default:
                break;
        }
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))  {
            while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))  {
                keyword_buf.append( (char)ch );
                ch = getc();
            }
        } else {
            generateException("unknown control symbol");
        }
        if (ch == '-')   {
            param_buf.append('-');
            ch = getc();
            if (ch == -1)
                generateException("premature end of file");
        }
        if (ch >= '0' && ch <= '9')  {
            has_param = true;     // a digit after the control means we have a parameter
            while (ch >= '0' && ch <= '9')  {
                param_buf.append( (char)ch );
                ch = getc();
            }
        }
        if (ch != ' ')    prev_ch = ch;
        // handle unicode
        switch (keyword_buf.length())  {
            case 1:
                if (keyword_buf.charAt(0) == 'u')  {
                    try {
                        if (uc_skip == 0)   uc_skip = 1;
                        v_int = Integer.parseInt( param_buf.toString() );
                        ucChar(v_int < 0 ? v_int + 65536 : v_int);
                    } catch (NumberFormatException e)  {
                        generateException("bad unicode format");
                    }
                    return;
                }
                break;
            case 2:
                if (keyword_buf.charAt(0) == 'u')  {
                    switch (keyword_buf.charAt(1))  {
                        case 'c':  // uc
                            try {
                                uc_skip = Integer.parseInt( param_buf.toString() );
                                if (ch == ' ' && uc_skip > 0)  // VERY EMPIRIC !!!
                                    uc_skip--;                 // ABSOLUTELY NO WARRANTY !!!
                            } catch (NumberFormatException e)  {
                                generateException("bad unicode format");
                            }
                            return;
                        case 'd':  // ud
                            if (dest_state == DS_UPR)
                                dest_state = DS_NORM;
                            return;
                    }
                }
                break;
        }
        // translate keyword
        int  tag = Tag.getNo( keyword_buf.toString() );
        if (tag == 0)  {
            System.err.print("<"+keyword_buf.toString()+">");
            flushText();
            if (is_ignorable)
                dest_state = DS_SKIP;
            is_ignorable = false;
            return;
        }
        if (has_param)  {
            v_str = param_buf.toString();
            switch (Tag.getType(tag)) {
                case Tag.T_TOGGLE:
                    if (param_buf.length() == 1 && param_buf.charAt(0) == '0')  {
                        v_flag = false;
                        v_int = 0;
                    } else {
                        v_flag = true;
                        v_int = 1;
                    }
                    break;
                case Tag.T_FLAG:
                    v_flag = true;
                    v_int = 1;
                    break;
                case Tag.T_VALUE:
                    v_int = 0;
                    try {
                        v_int = Integer.parseInt(v_str);
                    } catch (NumberFormatException e)  {
                        generateException("bad parameter format");
                    }
                    v_flag = (v_int != 0);
                    break;
                case Tag.T_SYMBOL:
                default:
                    v_int = 0;
                    v_flag = false;
                    break;
            }
            if (v_str != null && v_str.length()>0 && Character.isDigit(v_str.charAt(0)))  {
                if (v_str.charAt(0) == '0')
                    v_flag = false;
                try { v_int = Integer.decode(v_str).intValue(); }
                catch (Exception e) {}
            }
        } else {
            v_str = null;
            switch (Tag.getType(tag)) {
                case Tag.T_TOGGLE:
                case Tag.T_FLAG:
                    v_int = 1;
                    v_flag = true;
                    break;
                default:
                    v_int = 0;
                    v_flag = false;
                    break;
            }
        }
        switch (tag) {
            // control symbols
            case Tag._zwbo:       ucChar(0x200b);  break;
            case Tag._zwnbo:      ucChar(0xfeff);  break;
            case Tag._emdash:     ucChar(0x2014);  break;
            case Tag._endash:     ucChar(0x2013);  break;
            case Tag._qmspace:    ucChar(0x0);  break;
            case Tag._emspace:    ucChar(0x2003);  break;
            case Tag._enspace:    ucChar(0x0020);  break;
            case Tag._bullet:     ucChar(0x2022);  break;
            case Tag._lquote:     ucChar(0x2018);  break;
            case Tag._rquote:     ucChar(0x2019);  break;
            case Tag._ldblquote:  ucChar(0x201c);  break;
            case Tag._rdblquote:  ucChar(0x201d);  break;
            case Tag._ltrmark:    ucChar(0x200e);  break;
            case Tag._rtlmark:    ucChar(0x200f);  break;
            case Tag._zwj:        ucChar(0x200d);  break;
            case Tag._zwnj:       ucChar(0x200c);  break;
            case Tag._line:       ucChar(0x000d);  break;
            case Tag._tab:   kwTab();       break;
            case Tag._par:   kwPar(3);      break;
            case Tag._bin:   kwBin(v_int);  break;
            // unicode handling
            case Tag._upr:
                flushText();
                if (dest_state == DS_NORM)
                    dest_state = DS_UPR;
                break;
                // charsets
            case Tag._pc:
                switchCharset(Encoding.CHARSET_PC437);
                break;
            case Tag._pca:
                switchCharset(850);
                break;
            case Tag._mac:
                switchCharset(Encoding.CHARSET_MAX);
                break;
            case Tag._ansi:
                switchCharset(Encoding.CHARSET_ANSI);
                break;
            case Tag._ansicpg:
                switchCharset(v_int);
                break;
                // other keywords
            default:
                if (skip_asian && Tag.getIgnorable(tag))
                    break;
                flushText();
                if (dest_state == DS_NORM)  {
                    cur_tag = tag;
                    addToken(tag, v_str, v_int, v_flag, is_ignorable);
                }
                break;
        }
        is_ignorable = false;
    }

    private void kwPar (int type) throws RtfException
    {
        if (dest_state == DS_NORM)  {
            cur_tag = Tag._par;
            if (new_line_char > 0)
                ucChar (new_line_char);
            flushText();
            addToken (Tag._par, String.valueOf(type), type, false, false);
        }
    }

    private void kwTab() throws RtfException
    {
        ucChar(9);
    }

    private void kwBin (int n) throws RtfException
    {
        int saved_skip = uc_skip;
        int skip = uc_skip > 0 ? 1 : 0;
        for (int i=0; i<n; i++)  {
            int ch = getc();
            if (ch == -1)
                generateException ("end of file in binary data");
            uc_skip = skip;
            ansiChar (ch);
        }
        uc_skip = saved_skip - skip;
    }

    private int getc()  throws RtfException
    {
        if (prev_ch != -1)  {
            int ch = prev_ch;
            prev_ch = -1;
            return ch;
        }
        try  {
            int ch = in.read();
            switch (ch)
            {
                case '\n':
                    line_no++;
                    pos_no = 0;
                    break;
                case '\r':
                    pos_no = 0;
                    break;
                case '\t':
                    pos_no += TAB_SIZE - (pos_no % TAB_SIZE);
                    break;
                default:
                    pos_no++;
                    break;
            }
            return ch;
        } catch (IOException e) {
            generateException ("I/O exception");
        }
        return -1;
    }

    /** character handling **/

    private void ansiChar (int ch)  throws RtfException
    {
        if (uc_skip > 0)  {
            --uc_skip;
            return;
        }
        if (dest_state != DS_NORM)
            return;
        if (ch < 0 || ch > 255)  {
            generateException ("illegal ansi character");
            return;
        }
        switch(char_mode) {
            case CM_NORMAL:
                char_buf.append( Encoding.decode(cur_cs, ch) );
                break;
            case CM_SKIP:
                break;
            case CM_STREAM:
                writeStream(ch);
                break;
        }
    }

    private void ucChar (int ch)  throws RtfException
    {
        if (dest_state != DS_NORM)    return;
        switch(char_mode) {
            case CM_NORMAL:
                char_buf.append( (char)ch );
                break;
            case CM_SKIP:
                break;
            case CM_STREAM:
                writeStream(ch);
                break;
        }
    }

    private void flushText()  throws RtfException
    {
        if (char_buf.length() == 0)
            return;
        switch (char_mode)  {
            case CM_NORMAL:
                if (dest_state != DS_NORM)
                    break;
                addToken(Tag.CHARS, char_buf.toString(), 0, false, false);
                break;
            case CM_SKIP:
                break;
            case CM_STREAM:
                for (int i=0; i<char_buf.length(); i++)
                    writeStream( (int)char_buf.charAt(i) );
                break;
        }
        char_buf.setLength(0);
    }
    
    private void writeStream (int v)  throws RtfException
    {
        if (skip_until_linefeed)
            return;
        if (v==' ' || v=='\t' || v=='\r' || v=='\n' || v=='\f')
            return;
        if (v >= '0' && v <= '9')
            v -= '0';
        else if (v >= 'a' && v <= 'f')
            v -= 'a' - 10;
        else if (v >= 'A' && v <= 'F')
            v -= 'A' - 10;
        else
            generateException ("prohibited character in hex stream");
        if (hex_nibble == -1)  {
            hex_nibble = v << 4;
            return;
        }
        v += hex_nibble;
        hex_nibble = -1;
        hex_stream.write(v);
    }
    
    public void setHexStream (ByteArrayOutputStream baos, boolean skip_first)
    throws RtfException
    {
        flushText();
        if (baos == null)  {
            if (hex_stream == null)
                generateException ("hex stream already closed");
            if (hex_nibble != -1)
                generateException ("hanging nibble in hex stream");
            char_mode = CM_NORMAL;
            hex_stream = null;
            return;
        }
        if (hex_stream != null)
            generateException ("hex stream already open");
        hex_stream = baos;
        char_mode = CM_STREAM;
        hex_nibble = -1;
        skip_until_linefeed = skip_first;
    }

    //public final void skipGroup()    { dest_state = DS_SKIP; }

    public void skipGroup()  throws RtfException
    {
        flushText();
        int save_char_mode = char_mode;
        boolean save_tracing = tracing;
        if (!skip_tracing)  {
            char_mode = CM_SKIP;
            tracing = false;
        }
        int depth = tok_depth;
        while (tok_depth >= depth)  {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
        }
        undoToken();
        char_mode = save_char_mode;
        tracing = save_tracing;
    }
    
    public String getGroupText()  throws RtfException
    {
        StringBuffer sb = new StringBuffer();
        int depth = tok_depth;
        while (tok_depth >= depth)  {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            if (tok.ignore)  {
                skipGroup();
                continue;
            }
            if (tok.tag == Tag.CHARS)
                sb.append(tok.str);
            tok.str = null;
        }
        undoToken();
        String text = sb.toString();
        sb = null;
        garbageCollect (text.length());
        return text;
    }

    /** user-usable methods **/

    public final void garbageCollect (int len)   {
        if (len == -1 || len >= GC_THRESHOLD)  {
            System.gc();
            gc_count++;
        }
    }
        
    protected final void switchCharset (int cs)  throws RtfException
    {
        cur_cs = cs < 0 || cs > Encoding.CHARSET_MAX ? 0 : cs;
    }

    protected final void generateException (String msg)  throws RtfException
    {
        throw new RtfException (msg, line_no, pos_no);
    }

    protected final void generateException (Exception e)  throws RtfException
    {
        throw new RtfException (e, line_no, pos_no);
    }

    protected final void defToken (RtfToken tok)  throws RtfException
    {
        if (wrap_unknown && Tag.getType(tok.tag) == Tag.T_DEST)
            skipGroup();
    }

    protected final void setTarget (int new_target)  throws RtfException
    {
        if (new_target != cur_target)  {
            cur_target = new_target;
            addToken (Tag.O_TARGET, null, new_target, true, false);
        }
    }

    public final int getTarget()    { return cur_target; }
    public final String getDest()    { return cur_dest; }
    public final void setDest (String dest)   { cur_dest = dest; }
    public final int getTag()    { return cur_tag; }
    public final int getState()   { return cur_state; }
    public final void setState (int new_state)  { cur_state = new_state; }
    public final void setNewline (char ch)   { new_line_char = (int)ch; }
    public final void wrapUnknown (boolean flag)    { wrap_unknown = flag; }
    public final void skipAsian (boolean flag)   { skip_asian = flag; }
    public final boolean getIgnorable()    { return is_ignorable; }
    public final void setSkipTracing (boolean flag)  { skip_tracing = flag; }
    public final void setTracing (boolean flag)  { tracing = flag; }
    public final boolean getTracing()  { return tracing; }
    public final int getDepth()  { return tok_depth; }
    public final int getGcCount()  { return gc_count; }
    
    void pushProps()  throws RtfException {}
    void popProps()   throws RtfException {}

}
