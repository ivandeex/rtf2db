package net.vitki.rtf;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.vitki.charset.Encoding;

public class StyleAnalyser extends BasicAnalyser
{

    public static void main (String[] args) throws Exception
    {
        String name = "d:/java/rtf/test/sps";
        if (args.length > 0)
            name = args[0];
        StyleAnalyser aa;
        aa = new StyleAnalyser();
        aa.setOutputEncoding("windows-1251");
        aa.setDebugging(true);
        aa.setTracing(true);
        aa.setSkipTracing(true);
        aa.setFlushTracing(true);
        aa.setAllFiles(name);
        long t = System.currentTimeMillis();
        aa.parse();
        t = System.currentTimeMillis() - t;
        aa.flushStreams();
        System.out.println(name+" done in "+t+"ms with "+aa.getGcCount()+"gc");
    }
    
    public StyleAnalyser() throws RtfException
    {
        super();
    }

    private int def_font_no;
    private int def_lang_no;
    private StyleSheet stylesheet;
    private InfoProps  info_props;
    private SectProps  sect_props;
    private PageProps  page_props;
    private boolean    debug_tables;
    
    public StyleSheet getStylesheet()  { return stylesheet; }
    public int getDefLang()  { return def_lang_no; }
    
    public void setDebugTables(boolean flag)  { debug_tables = flag; }
    
    public void beginParse ()  throws RtfException
    {
        super.beginParse();
        def_font_no = 0;
        def_lang_no = 0;
        stylesheet = new StyleSheet();
        info_props = new InfoProps();
        sect_props = new SectProps();
        page_props = new PageProps();
        stylesheet.setInfoProps(info_props);
        stylesheet.setSectProps(sect_props);
        stylesheet.setPageProps(page_props);
    }
    
    public void endParse()  throws RtfException
    {
        super.endParse();
    }
    
    public void beginBody()  throws RtfException
    {
        super.beginBody();
    }

    public void processHeader (RtfToken tok)  throws RtfException
    {
        switch (tok.tag)  {
            case Tag._fonttbl:
                processFontTable();
                break;
            case Tag._colortbl:
                processColorTable();
                break;
            case Tag._stylesheet:
                processStylesheet();
                break;
            case Tag._listtable:
                processListTable();
                break;
            case Tag._listoverridetable:
                processOverrideTable();
                break;
            case Tag._info:
                processInfo();
                break;
            case Tag._pgptbl:
            case Tag._revtbl:
            case Tag._rsidtbl:
            case Tag._filetbl:
                skipGroup();
                break;
            case Tag._header:
            case Tag._footer:
                skipGroup();
                break;
            /* === global properties === */
            case Tag._deff:
                def_font_no = tok.val;
                break;
            case Tag._deflang:
                def_lang_no = tok.val;
                break;
            /* === page properties === */
            case Tag._paperw:
                page_props.page_width = Util.twips2mmd(tok.val);
                break;
            case Tag._paperh:
                page_props.page_height = Util.twips2mmd(tok.val);
                break;
            case Tag._margl:
                page_props.margin_left = Util.twips2mmd(tok.val);
                break;
            case Tag._margt:
                page_props.margin_top = Util.twips2mmd(tok.val);
                break;
            case Tag._margr:
                page_props.margin_right = Util.twips2mmd(tok.val);
                break;
            case Tag._margb:
                page_props.margin_bottom = Util.twips2mmd(tok.val);
                break;
            case Tag._deftab:
                page_props.def_tab = Util.twips2mmd(tok.val);
                break;
            case Tag._pgnstart:
                page_props.start_page_no = tok.val;
                break;
            case Tag._facingp:
                page_props.facing_pages = tok.flag;
                break;
            case Tag._landscape:
                page_props.is_landscape = tok.flag;
                break;
            /* === section properties === */
            case Tag._pnseclvl:
                skipGroup();
                break;
            case Tag._sect:
                break;
            case Tag._sectd:
                sect_props.clear();
                break;
            case Tag._pgnx:
                sect_props.page_no_x = Util.twips2mmd(tok.val);
                break;
            case Tag._pgny:
                sect_props.page_no_y = Util.twips2mmd(tok.val);
                break;
            case Tag._cols:
                sect_props.col_num = tok.val;
                break;
            case Tag._sbknone:
                sect_props.sect_break = SectProps.SBK_NONE;
                break;
            case Tag._sbkcol:
                sect_props.sect_break = SectProps.SBK_COLUMN;
                break;
            case Tag._sbkpage:
                sect_props.sect_break = SectProps.SBK_PAGE;
                break;
            case Tag._sbkeven:
                sect_props.sect_break = SectProps.SBK_EVEN;
                break;
            case Tag._sbkodd:
                sect_props.sect_break = SectProps.SBK_ODD;
                break;
            case Tag._pgndec:
                sect_props.page_no_fmt = SectProps.PGN_DECIMAL;
                break;
            case Tag._pgnucrm:
                sect_props.page_no_fmt = SectProps.PGN_UC_ROMAN;
                break;
            case Tag._pgnlcrm:
                sect_props.page_no_fmt = SectProps.PGN_LC_ROMAN;
                break;
            case Tag._pgnucltr:
                sect_props.page_no_fmt = SectProps.PGN_UC_LETTER;
                break;
            case Tag._pgnlcltr:
                sect_props.page_no_fmt = SectProps.PGN_LC_LETTER;
                break;
            /* === other tags === */
            case Tag._par:
            case Tag._pard:
                setStartBody();
                processBody(tok);
                break;
            default:
                defToken(tok);
                break;
        }
    }
    
