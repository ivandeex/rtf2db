package net.vitki.rtf;

import net.vitki.charset.Encoding;

import org.xml.sax.SAXException;
import java.util.Vector;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;

public class DocAnalyser extends StyleAnalyser
{    
    
    public static void main (String[] args) throws Exception
    {
        String name = "qqq";
        String prefix = "/home/vit/rtf/test/"+name;
        String rules = "/home/vit/rtf/test/options.opt";
        if (args.length > 0)
            prefix = args[0];
        if (args.length > 1)
            rules = args[1];
        DocAnalyser aa;
        aa = new DocAnalyser(rules);
        aa.setAllFiles(prefix);
        long t = System.currentTimeMillis();
        aa.parse();
        aa.dump();
        t = System.currentTimeMillis() - t;
        aa.flushStreams();
        System.out.println("["+prefix+"] done in "+t+"ms with "
                           +aa.getGcCount()+"gc...");
    }
    
    /* ==== properties ==== */
    
    public static final int SHAPE_CLOSING_THRESHOLD = 4;
    
    private   TextProps      text_props;
    private   ParaProps      para_props;
    private   RtfPara        para;
    private   RtfRow         row;
    private   RtfCell        cell;
    private   Vector         cell_limits;
    private   TextProps[]    props_stack;
    protected int            props_stack_top;
    private   RtfContainer[] cont_stack;
    protected int            cont_stack_top;
    private   RtfContainer   container;
    private   RtfDocument    document;
    private   RuleConfig     rules;
    private   boolean        auto_pictures;
    private   int            pixels_per_inch;
    private   Hashtable      bookmarks;
    private   RtfShapeGroup  last_shape_group;
    private   int            shape_counter;
    private   boolean        skip_shapes;

    public DocAnalyser(RuleConfig cfg)  throws RtfException
    {
        super();
        setRules(cfg);
    }
    
    public DocAnalyser(String file_name)  throws RtfException
    {
        this(new XmlRuleConfig(file_name));
    }
    
    public void dump()  throws RtfException
    {
        try {
            document.dump(getXml(), new DumpHelper(this));
        } catch (SAXException e) {
            throw new RtfException(e);
        }
    }
    
    /* ==== internal state ==== */
    
    protected void setRules (RuleConfig cfg)  throws RtfException
    {
        rules = cfg;
        setOutputEncoding(rules.getOption("encoding"));
        setDebugging(rules.getFlag("debugging"));
        setTracing(rules.getFlag("tracing"));
        setSkipTracing(rules.getFlag("skip-tracing"));
        setFlushTracing(rules.getFlag("flush-tracing"));
        setDebugTables(rules.getFlag("debug-rtf-tables"));
        auto_pictures = rules.getFlag("auto-pictures");
        pixels_per_inch = Integer.parseInt(rules.getOption("pixels-per-inch").trim());
        skip_shapes = !(rules.getFlag("dump-shapes") || rules.getFlag("draw-shapes"));
    }
    
    public RuleConfig getRules()   { return rules; }
    public int getPPI()  { return pixels_per_inch; }
    
    public void beginParse ()  throws RtfException
    {
        super.beginParse();
        skipAsian(true);
        text_props = new TextProps();
        para_props = new ParaProps();
        props_stack_top = 0;
        props_stack = new TextProps[STACK_SIZE];
        for (int i=0; i<props_stack.length; i++) props_stack[i] = new TextProps();
        cont_stack_top = 0;
        cont_stack = new RtfContainer[STACK_SIZE];
        document = new RtfDocument();
        pushContainer(document);
        para = null;
        row = null;
        cell = null;
        cell_limits = new Vector();
        bookmarks = new Hashtable();
        last_shape_group = null;
        shape_counter = 0;
    }
    
    public void endParse()  throws RtfException
    {
        super.endParse();
        if (props_stack_top != 0)
            generateException("property stack mismatch ("+props_stack_top+")");
        if (cont_stack_top != 1)
            generateException("container stack mismatch");
    }
    
    public void beginBody()  throws RtfException
    {
        super.beginBody();
        document.setStylesheet(getStylesheet());
        document.setLanguage(getDefLang());
    }    

    void pushProps()  throws RtfException
    {
        props_stack[props_stack_top++].copyFrom(text_props);
        //prints("("+props_stack_top+")");
    }
    