    /* === info === */

    private void processInfo()  throws RtfException
    {
        int depth = getDepth();
        wrapUnknown(true);
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._title:
                    info_props.title = getGroupText().trim();
                    break;
                case Tag._subject:
                    info_props.subject = getGroupText().trim();
                    break;
                case Tag._category:
                    info_props.category = getGroupText().trim();
                    break;
                case Tag._author:
                    info_props.author = getGroupText().trim();
                    break;
                case Tag._operator:
                    info_props.editor = getGroupText().trim();
                    break;
                case Tag._manager:
                    info_props.manager = getGroupText().trim();
                    break;
                case Tag._company:
                    info_props.company = getGroupText().trim();
                    break;
                case Tag._keywords:
                   info_props.setKeywords(getGroupText().trim());
                   break;
                case Tag._version:
                    info_props.version = tok.val;
                    break;
                case Tag._vern:
                    info_props.changeno = tok.val;
                    break;
                case Tag._creatim:
                    info_props.created = processInfoDate();
                    break;
                case Tag._revtim:
                    info_props.edited = processInfoDate();
                    break;
                case Tag._printim:
                    info_props.printed = processInfoDate();
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  info_props.print(getDebug());
    }
        
    private Date processInfoDate()  throws RtfException {
        Calendar date = new GregorianCalendar(0, 0, 0, 0, 0, 0);
        int depth = getDepth();
        while (getDepth() >= depth) {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag) {
                case Tag._yr:  date.set(Calendar.YEAR, tok.val);         break;
                case Tag._mo:  date.set(Calendar.MONTH, tok.val-1);      break;
                case Tag._dy:  date.set(Calendar.DAY_OF_MONTH, tok.val); break;
                case Tag._hr:  date.set(Calendar.HOUR_OF_DAY, tok.val);  break;
                case Tag._min: date.set(Calendar.MINUTE, tok.val);       break;
            }
        }
        return date.getTime();
    }    
    
    /* === colors === */
    
    private void processColorTable ()  throws RtfException
    {
        wrapUnknown (true);
        int depth = getDepth();
        ColorProps color = new ColorProps();
        boolean got = false;
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag.CHARS:
                    if (tok.str != null && ";".equals(tok.str) && got)  {
                        stylesheet.addColor(color);
                        color = new ColorProps();
                        got = false;
                    }
                    break;
                case Tag._red:
                    got = true;
                    color.red = tok.val;
                    break;
                case Tag._green:
                    got = true;
                    color.green = tok.val;
                    break;
                case Tag._blue:
                    got = true;
                    color.blue  = tok.val;
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  stylesheet.printColors(getDebug());
    }

    /* === fonts === */

    private void processFontTable ()  throws RtfException
    {
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag.OPEN:
                    stylesheet.addFont( processSingleFont() );
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  stylesheet.printFonts(getDebug());
    }
    
    private FontProps processSingleFont ()  throws RtfException
    {
        FontProps font = new FontProps();
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._f:
                    font.no = tok.val;
                    break;
                case Tag.CHARS:
                    if (depth == getDepth())
                        font.name = Util.trimSemicolon(tok.str);
                    break;
                case Tag._panose:
                    skipGroup();
                    break;
                case Tag._fcharset:
                    font.charset = tok.val;
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        int required_charset = Encoding.getRequiredCodepage(font.name);
        if (required_charset != 0)
            font.charset = required_charset;
        return font;
    }
    
    /* === stylesheet === */

    private void processStylesheet ()  throws RtfException
    {
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag.OPEN:
                    stylesheet.addStyle( processStyle() );
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  stylesheet.printStyles(getDebug(),true);
    }
   
    private TextProps processStyle()  throws RtfException
    {
        TextProps style = new TextProps(TextProps.T_PARA);
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._cs:
                    style.type = TextProps.T_CHAR;
                    style.additive = true;
                    style.no = tok.val;
                    break;
                case Tag._s:
                    style.type = TextProps.T_PARA;
                    style.additive = false;
                    style.no = tok.val;
                    break;
                case Tag._ds:
                    style.type = TextProps.T_SECT;
                    style.additive = false;
                    style.no = tok.val;
                    break;
                case Tag._ts:
                    style.type = TextProps.T_TABLE;
                    style.additive = false;
                    style.no = tok.val;
                    break;
                case Tag.CHARS:
                    style.name = Util.trimSemicolon(tok.str);
                    style.set(TextProps.NAME);
                    break;
                case Tag._f:
                    FontProps font = stylesheet.getFont(tok.val);
                    style.font = font.name;
                    style.set(TextProps.FONT);
                    style.charset = font.charset;
                    style.set(TextProps.CHARSET);
                    break;
                case Tag._fs:
                    style.size = Util.font2size(tok.val);
                    style.set(TextProps.SIZE);
                    break;
                case Tag._ql:
                    style.align = TextProps.ALIGN_LEFT;
                    style.set(TextProps.ALIGN);
                    break;
                case Tag._qc:
                    style.align = TextProps.ALIGN_CENTER;
                    style.set(TextProps.ALIGN);
                    break;
                case Tag._qr:
                    style.align = TextProps.ALIGN_RIGHT;
                    style.set(TextProps.ALIGN);
                    break;
                case Tag._qj:
                    style.align = TextProps.ALIGN_JUSTIFY;
                    style.set(TextProps.ALIGN);
                    break;
                case Tag._ilvl:
                    style.listlev = tok.val;
                    style.set(TextProps.LISTLEV);
                    break;
                case Tag._b:
                    style.bold = tok.flag;
                    style.set(TextProps.B);
                    break;
                case Tag._i:
                    style.italic = tok.flag;
                    style.set(TextProps.I);
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
                    style.underline = tok.flag;
                    style.set(TextProps.U);
                    break;
                case Tag._ulnone:
                    style.underline = false;
                    style.set(TextProps.U);
                    break;
                case Tag._nosupersub:
                    style.sub = false;
                    style.set(TextProps.SUB);
                    style.sup = false;
                    style.set(TextProps.SUP);
                    break;
                case Tag._sub:
                case Tag._dn:
                    style.sub = tok.flag;
                    style.set(TextProps.SUB);
                    break;
                case Tag._super:
                case Tag._up:
                    style.sup = tok.flag;
                    style.set(TextProps.SUP);
                    break;
                case Tag._strike:
                case Tag._striked:
                    style.strike = tok.flag;
                    style.set(TextProps.STRIKE);
                    break;
                case Tag._caps:
                    style.caps = tok.flag;
                    style.set(TextProps.CAPS);
                    break;
                case Tag._scaps:
                    style.scaps = tok.flag;
                    style.set(TextProps.SCAPS);
                    break;
                case Tag._v:
                    style.hidden = tok.flag;
                    style.set(TextProps.HIDDEN);
                    break;
                case Tag._cf:
                    style.color = stylesheet.getColor(tok.val);
                    style.set(TextProps.COLOR);
                    break;
                case Tag._sl:
                    style.linespc = tok.val;
                    style.set(TextProps.LINESPC);
                    break;
                case Tag._slmult:
                    style.linspmul = tok.val;
                    style.set(TextProps.LINSPMUL);
                    break;
                case Tag._li:
                    style.left_ind = Util.twips2mmd(tok.val);
                    style.set(TextProps.LEFTIND);
                    break;
                case Tag._ri:
                    style.right_ind = Util.twips2mmd(tok.val);
                    style.set(TextProps.RIGHTIND);
                    break;
                case Tag._fi:
                    style.first_ind = Util.twips2mmd(tok.val);
                    style.set(TextProps.FIRSTIND);
                    break;
                case Tag._sb:
                    style.spc_befor = Util.twips2mmd(tok.val);
                    style.set(TextProps.SPCBEFOR);
                    break;
                case Tag._sa:
                    style.spc_after = Util.twips2mmd(tok.val);
                    style.set(TextProps.SPCAFTER);
                    break;
                case Tag._additive:
                    style.additive = tok.flag;
                    break;
                case Tag._sbasedon:
                    //style.applyFrom(stylesheet.getParaStyle(no));
                    break;
                case Tag._outlinelevel:
                case Tag._keep:
                case Tag._keepn:
                    break;
                case Tag._snext:
                case Tag._itap:
                case Tag._sautoupd:
                case Tag._kerning:
                case Tag._tqc:
                case Tag._tqr:
                case Tag._shading:
                case Tag._cfpat:
                case Tag._cbpat:
                    break;
                case Tag._pn:
                case Tag._pnlvlbody:
                case Tag._pndec:
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if (style.type == TextProps.T_PARA && !style.has(TextProps.CHARSET))  {
            FontProps font = stylesheet.getFont(def_font_no);
            //style.font = font.name;
            //style.set(TextProps.FONT);
            style.charset = font.charset;
            style.set(TextProps.CHARSET);
        }
        return style;
    }
        
    /* === lists === */

    private void processListTable()  throws RtfException
    {
        wrapUnknown(true);
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._list:
                    stylesheet.addList( processSingleList() );
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  stylesheet.printLists(getDebug());
    }
     
    private ListProps processSingleList()  throws RtfException
    {
        ListProps list = new ListProps();
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._listlevel:
                    list.addLevel( processListLevel() );
                    break;
                case Tag._listtemplateid:
                    list.tmpl_id = tok.val;
                    break;
                case Tag._listid:
                    list.id = tok.val;
                    break;
                case Tag._listsimple:
                    list.setHybrid(false);
                    break;
                case Tag._listhybrid:
                    list.setHybrid(true);
                    break;
                case Tag._listname:
                    list.name = Util.trimSemicolon( getGroupText() );
                    break;
                case Tag._liststylename:
                    list.setStyleName( Util.trimSemicolon( getGroupText() ) );
                    break;
                case Tag._liststyleid:
                    list.setStyleId(tok.val);
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        return list;
    }

    private LevelProps processListLevel()  throws RtfException
    {
        LevelProps level = new LevelProps();
        level.font = stylesheet.getFont(def_font_no).name;
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._levelstartat:
                    level.start_at = tok.val;
                    break;
                case Tag._levelnfc:
                    level.setNumbering(tok.val, false);
                    break;
                case Tag._levelnfcn:
                    level.setNumbering(tok.val, true);
                    break;
                case Tag._leveljc:
                    level.setAlign(tok.val, false);
                    break;
                case Tag._leveljcn:
                    level.setAlign(tok.val, true);
                    break;
                case Tag._leveltemplateid:
                    level.tmpl_id = tok.val;
                    break;
                case Tag._levelnorestart:
                    level.continuous = true;
                    break;
                case Tag._levelfollow:
                    switch (tok.val)  {
                        case 0:   level.follow = '\t'; break;
                        case 1:   level.follow = ' ';  break;
                        default:  level.follow = '\0'; break;
                    }
                    break;
                case Tag._leveltext:
                    level.setFormat( Util.trimSemicolon( getGroupText() ) );
                    break;
                case Tag._f:
                    level.font = stylesheet.getFont(tok.val).name;
                    break;
                case Tag._levelpicture:
                case Tag._levelspace:
                case Tag._levelindent:
                case Tag._levelnumbers:
                case Tag._chbrdr:
                case Tag._brdrnone:
                case Tag._brdrcf:
                case Tag._chshdng:
                case Tag._chcfpat:
                case Tag._chcbpat:
                case Tag._fbias:
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        return level;
    }

    /* === overrides === */

    private void processOverrideTable()  throws RtfException
    {
        wrapUnknown(true);
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._listoverride:
                    stylesheet.addOver( processSingleOver() );
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        if(isDebug() && debug_tables)  stylesheet.printOvers(getDebug());
    }
     
    private ListProps processSingleOver()  throws RtfException
    {
        ListProps over = new ListProps();
        int depth = getDepth();
        int count = 0;
        int num = 0;
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._listid:
                    int saved_no = over.no;
                    ListProps ref = stylesheet.getListById(tok.val);
                    over.copyFrom(ref);
                    over.no = saved_no;
                    break;
                case Tag._listoverridecount:
                    num = tok.val;
                    break;
                case Tag._ls:
                    over.no = tok.val;
                    break;
                case Tag._lfolevel:
                    if (count >= over.level_count || count >= num)
                        skipGroup();
                    else
                        processOverLevel( over.levels[count++] );
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        return over;
    }

    private LevelProps processOverLevel (LevelProps level)  throws RtfException
    {
        int depth = getDepth();
        while (getDepth() >= depth)
        {
            RtfToken tok = nextToken();
            if (tok.tag == Tag.END)
                break;
            switch (tok.tag)
            {
                case Tag._levelstartat:
                    level.start_at = tok.val;
                    break;
                default:
                    defToken(tok);
                    break;
            }
        }
        return level;
    }

}