    void popProps()   throws RtfException
    {
        if (props_stack_top <= 0)
            generateException("property stack exhausted");
        text_props.copyFrom(props_stack[--props_stack_top]);
        //prints("("+props_stack_top+")");
    }
    
    void pushContainer (RtfContainer cont)
    {
        cont_stack[cont_stack_top++] = container;
        container = cont;
    }

    void popContainer()   throws RtfException
    {
        if (cont_stack_top <= 0)
            generateException("container stack exhausted");
        container = cont_stack[--cont_stack_top];
    }
    
    void setBookmark (String data)
    {
        bookmarks.put (data, data);
    }
    
    public boolean isBookmark (String data)
    {
        return bookmarks.get(data) != null;
    }
    
    RtfPara getPara()
    {
        if (para == null)
            para = new RtfPara(para_props);
        return para;
    }
    
    void endPara()  throws RtfException
    {
        if (para == null)
            return;
        //para.setProps(para_props);
        container.add(para);
        para = null;
        para_props.page_break = false;
        if (last_shape_group != null && ++shape_counter >= SHAPE_CLOSING_THRESHOLD)
            last_shape_group = null;
    }
    
    /* ==== charset switching ==== */
    
    public String getGroupText()  throws RtfException
    {
        TextProps style;
        StringBuffer sb = new StringBuffer();
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            if (tok.ignore)
                skipGroup();
            else if (tok.tag == Tag.CHARS) {
                sb.append(tok.str);
                tok.str = null;
                continue;
            } else if (charsetToken(tok) == false)
                defToken(tok);
        }
        undoToken();
        String text = sb.toString();
        sb = null;
        garbageCollect (text.length());
        return text;
    }
    
    protected boolean charsetToken (RtfToken tok)  throws RtfException
    {
        TextProps style;
        switch (tok.tag) {
            case Tag._pard:
                style = getStylesheet().getParaStyle(0);
                switchCharset( style.charset );
                return true;
            case Tag._s:
                style = getStylesheet().getParaStyle(tok.val);
                switchCharset( style.charset );
                return true;
            case Tag._cs:
                style = getStylesheet().getCharStyle(tok.val);
                if (style.has(TextProps.CHARSET))
                    switchCharset( style.charset );
                return true;
            case Tag._f:
                switchCharset( getStylesheet().getFont(tok.val).charset );
                return true;
            default:
                return false;
        }
    }

    /* ==== parser ==== */
        
    public void processBody (RtfToken tok)  throws RtfException
    {
        FontProps font;
        TextProps style;
        switch (tok.tag)
        {
            case Tag._par:
                endPara();
                para_props.clearList();
                break;
            case Tag._plain:
                text_props.clear();
                break;
            case Tag._pard:
                para_props.clear();
                para_props.no = 0;
                style = getStylesheet().getParaStyle(0);
                style.usage_count++;
                switchCharset (style.charset);
                break;
            case Tag._s:
                style = getStylesheet().getParaStyle(tok.val);
                style.usage_count++;
                para_props.no = style.no;
                para_props.align = style.align;
                switchCharset (style.charset);
                break;
            case Tag._ql:
                para_props.align = TextProps.ALIGN_LEFT;
                break;
            case Tag._qr:
                para_props.align = TextProps.ALIGN_RIGHT;
                break;
            case Tag._qc:
                para_props.align = TextProps.ALIGN_CENTER;
                break;
            case Tag._qj:
                para_props.align = TextProps.ALIGN_JUSTIFY;
                break;
            case Tag._cs:
                style = getStylesheet().getCharStyle(tok.val);
                style.usage_count++;
                text_props.applyFrom(style);
                text_props.no = style.no;
                text_props.name = style.name;
                text_props.set(TextProps.NAME);
                if (style.has(TextProps.CHARSET))
                    switchCharset (text_props.charset);
                break;
            case Tag._f:
                font = getStylesheet().getFont(tok.val);
                text_props.font = font.name;
                text_props.set(TextProps.FONT);
                text_props.charset = font.charset;
                text_props.set(TextProps.CHARSET);
                switchCharset (text_props.charset);
                break;
            case Tag._fs:
                text_props.size = Util.font2size(tok.val);
                text_props.set(TextProps.SIZE);
                break;
            case Tag._b:
                text_props.bold = tok.flag;
                text_props.set(TextProps.B);
                break;
            case Tag._i:
                text_props.italic = tok.flag;
                text_props.set(TextProps.I);
                break;
            case Tag._ul:
            case Tag._ulc:
            case Tag._uld:
            case Tag._uldash:
            case Tag._uldashd:
            case Tag._uldashdd:
            case Tag._uldb:
            case Tag._ulhwave:
            case Tag._ulldash:
            case Tag._ulth:
            case Tag._ulthd:
            case Tag._ulthdash:
            case Tag._ulthdashd:
            case Tag._ulthdashdd:
            case Tag._ulthldash:
            case Tag._ululdbwave:
            case Tag._ulw:
            case Tag._ulwave:
                text_props.underline = tok.flag;
                text_props.set(TextProps.U);
                break;
            case Tag._ulnone:
                text_props.underline = false;
                text_props.set(TextProps.U);
                break;
            case Tag._nosupersub:
                text_props.sub = false;
                text_props.set(TextProps.SUB);
                text_props.sup = false;
                text_props.set(TextProps.SUP);
                break;
            case Tag._sub:
            case Tag._dn:
                text_props.sub = tok.flag;
                text_props.set(TextProps.SUB);
                break;
            case Tag._super:
            case Tag._up:
                text_props.sup = tok.flag;
                text_props.set(TextProps.SUP);
                break;
            case Tag._strike:
            case Tag._striked:
                text_props.strike = tok.flag;
                text_props.set(TextProps.STRIKE);
                break;
            case Tag._caps:
                text_props.caps = tok.flag;
                text_props.set(TextProps.CAPS);
                break;
            case Tag._scaps:
                text_props.scaps = tok.flag;
                text_props.set(TextProps.SCAPS);
                break;
            case Tag._v:
                text_props.hidden = tok.flag;
                text_props.set(TextProps.HIDDEN);
                break;
            case Tag._cf:
                text_props.color = getStylesheet().getColor(tok.val);
                text_props.set(TextProps.COLOR);
                break;
            case Tag._sl:
                text_props.linespc = tok.val;
                break;
            case Tag._slmult:
                text_props.linspmul = tok.val;
                break;
            case Tag._li:
                text_props.left_ind = Util.twips2mmd(tok.val);
                break;
            case Tag._ri:
                text_props.right_ind = Util.twips2mmd(tok.val);
                break;
            case Tag._fi:
                text_props.first_ind = Util.twips2mmd(tok.val);
                break;
            case Tag._sb:
                text_props.spc_befor = Util.twips2mmd(tok.val);
                break;
            case Tag._sa:
                text_props.spc_after = Util.twips2mmd(tok.val);
                break;
            case Tag._outlinelevel:
            case Tag._keep:
            case Tag._keepn:
            case Tag._itap:
                break;
            case Tag.CHARS:
                getPara().addText (tok.str, text_props, getStylesheet());
                tok.str = null;
                break;
            case Tag._ls:
                para_props.list_no = tok.val;
                break;
            case Tag._ilvl:
                para_props.list_level = tok.val;
                break;
            case Tag._listtext:
                processListText();
                break;
            case Tag._pn:
                processPn();
                break;
            case Tag._pntext:
                processPnText();
                break;
            case Tag._intbl:
                processStartRow();
                break;
            case Tag._trhdr:
                processStartRow();
                row.setHeader(true);
                break;
            case Tag._cell:
                processCell();
                break;
            case Tag._row:
                processRow();
                break;
            case Tag._cellx:
                cell_limits.add( new Integer( Util.twips2mmd( tok.val ) ) );
                break;
            case Tag._trowd:
                cell_limits.clear();
                break;
            case Tag._bkmkstart:
                processBookmark();
                break;
            case Tag._bkmkend:
                skipGroup();
                break;
            case Tag._field:
                processField();                
                break;
            case Tag._shp:
                processShape(last_shape_group, false);
                break;
            case Tag._shpgrp:
                processShapeGroup(last_shape_group);
                break;
            case Tag._object:
                last_shape_group = null;
                processObject();
                break;
            case Tag._shppict:
                last_shape_group = null;
                processShownPicture(null);
                break;
            case Tag._nonshppict:
                last_shape_group = null;
                skipGroup();
                break;
            case Tag._pict:
                last_shape_group = null;
                processDocPicture(null);
                break;
            case Tag._sect:
            case Tag._page:
                last_shape_group = null;
                para_props.page_break = true;
                getPara().setProps(para_props);
                endPara();
                break;
            case Tag._pagebb:
                para_props.page_break = true;
                last_shape_group = null;
                break;
            case Tag._header:
            case Tag._footer:
                last_shape_group = null;
                skipGroup();
                break;
            case Tag._footnote:
            case Tag._background:
                skipGroup();
                break;
            default:
                defToken(tok);
                break;
        }
    }
    
    /* ==== tables ==== */
    
    private void processStartRow()  throws RtfException
    {
        if (container instanceof RtfCell)
            return;        
        endPara();
        row = new RtfRow();
        cell = new RtfCell();
        pushContainer(cell);
    }
    
    private void processRow()  throws RtfException
    {
        popContainer();
        row.setCellLimits(cell_limits);
        container.add(row);
        row = null;
    }
    
    private void processCell()  throws RtfException
    {
        endPara();
        if (!(container instanceof RtfCell)) {
            generateException("container must be a cell");
        }
        row.add(cell);
        cell = new RtfCell();
        container = cell;
    }
    
    /* ==== objects and pictures ==== */
    
    private void processObject()  throws RtfException
    {
        RtfImage image = new RtfImage();
        RtfPicture pict;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String type = "none";
        String oclass = null;
        int pwidth = 100, pheight = 100, pscalex = 100, pscaley = 100;
        int depth = getDepth();        
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._objemb:      type = "embedded";         break;
                case Tag._objlink:     type = "OLE-link";         break;
                case Tag._objautlink:  type = "OLE-auto-link";    break;
                case Tag._objsub:      type = "Mac-subscriber";   break;
                case Tag._objpub:      type = "Mac-publisher";    break;
                case Tag._objicemb:    type = "Mac-IC-embedder";  break;
                case Tag._objhtml:     type = "HTML-control";     break;
                case Tag._objocx:      type = "OLE-control";      break;
                case Tag._objclass:
                    oclass = getGroupText().trim();
                    break;
                case Tag._objdata:
                    setHexStream(data, true);
                    getGroupText();
                    setHexStream(null, false);
                    break;
                case Tag._objw:
                    pwidth = tok.val;
                    break;
                case Tag._objh:
                    pheight = tok.val;
                    break;
                case Tag._objscalex:
                    pscalex = tok.val;
                    break;
                case Tag._objscaley:
                    pscaley = tok.val;
                    break;
                case Tag._pict:
                    pict = processPicture();
                    image.add(pict);
                    break;
                case Tag._shppict:
                    processShownPicture(image);
                    break;
                case Tag._object:
                case Tag._nonshppict:
                case Tag._shp:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                case Tag._sp:
                case Tag._sn:
                case Tag._sv:
                    skipGroup();
                    break;
                case Tag._result:
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        int width = Util.twips2mmd(pwidth, pscalex);
        int height = Util.twips2mmd(pheight, pscaley);
        if (oclass != null)
        {
            if (oclass.startsWith("PowerPoint."))
                type = "ppt";
            else if (oclass.startsWith("Equation."))
                type = "eqn";
        }
        String name = writeToZip (type, "e", data);
        pict = new RtfPicture (name, type, width, height, oclass);
        if (auto_pictures && image.needsAutoPicture()) {
            pict.data = data;
            image.add(pict);
            image.makeAutoPicture(this);
        } else {
            image.add(pict);
            int len = data.size();
            data = null;
            garbageCollect(len);
            image.clearRawData(this);
        }
        getPara().add(image);
    }
    
    private RtfPicture processPicture()  throws RtfException
    {
        RtfPicture pict;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String type = "none";
        int pwidth = 100, pheight = 100, pscalex = 100, pscaley = 100;
        int depth = getDepth();
        setHexStream(data,false);
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._wmetafile:
                    type = "wmf";
                    break;
                case Tag._pngblip:
                    type = "png";
                    break;
                case Tag._jpegblip:
                    type = "jpg";
                    break;
                case Tag._emfblip:
                    type = "emf";
                    break;
                case Tag._picscalex:
                    pscalex = tok.val;
                    break;
                case Tag._picscaley:
                    pscaley = tok.val;
                    break;
                case Tag._picwgoal:
                    pwidth = tok.val;
                    break;
                case Tag._pichgoal:
                    pheight = tok.val;
                    break;
                case Tag._shppict:
                case Tag._pict:
                case Tag._object:
                case Tag._nonshppict:
                case Tag._shp:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                case Tag._sp:
                case Tag._sn:
                case Tag._sv:
                    skipGroup();
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        setHexStream(null,false);
        int width = Util.twips2mmd(pwidth, pscalex);
        int height = Util.twips2mmd(pheight, pscaley);
        String name = writeToZip (type, "p", data);
        pict = new RtfPicture (name, type, width, height, null);
        if (auto_pictures && !"png".equals(type))
            pict.data = data;
        else {
            int len = data.size();
            data = null;
            garbageCollect(len);
        }
        return pict;
    }
    
    private void processShownPicture (RtfImage image)  throws RtfException
    {
        boolean is_new = image == null;
        if (is_new)
            image = new RtfImage();
        RtfPicture pict = null;
        int depth = getDepth();
        while (getDepth() >= depth)  {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            else if (tok.tag == Tag._pict)
                pict = processPicture();
            else
                defToken(tok);
        }
        if (pict != null)  {
            pict.role = "shown";
            image.add(pict);
            pict = null;
        }
        if (nextToken().tag != Tag.OPEN)
            undoToken();
        else if (nextToken().tag != Tag._nonshppict)  {
            undoToken();
            undoToken();
        } else {
            depth = getDepth();
            while (getDepth() >= depth)  {
                RtfToken tok = nextToken();
                if (tok.tag == Tag.END)
                    break;
                else if (tok.tag == Tag._pict)
                    pict = processPicture();
                else
                    defToken(tok);
            }
            if (pict != null)  {
                pict.role = "nonshown";
                image.add(pict);
                pict = null;
            }
        }
        if (is_new)  {
            if (auto_pictures)
                image.makeAutoPicture(this);
            image.clearRawData(this);
            getPara().add(image);
        }
    }
    
    private void processDocPicture (String role)  throws RtfException
    {
        RtfPicture pict = processPicture();
        RtfImage image = new RtfImage();
        if (role != null)
            pict.role = role;
        image.add(pict);
        if (auto_pictures)
            image.makeAutoPicture(this);
        image.clearRawData(this);
        getPara().add(image);
    }
    
    /* ==== lists ==== */
    
    private void processListText()  throws RtfException
    {
        StringBuffer buf = new StringBuffer();
        FontProps font;
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            switch (tok.tag)
            {
                case Tag.CHARS:
                    buf.append(tok.str);
                    tok.str = null;
                    break;
                default:
                    if (!charsetToken(tok))
                        defToken(tok);
                    break;
            }
        }
        undoToken();
        para_props.bullet = buf.toString();
    }
    
    private void processPnText()  throws RtfException
    {
        processListText();
    }
    
    private void processPn()  throws RtfException
    {
        while (true)
        {
            RtfToken tok = nextToken();
            switch (tok.tag)
            {
                case Tag._pnlvlblt:
                    para_props.list_type = LevelProps.NUM_BULLET;
                    break;
                case Tag._pnlvlbody:
                    para_props.list_type = LevelProps.NUM_ARABIC;
                    break;
                case Tag.OPEN:
                    processPn();
                    break;
                case Tag.END:
                case Tag.CLOSE:
                    return;
                default:
                    break;
            }
        }
    }
    
    /* ==== fields and references ==== */
    
    private void processBookmark()  throws RtfException
    {
        String data = getGroupText().trim();
        if (data.equals("") || data.startsWith("_Toc") || data.startsWith("_H"))
            return;
        getPara().setBookmark(data);
        setBookmark(data);
    }
    
    private void processField()  throws RtfException
    {
        RtfField field = null;
        int depth = getDepth();
        boolean skip_result = false;
        boolean embed_result = false;
        boolean finish = false;
        String result = null;
        while (getDepth() >= depth && !finish)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            switch(tok.tag)
            {
                case Tag.CHARS:
                    break;
                case Tag._fldinst:
                    String data = getGroupText();
                    field = new RtfField(data);
                    skip_result = field.isSkipResult();
                    embed_result = field.isEmbedResult();
                    break;
                case Tag._fldrslt:
                    if (skip_result)
                        skipGroup();
                    else if (embed_result)
                        result = getGroupText();
                    else
                        finish = true;
                    break;
            }
        }
        if (field != null)  {
            if (result != null)
                field.setResult(result);
            //field.print(getDebug());
            getPara().add(field);
        }
    }

    private String getFieldResult()  throws RtfException
    {
        while (true)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return null;
            switch(tok.tag)
            {
                case Tag._fldrslt:
                   return getGroupText().trim();
                case Tag.OPEN:
                    String result = getFieldResult();
                    if (result != null)
                        return result;
                    break;
                case Tag.CLOSE:
                    return null;
                default:
                    if (tok.ignore)  {
                        skipGroup();
                        return null;
                    }
                    break;
            }
        }
    }
    
    /* ==== shapes ==== */

    private void processDrawings (RtfShape shape, boolean grouped)
    throws RtfException
    {
        wrapUnknown(false);
        RtfDrawing drawing = new RtfDrawing();
        drawing.order = shape.order;
        int sx = shape.cur_x;
        int sy = shape.cur_y;
        int gx = 0;
        int gy = 0;
        int gcount = 0;
        int px = 0;
        int py = 0;
        int pcount = 0;
        int type = 0;
        boolean group_head = false;
        boolean group_body = false;
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            int mmd = tok.val;//Util.twips2mmd(tok.val);
            switch(tok.tag)
            {
                case Tag._dpgroup:
                    group_head = true;
                    group_body = false;
                    gx = 0;
                    gy = 0;
                    break;
                case Tag._dpendgroup:
                    group_head = false;
                    group_body = false;
                    gx = 0;
                    gy = 0;
                    break;
                case Tag._dparc:
                    type = RtfDrawing.TYPE_ARC;
                    break;
                case Tag._dpcallout:
                    type = RtfDrawing.TYPE_CALLOUT;
                    break;
                case Tag._dpellipse:
                    type = RtfDrawing.TYPE_ELLIPSE;
                    break;
                case Tag._dpline:
                    type = RtfDrawing.TYPE_LINE;
                    break;
                case Tag._dppolygon:
                    type = RtfDrawing.TYPE_POLYGON;
                    break;
                case Tag._dppolyline:
                    type = RtfDrawing.TYPE_POLYLINE;
                    break;
                case Tag._dprect:
                    type = RtfDrawing.TYPE_RECTANGLE;
                    break;
                case Tag._dptxbx:
                    type = RtfDrawing.TYPE_TEXTBOX;
                    break;
                case Tag._dppolycount:
                    pcount = tok.val;
                    break;
                case Tag._dpptx:
                    px = mmd;
                    py = 0;
                    break;
                case Tag._dppty:
                    py = mmd;
                    if (--pcount >= 0)
                        drawing.addPolyPoint( px, py );
                    px = py = 0;
                    break;
                case Tag._dpx:
                    if (group_head)
                        gx = mmd;
                    else
                        drawing.left = mmd + gx;                    
                    break;
                case Tag._dpy:
                    if (group_head)
                        gy = mmd;
                    else
                        drawing.top = mmd + gy;
                    break;
                case Tag._dpxsize:
                    drawing.width = mmd;
                    break;
                case Tag._dpysize:
                    drawing.height = mmd;
                    break;
                case Tag._dplinew:
                    drawing.penwidth = mmd;
                    break;
                case Tag._dplinecor:
                    drawing.processLineColor().red = tok.val;
                    break;
                case Tag._dplinecog:
                    drawing.processLineColor().green = tok.val;
                    break;
                case Tag._dplinecob:
                    drawing.processLineColor().blue = tok.val;
                    break;
                case Tag._dpfillfgcr:
                    drawing.processFillForeColor().red = tok.val;
                    break;
                case Tag._dpfillfgcg:
                    drawing.processFillForeColor().green = tok.val;
                    break;
                case Tag._dpfillfgcb:
                    drawing.processFillForeColor().blue = tok.val;
                    break;
                case Tag._dpfillbgcr:
                    drawing.processFillBackColor().red = tok.val;
                    break;
                case Tag._dpfillbgcg:
                    drawing.processFillBackColor().green = tok.val;
                    break;
                case Tag._dpfillbgcb:
                    drawing.processFillBackColor().blue = tok.val;
                    break;
                case Tag._dpfillpat:
                    drawing.pattern = tok.val;
                    break;
                case Tag._dptxbxtext:
                    processDrawingText(drawing);
                    break;
                case Tag._dptxlrtb:
                    drawing.text_flow = RtfDrawing.FLOW_NORMAL;
                    break;
                case Tag._dptxtbrl:
                    drawing.text_flow = RtfDrawing.FLOW_FLIP_HOR;
                    break;
                case Tag._dptxbtlr:
                    drawing.text_flow = RtfDrawing.FLOW_FLIP_VERT;
                    break;
                case Tag._dptxlrtbv:
                    drawing.text_flow = RtfDrawing.FLOW_ROTATE_LEFT;
                    break;
                case Tag._dptxtbrlv:
                    drawing.text_flow = RtfDrawing.FLOW_ROTATE_RIGHT;
                    break;
                case Tag._pict:
                case Tag._object:
                case Tag._shppict:
                case Tag._nonshppict:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                case Tag._sp:
                case Tag._shptxt:
                    skipGroup();
                    break;
                default:
                    defToken(tok);
                    break;
            }
            if (type != 0)  {
                if (group_head)
                    group_body = true;
                group_head = false;
                drawing.order = shape.order;
                if (drawing.type != 0)
                    shape.add( drawing );
                drawing = new RtfDrawing();
                drawing.type = type;
                drawing.order= shape.order;
                type = 0;
            }
        }
        if (drawing.type != 0)
            shape.add( drawing );
    }
    
    private void processDrawingText (RtfDrawing drawing)  throws RtfException
    {
        StringBuffer buf = new StringBuffer();
        int depth = getDepth();
        boolean para_flag = false;
        String face = text_props.font;
        int size = text_props.size;
        int color = text_props.color;
        FontProps font;
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            switch(tok.tag)
            {
                case Tag.CHARS:
                    if (para_flag)
                        buf.append(RtfDrawing.PARAGRAPH);
                    para_flag = false;
                    buf.append(tok.str);
                    tok.str = null;
                    break;
                case Tag._par:
                    if (para_flag)
                        buf.append(RtfDrawing.PARAGRAPH);
                    para_flag = true;
                    break;
                case Tag._f:
                    font = getStylesheet().getFont(tok.val);
                    face = font.name;
                    switchCharset( font.charset );
                    break;
                case Tag._fs:
                    size = Util.font2size(tok.val);
                    break;
                case Tag._cf:
                    color = getStylesheet().getColor(tok.val);
                    break;
                case Tag._pict:
                case Tag._object:
                case Tag._shppict:
                case Tag._nonshppict:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                case Tag._sp:
                case Tag._shptxt:
                    skipGroup();
                    break;
                default:
                    if (!charsetToken(tok))
                        defToken(tok);
                    break;
            }
        }
        String str = buf.toString();
        drawing.text = str;
        drawing.text_font = face;
        drawing.text_size = size;
        drawing.text_color = color;
    }
    
    private void processShape (RtfShapeGroup group, boolean grouped)  throws RtfException
    {        
        if (skip_shapes)  {
            skipGroup();
            return;
        }
        boolean is_new = group == null;
        if (is_new)
            group = new RtfShapeGroup();
        RtfShape shape = new RtfShape();
        wrapUnknown(false);
        int depth = getDepth();
        RtfPicture pict = null;
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            int mmd = Util.twips2mmd(tok.val);
            switch(tok.tag)
            {
                case Tag._shpleft:
                    shape.left = mmd;
                    if (!grouped && shape.left < group.left)
                        group.left = shape.left;
                    shape.cur_x = mmd;
                    break;
                case Tag._shptop:
                    shape.top = mmd;
                    if (!grouped && shape.top < group.top)
                        group.top = shape.top;
                    shape.cur_y = mmd;
                    break;
                case Tag._shpright:
                    shape.right = mmd;
                    if (!grouped && shape.right > group.right)
                        group.right = shape.right;
                    break;
                case Tag._shpbottom:
                    shape.bottom = mmd;
                    if (!grouped && shape.bottom > group.bottom)
                        group.bottom = shape.bottom;
                    break;
                case Tag._shpz:
                    shape.order = tok.val;
                    break;
                case Tag._shpinst:
                    break;
                case Tag._shprslt:
                    if (shape.canDrawWord97())
                        skipGroup();
                    break;
                case Tag._do:
                    processDrawings (shape, grouped);
                    break;
                case Tag._pict:
                case Tag._object:
                case Tag._shppict:
                case Tag._nonshppict:
                case Tag._shp:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                    skipGroup();
                    break;
                case Tag._sp:
                    processShapeProp(shape);
                    break;
                case Tag._shptxt:
                    processShapeText(shape);
                    break;
                case Tag._sn:
                case Tag._sv:
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        group.add(shape);
        if (is_new)
            getPara().add(group);
        last_shape_group = group;
        shape_counter = 0;
    }
    
    private void processShapeGroup (RtfShapeGroup group)  throws RtfException
    {
        if (skip_shapes)  {
            skipGroup();
            return;
        }
        //group = null;
        boolean is_new = group == null;
        if (is_new)
            group = new RtfShapeGroup();
        wrapUnknown(false);
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            int mmd = Util.twips2mmd(tok.val);
            switch(tok.tag)
            {
                case Tag._shpleft:
                    if (is_new)
                        group.left = mmd;
                    else if (mmd < group.left)
                        group.left = mmd;
                    group.cur_x = mmd;
                    break;
                case Tag._shptop:
                    if (is_new)
                        group.top = mmd;
                    else if (mmd < group.top)
                        group.top = mmd;
                    group.cur_y = mmd;
                    break;
                case Tag._shpright:
                    if (is_new)
                        group.right = mmd;
                    else if (mmd > group.right)
                        group.right = mmd;
                    break;
                case Tag._shpbottom:
                    if (is_new)
                        group.bottom = mmd;
                    else if (mmd > group.bottom)
                        group.bottom = mmd;
                    break;
                case Tag._shp:
                    processShape (group, true);
                    break;
                case Tag._shpinst:
                    break;
                case Tag._shprslt:
                    if (group.canDrawWord97())
                        skipGroup();
                    break;
                case Tag._shpgrp:
                    RtfShapeGroup grp = new RtfShapeGroup();
                    group.add(grp);
                    processShapeGroup(grp);
                    break;
                case Tag._pict:
                case Tag._object:
                case Tag._shppict:
                case Tag._nonshppict:
                case Tag._blipuid:
                case Tag._picprop:
                    skipGroup();
                    break;
                case Tag._sp:
                    processShapeProp(group);
                    break;
                case Tag._shptxt:
                    processShapeText(group);
                    break;
                case Tag._sn:
                case Tag._sv:
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if (is_new)
            getPara().add(group);
        last_shape_group = group;
        shape_counter = 0;
    }
    
    private void processShapeProp (RtfShape shape)  throws RtfException
    {
        String name = null;
        String value = null;
        int depth = getDepth();
        while (getDepth() >= depth)  {
            RtfToken tok = nextToken();
            switch(tok.tag)
            {
                case Tag.END:
                    return;
                case Tag._sn:
                    name = getGroupText();
                    break;
                case Tag._sv:
                    if (nextToken().tag != Tag.OPEN)
                        undoToken();
                    else if (nextToken().tag != Tag._pict)
                        { undoToken(); undoToken(); }
                    else {
                        processDocPicture("inside-shape");
                        value = "{picture}";
                        break;
                    }
                    value = getGroupText();
                    break;
            }
        }
        if (name == null || value == null)
            return;
        int tag = DrawTag.getNo(name);
        if (DrawTag.getSkip(tag))
            return;
        DrawProp prop = new DrawProp(tag, value);
        shape.addProperty (prop);
    }
    
    private void processShapeText (RtfShape shape)  throws RtfException
    {
        StringBuffer buf = new StringBuffer();
        int depth = getDepth();
        boolean para_flag = false;
        String face = text_props.font;
        int size = text_props.size;
        int color = text_props.color;
        FontProps font;
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                return;
            switch(tok.tag)
            {
                case Tag.CHARS:
                    if (para_flag)
                        buf.append(RtfShape.PARAGRAPH);
                    para_flag = false;
                    buf.append(tok.str);
                    tok.str = null;
                    break;
                case Tag._par:
                    if (para_flag)
                        buf.append(RtfShape.PARAGRAPH);
                    para_flag = true;
                    break;
                case Tag._f:
                    font = getStylesheet().getFont(tok.val);
                    face = font.name;
                    switchCharset( font.charset );
                    break;
                case Tag._fs:
                    size = Util.font2size(tok.val);
                    break;
                case Tag._cf:
                    color = getStylesheet().getColor(tok.val);
                    break;
                case Tag._pict:
                case Tag._object:
                case Tag._shppict:
                case Tag._nonshppict:
                case Tag._shpgrp:
                case Tag._blipuid:
                case Tag._picprop:
                case Tag._sp:
                case Tag._shptxt:
                    skipGroup();
                    break;
                default:
                    if (!charsetToken(tok))
                        defToken(tok);
                    break;
            }
        }
        String str = buf.toString();
        shape.text_string = str;
        shape.text_face = face;
        shape.text_size = size;
        shape.text_color = color;
    }
    
}
