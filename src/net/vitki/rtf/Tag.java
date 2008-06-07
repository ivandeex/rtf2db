package net.vitki.rtf;

import java.util.Vector;
import java.util.Hashtable;

public abstract class Tag  {

    private static Hashtable   name2no      = null;
    private static String[]    no2name      = null;
    private static int[]       no2type      = null;
    private static boolean[]   no2ignorable = null;

    private static final int MAX = 2000;

    public static final int S = 0x00000;
    public static final int E = 0x80000;

    public static final int T_NONE   = 0;
    public static final int T_DEST   = 1;
    public static final int T_VALUE  = 2;
    public static final int T_FLAG   = 3;
    public static final int T_TOGGLE = 4;
    public static final int T_SYMBOL = 5;

    public static final int NONE     = 0;
    public static final int OPEN     = 1;
    public static final int CLOSE    = 2;
    public static final int BEGIN    = 3;
    public static final int END      = 4;
    public static final int CHARS    = 5;
    public static final int O_TARGET = 6;
    public static final int C_TARGET = 7;
    public static final int INDEX    = 8;
    public static final int FORMULA  = 9;

    private static void add (String name, int value, int type)  {
        name = name.trim();
        name2no.put (name, new Integer(value));
        no2name[value] = name;
        no2type[value] = type;
    }

    public static int getNo (String name)  {
        Object obj = name2no.get (name);
        return (obj == null ? 0 : ((Integer)obj).intValue());
    }

    public static String getName (int no)  {
        return no2name[no];
    }

    public static int getType (int no)  {
        return no2type[no];
    }
    
    public static boolean getIgnorable (int no)  {
        return no2ignorable[no];
    }

    private static void setupTables ()  {
        if (name2no != null)    return;
        name2no = new Hashtable();
        no2name = new String[MAX];
        no2type = new int[MAX];
        no2ignorable = new boolean[MAX];
        for (int i=0; i<MAX; i++)  no2name[i] = "";
        for (int i=0; i<MAX; i++)  no2type[i] = 0;
        for (int i=0; i<MAX; i++)  no2ignorable[i] = false;
        add ( "[NONE]"    , NONE    , T_NONE );
        add ( "[OPEN]"    , OPEN    , T_NONE );
        add ( "[CLOSE]"   , CLOSE   , T_NONE );
        add ( "[BEGIN]"   , BEGIN   , T_NONE );
        add ( "[END]"     , END     , T_NONE );
        add ( "[CHARS]"   , CHARS   , T_NONE );
        add ( "[INDEX]"   , INDEX   , T_NONE );
        add ( "[FORMULA]" , FORMULA , T_NONE );
        words();
    }

    static {
        setupTables();
    }

    public static final int _ab                 =  110; // [T]     Associated Character Properties
    public static final int _absh               =  111; // [V]     Positioned Objects and Frames
    public static final int _abslock            =  112; // [F/E]   Positioned Objects and Frames
    public static final int _absnoovrlp         =  113; // [TN/K]  Positioned Objects and Frames
    public static final int _absw               =  114; // [V]     Positioned Objects and Frames
    public static final int _acaps              =  115; // [T]     Associated Character Properties
    public static final int _acccomma           =  116; // [T/E]   Font (Character) Formatting Properties
    public static final int _accdot             =  117; // [T/E]   Font (Character) Formatting Properties
    public static final int _accnone            =  118; // [T/E]   Font (Character) Formatting Properties
    public static final int _acf                =  119; // [V]     Associated Character Properties
    public static final int _additive           =  120; // [F]     Style Sheet
    public static final int _adjustright        =  121; // [F/S]   Section Formatting Properties
    public static final int _adn                =  122; // [V]     Associated Character Properties
    public static final int _aenddoc            =  123; // [F]     Document Formatting Properties
    public static final int _aendnotes          =  124; // [F]     Document formatting Properties
    public static final int _aexpnd             =  125; // [V]     Associated Character Properties
    public static final int _af                 =  126; // [V]     Associated Character Properties
    public static final int _affixed            =  127; // [F/E]   Paragraph Formatting Properties
    public static final int _afs                =  128; // [V]     Associated Character Properties
    public static final int _aftnbj             =  129; // [F]     Document Formatting Properties
    public static final int _aftncn             =  130; // [D]     Document Formatting Properties
    public static final int _aftnnalc           =  131; // [F]     Document Formatting Properties
    public static final int _aftnnar            =  132; // [F]     Document Formatting Properties
    public static final int _aftnnauc           =  133; // [F]     Document Formatting Properties
    public static final int _aftnnchi           =  134; // [F]     Document Formatting Properties
    public static final int _aftnnchosung       =  135; // [F/S]   Document Formatting Properties
    public static final int _aftnncnum          =  136; // [F/S]   Document Formatting Properties
    public static final int _aftnndbar          =  137; // [F/S]   Document Formatting Properties
    public static final int _aftnndbnum         =  138; // [F/S]   Document Formatting Properties
    public static final int _aftnndbnumd        =  139; // [F/S]   Document Formatting Properties
    public static final int _aftnndbnumk        =  140; // [F/S]   Document Formatting Properties
    public static final int _aftnndbnumt        =  141; // [F/S]   Document Formatting Properties
    public static final int _aftnnganada        =  142; // [F/S]   Document Formatting Properties
    public static final int _aftnngbnum         =  143; // [F/S]   Document Formatting Properties
    public static final int _aftnngbnumd        =  144; // [F/S]   Document Formatting Properties
    public static final int _aftnngbnumk        =  145; // [F/S]   Document Formatting Properties
    public static final int _aftnngbnuml        =  146; // [F/S]   Document Formatting Properties
    public static final int _aftnnrlc           =  147; // [F]     Document Formatting Properties
    public static final int _aftnnruc           =  148; // [F]     Document Formatting Properties
    public static final int _aftnnzodiac        =  149; // [F/S]   Document Formatting Properties
    public static final int _aftnnzodiacd       =  150; // [F/S]   Document Formatting Properties
    public static final int _aftnnzodiacl       =  151; // [F/S]   Document Formatting Properties
    public static final int _aftnrestart        =  152; // [F]     Document Formatting Properties
    public static final int _aftnrstcont        =  153; // [F]     Document Formatting Properties
    public static final int _aftnsep            =  154; // [D]     Document Formatting Properties
    public static final int _aftnsepc           =  155; // [D]     Document Formatting Properties
    public static final int _aftnstart          =  156; // [V]     Document Formatting Properties
    public static final int _aftntj             =  157; // [F]     Document Formatting Properties
    public static final int _ai                 =  158; // [T]     Associated Character Properties
    public static final int _alang              =  159; // [V]     Associated Character Properties
    public static final int _allowfieldendsel   =  160; // [F/T]   Document Formatting Properties
    public static final int _allprot            =  161; // [F]     Document Formatting Properties
    public static final int _alntblind          =  162; // [F/K]   Document Formatting Properties
    public static final int _alt                =  163; // [F]     Style Sheet
    public static final int _animtext           =  164; // [VN/S]  Font (Character) Formatting Properties
    public static final int _annotation         =  165; // [D]     Comments (Annotations)
    public static final int _annotprot          =  166; // [F]     Document Formatting Properties
    public static final int _ansi               =  167; // [F]     Character Set
    public static final int _ansicpg            =  168; // [VN/S]  Unicode RTF
    public static final int _aoutl              =  169; // [T]     Associated Character Properties
    public static final int _ApplyBrkRules      =  170; // [F/T]   Document Formatting Properties
    public static final int _ascaps             =  171; // [T]     Associated Character Properties
    public static final int _ashad              =  172; // [T]     Associated Character Properties
    public static final int _asianbrkrule       =  173; // [F/T]   Document Formatting Properties
    public static final int _aspalpha           =  174; // [T/E]   Paragraph Formatting Properties
    public static final int _aspnum             =  175; // [T/E]   Paragraph Formatting Properties
    public static final int _astrike            =  176; // [T]     Associated Character Properties
    public static final int _atnauthor          =  177; // [D/T]   Comments (Annotations)
    public static final int _atndate            =  178; // [D]     Comments (Annotations)
    public static final int _atnicn             =  179; // [D]     Comments (Annotations)
    public static final int _atnid              =  180; // [D]     Comments (Annotations)
    public static final int _atnparent          =  181; // [D/T]   Comments (Annotations)
    public static final int _atnref             =  182; // [D]     Comments (Annotations)
    public static final int _atntime            =  183; // [D]     Comments (Annotations)
    public static final int _atrfend            =  184; // [D]     Comments (Annotations)
    public static final int _atrfstart          =  185; // [D]     Comments (Annotations)
    public static final int _aul                =  186; // [T]     Associated Character Properties
    public static final int _auld               =  187; // [T]     Associated Character Properties
    public static final int _auldb              =  188; // [T]     Associated Character Properties
    public static final int _aulnone            =  189; // [T]     Associated Character Properties
    public static final int _aulw               =  190; // [T]     Associated Character Properties
    public static final int _aup                =  191; // [V]     Associated Character Properties
    public static final int _author             =  192; // [D]     Information Group
    public static final int _b                  =  193; // [T]     Font (Character) Formatting Properties
    public static final int _background         =  194; // [D/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _bdbfhdr            =  195; // [F/S]   Document Formatting Properties
    public static final int _bdrrlswsix         =  196; // [F/K]   Document Formatting Properties
    public static final int _bgbdiag            =  197; // [F]     Paragraph Shading
    public static final int _bgcross            =  198; // [F]     Paragraph Shading
    public static final int _bgdcross           =  199; // [F]     Paragraph Shading
    public static final int _bgdkbdiag          =  200; // [F]     Paragraph Shading
    public static final int _bgdkcross          =  201; // [F]     Paragraph Shading
    public static final int _bgdkdcross         =  202; // [F]     Paragraph Shading
    public static final int _bgdkfdiag          =  203; // [F]     Paragraph Shading
    public static final int _bgdkhoriz          =  204; // [F]     Paragraph Shading
    public static final int _bgdkvert           =  205; // [F]     Paragraph Shading
    public static final int _bgfdiag            =  206; // [F]     Paragraph Shading
    public static final int _bghoriz            =  207; // [F]     Paragraph Shading
    public static final int _bgvert             =  208; // [F]     Paragraph Shading
    public static final int _bin                =  209; // [V]     Pictures
    public static final int _binfsxn            =  210; // [V]     Section Formatting Properties
    public static final int _binsxn             =  211; // [V]     Section Formatting Properties
    public static final int _bkmkcolf           =  212; // [V]     Bookmarks
    public static final int _bkmkcoll           =  213; // [V]     Bookmarks
    public static final int _bkmkend            =  214; // [D]     Bookmarks
    public static final int _bkmkpub            =  215; // [F]     Macintosh Edition Manager Publisher Objects
    public static final int _bkmkstart          =  216; // [D]     Bookmarks
    public static final int _bliptag            =  217; // [VN/S]  Pictures
    public static final int _blipuid            =  218; // [V/S]   Pictures
    public static final int _blipupi            =  219; // [VN/S]  Pictures
    public static final int _blue               =  220; // [V]     Color Table
    public static final int _bookfold           =  221; // [F/T]   Document Formatting Properties
    public static final int _bookfoldrev        =  222; // [F/T]   Document Formatting Properties
    public static final int _bookfoldsheets     =  223; // [VN/T]  Document Formatting Properties
    public static final int _box                =  224; // [F]     Paragraph Borders
    public static final int _brdrart            =  225; // [VN/S]  Document Formatting Properties
    public static final int _brdrb              =  226; // [F]     Paragraph Borders
    public static final int _brdrbar            =  227; // [F]     Paragraph Borders
    public static final int _brdrbtw            =  228; // [F]     Paragraph Borders
    public static final int _brdrcf             =  229; // [V]     Paragraph Borders
    public static final int _brdrdash           =  230; // [F]     Paragraph Borders
    public static final int _brdrdashd          =  231; // [F/S]   Paragraph Text
    public static final int _brdrdashdd         =  232; // [F/S]   Paragraph Text
    public static final int _brdrdashdotstr     =  233; // [F/S]   Paragraph Text
    public static final int _brdrdashsm         =  234; // [F/S]   Paragraph Text
    public static final int _brdrdb             =  235; // [F]     Paragraph Borders
    public static final int _brdrdot            =  236; // [F]     Paragraph Borders
    public static final int _brdremboss         =  237; // [F/S]   Paragraph Text
    public static final int _brdrengrave        =  238; // [F/S]   Paragraph Text
    public static final int _brdrframe          =  239; // [F/S]   Paragraph Borders
    public static final int _brdrhair           =  240; // [F]     Paragraph Borders
    public static final int _brdrinset          =  241; // [F/K]   Paragraph Text
    public static final int _brdrl              =  242; // [F]     Paragraph Borders
    public static final int _brdrnil            =  243; // [F/T]   Paragraph Borders
    public static final int _brdroutset         =  244; // [F/K]   Paragraph Text
    public static final int _brdrr              =  245; // [F]     Paragraph Borders
    public static final int _brdrs              =  246; // [F]     Paragraph Borders
    public static final int _brdrsh             =  247; // [F]     Paragraph Borders
    public static final int _brdrt              =  248; // [F]     Paragraph Borders
    public static final int _brdrtbl            =  249; // [F/T]   Paragraph Borders
    public static final int _brdrth             =  250; // [F]     Paragraph Borders
    public static final int _brdrthtnlg         =  251; // [F/S]   Paragraph Text
    public static final int _brdrthtnmg         =  252; // [F/S]   Paragraph Text
    public static final int _brdrthtnsg         =  253; // [F/S]   Paragraph Text
    public static final int _brdrtnthlg         =  254; // [F/S]   Paragraph Text
    public static final int _brdrtnthmg         =  255; // [F/S]   Paragraph Text
    public static final int _brdrtnthsg         =  256; // [F/S]   Paragraph Text
    public static final int _brdrtnthtnlg       =  257; // [F/S]   Paragraph Text
    public static final int _brdrtnthtnmg       =  258; // [F/S]   Paragraph Text
    public static final int _brdrtnthtnsg       =  259; // [F/S]   Paragraph Text
    public static final int _brdrtriple         =  260; // [F/S]   Paragraph Text
    public static final int _brdrw              =  261; // [V]     Paragraph Borders
    public static final int _brdrwavy           =  262; // [F/S]   Paragraph Text
    public static final int _brdrwavydb         =  263; // [F/S]   Paragraph Text
    public static final int _brkfrm             =  264; // [F]     Document Formatting Properties
    public static final int _brsp               =  265; // [V]     Paragraph Borders
    public static final int _bullet             =  266; // [S]     Special Characters
    public static final int _buptim             =  267; // [D]     Information Group
    public static final int _bxe                =  268; // [F]     Index Entries
    public static final int _caps               =  269; // [T]     Font (Character) Formatting Properties
    public static final int _category           =  270; // [D/E]   Information Group
    public static final int _cb                 =  271; // [V]     Font (Character) Formatting Properties
    public static final int _cbpat              =  272; // [V]     Paragraph Shading
    public static final int _cchs               =  273; // [V]     Font (Character) Formatting Properties
    public static final int _cell               =  274; // [S]     Special Characters
    public static final int _cellx              =  275; // [V]     Table Definitions
    public static final int _cf                 =  276; // [V]     Font (Character) Formatting Properties
    public static final int _cfpat              =  277; // [V]     Paragraph Shading
    public static final int _cgrid              =  278; // [VN/S]  Font (Character) Formatting Properties
    public static final int _charrsid           =  279; // [VN/T]  Track Changes (Revision Marks)
    public static final int _charscalex         =  280; // [V/E]   Font (Character) Formatting Properties
    public static final int ___charscalex       =  281; // [VN/S]  Character Text
    public static final int _chatn              =  282; // [S]     Special Characters
    public static final int _chbgbdiag          =  283; // [F/S]   Character Text
    public static final int _chbgcross          =  284; // [F/S]   Character Text
    public static final int _chbgdcross         =  285; // [F/S]   Character Text
    public static final int _chbgdkbdiag        =  286; // [F/S]   Character Text
    public static final int _chbgdkcross        =  287; // [F/S]   Character Text
    public static final int _chbgdkdcross       =  288; // [F/S]   Character Text
    public static final int _chbgdkfdiag        =  289; // [F/S]   Character Text
    public static final int _chbgdkhoriz        =  290; // [F/S]   Character Text
    public static final int _chbgdkvert         =  291; // [F/S]   Character Text
    public static final int _chbgfdiag          =  292; // [F/S]   Character Text
    public static final int _chbghoriz          =  293; // [F/S]   Character Text
    public static final int _chbgvert           =  294; // [F/S]   Character Text
    public static final int _chbrdr             =  295; // [F/S]   Character Text
    public static final int _chcbpat            =  296; // [VN/S]  Character Text
    public static final int _chcfpat            =  297; // [VN/S]  Character Text
    public static final int _chdate             =  298; // [S]     Special Characters
    public static final int _chdpa              =  299; // [S]     Special Characters
    public static final int _chdpl              =  300; // [S]     Special Characters
    public static final int _chftn              =  301; // [S]     Special Characters
    public static final int _chftnsep           =  302; // [S]     Special Characters
    public static final int _chftnsepc          =  303; // [S]     Special Characters
    public static final int _chpgn              =  304; // [S]     Special Characters
    public static final int _chshdng            =  305; // [VN/S]  Character Text
    public static final int _chtime             =  306; // [S]     Special Characters
    public static final int _clbgbdiag          =  307; // [F]     Table Definitions
    public static final int _clbgcross          =  308; // [F]     Table Definitions
    public static final int _clbgdcross         =  309; // [F]     Table Definitions
    public static final int _clbgdkbdiag        =  310; // [F]     Table Definitions
    public static final int _clbgdkcross        =  311; // [F]     Table Definitions
    public static final int _clbgdkdcross       =  312; // [F]     Table Definitions
    public static final int _clbgdkfdiag        =  313; // [F]     Table Definitions
    public static final int _clbgdkhor          =  314; // [F]     Table Definitions
    public static final int _clbgdkvert         =  315; // [F]     Table Definitions
    public static final int _clbgfdiag          =  316; // [F]     Table Definitions
    public static final int _clbghoriz          =  317; // [F]     Table Definitions
    public static final int _clbgvert           =  318; // [F]     Table Definitions
    public static final int _clbrdrb            =  319; // [F]     Table Definitions
    public static final int _clbrdrl            =  320; // [F]     Table Definitions
    public static final int _clbrdrr            =  321; // [F]     Table Definitions
    public static final int _clbrdrt            =  322; // [F]     Table Definitions
    public static final int _clcbpat            =  323; // [V]     Table Definitions
    public static final int _clcbpatraw         =  324; // [VN/T]  Table Definitions
    public static final int _clcfpat            =  325; // [V]     Table Definitions
    public static final int _clcfpatraw         =  326; // [VN/T]  Table Definitions
    public static final int _cldgll             =  327; // [F/E]   Table Definitions
    public static final int _cldglu             =  328; // [F/E]   Table Definitions
    public static final int _clFitText          =  329; // [F/K]   Table Definitions
    public static final int _clftsWidth         =  330; // [VN/K]  Table Definitions
    public static final int _clmgf              =  331; // [F]     Table Definitions
    public static final int _clmrg              =  332; // [F]     Table Definitions
    public static final int _clNoWrap           =  333; // [F/K]   Table Definitions
    public static final int _clpadb             =  334; // [VN/K]  Table Definitions
    public static final int _clpadfb            =  335; // [VN/K]  Table Definitions
    public static final int _clpadfl            =  336; // [VN/K]  Table Definitions
    public static final int _clpadfr            =  337; // [VN/K]  Table Definitions
    public static final int _clpadft            =  338; // [VN/K]  Table Definitions
    public static final int _clpadl             =  339; // [VN/K]  Table Definitions
    public static final int _clpadr             =  340; // [VN/K]  Table Definitions
    public static final int _clpadt             =  341; // [VN/K]  Table Definitions
    public static final int _clshdng            =  342; // [V]     Table Definitions
    public static final int _clshdngraw         =  343; // [V/T]   Table Definitions
    public static final int _clshdrawnil        =  344; // [F/T]   Table Definitions
    public static final int _cltxbtlr           =  345; // [F/E]   Table Definitions
    public static final int _cltxlrtb           =  346; // [F/E]   Table Definitions
    public static final int ___cltxlrtb         =  347; // [F/S]   Table Definitions
    public static final int _cltxlrtbv          =  348; // [F/E]   Table Definitions
    public static final int _cltxtbrl           =  349; // [F/S]   Table Definitions
    public static final int ___cltxtbrl         =  350; // [F/E]   Table Definitions
    public static final int _cltxtbrlv          =  351; // [F/E]   Table Definitions
    public static final int _clvertalb          =  352; // [F/E]   Table Definitions
    public static final int _clvertalc          =  353; // [F/E]   Table Definitions
    public static final int _clvertalt          =  354; // [F/E]   Table Definitions
    public static final int _clvmgf             =  355; // [F/E]   Table Definitions
    public static final int _clvmrg             =  356; // [F/E]   Table Definitions
    public static final int _clwWidth           =  357; // [VN/K]  Table Definitions
    public static final int _collapsed          =  358; // [F]     Paragraph Formatting Properties
    public static final int _colno              =  359; // [V]     Section Formatting Properties
    public static final int _colortbl           =  360; // [D]     Color Table
    public static final int _cols               =  361; // [V]     Section Formatting Properties
    public static final int _colsr              =  362; // [V]     Section Formatting Properties
    public static final int _colsx              =  363; // [V]     Section Formatting Properties
    public static final int _column             =  364; // [S]     Special Characters
    public static final int _colw               =  365; // [V]     Section Formatting Properties
    public static final int _comment            =  366; // [D]     Information Group
    public static final int _company            =  367; // [D/E]   Information Group
    public static final int _cpg                =  368; // [V]     Code Page Support
    public static final int _crauth             =  369; // [VN/S]  Character Text
    public static final int _crdate             =  370; // [VN/S]  Character Text
    public static final int _creatim            =  371; // [D]     Information Group
    public static final int _cs                 =  372; // [V]     Font (Character) Formatting Properties
    public static final int _ctrl               =  373; // [F]     Style Sheet
    public static final int _cts                =  374; // [VN/K]  Document Formatting Properties
    public static final int _cufi               =  375; // [VN/K]  Paragraph Formatting Properties
    public static final int _culi               =  376; // [VN/K]  Paragraph Formatting Properties
    public static final int _curi               =  377; // [VN/K]  Paragraph Formatting Properties
    public static final int _cvmme              =  378; // [F]     Document Formatting Properties
    public static final int _datafield          =  379; // [D]     Fields
    public static final int _date               =  380; // [F/S]   Fields
    public static final int _dbch               =  381; // [F/E]   Associated Character Properties
    public static final int _deff               =  382; // [V]     Font Table
    public static final int _defformat          =  383; // [F]     Document Formatting Properties
    public static final int _deflang            =  384; // [V]     Document Formatting Properties
    public static final int _deflangfe          =  385; // [V/S]   Document Formatting Properties
    public static final int _defshp             =  386; // [F/K]   Pictures
    public static final int _deftab             =  387; // [V]     Document Formatting Properties
    public static final int _deleted            =  388; // [T]     Font (Character) Formatting Properties
    public static final int _delrsid            =  389; // [VN/T]  Track Changes (Revision Marks)
    public static final int _dfrauth            =  390; // [VN/S]  Paragraph Text
    public static final int _dfrdate            =  391; // [VN/S]  Paragraph Text
    public static final int _dfrmtxtx           =  392; // [V]     Positioned Objects and Frames
    public static final int _dfrmtxty           =  393; // [V]     Positioned Objects and Frames
    public static final int _dfrstart           =  394; // [V/S]   Paragraph Text
    public static final int _dfrstop            =  395; // [V/S]   Paragraph Text
    public static final int _dfrxst             =  396; // [V/S]   Paragraph Text
    public static final int _dghorigin          =  397; // [VN/E]  Document Formatting Properties
    public static final int _dghshow            =  398; // [VN/E]  Document Formatting Properties
    public static final int _dghspace           =  399; // [VN/E]  Document Formatting Properties
    public static final int _dgmargin           =  400; // [F/S]   Document Formatting Properties
    public static final int _dgsnap             =  401; // [F/E]   Document Formatting Properties
    public static final int _dgvorigin          =  402; // [VN/E]  Document Formatting Properties
    public static final int _dgvshow            =  403; // [VN/E]  Document Formatting Properties
    public static final int _dgvspace           =  404; // [VN/E]  Document Formatting Properties
    public static final int _dibitmap           =  405; // [V]     Pictures
    public static final int _dn                 =  406; // [V]     Font (Character) Formatting Properties
    public static final int _dntblnsbdb         =  407; // [F/S]   Document Formatting Properties
    public static final int _do                 =  408; // [D]     Drawing Objects
    public static final int _dobxcolumn         =  409; // [F]     Drawing Objects
    public static final int _dobxmargin         =  410; // [F]     Drawing Objects
    public static final int _dobxpage           =  411; // [F]     Drawing Objects
    public static final int _dobymargin         =  412; // [F]     Drawing Objects
    public static final int _dobypage           =  413; // [F]     Drawing Objects
    public static final int _dobypara           =  414; // [F]     Drawing Objects
    public static final int _doccomm            =  415; // [D]     Information Group
    public static final int _doctemp            =  416; // [F]     Document Formatting Properties
    public static final int _doctype            =  417; // [VN/S]  Document Formatting Properties
    public static final int _docvar             =  418; // [D/E]   Document Variables
    public static final int _dodhgt             =  419; // [V]     Drawing Objects
    public static final int _dolock             =  420; // [F]     Drawing Objects
    public static final int _donotshowcomments  =  421; // [F/T]   Document Formatting Properties
    public static final int _donotshowinsdel    =  422; // [F/T]   Document Formatting Properties
    public static final int _donotshowmarkup    =  423; // [F/T]   Document Formatting Properties
    public static final int _donotshowprops     =  424; // [F/T]   Document Formatting Properties
    public static final int _dpaendhol          =  425; // [F]     Drawing Objects
    public static final int _dpaendl            =  426; // [V]     Drawing Objects
    public static final int _dpaendsol          =  427; // [F]     Drawing Objects
    public static final int _dpaendw            =  428; // [V]     Drawing Objects
    public static final int _dparc              =  429; // [F]     Drawing Objects
    public static final int _dparcflipx         =  430; // [F]     Drawing Objects
    public static final int _dparcflipy         =  431; // [F]     Drawing Objects
    public static final int _dpastarthol        =  432; // [F]     Drawing Objects
    public static final int _dpastartl          =  433; // [V]     Drawing Objects
    public static final int _dpastartsol        =  434; // [F]     Drawing Objects
    public static final int _dpastartw          =  435; // [V]     Drawing Objects
    public static final int _dpcallout          =  436; // [F]     Drawing Objects
    public static final int _dpcoa              =  437; // [V]     Drawing Objects
    public static final int _dpcoaccent         =  438; // [F]     Drawing Objects
    public static final int _dpcobestfit        =  439; // [F]     Drawing Objects
    public static final int _dpcoborder         =  440; // [F]     Drawing Objects
    public static final int _dpcodabs           =  441; // [V]     Drawing Objects
    public static final int _dpcodbottom        =  442; // [F]     Drawing Objects
    public static final int _dpcodcenter        =  443; // [F]     Drawing Objects
    public static final int _dpcodescent        =  444; // [V]     Drawing Objects
    public static final int _dpcodtop           =  445; // [F]     Drawing Objects
    public static final int _dpcolength         =  446; // [V]     Drawing Objects
    public static final int _dpcominusx         =  447; // [F]     Drawing Objects
    public static final int _dpcominusy         =  448; // [F]     Drawing Objects
    public static final int _dpcooffset         =  449; // [V]     Drawing Objects
    public static final int _dpcosmarta         =  450; // [F]     Drawing Objects
    public static final int _dpcotdouble        =  451; // [F]     Drawing Objects
    public static final int _dpcotright         =  452; // [F]     Drawing Objects
    public static final int _dpcotsingle        =  453; // [F]     Drawing Objects
    public static final int _dpcottriple        =  454; // [F]     Drawing Objects
    public static final int _dpcount            =  455; // [V]     Drawing Objects
    public static final int _dpellipse          =  456; // [F]     Drawing Objects
    public static final int _dpendgroup         =  457; // [F]     Drawing Objects
    public static final int _dpfillbgcb         =  458; // [V]     Drawing Objects
    public static final int _dpfillbgcg         =  459; // [V]     Drawing Objects
    public static final int _dpfillbgcr         =  460; // [V]     Drawing Objects
    public static final int _dpfillbggray       =  461; // [V]     Drawing Objects
    public static final int _dpfillbgpal        =  462; // [F]     Drawing Objects
    public static final int _dpfillfgcb         =  463; // [V]     Drawing Objects
    public static final int _dpfillfgcg         =  464; // [V]     Drawing Objects
    public static final int _dpfillfgcr         =  465; // [V]     Drawing Objects
    public static final int _dpfillfggray       =  466; // [V]     Drawing Objects
    public static final int _dpfillfgpal        =  467; // [F]     Drawing Objects
    public static final int _dpfillpat          =  468; // [V]     Drawing Objects
    public static final int _dpgroup            =  469; // [F]     Drawing Objects
    public static final int _dpline             =  470; // [F]     Drawing Objects
    public static final int _dplinecob          =  471; // [V]     Drawing Objects
    public static final int _dplinecog          =  472; // [V]     Drawing Objects
    public static final int _dplinecor          =  473; // [V]     Drawing Objects
    public static final int _dplinedado         =  474; // [F]     Drawing Objects
    public static final int _dplinedadodo       =  475; // [F]     Drawing Objects
    public static final int _dplinedash         =  476; // [F]     Drawing Objects
    public static final int _dplinedot          =  477; // [F]     Drawing Objects
    public static final int _dplinegray         =  478; // [V]     Drawing Objects
    public static final int _dplinehollow       =  479; // [F]     Drawing Objects
    public static final int _dplinepal          =  480; // [F]     Drawing Objects
    public static final int _dplinesolid        =  481; // [F]     Drawing Objects
    public static final int _dplinew            =  482; // [V]     Drawing Objects
    public static final int _dppolycount        =  483; // [V]     Drawing Objects
    public static final int _dppolygon          =  484; // [F]     Drawing Objects
    public static final int _dppolyline         =  485; // [F]     Drawing Objects
    public static final int _dpptx              =  486; // [V]     Drawing Objects
    public static final int _dppty              =  487; // [V]     Drawing Objects
    public static final int _dprect             =  488; // [F]     Drawing Objects
    public static final int _dproundr           =  489; // [F]     Drawing Objects
    public static final int _dpshadow           =  490; // [F]     Drawing Objects
    public static final int _dpshadx            =  491; // [V]     Drawing Objects
    public static final int _dpshady            =  492; // [V]     Drawing Objects
    public static final int _dptxbtlr           =  493; // [F/E]   Drawing Objects
    public static final int _dptxbx             =  494; // [F]     Drawing Objects
    public static final int _dptxbxmar          =  495; // [V]     Drawing Objects
    public static final int _dptxbxtext         =  496; // [D]     Drawing Objects
    public static final int _dptxlrtb           =  497; // [F/E]   Drawing Objects
    public static final int _dptxlrtbv          =  498; // [F/E]   Drawing Objects
    public static final int _dptxtbrl           =  499; // [F/E]   Drawing Objects
    public static final int _dptxtbrlv          =  500; // [F/E]   Drawing Objects
    public static final int _dpx                =  501; // [V]     Drawing Objects
    public static final int _dpxsize            =  502; // [V]     Drawing Objects
    public static final int _dpy                =  503; // [V]     Drawing Objects
    public static final int _dpysize            =  504; // [V]     Drawing Objects
    public static final int _dropcapli          =  505; // [V]     Positioned Objects and Frames
    public static final int _dropcapt           =  506; // [V]     Positioned Objects and Frames
    public static final int _ds                 =  507; // [V]     Section Formatting Properties
    public static final int _dxfrtext           =  508; // [V]     Positioned Objects and Frames
    public static final int _dy                 =  509; // [V]     Information Group
    public static final int _edmins             =  510; // [V]     Information Group
    public static final int _embo               =  511; // [T/S]   Character Text
    public static final int _emdash             =  512; // [S]     Special Characters
    public static final int _emfblip            =  513; // [F/S]   Pictures
    public static final int _emspace            =  514; // [S]     Special Characters
    public static final int _endash             =  515; // [S]     Special Characters
    public static final int _enddoc             =  516; // [F]     Document Formatting Properties
    public static final int _endnhere           =  517; // [F]     Section Formatting Properties
    public static final int _endnotes           =  518; // [F]     Document Formatting Properties
    public static final int _enspace            =  519; // [S]     Special Characters
    public static final int _expnd              =  520; // [V]     Font (Character) Formatting Properties
    public static final int _expndtw            =  521; // [V]     Font (Character) Formatting Properties
    public static final int _expshrtn           =  522; // [F/S]   Document Formatting Properties
    public static final int _f                  =  523; // [V]     Font (Character) Formatting Properties
    public static final int _faauto             =  524; // [V/S]   Paragraph Formatting Properties
    public static final int _facenter           =  525; // [F/S]   Paragraph Formatting Properties
    public static final int _facingp            =  526; // [F]     Document Formatting Properties
    public static final int _fahang             =  527; // [F/E]   Paragraph Formatting Properties
    public static final int _falt               =  528; // [D]     Font Table
    public static final int _faroman            =  529; // [F/E]   Paragraph Formatting Properties
    public static final int _favar              =  530; // [F/E]   Paragraph Formatting Properties
    public static final int _fbias              =  531; // [VN/S]  Font Table
    public static final int _fbidi              =  532; // [F]     Font Table
    public static final int _fchars             =  533; // [D/E]   Document Formatting Properties
    public static final int _fcharset           =  534; // [V]     Font Table
    public static final int _fdecor             =  535; // [F]     Font Table
    public static final int _fet                =  536; // [V]     Document Formatting Properties
    public static final int _fetch              =  537; // [F]     Font Table
    public static final int _ffdefres           =  538; // [V/S]   Form Fields
    public static final int _ffdeftext          =  539; // [D/S]   Form Fields
    public static final int _ffentrymcr         =  540; // [D/S]   Form Fields
    public static final int _ffexitmcr          =  541; // [D/S]   Form Fields
    public static final int _ffformat           =  542; // [D/S]   Form Fields
    public static final int _ffhaslistbox       =  543; // [VN/S]  Form Fields
    public static final int _ffhelptext         =  544; // [D/S]   Form Fields
    public static final int _ffhps              =  545; // [VN/S]  Form Fields
    public static final int _ffl                =  546; // [D/S]   Form Fields
    public static final int _ffmaxlen           =  547; // [V/S]   Form Fields
    public static final int _ffname             =  548; // [D/S]   Form Fields
    public static final int _ffownhelp          =  549; // [VN/S]  Form Fields
    public static final int _ffownstat          =  550; // [VN/S]  Form Fields
    public static final int _ffprot             =  551; // [VN/S]  Form Fields
    public static final int _ffrecalc           =  552; // [VN/S]  Form Fields
    public static final int _ffres              =  553; // [VN/S]  Form Fields
    public static final int _ffsize             =  554; // [VN/S]  Form Fields
    public static final int _ffstattext         =  555; // [D/S]   Form Fields
    public static final int _fftype             =  556; // [VN/S]  Form Fields
    public static final int _fftypetxt          =  557; // [VN/S]  Form Fields
    public static final int _fi                 =  558; // [V]     Paragraph Formatting Properties
    public static final int _fid                =  559; // [V]     File Table
    public static final int _field              =  560; // [D]     Fields
    public static final int _file               =  561; // [D]     File Table
    public static final int _filetbl            =  562; // [D]     File Table
    public static final int _fittext            =  563; // [VN/K]  Font (Character) Formatting Properties
    public static final int _fldalt             =  564; // [F]     Document Formatting Properties
    public static final int _flddirty           =  565; // [F]     Fields
    public static final int _fldedit            =  566; // [F]     Fields
    public static final int _fldinst            =  567; // [D]     Fields
    public static final int _fldlock            =  568; // [F]     Fields
    public static final int _fldpriv            =  569; // [F]     Fields
    public static final int _fldrslt            =  570; // [D]     Fields
    public static final int _fldtype            =  571; // [D/S]   Fields
    public static final int _fmodern            =  572; // [F]     Font Table
    public static final int _fn                 =  573; // [V]     Style Sheet
    public static final int _fname              =  574; // [D/E]   Font Table
    public static final int _fnetwork           =  575; // [F]     File Table
    public static final int _fnil               =  576; // [F]     Font Table
    public static final int _fnonfilesys        =  577; // [F/T]   File Table
    public static final int _fontemb            =  578; // [D]     Font Table
    public static final int _fontfile           =  579; // [D]     Font Table
    public static final int _fonttbl            =  580; // [D]     Font Table
    public static final int _footer             =  581; // [D]     Headers and Footers
    public static final int ___footer           =  582; // [D]     Headers and Footers
    public static final int _footerf            =  583; // [D]     Headers and Footers
    public static final int _footerl            =  584; // [D]     Headers and Footers
    public static final int _footery            =  585; // [V]     Section Formatting Properties
    public static final int _footnote           =  586; // [D]     Footnotes
    public static final int _formdisp           =  587; // [F]     Document Formatting Properties
    public static final int _formfield          =  588; // [D/S]   Form Fields
    public static final int _formprot           =  589; // [F]     Document Formatting Properties
    public static final int _formshade          =  590; // [F]     Document Formatting Properties
    public static final int _fosnum             =  591; // [V]     File Table
    public static final int _fprq               =  592; // [V]     Font Table
    public static final int _fracwidth          =  593; // [F]     Document Formatting Properties
    public static final int _frelative          =  594; // [V]     File Table
    public static final int _frmtxbtlr          =  595; // [F/E]   Positioned Objects and Frames
    public static final int _frmtxlrtb          =  596; // [F/E]   Positioned Objects and Frames
    public static final int _frmtxlrtbv         =  597; // [F/E]   Positioned Objects and Frames
    public static final int _frmtxtbrl          =  598; // [F/E]   Positioned Objects and Frames
    public static final int _frmtxtbrlv         =  599; // [F/E]   Positioned Objects and Frames
    public static final int _froman             =  600; // [F]     Font Table
    public static final int _fromhtml           =  601; // [F/S]   Document Formatting Properties
    public static final int _fromtext           =  602; // [F/S]   Document Formatting Properties
    public static final int _fs                 =  603; // [V]     Font (Character) Formatting Properties
    public static final int _fscript            =  604; // [F]     Font Table
    public static final int _fswiss             =  605; // [F]     Font Table
    public static final int _ftnalt             =  606; // [F]     Document Formatting Properties
    public static final int _ftnbj              =  607; // [F]     Document Formatting Properties
    public static final int _ftncn              =  608; // [D]     Document Formatting Properties
    public static final int _ftnil              =  609; // [F]     Font Table
    public static final int _ftnlytwnine        =  610; // [F/K]   Document Formatting Properties
    public static final int _ftnnalc            =  611; // [F]     Document Formatting Properties
    public static final int _ftnnar             =  612; // [F]     Document Formatting Properties
    public static final int _ftnnauc            =  613; // [F]     Document Formatting Properties
    public static final int _ftnnchi            =  614; // [F]     Document Formatting Properties
    public static final int _ftnnchosung        =  615; // [F/S]   Document Formatting Properties
    public static final int _ftnncnum           =  616; // [F/S]   Document Formatting Properties
    public static final int _ftnndbar           =  617; // [F/S]   Document Formatting Properties
    public static final int _ftnndbnum          =  618; // [F/S]   Document Formatting Properties
    public static final int _ftnndbnumd         =  619; // [F/S]   Document Formatting Properties
    public static final int _ftnndbnumk         =  620; // [F/S]   Document Formatting Properties
    public static final int _ftnndbnumt         =  621; // [F/S]   Document Formatting Properties
    public static final int _ftnnganada         =  622; // [F/S]   Document Formatting Properties
    public static final int _ftnngbnum          =  623; // [F/S]   Document Formatting Properties
    public static final int _ftnngbnumd         =  624; // [F/S]   Document Formatting Properties
    public static final int _ftnngbnumk         =  625; // [F/S]   Document Formatting Properties
    public static final int _ftnngbnuml         =  626; // [F/S]   Document Formatting Properties
    public static final int _ftnnrlc            =  627; // [F]     Document Formatting Properties
    public static final int _ftnnruc            =  628; // [F]     Document Formatting Properties
    public static final int _ftnnzodiac         =  629; // [F/S]   Document Formatting Properties
    public static final int _ftnnzodiacd        =  630; // [F/S]   Document Formatting Properties
    public static final int _ftnnzodiacl        =  631; // [F/S]   Document Formatting Properties
    public static final int _ftnrestart         =  632; // [F]     Document Formatting Properties
    public static final int _ftnrstcont         =  633; // [F]     Document Formatting Properties
    public static final int _ftnrstpg           =  634; // [F]     Document Formatting Properties
    public static final int _ftnsep             =  635; // [D]     Document Formatting Properties
    public static final int _ftnsepc            =  636; // [D]     Document Formatting Properties
    public static final int _ftnstart           =  637; // [V]     Document Formatting Properties
    public static final int _ftntj              =  638; // [F]     Document Formatting Properties
    public static final int _fttruetype         =  639; // [F]     Font Table
    public static final int _fvaliddos          =  640; // [F]     File Table
    public static final int _fvalidhpfs         =  641; // [F]     File Table
    public static final int _fvalidmac          =  642; // [F]     File Table
    public static final int _fvalidntfs         =  643; // [F]     File Table
    public static final int _g                  =  644; // [D/S]   Font (Character) Formatting Properties
    public static final int _gcw                =  645; // [V/S]   Font (Character) Formatting Properties
    public static final int _generator          =  646; // [D/T]   Generator
    public static final int _green              =  647; // [V]     Color Table
    public static final int _gridtbl            =  648; // [D/S]   Font (Character) Formatting Properties
    public static final int _gutter             =  649; // [V]     Document Formatting Properties
    public static final int _gutterprl          =  650; // [F/E]   Document Formatting Properties
    public static final int _guttersxn          =  651; // [V]     Section Formatting Properties
    public static final int _header             =  652; // [D]     Headers and Footers
    public static final int ___header           =  653; // [D]     Headers and Footers
    public static final int _headerf            =  654; // [D]     Headers and Footers
    public static final int _headerl            =  655; // [D]     Headers and Footers
    public static final int _headery            =  656; // [V]     Section Formatting Properties
    public static final int _hich               =  657; // [F/E]   Associated Character Properties
    public static final int _highlight          =  658; // [V/E]   Highlighting
    public static final int _hlfr               =  659; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _hlinkbase          =  660; // [V/S]   Information Group
    public static final int _hlloc              =  661; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _hlsrc              =  662; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _horzdoc            =  663; // [F/E]   Document Formatting Properties
    public static final int _horzsect           =  664; // [F/E]   Section Formatting Properties
    public static final int _hr                 =  665; // [V]     Information Group
    public static final int _htmautsp           =  666; // [F/K]   Document Formatting Properties
    public static final int _htmlbase           =  667; // [F]     Control Words Introduced by Other Microsoft Products
    public static final int _htmlrtf            =  668; // [T]     Control Words Introduced by Other Microsoft Products
    public static final int _htmltag            =  669; // [D]     Control Words Introduced by Other Microsoft Products
    public static final int _hyphauto           =  670; // [T]     Document Formatting Properties
    public static final int _hyphcaps           =  671; // [T]     Document Formatting Properties
    public static final int _hyphconsec         =  672; // [V]     Document Formatting Properties
    public static final int _hyphhotz           =  673; // [V]     Document Formatting Properties
    public static final int _hyphpar            =  674; // [T]     Paragraph Formatting Properties
    public static final int _i                  =  675; // [T]     Font (Character) Formatting Properties
    public static final int _id                 =  676; // [V]     Information Group
    public static final int _ilvl               =  677; // [V/S]   Paragraph Text
    public static final int _impr               =  678; // [T/S]   Character Text
    public static final int _info               =  679; // [D]     Information Group
    public static final int _insrsid            =  680; // [VN/T]  Track Changes (Revision Marks)
    public static final int _intbl              =  681; // [F]     Paragraph Formatting Properties
    public static final int _ipgp               =  682; // [VN/T]  Paragraph Group Propreties
    public static final int _irow               =  683; // [VN/T]  Table Definitions
    public static final int _irowband           =  684; // [VN/T]  Table Definitions
    public static final int _itap               =  685; // [VN/K]  Paragraph Formatting Properties
    public static final int _ixe                =  686; // [F]     Index Entries
    public static final int _jcompress          =  687; // [F/E]   Document Formatting Properties
    public static final int _jexpand            =  688; // [F/E]   Document Formatting Properties
    public static final int _jpegblip           =  689; // [F/S]   Pictures
    public static final int _jsksu              =  690; // [F/K]   Document Formatting Properties
    public static final int _keep               =  691; // [F]     Paragraph Formatting Properties
    public static final int _keepn              =  692; // [F]     Paragraph Formatting Properties
    public static final int _kerning            =  693; // [V]     Font (Character) Formatting Properties
    public static final int _keycode            =  694; // [D]     Style Sheet
    public static final int _keywords           =  695; // [D]     Information Group
    public static final int _ksulang            =  696; // [VN/K]  Document Formatting Properties
    public static final int _landscape          =  697; // [F]     Document Formatting Properties
    public static final int _lang               =  698; // [V]     Font (Character) Formatting Properties
    public static final int _langfe             =  699; // [VN/K]  Font (Character) Formatting Properties
    public static final int _langfenp           =  700; // [VN/K]  Font (Character) Formatting Properties
    public static final int _langnp             =  701; // [VN/K]  Font (Character) Formatting Properties
    public static final int _lastrow            =  702; // [F/T]   Table Definitions
    public static final int _lbr                =  703; // [SN/K]  Special Characters
    public static final int _lchars             =  704; // [D/E]   Document Formatting Properties
    public static final int _ldblquote          =  705; // [S]     Special Characters
    public static final int _level              =  706; // [V]     Paragraph Formatting Properties
    public static final int _levelfollow        =  707; // [VN/S]  List Table
    public static final int _levelindent        =  708; // [VN/S]  List Table
    public static final int _leveljc            =  709; // [VN/S]  List Table
    public static final int _leveljcn           =  710; // [VN/K]  List Table
    public static final int _levellegal         =  711; // [VN/S]  List Table
    public static final int _levelnfc           =  712; // [VN/S]  List Table
    public static final int _levelnfcn          =  713; // [VN/K]  List Table
    public static final int _levelnorestart     =  714; // [VN/S]  List Table
    public static final int _levelnumbers       =  715; // [D/S]   List Table
    public static final int _levelold           =  716; // [VN/S]  List Table
    public static final int _levelpicture       =  717; // [VN/T]  List Table
    public static final int _levelprev          =  718; // [VN/S]  List Table
    public static final int _levelprevspace     =  719; // [VN/S]  List Table
    public static final int _levelspace         =  720; // [VN/S]  List Table
    public static final int _levelstartat       =  721; // [VN/S]  List Table
    public static final int _leveltemplateid    =  722; // [VN/K]  List Table
    public static final int _leveltext          =  723; // [V/S]   List Table
    public static final int _li                 =  724; // [V]     Paragraph Formatting Properties
    public static final int _line               =  725; // [S]     Special Characters
    public static final int _linebetcol         =  726; // [F]     Section Formatting Properties
    public static final int _linecont           =  727; // [F]     Section Formatting Properties
    public static final int _linemod            =  728; // [V]     Section Formatting Properties
    public static final int _lineppage          =  729; // [F]     Section Formatting Properties
    public static final int _linerestart        =  730; // [F]     Section Formatting Properties
    public static final int _linestart          =  731; // [V]     Document Formatting Properties
    public static final int _linestarts         =  732; // [V]     Section Formatting Properties
    public static final int _linex              =  733; // [V]     Section Formatting Properties
    public static final int _linkself           =  734; // [F]     Objects
    public static final int _linkstyles         =  735; // [F]     Document Formatting Properties
    public static final int _linkval            =  736; // [V/E]   Information Group
    public static final int _lin                =  737; // [VN/K]  Paragraph Formatting Properties
    public static final int _lisa               =  738; // [VN/K]  Paragraph Formatting Properties
    public static final int _lisb               =  739; // [VN/K]  Paragraph Formatting Properties
    public static final int _listhybrid         =  740; // [F/K]   List Table
    public static final int _listid             =  741; // [VN/S]  List Table
    public static final int _listname           =  742; // [D/S]   List Table
    public static final int _listoverridecount  =  743; // [VN/S]  List Table
    public static final int _listoverrideformat =  744; // [VN/S]  List Table
    public static final int _listoverridestart  =  745; // [VN/S]  List Table
    public static final int _listpicture        =  746; // [VN/T]  List Table
    public static final int _listrestarthdn     =  747; // [VN/S]  List Table
    public static final int _listsimple         =  748; // [VN/S]  List Table
    public static final int _liststyleid        =  749; // [VN/T]  List Table
    public static final int _liststylename      =  750; // [V/T]   List Table
    public static final int _listtemplateid     =  751; // [VN/S]  List Table
    public static final int _listtext           =  752; // [D/S]   Paragraph Text
    public static final int _lnbrkrule          =  753; // [F/K]   Document Formatting Properties
    public static final int _lndscpsxn          =  754; // [F]     Section Formatting Properties
    public static final int _lnongrid           =  755; // [F/E]   Document Formatting Properties
    public static final int _loch               =  756; // [F/E]   Associated Character Properties
    public static final int _lquote             =  757; // [S]     Special Characters
    public static final int _ls                 =  758; // [V/S]   List Table
    public static final int _ltrch              =  759; // [F]     Font (Character) Formatting Properties
    public static final int _ltrdoc             =  760; // [F]     Document Formatting Properties
    public static final int _ltrmark            =  761; // [S/T]   Special Characters
    public static final int _ltrpar             =  762; // [F]     Paragraph Formatting Properties
    public static final int _ltrrow             =  763; // [F]     Table Definitions
    public static final int _ltrsect            =  764; // [F]     Section Formatting Properties
    public static final int _lytcalctblwd       =  765; // [F/K]   Document Formatting Properties
    public static final int _lytexcttp          =  766; // [F/S]   Document Formatting Properties
    public static final int _lytprtmet          =  767; // [F/S]   Document Formatting Properties
    public static final int _lyttblrtgr         =  768; // [F/K]   Document Formatting Properties
    public static final int _mac                =  769; // [F]     Character Set
    public static final int _macpict            =  770; // [F]     Pictures
    public static final int _makebackup         =  771; // [F]     Document Formatting Properties
    public static final int _manager            =  772; // [D/E]   Information Group
    public static final int _margb              =  773; // [V]     Document Formatting Properties
    public static final int _margbsxn           =  774; // [V]     Section Formatting Properties
    public static final int _margl              =  775; // [V]     Document Formatting Properties
    public static final int _marglsxn           =  776; // [V]     Section Formatting Properties
    public static final int _margmirror         =  777; // [F]     Document Formatting Properties
    public static final int _margr              =  778; // [V]     Document Formatting Properties
    public static final int _margrsxn           =  779; // [V]     Section Formatting Properties
    public static final int _margt              =  780; // [V]     Document Formatting Properties
    public static final int _margtsxn           =  781; // [V]     Section Formatting Properties
    public static final int _mhtmltag           =  782; // [D]     Control Words Introduced by Other Microsoft Products
    public static final int _min                =  783; // [V]     Information Group
    public static final int _mo                 =  784; // [V]     Information Group
    public static final int _msmcap             =  785; // [F/S]   Document Formatting Properties
    public static final int _nestcell           =  786; // [S/K]   Table Definitions
    public static final int _nestrow            =  787; // [S/K]   Table Definitions
    public static final int _nesttableprops     =  788; // [D/K]   Table Definitions
    public static final int _nextfile           =  789; // [D]     Document Formatting Properties
    public static final int _nobrkwrptbl        =  790; // [F/T]   Document Formatting Properties
    public static final int _nocolbal           =  791; // [F]     Document Formatting Properties
    public static final int _nocompatoptions    =  792; // [F/T]   Document Formatting Properties
    public static final int _nocwrap            =  793; // [F/E]   Paragraph Formatting Properties
    public static final int _noextrasprl        =  794; // [F]     Document Formatting Properties
    public static final int _nofchars           =  795; // [V]     Information Group
    public static final int _nofcharsws         =  796; // [V/S]   Information Group
    public static final int _nofpages           =  797; // [V]     Information Group
    public static final int _nofwords           =  798; // [V]     Information Group
    public static final int _nolead             =  799; // [F/S]   Document Formatting Properties
    public static final int _noline             =  800; // [F]     Paragraph Formatting Properties
    public static final int _nolnhtadjtbl       =  801; // [F/K]   Document Formatting Properties
    public static final int _nonesttables       =  802; // [D/K]   Table Definitions
    public static final int _nonshppict         =  803; // [F/S]   Pictures
    public static final int _nooverflow         =  804; // [F/E]   Paragraph Formatting Properties
    public static final int _noproof            =  805; // [F/K]   Font (Character) Formatting Properties
    public static final int _nosectexpand       =  806; // [F/S]   Font (Character) Formatting Properties
    public static final int _nosnaplinegrid     =  807; // [F/S]   Paragraph Formatting Properties
    public static final int _nospaceforul       =  808; // [F/S]   Document Formatting Properties
    public static final int _nosupersub         =  809; // [F]     Font (Character) Formatting Properties
    public static final int _notabind           =  810; // [F]     Document Formatting Properties
    public static final int _noultrlspc         =  811; // [F/S]   Document Formatting Properties
    public static final int _nowidctlpar        =  812; // [F]     Paragraph Formatting Properties
    public static final int _nowrap             =  813; // [F]     Positioned Objects and Frames
    public static final int _nowwrap            =  814; // [F/E]   Paragraph Formatting Properties
    public static final int _noxlattoyen        =  815; // [F/S]   Document Formatting Properties
    public static final int _objalias           =  816; // [D]     Objects
    public static final int _objalign           =  817; // [V]     Objects
    public static final int _objattph           =  818; // [F/E]   Objects
    public static final int _objautlink         =  819; // [F]     Objects
    public static final int _objclass           =  820; // [D]     Objects
    public static final int _objcropb           =  821; // [V]     Objects
    public static final int _objcropl           =  822; // [V]     Objects
    public static final int _objcropr           =  823; // [V]     Objects
    public static final int _objcropt           =  824; // [V]     Objects
    public static final int _objdata            =  825; // [D]     Objects
    public static final int _object             =  826; // [D]     Objects
    public static final int _objemb             =  827; // [F]     Objects
    public static final int _objh               =  828; // [V]     Objects
    public static final int _objhtml            =  829; // [F/S]   Objects
    public static final int _objicemb           =  830; // [F]     Objects
    public static final int _objlink            =  831; // [F]     Objects
    public static final int _objlock            =  832; // [F]     Objects
    public static final int _objname            =  833; // [D]     Objects
    public static final int _objocx             =  834; // [F/S]   Objects
    public static final int _objpub             =  835; // [F]     Objects
    public static final int _objscalex          =  836; // [V]     Objects
    public static final int _objscaley          =  837; // [V]     Objects
    public static final int _objsect            =  838; // [D]     Objects
    public static final int _objsetsize         =  839; // [F]     Objects
    public static final int _objsub             =  840; // [F]     Objects
    public static final int _objtime            =  841; // [D]     Objects
    public static final int _objtransy          =  842; // [V]     Objects
    public static final int _objupdate          =  843; // [F]     Objects
    public static final int _objw               =  844; // [V]     Objects
    public static final int _oldas              =  845; // [F/K]   Document Formatting Properties
    public static final int _oldcprops          =  846; // [D/T]   Track Changes (Revision Marks)
    public static final int _oldpprops          =  847; // [D/T]   Track Changes (Revision Marks)
    public static final int _oldsprops          =  848; // [D/T]   Track Changes (Revision Marks)
    public static final int _oldtprops          =  849; // [D/T]   Track Changes (Revision Marks)
    public static final int _oldlinewrap        =  850; // [F/S]   Document Formatting Properties
    public static final int _operator           =  851; // [D]     Information Group
    public static final int _otblrul            =  852; // [F]     Document Formatting Properties
    public static final int _outl               =  853; // [T]     Font (Character) Formatting Properties
    public static final int _outlinelevel       =  854; // [VN/S]  Paragraph Text
    public static final int _overlay            =  855; // [F/S]   Paragraph Text
    public static final int _page               =  856; // [S]     Special Characters
    public static final int _pagebb             =  857; // [F]     Paragraph Formatting Properties
    public static final int _panose             =  858; // [D/S]   Font Table
    public static final int _paperh             =  859; // [V]     Document Formatting Properties
    public static final int _paperw             =  860; // [V]     Document Formatting Properties
    public static final int _par                =  861; // [S]     Special Characters
    public static final int _pararsid           =  862; // [VN/T]  Track Changes (Revision Marks)
    public static final int _pard               =  863; // [F]     Paragraph Formatting Properties
    public static final int _pc                 =  864; // [F]     Character Set
    public static final int _pca                =  865; // [F]     Character Set
    public static final int _pgbrdrb            =  866; // [F/S]   Document Formatting Properties
    public static final int _pgbrdrfoot         =  867; // [F/S]   Document Formatting Properties
    public static final int _pgbrdrhead         =  868; // [F/S]   Document Formatting Properties
    public static final int _pgbrdrl            =  869; // [F/S]   Document Formatting Properties
    public static final int _pgbrdropt          =  870; // [VN/S]  Document Formatting Properties
    public static final int _pgbrdrr            =  871; // [F/S]   Document Formatting Properties
    public static final int _pgbrdrsnap         =  872; // [F/S]   Document Formatting Properties
    public static final int _pgbrdrt            =  873; // [F/S]   Document Formatting Properties
    public static final int _pghsxn             =  874; // [V]     Section Formatting Properties
    public static final int _pgnbidia           =  875; // [F/K]   Section Formatting Properties
    public static final int _pgnbidib           =  876; // [F/K]   Section Formatting Properties
    public static final int _pgnchosung         =  877; // [F/S]   Bullets and Numbering
    public static final int _pgncnum            =  878; // [F/S]   Bullets and Numbering
    public static final int _pgncont            =  879; // [F]     Section Formatting Properties
    public static final int _pgndbnum           =  880; // [F/E]   Section Formatting Properties
    public static final int _pgndbnumd          =  881; // [F/E]   Section Formatting Properties
    public static final int _pgndbnumk          =  882; // [F/S]   Bullets and Numbering
    public static final int _pgndbnumt          =  883; // [F/S]   Bullets and Numbering
    public static final int _pgndec             =  884; // [F]     Section Formatting Properties
    public static final int _pgndecd            =  885; // [F/E]   Section Formatting Properties
    public static final int _pgnganada          =  886; // [F/S]   Bullets and Numbering
    public static final int _pgngbnum           =  887; // [F/S]   Bullets and Numbering
    public static final int _pgngbnumd          =  888; // [F/S]   Bullets and Numbering
    public static final int _pgngbnumk          =  889; // [F/S]   Bullets and Numbering
    public static final int _pgngbnuml          =  890; // [F/S]   Bullets and Numbering
    public static final int _pgnhindia          =  891; // [F/T]   Section Formatting Properties
    public static final int _pgnhindib          =  892; // [F/T]   Section Formatting Properties
    public static final int _pgnhindic          =  893; // [F/T]   Section Formatting Properties
    public static final int _pgnhindid          =  894; // [F/T]   Section Formatting Properties
    public static final int _pgnhn              =  895; // [V]     Section Formatting Properties
    public static final int _pgnhnsc            =  896; // [F]     Section Formatting Properties
    public static final int _pgnhnsh            =  897; // [F]     Section Formatting Properties
    public static final int _pgnhnsm            =  898; // [F]     Section Formatting Properties
    public static final int _pgnhnsn            =  899; // [F]     Section Formatting Properties
    public static final int _pgnhnsp            =  900; // [F]     Section Formatting Properties
    public static final int _pgnid              =  901; // [VN/T]  Section Formatting Properties
    public static final int _pgnlcltr           =  902; // [F]     Section Formatting Properties
    public static final int _pgnlcrm            =  903; // [F]     Section Formatting Properties
    public static final int _pgnrestart         =  904; // [F]     Section Formatting Properties
    public static final int _pgnstart           =  905; // [V]     Document Formatting Properties
    public static final int _pgnstarts          =  906; // [V]     Section Formatting Properties
    public static final int _pgnthaia           =  907; // [F/T]   Section Formatting Properties
    public static final int _pgnthaib           =  908; // [F/T]   Section Formatting Properties
    public static final int _pgnthaic           =  909; // [F/T]   Section Formatting Properties
    public static final int _pgnucltr           =  910; // [F]     Section Formatting Properties
    public static final int _pgnucrm            =  911; // [F]     Section Formatting Properties
    public static final int _pgnvieta           =  912; // [F/T]   Section Formatting Properties
    public static final int _pgnx               =  913; // [V]     Section Formatting Properties
    public static final int _pgny               =  914; // [V]     Section Formatting Properties
    public static final int _pgnzodiac          =  915; // [F/S]   Bullets and Numbering
    public static final int _pgnzodiacd         =  916; // [F/S]   Bullets and Numbering
    public static final int _pgnzodiacl         =  917; // [F/S]   Bullets and Numbering
    public static final int _pgp                =  918; // [D/T]   Paragraph Group Properties
    public static final int _pgptbl             =  919; // [D/T]   Paragraph Group Properties
    public static final int _pgwsxn             =  920; // [V]     Section Formatting Properties
    public static final int _phcol              =  921; // [F]     Positioned Objects and Frames
    public static final int _phmrg              =  922; // [F]     Positioned Objects and Frames
    public static final int _phpg               =  923; // [F]     Positioned Objects and Frames
    public static final int _picbmp             =  924; // [F]     Pictures
    public static final int _picbpp             =  925; // [V]     Pictures
    public static final int _piccropb           =  926; // [V]     Pictures
    public static final int _piccropl           =  927; // [V]     Pictures
    public static final int _piccropr           =  928; // [V]     Pictures
    public static final int _piccropt           =  929; // [V]     Pictures
    public static final int _pich               =  930; // [V]     Pictures
    public static final int _pichgoal           =  931; // [V]     Pictures
    public static final int _picprop            =  932; // [D/S]   Pictures
    public static final int _picscaled          =  933; // [F]     Pictures
    public static final int _picscalex          =  934; // [V]     Pictures
    public static final int _picscaley          =  935; // [V]     Pictures
    public static final int _pict               =  936; // [D]     Pictures
    public static final int _picw               =  937; // [V]     Pictures
    public static final int _picwgoal           =  938; // [V]     Pictures
    public static final int _plain              =  939; // [F]     Font (Character) Formatting Properties
    public static final int _pmmetafile         =  940; // [V]     Pictures
    public static final int _pn                 =  941; // [D]     Bullets and Numbering
    public static final int _pnacross           =  942; // [F]     Bullets and Numbering
    public static final int _pnaiu              =  943; // [F/E]   Bullets and Numbering
    public static final int _pnaiud             =  944; // [F/E]   Bullets and Numbering
    public static final int _pnaiueo            =  945; // [F/S]   Bullets and Numbering
    public static final int _pnaiueod           =  946; // [F/S]   Bullets and Numbering
    public static final int _pnb                =  947; // [T]     Bullets and Numbering
    public static final int _pnbidia            =  948; // [F/K]   Bullets and Numbering
    public static final int _pnbidib            =  949; // [F/K]   Bullets and Numbering
    public static final int _pncaps             =  950; // [T]     Bullets and Numbering
    public static final int _pncard             =  951; // [F]     Bullets and Numbering
    public static final int _pncf               =  952; // [V]     Bullets and Numbering
    public static final int _pnchosung          =  953; // [F/S]   Bullets and Numbering
    public static final int _pncnum             =  954; // [F/E]   Bullets and Numbering
    public static final int _pndbnum            =  955; // [F/E]   Bullets and Numbering
    public static final int _pndbnumd           =  956; // [F/S]   Bullets and Numbering
    public static final int _pndbnumk           =  957; // [F/S]   Bullets and Numbering
    public static final int _pndbnuml           =  958; // [F/S]   Bullets and Numbering
    public static final int _pndbnumt           =  959; // [F/S]   Bullets and Numbering
    public static final int _pndec              =  960; // [F]     Bullets and Numbering
    public static final int _pndecd             =  961; // [F/E]   Bullets and Numbering
    public static final int _pnf                =  962; // [V]     Bullets and Numbering
    public static final int _pnfs               =  963; // [V]     Bullets and Numbering
    public static final int _pnganada           =  964; // [F/S]   Bullets and Numbering
    public static final int ___pnganada         =  965; // [F/S]   Bullets and Numbering
    public static final int _pngblip            =  966; // [F/S]   Pictures
    public static final int _pngbnum            =  967; // [F/S]   Bullets and Numbering
    public static final int _pngbnumd           =  968; // [F/S]   Bullets and Numbering
    public static final int _pngbnumk           =  969; // [F/S]   Bullets and Numbering
    public static final int _pngbnuml           =  970; // [F/S]   Bullets and Numbering
    public static final int _pnhang             =  971; // [F]     Bullets and Numbering
    public static final int _pni                =  972; // [T]     Bullets and Numbering
    public static final int _pnindent           =  973; // [V]     Bullets and Numbering
    public static final int _pniroha            =  974; // [F/E]   Bullets and Numbering
    public static final int _pnirohad           =  975; // [F/E]   Bullets and Numbering
    public static final int _pnlcltr            =  976; // [F]     Bullets and Numbering
    public static final int _pnlcrm             =  977; // [F]     Bullets and Numbering
    public static final int _pnlvl              =  978; // [V]     Bullets and Numbering
    public static final int _pnlvlblt           =  979; // [F]     Bullets and Numbering
    public static final int _pnlvlbody          =  980; // [F]     Bullets and Numbering
    public static final int _pnlvlcont          =  981; // [F]     Bullets and Numbering
    public static final int _pnnumonce          =  982; // [F]     Bullets and Numbering
    public static final int _pnord              =  983; // [F]     Bullets and Numbering
    public static final int _pnordt             =  984; // [F]     Bullets and Numbering
    public static final int _pnprev             =  985; // [F]     Bullets and Numbering
    public static final int _pnqc               =  986; // [F]     Bullets and Numbering
    public static final int _pnql               =  987; // [F]     Bullets and Numbering
    public static final int _pnqr               =  988; // [F]     Bullets and Numbering
    public static final int _pnrauth            =  989; // [VN/S]  Paragraph Text
    public static final int _pnrdate            =  990; // [VN/S]  Paragraph Text
    public static final int _pnrestart          =  991; // [F]     Bullets and Numbering
    public static final int _pnrnfc             =  992; // [VN/S]  Paragraph Text
    public static final int _pnrnot             =  993; // [F/S]   Paragraph Text
    public static final int _pnrpnbr            =  994; // [VN/S]  Paragraph Text
    public static final int _pnrrgb             =  995; // [VN/S]  Paragraph Text
    public static final int _pnrstart           =  996; // [VN/S]  Paragraph Text
    public static final int _pnrstop            =  997; // [VN/S]  Paragraph Text
    public static final int _pnrxst             =  998; // [VN/S]  Paragraph Text
    public static final int _pnscaps            =  999; // [T]     Bullets and Numbering
    public static final int _pnseclvl           = 1000; // [D]     Bullets and Numbering
    public static final int _pnsp               = 1001; // [V]     Bullets and Numbering
    public static final int _pnstart            = 1002; // [V]     Bullets and Numbering
    public static final int _pnstrike           = 1003; // [T]     Bullets and Numbering
    public static final int _pntext             = 1004; // [D]     Bullets and Numbering
    public static final int _pntxta             = 1005; // [D]     Bullets and Numbering
    public static final int _pntxtb             = 1006; // [D]     Bullets and Numbering
    public static final int _pnucltr            = 1007; // [F]     Bullets and Numbering
    public static final int _pnucrm             = 1008; // [F]     Bullets and Numbering
    public static final int _pnul               = 1009; // [T]     Bullets and Numbering
    public static final int _pnuld              = 1010; // [F]     Bullets and Numbering
    public static final int _pnuldash           = 1011; // [F/E]   Bullets and Numbering
    public static final int _pnuldashd          = 1012; // [F/E]   Bullets and Numbering
    public static final int _pnuldashdd         = 1013; // [F/E]   Bullets and Numbering
    public static final int _pnuldb             = 1014; // [F]     Bullets and Numbering
    public static final int _pnulhair           = 1015; // [F/E]   Bullets and Numbering
    public static final int _pnulnone           = 1016; // [F]     Bullets and Numbering
    public static final int _pnulth             = 1017; // [F/E]   Bullets and Numbering
    public static final int _pnulw              = 1018; // [F]     Bullets and Numbering
    public static final int _pnulwave           = 1019; // [F/E]   Bullets and Numbering
    public static final int _pnzodiac           = 1020; // [F/S]   Bullets and Numbering
    public static final int _pnzodiacd          = 1021; // [F/S]   Bullets and Numbering
    public static final int _pnzodiacl          = 1022; // [F/S]   Bullets and Numbering
    public static final int _posnegx            = 1023; // [V]     Positioned Objects and Frames
    public static final int _posnegy            = 1024; // [V]     Positioned Objects and Frames
    public static final int _posx               = 1025; // [V]     Positioned Objects and Frames
    public static final int _posxc              = 1026; // [F]     Positioned Objects and Frames
    public static final int _posxi              = 1027; // [F]     Positioned Objects and Frames
    public static final int _posxl              = 1028; // [F]     Positioned Objects and Frames
    public static final int _posxo              = 1029; // [F]     Positioned Objects and Frames
    public static final int _posxr              = 1030; // [F]     Positioned Objects and Frames
    public static final int _posy               = 1031; // [V]     Positioned Objects and Frames
    public static final int _posyb              = 1032; // [F]     Positioned Objects and Frames
    public static final int _posyc              = 1033; // [F]     Positioned Objects and Frames
    public static final int _posyil             = 1034; // [F]     Positioned Objects and Frames
    public static final int _posyin             = 1035; // [F/S]   Paragraph Text
    public static final int _posyout            = 1036; // [F/S]   Paragraph Text
    public static final int _posyt              = 1037; // [F]     Positioned Objects and Frames
    public static final int _prcolbl            = 1038; // [F]     Document Formatting Properties
    public static final int _printdata          = 1039; // [F]     Document Formatting Properties
    public static final int _printim            = 1040; // [D]     Information Group
    public static final int _private            = 1041; // [D/S]   Document Formatting Properties
    public static final int _propname           = 1042; // [V/E]   Information Group
    public static final int _proptype           = 1043; // [V/E]   Information Group
    public static final int _psover             = 1044; // [F]     Document Formatting Properties
    public static final int _psz                = 1045; // [V]     Document Formatting Properties
    public static final int _pubauto            = 1046; // [F]     Macintosh Edition Manager Publisher Objects
    public static final int _pvmrg              = 1047; // [F]     Positioned Objects and Frames
    public static final int _pvpara             = 1048; // [F]     Positioned Objects and Frames
    public static final int _pvpg               = 1049; // [F]     Positioned Objects and Frames
    public static final int _pwd                = 1050; // [DN]    Control Words Introduced by Other Microsoft Products
    public static final int _pxe                = 1051; // [D/E]   Index Entries
    public static final int _qc                 = 1052; // [F]     Paragraph Formatting Properties
    public static final int _qd                 = 1053; // [F/E]   Paragraph Formatting Properties
    public static final int _qj                 = 1054; // [F]     Paragraph Formatting Properties
    public static final int _qk                 = 1055; // [F/T]   Paragraph Formatting Properties
    public static final int _ql                 = 1056; // [F]     Paragraph Formatting Properties
    public static final int _qmspace            = 1057; // [S/E]    Special Characters
    public static final int _qr                 = 1058; // [F]     Paragraph Formatting Properties
    public static final int _qt                 = 1059; // [F/T]   Paragraph Formatting Properties
    public static final int _rawclbgbdiag       = 1060; // [F/T]   Table Definitions
    public static final int _rawclbgcross       = 1061; // [F/T]   Table Definitions
    public static final int _rawclbgdcross      = 1062; // [F/T]   Table Definitions
    public static final int _rawbgdkbdiag       = 1063; // [F/T]   Table Definitions
    public static final int _rawclbgdkcross     = 1064; // [F/T]   Table Definitions
    public static final int _rawclbgdkdcross    = 1065; // [F/T]   Table Definitions
    public static final int _rawclbgdkfdiag     = 1066; // [F/T]   Table Definitions
    public static final int _rawclbgdkhor       = 1067; // [F/T]   Table Definitions
    public static final int _rawclbgdkvert      = 1068; // [F/T]   Table Definitions
    public static final int _rawclbgfdiag       = 1069; // [F/T]   Table Definitions
    public static final int _rawclbghoriz       = 1070; // [F/T]   Table Definitions
    public static final int _rawclbgvert        = 1071; // [F/T]   Table Definitions
    public static final int _rdblquote          = 1072; // [S]     Special Characters
    public static final int _red                = 1073; // [V]     Color Table
    public static final int _rempersonalinfo    = 1074; // [F/T]   Document Formatting Properties
    public static final int _result             = 1075; // [D]     Objects
    public static final int _revauth            = 1076; // [V]     Font (Character) Formatting Properties
    public static final int _revauthdel         = 1077; // [VN/S]  Character Text
    public static final int _revbar             = 1078; // [V]     Document Formatting Properties
    public static final int _revdttm            = 1079; // [V]     Font (Character) Formatting Properties
    public static final int _revdttmdel         = 1080; // [VN/S]  Character Text
    public static final int _revised            = 1081; // [T]     Font (Character) Formatting Properties
    public static final int _revisions          = 1082; // [F]     Document Formatting Properties
    public static final int _revprop            = 1083; // [V]     Document Formatting Properties
    public static final int _revprot            = 1084; // [F]     Document Formatting Properties
    public static final int _revtbl             = 1085; // [D]     Track Changes
    public static final int _revtim             = 1086; // [D]     Information Group
    public static final int _ri                 = 1087; // [V]     Paragraph Formatting Properties
    public static final int _rin                = 1088; // [VN/K]  Paragraph Formatting Properties
    public static final int _row                = 1089; // [S]     Special Characters
    public static final int _rquote             = 1090; // [S]     Special Characters
    public static final int _rsid               = 1091; // [VN/T]  Track Changes (Revision Marks)
    public static final int _rsidroot           = 1092; // [VN/T]  Track Changes (Revision Marks)
    public static final int _rsidtbl            = 1093; // [D/T]   Track Changes (Revision Marks)
    public static final int _rsltbmp            = 1094; // [F]     Objects
    public static final int _rslthtml           = 1095; // [F/K]   Objects
    public static final int _rsltmerge          = 1096; // [F]     Objects
    public static final int _rsltpict           = 1097; // [F]     Objects
    public static final int _rsltrtf            = 1098; // [F]     Objects
    public static final int _rslttxt            = 1099; // [F]     Objects
    public static final int _rtf                = 1100; // [D]     RTF Version
    public static final int _rtlch              = 1101; // [F]     Font (Character) Formatting Properties
    public static final int _rtldoc             = 1102; // [F]     Document Formatting Properties
    public static final int _rtlgutter          = 1103; // [F/K]   Document Formatting Properties
    public static final int _rtlmark            = 1104; // [S/T]    Special Characters
    public static final int _rtlpar             = 1105; // [F]     Paragraph Formatting Properties
    public static final int _rtlrow             = 1106; // [F]     Table Definitions
    public static final int _rtlsect            = 1107; // [F]     Section Formatting Properties
    public static final int _rxe                = 1108; // [D]     Index Entries
    public static final int _s                  = 1109; // [V]     Paragraph Formatting Properties
    public static final int _sa                 = 1110; // [V]     Paragraph Formatting Properties
    public static final int _saauto             = 1111; // [TN/K]  Paragraph Formatting Properties
    public static final int _saftnnalc          = 1112; // [F/T]   Section Formatting Properties
    public static final int _saftnnar           = 1113; // [F/T]   Section Formatting Properties
    public static final int _saftnnauc          = 1114; // [F/T]   Section Formatting Properties
    public static final int _saftnnchi          = 1115; // [F/T]   Section Formatting Properties
    public static final int _saftnnchosung      = 1116; // [F/T]   Section Formatting Properties
    public static final int _saftnncnum         = 1117; // [F/T]   Section Formatting Properties
    public static final int _saftnndbar         = 1118; // [F/T]   Section Formatting Properties
    public static final int _saftnndbnum        = 1119; // [F/T]   Section Formatting Properties
    public static final int _saftnndbnumd       = 1120; // [F/T]   Section Formatting Properties
    public static final int _saftnndbnumk       = 1121; // [F/T]   Section Formatting Properties
    public static final int _saftnndbnumt       = 1122; // [F/T]   Section Formatting Properties
    public static final int _saftnnganada       = 1123; // [F/T]   Section Formatting Properties
    public static final int _saftnngbnum        = 1124; // [F/T]   Section Formatting Properties
    public static final int _saftnngbnumd       = 1125; // [F/T]   Section Formatting Properties
    public static final int _saftnngbnumk       = 1126; // [F/T]   Section Formatting Properties
    public static final int _saftnngbnuml       = 1127; // [F/T]   Section Formatting Properties
    public static final int _saftnnrlc          = 1128; // [F/T]   Section Formatting Properties
    public static final int _saftnnruc          = 1129; // [F/T]   Section Formatting Properties
    public static final int _saftnnzodiac       = 1130; // [F/T]   Section Formatting Properties
    public static final int _saftnnzodiacd      = 1131; // [F/T]   Section Formatting Properties
    public static final int _saftnnzodiacl      = 1132; // [F/T]   Section Formatting Properties
    public static final int _saftnrestart       = 1133; // [F/T]   Section Formatting Properties
    public static final int _saftnrstcont       = 1134; // [F/T]   Section Formatting Properties
    public static final int _saftnstart         = 1135; // [F/T]   Section Formatting Properties
    public static final int _sautoupd           = 1136; // [F/S]   Style Sheet
    public static final int _sb                 = 1137; // [V]     Paragraph Formatting Properties
    public static final int _sbasedon           = 1138; // [V]     Style Sheet
    public static final int _sbauto             = 1139; // [TN/K]  Paragraph Formatting Properties
    public static final int _sbkcol             = 1140; // [F]     Section Formatting Properties
    public static final int _sbkeven            = 1141; // [F]     Section Formatting Properties
    public static final int _sbknone            = 1142; // [F]     Section Formatting Properties
    public static final int _sbkodd             = 1143; // [F]     Section Formatting Properties
    public static final int _sbkpage            = 1144; // [F]     Section Formatting Properties
    public static final int _sbys               = 1145; // [F]     Paragraph Formatting Properties
    public static final int _scaps              = 1146; // [T]     Font (Character) Formatting Properties
    public static final int _scompose           = 1147; // [F/K]   Style Sheet
    public static final int _sec                = 1148; // [V]     Information Group
    public static final int _sect               = 1149; // [S]     Special Characters
    public static final int _sectd              = 1150; // [F]     Section Formatting Properties
    public static final int _sectdefaultcl      = 1151; // [V/S]   Section Formatting Properties
    public static final int _sectexpand         = 1152; // [VN/S]  Section Formatting Properties
    public static final int _sectlinegrid       = 1153; // [VN/S]  Section Formatting Properties
    public static final int _sectnum            = 1154; // [S]     Special Characters
    public static final int _sectrsid           = 1155; // [VN/T]  Track Changes (Revision Marks)
    public static final int _sectspecifycl      = 1156; // [V/S]   Section Formatting Properties
    public static final int _sectspecifygen     = 1157; // [FN]    Section Formatting Properties
    public static final int _sectspecifyl       = 1158; // [V/S]   Section Formatting Properties
    public static final int _sectunlocked       = 1159; // [F]     Section Formatting Properties
    public static final int _sftnbj             = 1160; // [F/T]   Section Formatting Properties
    public static final int _sftnnalc           = 1161; // [F/T]   Section Formatting Properties
    public static final int _sftnnar            = 1162; // [F/T]   Section Formatting Properties
    public static final int _sftnnauc           = 1163; // [F/T]   Section Formatting Properties
    public static final int _sftnnchi           = 1164; // [F/T]   Section Formatting Properties
    public static final int _sftnnchosung       = 1165; // [F/T]   Section Formatting Properties
    public static final int _sftnncnum          = 1166; // [F/T]   Section Formatting Properties
    public static final int _sftnndbar          = 1167; // [F/T]   Section Formatting Properties
    public static final int _sftnndbnum         = 1168; // [F/T]   Section Formatting Properties
    public static final int _sftnndbnumd        = 1169; // [F/T]   Section Formatting Properties
    public static final int _sftnndbnumk        = 1170; // [F/T]   Section Formatting Properties
    public static final int _sftnndbnumt        = 1171; // [F/T]   Section Formatting Properties
    public static final int _sftnnganada        = 1172; // [F/T]   Section Formatting Properties
    public static final int _sftnngbnum         = 1173; // [F/T]   Section Formatting Properties
    public static final int _sftnngbnumd        = 1174; // [F/T]   Section Formatting Properties
    public static final int _sftnngbnumk        = 1175; // [F/T]   Section Formatting Properties
    public static final int _sftnngbnuml        = 1176; // [F/T]   Section Formatting Properties
    public static final int _sftnnrlc           = 1177; // [F/T]   Section Formatting Properties
    public static final int _sftnnruc           = 1178; // [F/T]   Section Formatting Properties
    public static final int _sftnnzodiac        = 1179; // [F/T]   Section Formatting Properties
    public static final int _sftnnzodiacd       = 1180; // [F/T]   Section Formatting Properties
    public static final int _sftnnzodiacl       = 1181; // [F/T]   Section Formatting Properties
    public static final int _sftnrestart        = 1182; // [F/T]   Section Formatting Properties
    public static final int _sftnrstcont        = 1183; // [F/T]   Section Formatting Properties
    public static final int _sftnrstpg          = 1184; // [F/T]   Section Formatting Properties
    public static final int _sftnstart          = 1185; // [F/T]   Section Formatting Properties
    public static final int _sftntj             = 1186; // [F/T]   Section Formatting Properties
    public static final int _shad               = 1187; // [T]     Font (Character) Formatting Properties
    public static final int _shading            = 1188; // [V]     Paragraph Shading
    public static final int _shidden            = 1189; // [F/S]   Style Sheet
    public static final int _shift              = 1190; // [F]     Style Sheet
    public static final int _shpbottom          = 1191; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbxcolumn        = 1192; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbxignore        = 1193; // [F/K]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbxmargin        = 1194; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbxpage          = 1195; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbyignore        = 1196; // [F/K]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbymargin        = 1197; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbypage          = 1198; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpbypara          = 1199; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpfblwtxt         = 1200; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpfhdr            = 1201; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpgrp             = 1202; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpleft            = 1203; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shplid             = 1204; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shplockanchor      = 1205; // [F/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shppict            = 1206; // [D/S]   Pictures
    public static final int _shpright           = 1207; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shprslt            = 1208; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shptop             = 1209; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shptxt             = 1210; // [V/S]   Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpwrk             = 1211; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpwr              = 1212; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _shpz               = 1213; // [VN/S]  Word 97 through Word 2002 RTF for Drawing Objects (Shapes)
    public static final int _sl                 = 1214; // [V]     Paragraph Formatting Properties
    public static final int _slmult             = 1215; // [V]     Paragraph Formatting Properties
    public static final int _snaptogridincell   = 1216; // [F/T]   Document Formatting Properties
    public static final int _snext              = 1217; // [V]     Style Sheet
    public static final int _softcol            = 1218; // [F]     Special Characters
    public static final int _softlheight        = 1219; // [V]     Special Characters
    public static final int _softline           = 1220; // [F]     Special Characters
    public static final int _softpage           = 1221; // [F]     Special Characters
    public static final int _spersonal          = 1222; // [F/K]   Style Sheet
    public static final int _splytwnine         = 1223; // [F/K]   Document Formatting Properties
    public static final int _sprsbsp            = 1224; // [F/S]   Document Formatting Properties
    public static final int _sprslnsp           = 1225; // [F/E]   Document Formatting Properties
    public static final int _sprsspbf           = 1226; // [F]     Document Formatting Properties
    public static final int _sprstsm            = 1227; // [F/S]   Document Formatting Properties
    public static final int _sprstsp            = 1228; // [F]     Document Formatting Properties
    public static final int _spv                = 1229; // [F/T]   Paragraph Formatting Properties
    public static final int _sreply             = 1230; // [F/K]   Style Sheet
    public static final int _ssemihidden        = 1231; // [F/T]   Style Sheet
    public static final int _staticval          = 1232; // [V/E]   Information Group
    public static final int _stextflow          = 1233; // [V/S]   Section Text
    public static final int _strike             = 1234; // [T]     Font (Character) Formatting Properties
    public static final int _striked            = 1235; // [T/S]   Character Text
    public static final int _stshfbi            = 1236; // [VN/T]  Default Fonts
    public static final int _stshfdbch          = 1237; // [VN/T]  Default Fonts
    public static final int _stshfhich          = 1238; // [VN/T]  Default Fonts
    public static final int _stshfloch          = 1239; // [VN/T]  Default Fonts
    public static final int _stylesheet         = 1240; // [D]     Style Sheet
    public static final int _styrsid            = 1241; // [VN/T]  Track Changes (Revision Marks)
    public static final int _sub                = 1242; // [F]     Font (Character) Formatting Properties
    public static final int _subdocument        = 1243; // [V]     Paragraph Formatting Properties
    public static final int _subfontbysize      = 1244; // [F/E]   Document Formatting Properties
    public static final int _subject            = 1245; // [D]     Information Group
    public static final int _super              = 1246; // [F]     Font (Character) Formatting Properties
    public static final int _swpbdr             = 1247; // [F]     Document Formatting Properties
    public static final int _tab                = 1248; // [S]     Special Characters
    public static final int _tabsnoovrlp        = 1249; // [F/K]   Table Definitions
    public static final int _taprtl             = 1250; // [F/K]   Table Definitions
    public static final int _tb                 = 1251; // [V]     Tabs
    public static final int _tbllkbestfit       = 1252; // [F/T]   Table Definitions
    public static final int _tbllkborder        = 1253; // [F/T]   Table Definitions
    public static final int _tbllkcolor         = 1254; // [F/T]   Table Definitions
    public static final int _tbllkfont          = 1255; // [F/T]   Table Definitions
    public static final int _tbllkhdrcols       = 1256; // [F/T]   Table Definitions
    public static final int _tbllkhdrrows       = 1257; // [F/T]   Table Definitions
    public static final int _tbllklastcol       = 1258; // [F/T]   Table Definitions
    public static final int _tbllklastrow       = 1259; // [F/T]   Table Definitions
    public static final int _tbllkshading       = 1260; // [F/T]   Table Definitions
    public static final int _tblrsid            = 1261; // [FN/T]  Table Definitions
    public static final int _tc                 = 1262; // [D]     Table of Contents Entries
    public static final int _tcelld             = 1263; // [F/S]   Table Definitions
    public static final int _tcf                = 1264; // [V]     Table of Contents Entries
    public static final int _tcl                = 1265; // [V]     Table of Contents Entries
    public static final int _tcn                = 1266; // [F]     Table of Contents Entries
    public static final int _tdfrmtxtBottom     = 1267; // [VN/K]  Table Definitions
    public static final int _tdfrmtxtLeft       = 1268; // [VN/K]  Table Definitions
    public static final int _tdfrmtxtRight      = 1269; // [VN/K]  Table Definitions
    public static final int _tdfrmtxtTop        = 1270; // [VN/K]  Table Definitions
    public static final int _template           = 1271; // [D]     Document Formatting Properties
    public static final int _time               = 1272; // [F/S]   Fields
    public static final int _title              = 1273; // [D]     Information Group
    public static final int _titlepg            = 1274; // [F]     Section Formatting Properties
    public static final int _tldot              = 1275; // [F]     Tabs
    public static final int _tleq               = 1276; // [F]     Tabs
    public static final int _tlhyph             = 1277; // [F]     Tabs
    public static final int _tlmdot             = 1278; // [F/E]   Tabs
    public static final int _tlth               = 1279; // [F]     Tabs
    public static final int _tlul               = 1280; // [F]     Tabs
    public static final int _toplinepunct       = 1281; // [F/T]   Document Formatting Properties
    public static final int _tphcol             = 1282; // [F/K]   Table Definitions
    public static final int _tphmrg             = 1283; // [F/K]   Table Definitions
    public static final int _tphpg              = 1284; // [F/K]   Table Definitions
    public static final int _tposnegx           = 1285; // [VN/K]  Table Definitions
    public static final int _tposnegy           = 1286; // [VN/K]  Table Definitions
    public static final int _tposxc             = 1287; // [F/K]   Table Definitions
    public static final int _tposxi             = 1288; // [F/K]   Table Definitions
    public static final int _tposxl             = 1289; // [F/K]   Table Definitions
    public static final int _tposx              = 1290; // [VN/K]  Table Definitions
    public static final int _tposxo             = 1291; // [F/K]   Table Definitions
    public static final int _tposxr             = 1292; // [F/K]   Table Definitions
    public static final int _tposy              = 1293; // [F/K]   Table Definitions
    public static final int _tposyb             = 1294; // [F/K]   Table Definitions
    public static final int _tposyc             = 1295; // [F/K]   Table Definitions
    public static final int _tposyil            = 1296; // [F/K]   Table Definitions
    public static final int _tposyin            = 1297; // [F/K]   Table Definitions
    public static final int _tposyoutv          = 1298; // [F/K]   Table Definitions
    public static final int _tposyt             = 1299; // [F/K]   Table Definitions
    public static final int _tpvmrg             = 1300; // [F/K]   Table Definitions
    public static final int _tpvpara            = 1301; // [F/K]   Table Definitions
    public static final int _tpvpg              = 1302; // [F/K]   Table Definitions
    public static final int _tqc                = 1303; // [F]     Tabs
    public static final int _tqdec              = 1304; // [F]     Tabs
    public static final int _tqr                = 1305; // [F]     Tabs
    public static final int _transmf            = 1306; // [F]     Document Formatting Properties
    public static final int _trauth             = 1307; // [VN/T]  Table Definitions
    public static final int _trautofit          = 1308; // [TN/K]  Table Definitions
    public static final int _trbgbdiag          = 1309; // [F/T]   Table Definitions
    public static final int _trbgcross          = 1310; // [F/T]   Table Definitions
    public static final int _trbgdcross         = 1311; // [F/T]   Table Definitions
    public static final int _trbgdkbdiag        = 1312; // [F/T]   Table Definitions
    public static final int _trbgdkcross        = 1313; // [F/T]   Table Definitions
    public static final int _trbgdkdcross       = 1314; // [F/T]   Table Definitions
    public static final int _trbgdkfdiag        = 1315; // [F/T]   Table Definitions
    public static final int _trbgdkhor          = 1316; // [F/T]   Table Definitions
    public static final int _trbgdkvert         = 1317; // [F/T]   Table Definitions
    public static final int _trbgfdiag          = 1318; // [F/T]   Table Definitions
    public static final int _trbghoriz          = 1319; // [F/T]   Table Definitions
    public static final int _trbgvert           = 1320; // [F/T]   Table Definitions
    public static final int _trbrdrb            = 1321; // [F]     Table Definitions
    public static final int _trbrdrh            = 1322; // [F]     Table Definitions
    public static final int _trbrdrl            = 1323; // [F]     Table Definitions
    public static final int _trbrdrr            = 1324; // [F]     Table Definitions
    public static final int _trbrdrt            = 1325; // [F]     Table Definitions
    public static final int _trbrdrv            = 1326; // [F]     Table Definitions
    public static final int _trcbpat            = 1327; // [VN/T]  Table Definitions
    public static final int _trcfpat            = 1328; // [VN/T]  Table Definitions
    public static final int _trdate             = 1329; // [VN]    Table Definitions
    public static final int _trftsWidthA        = 1330; // [VN/K]  Table Definitions
    public static final int _trftsWidthB        = 1331; // [VN/K]  Table Definitions
    public static final int _trftsWidth         = 1332; // [VN/K]  Table Definitions
    public static final int _trgaph             = 1333; // [V]     Table Definitions
    public static final int _trhdr              = 1334; // [F]     Table Definitions
    public static final int _trkeep             = 1335; // [F]     Table Definitions
    public static final int _trleft             = 1336; // [V]     Table Definitions
    public static final int _trowd              = 1337; // [F]     Table Definitions
    public static final int _trpaddb            = 1338; // [VN/K]  Table Definitions
    public static final int _trpaddfb           = 1339; // [VN/K]  Table Definitions
    public static final int _trpaddfl           = 1340; // [VN/K]  Table Definitions
    public static final int _trpaddfr           = 1341; // [VN/K]  Table Definitions
    public static final int _trpaddft           = 1342; // [VN/K]  Table Definitions
    public static final int _trpaddl            = 1343; // [VN/K]  Table Definitions
    public static final int _trpaddr            = 1344; // [VN/K]  Table Definitions
    public static final int _trpaddt            = 1345; // [VN/K]  Table Definitions
    public static final int _trpat              = 1346; // [VN/T]  Table Definitions
    public static final int _trqc               = 1347; // [F]     Table Definitions
    public static final int _trql               = 1348; // [F]     Table Definitions
    public static final int _trqr               = 1349; // [F]     Table Definitions
    public static final int _trrh               = 1350; // [V]     Table Definitions
    public static final int _trshdng            = 1351; // [VN/T]  Table Definitions
    public static final int _trspdb             = 1352; // [VN/K]  Table Definitions
    public static final int _trspdfb            = 1353; // [VN/K]  Table Definitions
    public static final int _trspdfl            = 1354; // [VN/K]  Table Definitions
    public static final int _trspdfr            = 1355; // [VN/K]  Table Definitions
    public static final int _trspdft            = 1356; // [VN/K]  Table Definitions
    public static final int _trspdl             = 1357; // [VN/K]  Table Definitions
    public static final int _trspdr             = 1358; // [VN/K]  Table Definitions
    public static final int _trspdt             = 1359; // [VN/K]  Table Definitions
    public static final int _truncatefontheight = 1360; // [F]     Document Formatting Properties
    public static final int _trwWidthA          = 1361; // [VN/K]  Table Definitions
    public static final int _trwWidthB          = 1362; // [VN/K]  Table Definitions
    public static final int _trwWidth           = 1363; // [VN/K]  Table Definitions
    public static final int _ts                 = 1364; // [V/T]   Style Sheet
    public static final int _tsbgbdiag          = 1365; // [F/T]   Table Styles
    public static final int _tsbgcross          = 1366; // [F/T]   Table Styles
    public static final int _tsbgdcross         = 1367; // [F/T]   Table Styles
    public static final int _tsbgdkbdiag        = 1368; // [F/T]   Table Styles
    public static final int _tsbgdkcross        = 1369; // [F/T]   Table Styles
    public static final int _tsbgdkdcross       = 1370; // [F/T]   Table Styles
    public static final int _tsbgdkfdiag        = 1371; // [F/T]   Table Styles
    public static final int _tsbgdkhor          = 1372; // [F/T]   Table Styles
    public static final int _tsbgdkvert         = 1373; // [F/T]   Table Styles
    public static final int _tsbgfdiag          = 1374; // [F/T]   Table Styles
    public static final int _tsbghoriz          = 1375; // [F/T]   Table Styles
    public static final int _tsbgvert           = 1376; // [F/T]   Table Styles
    public static final int _tsbrdrb            = 1377; // [F/T]   Table Styles
    public static final int _tsbrdrdgl          = 1378; // [F/T]   Table Styles
    public static final int _tsbrdrdgr          = 1379; // [F/T]   Table Styles
    public static final int _tsbrdrh            = 1380; // [F/T]   Table Styles
    public static final int _tsbrdrl            = 1381; // [F/T]   Table Styles
    public static final int _tsbrdrr            = 1382; // [F/T]   Table Styles
    public static final int ___tsbrdrr          = 1383; // [F/T]   Table Styles
    public static final int _tsbrdrt            = 1384; // [F/T]   Table Styles
    public static final int _tsbrdrv            = 1385; // [F/T]   Table Styles
    public static final int _tscbandhorzeven    = 1386; // [F/T]   Table Styles
    public static final int _tscbandhorzodd     = 1387; // [F/T]   Table Styles
    public static final int _tscbandsh          = 1388; // [F/T]   Table Styles
    public static final int _tscbandsv          = 1389; // [F/T]   Table Styles
    public static final int _tscbandverteven    = 1390; // [F/T]   Table Styles
    public static final int _tscbandvertodd     = 1391; // [F/T]   Table Styles
    public static final int _tscellcbpat        = 1392; // [VN/T]  Table Styles
    public static final int _tscellcfpat        = 1393; // [VN/T]  Table Styles
    public static final int _tscellpaddb        = 1394; // [VN/T]  Table Styles
    public static final int _tscellpaddfb       = 1395; // [VN/T]  Table Styles
    public static final int _tscellpaddfl       = 1396; // [VN/T]  Table Styles
    public static final int _tscellpaddfr       = 1397; // [VN/T]  Table Styles
    public static final int _tscellpaddft       = 1398; // [VN/T]  Table Styles
    public static final int _tscellpaddl        = 1399; // [VN/T]  Table Styles
    public static final int _tscellpaddr        = 1400; // [VN/T]  Table Styles
    public static final int _tscellpaddt        = 1401; // [VN/T]  Table Styles
    public static final int _tscellpct          = 1402; // [VN/T]  Table Styles
    public static final int _tscellwidth        = 1403; // [F/T]   Table Styles
    public static final int _tscellwidthfts     = 1404; // [F/T]   Table Styles
    public static final int _tscfirstcol        = 1405; // [F/T]   Table Styles
    public static final int _tscfirstrow        = 1406; // [F/T]   Table Styles
    public static final int _tsclastcol         = 1407; // [F/T]   Table Styles
    public static final int _tsclastrow         = 1408; // [F/T]   Table Styles
    public static final int _tscnecell          = 1409; // [F/T]   Table Styles
    public static final int _tscnwcell          = 1410; // [F/T]   Table Styles
    public static final int _tscsecell          = 1411; // [F/T]   Table Styles
    public static final int _tscswcell          = 1412; // [F/T]   Table Styles
    public static final int _tsd                = 1413; // [F/T]   Table Styles
    public static final int _tsnowrap           = 1414; // [F/T]   Table Styles
    public static final int _tsrowd             = 1415; // [F/T]   Style Sheet
    public static final int _tsvertalb          = 1416; // [F/T]   Table Styles
    public static final int _tsvertalc          = 1417; // [F/T]   Table Styles
    public static final int _tsvertalt          = 1418; // [F/T]   Table Styles
    public static final int _twoonone           = 1419; // [F/E]   Document Formatting Properties
    public static final int _tx                 = 1420; // [V]     Tabs
    public static final int _txe                = 1421; // [D]     Index Entries
    public static final int _uc                 = 1422; // [VN/S]  Unicode RTF
    public static final int _ud                 = 1423; // [D/S]   Unicode RTF
    public static final int _ul                 = 1424; // [T]     Font (Character) Formatting Properties
    public static final int _ulc                = 1425; // [VN/K]  Font (Character) Formatting Properties
    public static final int _uld                = 1426; // [F]     Font (Character) Formatting Properties
    public static final int _uldash             = 1427; // [T/E]   Font (Character) Formatting Properties
    public static final int _uldashd            = 1428; // [T/E]   Font (Character) Formatting Properties
    public static final int _uldashdd           = 1429; // [T/E]   Font (Character) Formatting Properties
    public static final int _uldb               = 1430; // [T]     Font (Character) Formatting Properties
    public static final int _ulhair             = 1431; // [T/E]   Font (Character) Formatting Properties
    public static final int _ulhwave            = 1432; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulldash            = 1433; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulnone             = 1434; // [F]     Font (Character) Formatting Properties
    public static final int _ulth               = 1435; // [T/E]   Font (Character) Formatting Properties
    public static final int ___ulth             = 1436; // [T/S]   Character Text
    public static final int _ulthd              = 1437; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulthdash           = 1438; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulthdashd          = 1439; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulthdashdd         = 1440; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulthldash          = 1441; // [T/K]   Font (Character) Formatting Properties
    public static final int _ululdbwave         = 1442; // [T/K]   Font (Character) Formatting Properties
    public static final int _ulw                = 1443; // [F]     Font (Character) Formatting Properties
    public static final int _ulwave             = 1444; // [T/E]   Font (Character) Formatting Properties
    public static final int _u                  = 1445; // [VN/S]  Unicode RTF
    public static final int _up                 = 1446; // [V]     Font (Character) Formatting Properties
    public static final int _upr                = 1447; // [D/S]   Unicode RTF
    public static final int _urtf               = 1448; // [DN]    Control Words Introduced by Other Microsoft Products
    public static final int _useltbaln          = 1449; // [F/K]   Document Formatting Properties
    public static final int _userprops          = 1450; // [D/E]   Information Group
    public static final int _v                  = 1451; // [T]     Font (Character) Formatting Properties
    public static final int _vern               = 1452; // [V]     Information Group
    public static final int _version            = 1453; // [V]     Information Group
    public static final int _vertalb            = 1454; // [F]     Section Formatting Properties
    public static final int _vertalc            = 1455; // [F]     Section Formatting Properties
    public static final int _vertalj            = 1456; // [F]     Section Formatting Properties
    public static final int _vertalt            = 1457; // [F]     Section Formatting Properties
    public static final int _vertdoc            = 1458; // [F/E]   Document Formatting Properties
    public static final int _vertsect           = 1459; // [F/E]   Section Formatting Properties
    public static final int _viewkind           = 1460; // [VN/S]  Document Formatting Properties
    public static final int _viewnobound        = 1461; // [F/T]   Document Formatting Properties
    public static final int _viewscale          = 1462; // [VN/S]  Document Formatting Properties
    public static final int _viewzk             = 1463; // [VN/S]  Document Formatting Properties
    public static final int _wbitmap            = 1464; // [V]     Pictures
    public static final int _wbmbitspixel       = 1465; // [V]     Pictures
    public static final int _wbmplanes          = 1466; // [V]     Pictures
    public static final int _wbmwidthbytes      = 1467; // [V]     Pictures
    public static final int _webhidden          = 1468; // [F/K]   Font (Character) Formatting Properties
    public static final int _widctlpar          = 1469; // [F]     Paragraph Formatting Properties
    public static final int _widowctrl          = 1470; // [F]     Document Formatting Properties
    public static final int _windowcaption      = 1471; // [V/S]   Document Formatting Properties
    public static final int _wmetafile          = 1472; // [V]     Pictures
    public static final int _wpeqn              = 1473; // [F/S]   Fields
    public static final int _wpjst              = 1474; // [F/S]   Document Formatting Properties
    public static final int _wpsp               = 1475; // [F/S]   Document Formatting Properties
    public static final int _wraptrsp           = 1476; // [F]     Document Formatting Properties
    public static final int _wrppunct           = 1477; // [F/T]   Document Formatting Properties
    public static final int _xe                 = 1478; // [D]     Index Entries
    public static final int _xef                = 1479; // [V]     Index Entries
    public static final int _yr                 = 1480; // [V]     Information Group
    public static final int _yts                = 1481; // [VN/T]  Paragraph Formatting Properties
    public static final int _yxe                = 1482; // [F/S]   Index Entries
    public static final int _zwbo               = 1483; // [S/E]   Special Characters
    public static final int _zwj                = 1484; // [S/T]   Special Characters
    public static final int _zwnbo              = 1485; // [S/E]   Special Characters
    public static final int _zwnj               = 1486; // [S/T]   Special Characters

    public static final int _brdrnone           = 1700;
    public static final int _jclisttab          = 1701;
    public static final int _shp                = 1702;
    public static final int _shpinst            = 1703;
    public static final int _sn                 = 1704;
    public static final int _sp                 = 1705;
    public static final int _sv                 = 1706;
    public static final int _list               = 1707;
    public static final int _listtable          = 1708;
    public static final int _listlevel          = 1709;
    public static final int _lfolevel           = 1710;
    public static final int _listoverridetable  = 1711;
    public static final int _listoverride       = 1712;
    public static final int _listoverridestartat= 1713;

    private static void words()  {
        add( "ab                  ", _ab                  , T_TOGGLE );
        add( "absh                ", _absh                , T_VALUE  );
        add( "abslock             ", _abslock             , T_FLAG   );
        add( "absnoovrlp          ", _absnoovrlp          , T_TOGGLE );
        add( "absw                ", _absw                , T_VALUE  );
        add( "acaps               ", _acaps               , T_TOGGLE );
        add( "acccomma            ", _acccomma            , T_TOGGLE );
        add( "accdot              ", _accdot              , T_TOGGLE );
        add( "accnone             ", _accnone             , T_TOGGLE );
        add( "acf                 ", _acf                 , T_VALUE  );
        add( "additive            ", _additive            , T_FLAG   );
        add( "adjustright         ", _adjustright         , T_FLAG   );
        add( "adn                 ", _adn                 , T_VALUE  );
        add( "aenddoc             ", _aenddoc             , T_FLAG   );
        add( "aendnotes           ", _aendnotes           , T_FLAG   );
        add( "aexpnd              ", _aexpnd              , T_VALUE  );
        add( "af                  ", _af                  , T_VALUE  );
        add( "affixed             ", _affixed             , T_FLAG   );
        add( "afs                 ", _afs                 , T_VALUE  );
        add( "aftnbj              ", _aftnbj              , T_FLAG   );
        add( "aftncn              ", _aftncn              , T_DEST   );
        add( "aftnnalc            ", _aftnnalc            , T_FLAG   );
        add( "aftnnar             ", _aftnnar             , T_FLAG   );
        add( "aftnnauc            ", _aftnnauc            , T_FLAG   );
        add( "aftnnchi            ", _aftnnchi            , T_FLAG   );
        add( "aftnnchosung        ", _aftnnchosung        , T_FLAG   );
        add( "aftnncnum           ", _aftnncnum           , T_FLAG   );
        add( "aftnndbar           ", _aftnndbar           , T_FLAG   );
        add( "aftnndbnum          ", _aftnndbnum          , T_FLAG   );
        add( "aftnndbnumd         ", _aftnndbnumd         , T_FLAG   );
        add( "aftnndbnumk         ", _aftnndbnumk         , T_FLAG   );
        add( "aftnndbnumt         ", _aftnndbnumt         , T_FLAG   );
        add( "aftnnganada         ", _aftnnganada         , T_FLAG   );
        add( "aftnngbnum          ", _aftnngbnum          , T_FLAG   );
        add( "aftnngbnumd         ", _aftnngbnumd         , T_FLAG   );
        add( "aftnngbnumk         ", _aftnngbnumk         , T_FLAG   );
        add( "aftnngbnuml         ", _aftnngbnuml         , T_FLAG   );
        add( "aftnnrlc            ", _aftnnrlc            , T_FLAG   );
        add( "aftnnruc            ", _aftnnruc            , T_FLAG   );
        add( "aftnnzodiac         ", _aftnnzodiac         , T_FLAG   );
        add( "aftnnzodiacd        ", _aftnnzodiacd        , T_FLAG   );
        add( "aftnnzodiacl        ", _aftnnzodiacl        , T_FLAG   );
        add( "aftnrestart         ", _aftnrestart         , T_FLAG   );
        add( "aftnrstcont         ", _aftnrstcont         , T_FLAG   );
        add( "aftnsep             ", _aftnsep             , T_DEST   );
        add( "aftnsepc            ", _aftnsepc            , T_DEST   );
        add( "aftnstart           ", _aftnstart           , T_VALUE  );
        add( "aftntj              ", _aftntj              , T_FLAG   );
        add( "ai                  ", _ai                  , T_TOGGLE );
        add( "alang               ", _alang               , T_VALUE  );
        add( "allowfieldendsel    ", _allowfieldendsel    , T_FLAG   );
        add( "allprot             ", _allprot             , T_FLAG   );
        add( "alntblind           ", _alntblind           , T_FLAG   );
        add( "alt                 ", _alt                 , T_FLAG   );
        add( "animtext            ", _animtext            , T_VALUE  );
        add( "annotation          ", _annotation          , T_DEST   );
        add( "annotprot           ", _annotprot           , T_FLAG   );
        add( "ansi                ", _ansi                , T_FLAG   );
        add( "ansicpg             ", _ansicpg             , T_VALUE  );
        add( "aoutl               ", _aoutl               , T_TOGGLE );
        add( "ApplyBrkRules       ", _ApplyBrkRules       , T_FLAG   );
        add( "ascaps              ", _ascaps              , T_TOGGLE );
        add( "ashad               ", _ashad               , T_TOGGLE );
        add( "asianbrkrule        ", _asianbrkrule        , T_FLAG   );
        add( "aspalpha            ", _aspalpha            , T_TOGGLE );
        add( "aspnum              ", _aspnum              , T_TOGGLE );
        add( "astrike             ", _astrike             , T_TOGGLE );
        add( "atnauthor           ", _atnauthor           , T_DEST   );
        add( "atndate             ", _atndate             , T_DEST   );
        add( "atnicn              ", _atnicn              , T_DEST   );
        add( "atnid               ", _atnid               , T_DEST   );
        add( "atnparent           ", _atnparent           , T_DEST   );
        add( "atnref              ", _atnref              , T_DEST   );
        add( "atntime             ", _atntime             , T_DEST   );
        add( "atrfend             ", _atrfend             , T_DEST   );
        add( "atrfstart           ", _atrfstart           , T_DEST   );
        add( "aul                 ", _aul                 , T_TOGGLE );
        add( "auld                ", _auld                , T_TOGGLE );
        add( "auldb               ", _auldb               , T_TOGGLE );
        add( "aulnone             ", _aulnone             , T_TOGGLE );
        add( "aulw                ", _aulw                , T_TOGGLE );
        add( "aup                 ", _aup                 , T_VALUE  );
        add( "author              ", _author              , T_DEST   );
        add( "b                   ", _b                   , T_TOGGLE );
        add( "background          ", _background          , T_DEST   );
        add( "bdbfhdr             ", _bdbfhdr             , T_FLAG   );
        add( "bdrrlswsix          ", _bdrrlswsix          , T_FLAG   );
        add( "bgbdiag             ", _bgbdiag             , T_FLAG   );
        add( "bgcross             ", _bgcross             , T_FLAG   );
        add( "bgdcross            ", _bgdcross            , T_FLAG   );
        add( "bgdkbdiag           ", _bgdkbdiag           , T_FLAG   );
        add( "bgdkcross           ", _bgdkcross           , T_FLAG   );
        add( "bgdkdcross          ", _bgdkdcross          , T_FLAG   );
        add( "bgdkfdiag           ", _bgdkfdiag           , T_FLAG   );
        add( "bgdkhoriz           ", _bgdkhoriz           , T_FLAG   );
        add( "bgdkvert            ", _bgdkvert            , T_FLAG   );
        add( "bgfdiag             ", _bgfdiag             , T_FLAG   );
        add( "bghoriz             ", _bghoriz             , T_FLAG   );
        add( "bgvert              ", _bgvert              , T_FLAG   );
        add( "bin                 ", _bin                 , T_VALUE  );
        add( "binfsxn             ", _binfsxn             , T_VALUE  );
        add( "binsxn              ", _binsxn              , T_VALUE  );
        add( "bkmkcolf            ", _bkmkcolf            , T_VALUE  );
        add( "bkmkcoll            ", _bkmkcoll            , T_VALUE  );
        add( "bkmkend             ", _bkmkend             , T_DEST   );
        add( "bkmkpub             ", _bkmkpub             , T_FLAG   );
        add( "bkmkstart           ", _bkmkstart           , T_DEST   );
        add( "bliptag             ", _bliptag             , T_VALUE  );
        add( "blipuid             ", _blipuid             , T_VALUE  );
        add( "blipupi             ", _blipupi             , T_VALUE  );
        add( "blue                ", _blue                , T_VALUE  );
        add( "bookfold            ", _bookfold            , T_FLAG   );
        add( "bookfoldrev         ", _bookfoldrev         , T_FLAG   );
        add( "bookfoldsheets      ", _bookfoldsheets      , T_VALUE  );
        add( "box                 ", _box                 , T_FLAG   );
        add( "brdrart             ", _brdrart             , T_VALUE  );
        add( "brdrb               ", _brdrb               , T_FLAG   );
        add( "brdrbar             ", _brdrbar             , T_FLAG   );
        add( "brdrbtw             ", _brdrbtw             , T_FLAG   );
        add( "brdrcf              ", _brdrcf              , T_VALUE  );
        add( "brdrdash            ", _brdrdash            , T_FLAG   );
        add( "brdrdashd           ", _brdrdashd           , T_FLAG   );
        add( "brdrdashdd          ", _brdrdashdd          , T_FLAG   );
        add( "brdrdashdotstr      ", _brdrdashdotstr      , T_FLAG   );
        add( "brdrdashsm          ", _brdrdashsm          , T_FLAG   );
        add( "brdrdb              ", _brdrdb              , T_FLAG   );
        add( "brdrdot             ", _brdrdot             , T_FLAG   );
        add( "brdremboss          ", _brdremboss          , T_FLAG   );
        add( "brdrengrave         ", _brdrengrave         , T_FLAG   );
        add( "brdrframe           ", _brdrframe           , T_FLAG   );
        add( "brdrhair            ", _brdrhair            , T_FLAG   );
        add( "brdrinset           ", _brdrinset           , T_FLAG   );
        add( "brdrl               ", _brdrl               , T_FLAG   );
        add( "brdrnil             ", _brdrnil             , T_FLAG   );
        add( "brdroutset          ", _brdroutset          , T_FLAG   );
        add( "brdrr               ", _brdrr               , T_FLAG   );
        add( "brdrs               ", _brdrs               , T_FLAG   );
        add( "brdrsh              ", _brdrsh              , T_FLAG   );
        add( "brdrt               ", _brdrt               , T_FLAG   );
        add( "brdrtbl             ", _brdrtbl             , T_FLAG   );
        add( "brdrth              ", _brdrth              , T_FLAG   );
        add( "brdrthtnlg          ", _brdrthtnlg          , T_FLAG   );
        add( "brdrthtnmg          ", _brdrthtnmg          , T_FLAG   );
        add( "brdrthtnsg          ", _brdrthtnsg          , T_FLAG   );
        add( "brdrtnthlg          ", _brdrtnthlg          , T_FLAG   );
        add( "brdrtnthmg          ", _brdrtnthmg          , T_FLAG   );
        add( "brdrtnthsg          ", _brdrtnthsg          , T_FLAG   );
        add( "brdrtnthtnlg        ", _brdrtnthtnlg        , T_FLAG   );
        add( "brdrtnthtnmg        ", _brdrtnthtnmg        , T_FLAG   );
        add( "brdrtnthtnsg        ", _brdrtnthtnsg        , T_FLAG   );
        add( "brdrtriple          ", _brdrtriple          , T_FLAG   );
        add( "brdrw               ", _brdrw               , T_VALUE  );
        add( "brdrwavy            ", _brdrwavy            , T_FLAG   );
        add( "brdrwavydb          ", _brdrwavydb          , T_FLAG   );
        add( "brkfrm              ", _brkfrm              , T_FLAG   );
        add( "brsp                ", _brsp                , T_VALUE  );
        add( "bullet              ", _bullet              , T_SYMBOL );
        add( "buptim              ", _buptim              , T_DEST   );
        add( "bxe                 ", _bxe                 , T_FLAG   );
        add( "caps                ", _caps                , T_TOGGLE );
        add( "category            ", _category            , T_DEST   );
        add( "cb                  ", _cb                  , T_VALUE  );
        add( "cbpat               ", _cbpat               , T_VALUE  );
        add( "cchs                ", _cchs                , T_VALUE  );
        add( "cell                ", _cell                , T_SYMBOL );
        add( "cellx               ", _cellx               , T_VALUE  );
        add( "cf                  ", _cf                  , T_VALUE  );
        add( "cfpat               ", _cfpat               , T_VALUE  );
        add( "cgrid               ", _cgrid               , T_VALUE  );
        add( "charrsid            ", _charrsid            , T_VALUE  );
        add( "charscalex          ", _charscalex          , T_VALUE  );
        add( "chatn               ", _chatn               , T_SYMBOL );
        add( "chbgbdiag           ", _chbgbdiag           , T_FLAG   );
        add( "chbgcross           ", _chbgcross           , T_FLAG   );
        add( "chbgdcross          ", _chbgdcross          , T_FLAG   );
        add( "chbgdkbdiag         ", _chbgdkbdiag         , T_FLAG   );
        add( "chbgdkcross         ", _chbgdkcross         , T_FLAG   );
        add( "chbgdkdcross        ", _chbgdkdcross        , T_FLAG   );
        add( "chbgdkfdiag         ", _chbgdkfdiag         , T_FLAG   );
        add( "chbgdkhoriz         ", _chbgdkhoriz         , T_FLAG   );
        add( "chbgdkvert          ", _chbgdkvert          , T_FLAG   );
        add( "chbgfdiag           ", _chbgfdiag           , T_FLAG   );
        add( "chbghoriz           ", _chbghoriz           , T_FLAG   );
        add( "chbgvert            ", _chbgvert            , T_FLAG   );
        add( "chbrdr              ", _chbrdr              , T_FLAG   );
        add( "chcbpat             ", _chcbpat             , T_VALUE  );
        add( "chcfpat             ", _chcfpat             , T_VALUE  );
        add( "chdate              ", _chdate              , T_SYMBOL );
        add( "chdpa               ", _chdpa               , T_SYMBOL );
        add( "chdpl               ", _chdpl               , T_SYMBOL );
        add( "chftn               ", _chftn               , T_SYMBOL );
        add( "chftnsep            ", _chftnsep            , T_SYMBOL );
        add( "chftnsepc           ", _chftnsepc           , T_SYMBOL );
        add( "chpgn               ", _chpgn               , T_SYMBOL );
        add( "chshdng             ", _chshdng             , T_VALUE  );
        add( "chtime              ", _chtime              , T_SYMBOL );
        add( "clbgbdiag           ", _clbgbdiag           , T_FLAG   );
        add( "clbgcross           ", _clbgcross           , T_FLAG   );
        add( "clbgdcross          ", _clbgdcross          , T_FLAG   );
        add( "clbgdkbdiag         ", _clbgdkbdiag         , T_FLAG   );
        add( "clbgdkcross         ", _clbgdkcross         , T_FLAG   );
        add( "clbgdkdcross        ", _clbgdkdcross        , T_FLAG   );
        add( "clbgdkfdiag         ", _clbgdkfdiag         , T_FLAG   );
        add( "clbgdkhor           ", _clbgdkhor           , T_FLAG   );
        add( "clbgdkvert          ", _clbgdkvert          , T_FLAG   );
        add( "clbgfdiag           ", _clbgfdiag           , T_FLAG   );
        add( "clbghoriz           ", _clbghoriz           , T_FLAG   );
        add( "clbgvert            ", _clbgvert            , T_FLAG   );
        add( "clbrdrb             ", _clbrdrb             , T_FLAG   );
        add( "clbrdrl             ", _clbrdrl             , T_FLAG   );
        add( "clbrdrr             ", _clbrdrr             , T_FLAG   );
        add( "clbrdrt             ", _clbrdrt             , T_FLAG   );
        add( "clcbpat             ", _clcbpat             , T_VALUE  );
        add( "clcbpatraw          ", _clcbpatraw          , T_VALUE  );
        add( "clcfpat             ", _clcfpat             , T_VALUE  );
        add( "clcfpatraw          ", _clcfpatraw          , T_VALUE  );
        add( "cldgll              ", _cldgll              , T_FLAG   );
        add( "cldglu              ", _cldglu              , T_FLAG   );
        add( "clFitText           ", _clFitText           , T_FLAG   );
        add( "clftsWidth          ", _clftsWidth          , T_VALUE  );
        add( "clmgf               ", _clmgf               , T_FLAG   );
        add( "clmrg               ", _clmrg               , T_FLAG   );
        add( "clNoWrap            ", _clNoWrap            , T_FLAG   );
        add( "clpadb              ", _clpadb              , T_VALUE  );
        add( "clpadfb             ", _clpadfb             , T_VALUE  );
        add( "clpadfl             ", _clpadfl             , T_VALUE  );
        add( "clpadfr             ", _clpadfr             , T_VALUE  );
        add( "clpadft             ", _clpadft             , T_VALUE  );
        add( "clpadl              ", _clpadl              , T_VALUE  );
        add( "clpadr              ", _clpadr              , T_VALUE  );
        add( "clpadt              ", _clpadt              , T_VALUE  );
        add( "clshdng             ", _clshdng             , T_VALUE  );
        add( "clshdngraw          ", _clshdngraw          , T_VALUE  );
        add( "clshdrawnil         ", _clshdrawnil         , T_FLAG   );
        add( "cltxbtlr            ", _cltxbtlr            , T_FLAG   );
        add( "cltxlrtb            ", _cltxlrtb            , T_FLAG   );
        add( "cltxlrtb            ", _cltxlrtb            , T_FLAG   );
        add( "cltxlrtbv           ", _cltxlrtbv           , T_FLAG   );
        add( "cltxtbrl            ", _cltxtbrl            , T_FLAG   );
        add( "cltxtbrlv           ", _cltxtbrlv           , T_FLAG   );
        add( "clvertalb           ", _clvertalb           , T_FLAG   );
        add( "clvertalc           ", _clvertalc           , T_FLAG   );
        add( "clvertalt           ", _clvertalt           , T_FLAG   );
        add( "clvmgf              ", _clvmgf              , T_FLAG   );
        add( "clvmrg              ", _clvmrg              , T_FLAG   );
        add( "clwWidth            ", _clwWidth            , T_VALUE  );
        add( "collapsed           ", _collapsed           , T_FLAG   );
        add( "colno               ", _colno               , T_VALUE  );
        add( "colortbl            ", _colortbl            , T_DEST   );
        add( "cols                ", _cols                , T_VALUE  );
        add( "colsr               ", _colsr               , T_VALUE  );
        add( "colsx               ", _colsx               , T_VALUE  );
        add( "column              ", _column              , T_SYMBOL );
        add( "colw                ", _colw                , T_VALUE  );
        add( "comment             ", _comment             , T_DEST   );
        add( "company             ", _company             , T_DEST   );
        add( "cpg                 ", _cpg                 , T_VALUE  );
        add( "crauth              ", _crauth              , T_VALUE  );
        add( "crdate              ", _crdate              , T_VALUE  );
        add( "creatim             ", _creatim             , T_DEST   );
        add( "cs                  ", _cs                  , T_VALUE  );
        add( "ctrl                ", _ctrl                , T_FLAG   );
        add( "cts                 ", _cts                 , T_VALUE  );
        add( "cufi                ", _cufi                , T_VALUE  );
        add( "culi                ", _culi                , T_VALUE  );
        add( "curi                ", _curi                , T_VALUE  );
        add( "cvmme               ", _cvmme               , T_FLAG   );
        add( "datafield           ", _datafield           , T_DEST   );
        add( "date                ", _date                , T_FLAG   );
        add( "dbch                ", _dbch                , T_FLAG   );
        add( "deff                ", _deff                , T_VALUE  );
        add( "defformat           ", _defformat           , T_FLAG   );
        add( "deflang             ", _deflang             , T_VALUE  );
        add( "deflangfe           ", _deflangfe           , T_VALUE  );
        add( "defshp              ", _defshp              , T_FLAG   );
        add( "deftab              ", _deftab              , T_VALUE  );
        add( "deleted             ", _deleted             , T_TOGGLE );
        add( "delrsid             ", _delrsid             , T_VALUE  );
        add( "dfrauth             ", _dfrauth             , T_VALUE  );
        add( "dfrdate             ", _dfrdate             , T_VALUE  );
        add( "dfrmtxtx            ", _dfrmtxtx            , T_VALUE  );
        add( "dfrmtxty            ", _dfrmtxty            , T_VALUE  );
        add( "dfrstart            ", _dfrstart            , T_VALUE  );
        add( "dfrstop             ", _dfrstop             , T_VALUE  );
        add( "dfrxst              ", _dfrxst              , T_VALUE  );
        add( "dghorigin           ", _dghorigin           , T_VALUE  );
        add( "dghshow             ", _dghshow             , T_VALUE  );
        add( "dghspace            ", _dghspace            , T_VALUE  );
        add( "dgmargin            ", _dgmargin            , T_FLAG   );
        add( "dgsnap              ", _dgsnap              , T_FLAG   );
        add( "dgvorigin           ", _dgvorigin           , T_VALUE  );
        add( "dgvshow             ", _dgvshow             , T_VALUE  );
        add( "dgvspace            ", _dgvspace            , T_VALUE  );
        add( "dibitmap            ", _dibitmap            , T_VALUE  );
        add( "dn                  ", _dn                  , T_VALUE  );
        add( "dntblnsbdb          ", _dntblnsbdb          , T_FLAG   );
        add( "do                  ", _do                  , T_DEST   );
        add( "dobxcolumn          ", _dobxcolumn          , T_FLAG   );
        add( "dobxmargin          ", _dobxmargin          , T_FLAG   );
        add( "dobxpage            ", _dobxpage            , T_FLAG   );
        add( "dobymargin          ", _dobymargin          , T_FLAG   );
        add( "dobypage            ", _dobypage            , T_FLAG   );
        add( "dobypara            ", _dobypara            , T_FLAG   );
        add( "doccomm             ", _doccomm             , T_DEST   );
        add( "doctemp             ", _doctemp             , T_FLAG   );
        add( "doctype             ", _doctype             , T_VALUE  );
        add( "docvar              ", _docvar              , T_DEST   );
        add( "dodhgt              ", _dodhgt              , T_VALUE  );
        add( "dolock              ", _dolock              , T_FLAG   );
        add( "donotshowcomments   ", _donotshowcomments   , T_FLAG   );
        add( "donotshowinsdel     ", _donotshowinsdel     , T_FLAG   );
        add( "donotshowmarkup     ", _donotshowmarkup     , T_FLAG   );
        add( "donotshowprops      ", _donotshowprops      , T_FLAG   );
        add( "dpaendhol           ", _dpaendhol           , T_FLAG   );
        add( "dpaendl             ", _dpaendl             , T_VALUE  );
        add( "dpaendsol           ", _dpaendsol           , T_FLAG   );
        add( "dpaendw             ", _dpaendw             , T_VALUE  );
        add( "dparc               ", _dparc               , T_FLAG   );
        add( "dparcflipx          ", _dparcflipx          , T_FLAG   );
        add( "dparcflipy          ", _dparcflipy          , T_FLAG   );
        add( "dpastarthol         ", _dpastarthol         , T_FLAG   );
        add( "dpastartl           ", _dpastartl           , T_VALUE  );
        add( "dpastartsol         ", _dpastartsol         , T_FLAG   );
        add( "dpastartw           ", _dpastartw           , T_VALUE  );
        add( "dpcallout           ", _dpcallout           , T_FLAG   );
        add( "dpcoa               ", _dpcoa               , T_VALUE  );
        add( "dpcoaccent          ", _dpcoaccent          , T_FLAG   );
        add( "dpcobestfit         ", _dpcobestfit         , T_FLAG   );
        add( "dpcoborder          ", _dpcoborder          , T_FLAG   );
        add( "dpcodabs            ", _dpcodabs            , T_VALUE  );
        add( "dpcodbottom         ", _dpcodbottom         , T_FLAG   );
        add( "dpcodcenter         ", _dpcodcenter         , T_FLAG   );
        add( "dpcodescent         ", _dpcodescent         , T_VALUE  );
        add( "dpcodtop            ", _dpcodtop            , T_FLAG   );
        add( "dpcolength          ", _dpcolength          , T_VALUE  );
        add( "dpcominusx          ", _dpcominusx          , T_FLAG   );
        add( "dpcominusy          ", _dpcominusy          , T_FLAG   );
        add( "dpcooffset          ", _dpcooffset          , T_VALUE  );
        add( "dpcosmarta          ", _dpcosmarta          , T_FLAG   );
        add( "dpcotdouble         ", _dpcotdouble         , T_FLAG   );
        add( "dpcotright          ", _dpcotright          , T_FLAG   );
        add( "dpcotsingle         ", _dpcotsingle         , T_FLAG   );
        add( "dpcottriple         ", _dpcottriple         , T_FLAG   );
        add( "dpcount             ", _dpcount             , T_VALUE  );
        add( "dpellipse           ", _dpellipse           , T_FLAG   );
        add( "dpendgroup          ", _dpendgroup          , T_FLAG   );
        add( "dpfillbgcb          ", _dpfillbgcb          , T_VALUE  );
        add( "dpfillbgcg          ", _dpfillbgcg          , T_VALUE  );
        add( "dpfillbgcr          ", _dpfillbgcr          , T_VALUE  );
        add( "dpfillbggray        ", _dpfillbggray        , T_VALUE  );
        add( "dpfillbgpal         ", _dpfillbgpal         , T_FLAG   );
        add( "dpfillfgcb          ", _dpfillfgcb          , T_VALUE  );
        add( "dpfillfgcg          ", _dpfillfgcg          , T_VALUE  );
        add( "dpfillfgcr          ", _dpfillfgcr          , T_VALUE  );
        add( "dpfillfggray        ", _dpfillfggray        , T_VALUE  );
        add( "dpfillfgpal         ", _dpfillfgpal         , T_FLAG   );
        add( "dpfillpat           ", _dpfillpat           , T_VALUE  );
        add( "dpgroup             ", _dpgroup             , T_FLAG   );
        add( "dpline              ", _dpline              , T_FLAG   );
        add( "dplinecob           ", _dplinecob           , T_VALUE  );
        add( "dplinecog           ", _dplinecog           , T_VALUE  );
        add( "dplinecor           ", _dplinecor           , T_VALUE  );
        add( "dplinedado          ", _dplinedado          , T_FLAG   );
        add( "dplinedadodo        ", _dplinedadodo        , T_FLAG   );
        add( "dplinedash          ", _dplinedash          , T_FLAG   );
        add( "dplinedot           ", _dplinedot           , T_FLAG   );
        add( "dplinegray          ", _dplinegray          , T_VALUE  );
        add( "dplinehollow        ", _dplinehollow        , T_FLAG   );
        add( "dplinepal           ", _dplinepal           , T_FLAG   );
        add( "dplinesolid         ", _dplinesolid         , T_FLAG   );
        add( "dplinew             ", _dplinew             , T_VALUE  );
        add( "dppolycount         ", _dppolycount         , T_VALUE  );
        add( "dppolygon           ", _dppolygon           , T_FLAG   );
        add( "dppolyline          ", _dppolyline          , T_FLAG   );
        add( "dpptx               ", _dpptx               , T_VALUE  );
        add( "dppty               ", _dppty               , T_VALUE  );
        add( "dprect              ", _dprect              , T_FLAG   );
        add( "dproundr            ", _dproundr            , T_FLAG   );
        add( "dpshadow            ", _dpshadow            , T_FLAG   );
        add( "dpshadx             ", _dpshadx             , T_VALUE  );
        add( "dpshady             ", _dpshady             , T_VALUE  );
        add( "dptxbtlr            ", _dptxbtlr            , T_FLAG   );
        add( "dptxbx              ", _dptxbx              , T_FLAG   );
        add( "dptxbxmar           ", _dptxbxmar           , T_VALUE  );
        add( "dptxbxtext          ", _dptxbxtext          , T_DEST   );
        add( "dptxlrtb            ", _dptxlrtb            , T_FLAG   );
        add( "dptxlrtbv           ", _dptxlrtbv           , T_FLAG   );
        add( "dptxtbrl            ", _dptxtbrl            , T_FLAG   );
        add( "dptxtbrlv           ", _dptxtbrlv           , T_FLAG   );
        add( "dpx                 ", _dpx                 , T_VALUE  );
        add( "dpxsize             ", _dpxsize             , T_VALUE  );
        add( "dpy                 ", _dpy                 , T_VALUE  );
        add( "dpysize             ", _dpysize             , T_VALUE  );
        add( "dropcapli           ", _dropcapli           , T_VALUE  );
        add( "dropcapt            ", _dropcapt            , T_VALUE  );
        add( "ds                  ", _ds                  , T_VALUE  );
        add( "dxfrtext            ", _dxfrtext            , T_VALUE  );
        add( "dy                  ", _dy                  , T_VALUE  );
        add( "edmins              ", _edmins              , T_VALUE  );
        add( "embo                ", _embo                , T_TOGGLE );
        add( "emdash              ", _emdash              , T_SYMBOL );
        add( "emfblip             ", _emfblip             , T_FLAG   );
        add( "emspace             ", _emspace             , T_SYMBOL );
        add( "endash              ", _endash              , T_SYMBOL );
        add( "enddoc              ", _enddoc              , T_FLAG   );
        add( "endnhere            ", _endnhere            , T_FLAG   );
        add( "endnotes            ", _endnotes            , T_FLAG   );
        add( "enspace             ", _enspace             , T_SYMBOL );
        add( "expnd               ", _expnd               , T_VALUE  );
        add( "expndtw             ", _expndtw             , T_VALUE  );
        add( "expshrtn            ", _expshrtn            , T_FLAG   );
        add( "f                   ", _f                   , T_VALUE  );
        add( "faauto              ", _faauto              , T_VALUE  );
        add( "facenter7           ", _facenter            , T_FLAG   );
        add( "facingp             ", _facingp             , T_FLAG   );
        add( "fahang              ", _fahang              , T_FLAG   );
        add( "falt                ", _falt                , T_DEST   );
        add( "faroman             ", _faroman             , T_FLAG   );
        add( "favar               ", _favar               , T_FLAG   );
        add( "fbias               ", _fbias               , T_VALUE  );
        add( "fbidi               ", _fbidi               , T_FLAG   );
        add( "fchars              ", _fchars              , T_DEST   );
        add( "fcharset            ", _fcharset            , T_VALUE  );
        add( "fdecor              ", _fdecor              , T_FLAG   );
        add( "fet                 ", _fet                 , T_VALUE  );
        add( "fetch               ", _fetch               , T_FLAG   );
        add( "ffdefres            ", _ffdefres            , T_VALUE  );
        add( "ffdeftext           ", _ffdeftext           , T_DEST   );
        add( "ffentrymcr          ", _ffentrymcr          , T_DEST   );
        add( "ffexitmcr           ", _ffexitmcr           , T_DEST   );
        add( "ffformat            ", _ffformat            , T_DEST   );
        add( "ffhaslistbox        ", _ffhaslistbox        , T_VALUE  );
        add( "ffhelptext          ", _ffhelptext          , T_DEST   );
        add( "ffhps               ", _ffhps               , T_VALUE  );
        add( "ffl                 ", _ffl                 , T_DEST   );
        add( "ffmaxlen            ", _ffmaxlen            , T_VALUE  );
        add( "ffname              ", _ffname              , T_DEST   );
        add( "ffownhelp           ", _ffownhelp           , T_VALUE  );
        add( "ffownstat           ", _ffownstat           , T_VALUE  );
        add( "ffprot              ", _ffprot              , T_VALUE  );
        add( "ffrecalc            ", _ffrecalc            , T_VALUE  );
        add( "ffres               ", _ffres               , T_VALUE  );
        add( "ffsize              ", _ffsize              , T_VALUE  );
        add( "ffstattext          ", _ffstattext          , T_DEST   );
        add( "fftype              ", _fftype              , T_VALUE  );
        add( "fftypetxt           ", _fftypetxt           , T_VALUE  );
        add( "fi                  ", _fi                  , T_VALUE  );
        add( "fid                 ", _fid                 , T_VALUE  );
        add( "field               ", _field               , T_DEST   );
        add( "file                ", _file                , T_DEST   );
        add( "filetbl             ", _filetbl             , T_DEST   );
        add( "fittext             ", _fittext             , T_VALUE  );
        add( "fldalt              ", _fldalt              , T_FLAG   );
        add( "flddirty            ", _flddirty            , T_FLAG   );
        add( "fldedit             ", _fldedit             , T_FLAG   );
        add( "fldinst             ", _fldinst             , T_DEST   );
        add( "fldlock             ", _fldlock             , T_FLAG   );
        add( "fldpriv             ", _fldpriv             , T_FLAG   );
        add( "fldrslt             ", _fldrslt             , T_DEST   );
        add( "fldtype             ", _fldtype             , T_DEST   );
        add( "fmodern             ", _fmodern             , T_FLAG   );
        add( "fn                  ", _fn                  , T_VALUE  );
        add( "fname               ", _fname               , T_DEST   );
        add( "fnetwork            ", _fnetwork            , T_FLAG   );
        add( "fnil                ", _fnil                , T_FLAG   );
        add( "fnonfilesys         ", _fnonfilesys         , T_FLAG   );
        add( "fontemb             ", _fontemb             , T_DEST   );
        add( "fontfile            ", _fontfile            , T_DEST   );
        add( "fonttbl             ", _fonttbl             , T_DEST   );
        add( "footer              ", _footer              , T_DEST   );
        add( "footerf             ", _footerf             , T_DEST   );
        add( "footerl             ", _footerl             , T_DEST   );
        add( "footery             ", _footery             , T_VALUE  );
        add( "footnote            ", _footnote            , T_DEST   );
        add( "formdisp            ", _formdisp            , T_FLAG   );
        add( "formfield           ", _formfield           , T_DEST   );
        add( "formprot            ", _formprot            , T_FLAG   );
        add( "formshade           ", _formshade           , T_FLAG   );
        add( "fosnum              ", _fosnum              , T_VALUE  );
        add( "fprq                ", _fprq                , T_VALUE  );
        add( "fracwidth           ", _fracwidth           , T_FLAG   );
        add( "frelative           ", _frelative           , T_VALUE  );
        add( "frmtxbtlr           ", _frmtxbtlr           , T_FLAG   );
        add( "frmtxlrtb           ", _frmtxlrtb           , T_FLAG   );
        add( "frmtxlrtbv          ", _frmtxlrtbv          , T_FLAG   );
        add( "frmtxtbrl           ", _frmtxtbrl           , T_FLAG   );
        add( "frmtxtbrlv          ", _frmtxtbrlv          , T_FLAG   );
        add( "froman              ", _froman              , T_FLAG   );
        add( "fromhtml            ", _fromhtml            , T_FLAG   );
        add( "fromtext            ", _fromtext            , T_FLAG   );
        add( "fs                  ", _fs                  , T_VALUE  );
        add( "fscript             ", _fscript             , T_FLAG   );
        add( "fswiss              ", _fswiss              , T_FLAG   );
        add( "ftnalt              ", _ftnalt              , T_FLAG   );
        add( "ftnbj               ", _ftnbj               , T_FLAG   );
        add( "ftncn               ", _ftncn               , T_DEST   );
        add( "ftnil               ", _ftnil               , T_FLAG   );
        add( "ftnlytwnine         ", _ftnlytwnine         , T_FLAG   );
        add( "ftnnalc             ", _ftnnalc             , T_FLAG   );
        add( "ftnnar              ", _ftnnar              , T_FLAG   );
        add( "ftnnauc             ", _ftnnauc             , T_FLAG   );
        add( "ftnnchi             ", _ftnnchi             , T_FLAG   );
        add( "ftnnchosung         ", _ftnnchosung         , T_FLAG   );
        add( "ftnncnum            ", _ftnncnum            , T_FLAG   );
        add( "ftnndbar            ", _ftnndbar            , T_FLAG   );
        add( "ftnndbnum           ", _ftnndbnum           , T_FLAG   );
        add( "ftnndbnumd          ", _ftnndbnumd          , T_FLAG   );
        add( "ftnndbnumk          ", _ftnndbnumk          , T_FLAG   );
        add( "ftnndbnumt          ", _ftnndbnumt          , T_FLAG   );
        add( "ftnnganada          ", _ftnnganada          , T_FLAG   );
        add( "ftnngbnum           ", _ftnngbnum           , T_FLAG   );
        add( "ftnngbnumd          ", _ftnngbnumd          , T_FLAG   );
        add( "ftnngbnumk          ", _ftnngbnumk          , T_FLAG   );
        add( "ftnngbnuml          ", _ftnngbnuml          , T_FLAG   );
        add( "ftnnrlc             ", _ftnnrlc             , T_FLAG   );
        add( "ftnnruc             ", _ftnnruc             , T_FLAG   );
        add( "ftnnzodiac          ", _ftnnzodiac          , T_FLAG   );
        add( "ftnnzodiacd         ", _ftnnzodiacd         , T_FLAG   );
        add( "ftnnzodiacl         ", _ftnnzodiacl         , T_FLAG   );
        add( "ftnrestart          ", _ftnrestart          , T_FLAG   );
        add( "ftnrstcont          ", _ftnrstcont          , T_FLAG   );
        add( "ftnrstpg            ", _ftnrstpg            , T_FLAG   );
        add( "ftnsep              ", _ftnsep              , T_DEST   );
        add( "ftnsepc             ", _ftnsepc             , T_DEST   );
        add( "ftnstart            ", _ftnstart            , T_VALUE  );
        add( "ftntj               ", _ftntj               , T_FLAG   );
        add( "fttruetype          ", _fttruetype          , T_FLAG   );
        add( "fvaliddos           ", _fvaliddos           , T_FLAG   );
        add( "fvalidhpfs          ", _fvalidhpfs          , T_FLAG   );
        add( "fvalidmac           ", _fvalidmac           , T_FLAG   );
        add( "fvalidntfs          ", _fvalidntfs          , T_FLAG   );
        add( "g                   ", _g                   , T_DEST   );
        add( "gcw                 ", _gcw                 , T_VALUE  );
        add( "generator           ", _generator           , T_DEST   );
        add( "green               ", _green               , T_VALUE  );
        add( "gridtbl             ", _gridtbl             , T_DEST   );
        add( "gutter              ", _gutter              , T_VALUE  );
        add( "gutterprl           ", _gutterprl           , T_FLAG   );
        add( "guttersxn           ", _guttersxn           , T_VALUE  );
        add( "header              ", _header              , T_DEST   );
        add( "headerf             ", _headerf             , T_DEST   );
        add( "headerl             ", _headerl             , T_DEST   );
        add( "headery             ", _headery             , T_VALUE  );
        add( "hich                ", _hich                , T_FLAG   );
        add( "highlight           ", _highlight           , T_VALUE  );
        add( "hlfr                ", _hlfr                , T_VALUE  );
        add( "hlinkbase           ", _hlinkbase           , T_VALUE  );
        add( "hlloc               ", _hlloc               , T_VALUE  );
        add( "hlsrc               ", _hlsrc               , T_VALUE  );
        add( "horzdoc             ", _horzdoc             , T_FLAG   );
        add( "horzsect            ", _horzsect            , T_FLAG   );
        add( "hr                  ", _hr                  , T_VALUE  );
        add( "htmautsp            ", _htmautsp            , T_FLAG   );
        add( "htmlbase            ", _htmlbase            , T_FLAG   );
        add( "htmlrtf             ", _htmlrtf             , T_TOGGLE );
        add( "htmltag             ", _htmltag             , T_DEST   );
        add( "hyphauto            ", _hyphauto            , T_TOGGLE );
        add( "hyphcaps            ", _hyphcaps            , T_TOGGLE );
        add( "hyphconsec          ", _hyphconsec          , T_VALUE  );
        add( "hyphhotz            ", _hyphhotz            , T_VALUE  );
        add( "hyphpar             ", _hyphpar             , T_TOGGLE );
        add( "i                   ", _i                   , T_TOGGLE );
        add( "id                  ", _id                  , T_VALUE  );
        add( "ilvl                ", _ilvl                , T_VALUE  );
        add( "impr                ", _impr                , T_TOGGLE );
        add( "info                ", _info                , T_DEST   );
        add( "insrsid             ", _insrsid             , T_VALUE  );
        add( "intbl               ", _intbl               , T_FLAG   );
        add( "ipgp                ", _ipgp                , T_VALUE  );
        add( "irow                ", _irow                , T_VALUE  );
        add( "irowband            ", _irowband            , T_VALUE  );
        add( "itap                ", _itap                , T_VALUE  );
        add( "ixe                 ", _ixe                 , T_FLAG   );
        add( "jcompress           ", _jcompress           , T_FLAG   );
        add( "jexpand             ", _jexpand             , T_FLAG   );
        add( "jpegblip            ", _jpegblip            , T_FLAG   );
        add( "jsksu               ", _jsksu               , T_FLAG   );
        add( "keep                ", _keep                , T_FLAG   );
        add( "keepn               ", _keepn               , T_FLAG   );
        add( "kerning             ", _kerning             , T_VALUE  );
        add( "keycode             ", _keycode             , T_DEST   );
        add( "keywords            ", _keywords            , T_DEST   );
        add( "ksulang             ", _ksulang             , T_VALUE  );
        add( "landscape           ", _landscape           , T_FLAG   );
        add( "lang                ", _lang                , T_VALUE  );
        add( "langfe              ", _langfe              , T_VALUE  );
        add( "langfenp            ", _langfenp            , T_VALUE  );
        add( "langnp              ", _langnp              , T_VALUE  );
        add( "lastrow             ", _lastrow             , T_FLAG   );
        add( "lbr                 ", _lbr                 , T_SYMBOL );
        add( "lchars              ", _lchars              , T_DEST   );
        add( "ldblquote           ", _ldblquote           , T_SYMBOL );
        add( "level               ", _level               , T_VALUE  );
        add( "levelfollow         ", _levelfollow         , T_VALUE  );
        add( "levelindent         ", _levelindent         , T_VALUE  );
        add( "leveljc             ", _leveljc             , T_VALUE  );
        add( "leveljcn            ", _leveljcn            , T_VALUE  );
        add( "levellegal          ", _levellegal          , T_VALUE  );
        add( "levelnfc            ", _levelnfc            , T_VALUE  );
        add( "levelnfcn           ", _levelnfcn           , T_VALUE  );
        add( "levelnorestart      ", _levelnorestart      , T_VALUE  );
        add( "levelnumbers        ", _levelnumbers        , T_DEST   );
        add( "levelold            ", _levelold            , T_VALUE  );
        add( "levelpicture        ", _levelpicture        , T_VALUE  );
        add( "levelprev           ", _levelprev           , T_VALUE  );
        add( "levelprevspace      ", _levelprevspace      , T_VALUE  );
        add( "levelspace          ", _levelspace          , T_VALUE  );
        add( "levelstartat        ", _levelstartat        , T_VALUE  );
        add( "leveltemplateid     ", _leveltemplateid     , T_VALUE  );
        add( "leveltext           ", _leveltext           , T_VALUE  );
        add( "li                  ", _li                  , T_VALUE  );
        add( "line                ", _line                , T_SYMBOL );
        add( "linebetcol          ", _linebetcol          , T_FLAG   );
        add( "linecont            ", _linecont            , T_FLAG   );
        add( "linemod             ", _linemod             , T_VALUE  );
        add( "lineppage           ", _lineppage           , T_FLAG   );
        add( "linerestart         ", _linerestart         , T_FLAG   );
        add( "linestart           ", _linestart           , T_VALUE  );
        add( "linestarts          ", _linestarts          , T_VALUE  );
        add( "linex               ", _linex               , T_VALUE  );
        add( "linkself            ", _linkself            , T_FLAG   );
        add( "linkstyles          ", _linkstyles          , T_FLAG   );
        add( "linkval             ", _linkval             , T_VALUE  );
        add( "lin                 ", _lin                 , T_VALUE  );
        add( "lisa                ", _lisa                , T_VALUE  );
        add( "lisb                ", _lisb                , T_VALUE  );
        add( "listhybrid          ", _listhybrid          , T_FLAG   );
        add( "listid              ", _listid              , T_VALUE  );
        add( "listname            ", _listname            , T_DEST   );
        add( "listoverridecount   ", _listoverridecount   , T_VALUE  );
        add( "listoverrideformat  ", _listoverrideformat  , T_VALUE  );
        add( "listoverridestart   ", _listoverridestart   , T_VALUE  );
        add( "listpicture         ", _listpicture         , T_VALUE  );
        add( "listrestarthdn      ", _listrestarthdn      , T_VALUE  );
        add( "listsimple          ", _listsimple          , T_VALUE  );
        add( "liststyleid         ", _liststyleid         , T_VALUE  );
        add( "liststylename       ", _liststylename       , T_VALUE  );
        add( "listtemplateid      ", _listtemplateid      , T_VALUE  );
        add( "listtext            ", _listtext            , T_DEST   );
        add( "lnbrkrule           ", _lnbrkrule           , T_FLAG   );
        add( "lndscpsxn           ", _lndscpsxn           , T_FLAG   );
        add( "lnongrid            ", _lnongrid            , T_FLAG   );
        add( "loch                ", _loch                , T_FLAG   );
        add( "lquote              ", _lquote              , T_SYMBOL );
        add( "ls                  ", _ls                  , T_VALUE  );
        add( "ltrch               ", _ltrch               , T_FLAG   );
        add( "ltrdoc              ", _ltrdoc              , T_FLAG   );
        add( "ltrmark             ", _ltrmark             , T_SYMBOL );
        add( "ltrpar              ", _ltrpar              , T_FLAG   );
        add( "ltrrow              ", _ltrrow              , T_FLAG   );
        add( "ltrsect             ", _ltrsect             , T_FLAG   );
        add( "lytcalctblwd        ", _lytcalctblwd        , T_FLAG   );
        add( "lytexcttp           ", _lytexcttp           , T_FLAG   );
        add( "lytprtmet           ", _lytprtmet           , T_FLAG   );
        add( "lyttblrtgr          ", _lyttblrtgr          , T_FLAG   );
        add( "mac                 ", _mac                 , T_FLAG   );
        add( "macpict             ", _macpict             , T_FLAG   );
        add( "makebackup          ", _makebackup          , T_FLAG   );
        add( "manager             ", _manager             , T_DEST   );
        add( "margb               ", _margb               , T_VALUE  );
        add( "margbsxn            ", _margbsxn            , T_VALUE  );
        add( "margl               ", _margl               , T_VALUE  );
        add( "marglsxn            ", _marglsxn            , T_VALUE  );
        add( "margmirror          ", _margmirror          , T_FLAG   );
        add( "margr               ", _margr               , T_VALUE  );
        add( "margrsxn            ", _margrsxn            , T_VALUE  );
        add( "margt               ", _margt               , T_VALUE  );
        add( "margtsxn            ", _margtsxn            , T_VALUE  );
        add( "mhtmltag            ", _mhtmltag            , T_DEST   );
        add( "min                 ", _min                 , T_VALUE  );
        add( "mo                  ", _mo                  , T_VALUE  );
        add( "msmcap              ", _msmcap              , T_FLAG   );
        add( "nestcell            ", _nestcell            , T_SYMBOL );
        add( "nestrow             ", _nestrow             , T_SYMBOL );
        add( "nesttableprops      ", _nesttableprops      , T_DEST   );
        add( "nextfile            ", _nextfile            , T_DEST   );
        add( "nobrkwrptbl         ", _nobrkwrptbl         , T_FLAG   );
        add( "nocolbal            ", _nocolbal            , T_FLAG   );
        add( "nocompatoptions     ", _nocompatoptions     , T_FLAG   );
        add( "nocwrap             ", _nocwrap             , T_FLAG   );
        add( "noextrasprl         ", _noextrasprl         , T_FLAG   );
        add( "nofchars            ", _nofchars            , T_VALUE  );
        add( "nofcharsws          ", _nofcharsws          , T_VALUE  );
        add( "nofpages            ", _nofpages            , T_VALUE  );
        add( "nofwords            ", _nofwords            , T_VALUE  );
        add( "nolead              ", _nolead              , T_FLAG   );
        add( "noline              ", _noline              , T_FLAG   );
        add( "nolnhtadjtbl        ", _nolnhtadjtbl        , T_FLAG   );
        add( "nonesttables        ", _nonesttables        , T_DEST   );
        add( "nonshppict          ", _nonshppict          , T_FLAG   );
        add( "nooverflow          ", _nooverflow          , T_FLAG   );
        add( "noproof             ", _noproof             , T_FLAG   );
        add( "nosectexpand        ", _nosectexpand        , T_FLAG   );
        add( "nosnaplinegrid      ", _nosnaplinegrid      , T_FLAG   );
        add( "nospaceforul        ", _nospaceforul        , T_FLAG   );
        add( "nosupersub          ", _nosupersub          , T_FLAG   );
        add( "notabind            ", _notabind            , T_FLAG   );
        add( "noultrlspc          ", _noultrlspc          , T_FLAG   );
        add( "nowidctlpar         ", _nowidctlpar         , T_FLAG   );
        add( "nowrap              ", _nowrap              , T_FLAG   );
        add( "nowwrap             ", _nowwrap             , T_FLAG   );
        add( "noxlattoyen         ", _noxlattoyen         , T_FLAG   );
        add( "objalias            ", _objalias            , T_DEST   );
        add( "objalign            ", _objalign            , T_VALUE  );
        add( "objattph            ", _objattph            , T_FLAG   );
        add( "objautlink          ", _objautlink          , T_FLAG   );
        add( "objclass            ", _objclass            , T_DEST   );
        add( "objcropb            ", _objcropb            , T_VALUE  );
        add( "objcropl            ", _objcropl            , T_VALUE  );
        add( "objcropr            ", _objcropr            , T_VALUE  );
        add( "objcropt            ", _objcropt            , T_VALUE  );
        add( "objdata             ", _objdata             , T_DEST   );
        add( "object              ", _object              , T_DEST   );
        add( "objemb              ", _objemb              , T_FLAG   );
        add( "objh                ", _objh                , T_VALUE  );
        add( "objhtml             ", _objhtml             , T_FLAG   );
        add( "objicemb            ", _objicemb            , T_FLAG   );
        add( "objlink             ", _objlink             , T_FLAG   );
        add( "objlock             ", _objlock             , T_FLAG   );
        add( "objname             ", _objname             , T_DEST   );
        add( "objocx              ", _objocx              , T_FLAG   );
        add( "objpub              ", _objpub              , T_FLAG   );
        add( "objscalex           ", _objscalex           , T_VALUE  );
        add( "objscaley           ", _objscaley           , T_VALUE  );
        add( "objsect             ", _objsect             , T_DEST   );
        add( "objsetsize          ", _objsetsize          , T_FLAG   );
        add( "objsub              ", _objsub              , T_FLAG   );
        add( "objtime             ", _objtime             , T_DEST   );
        add( "objtransy           ", _objtransy           , T_VALUE  );
        add( "objupdate           ", _objupdate           , T_FLAG   );
        add( "objw                ", _objw                , T_VALUE  );
        add( "oldas               ", _oldas               , T_FLAG   );
        add( "oldcprops           ", _oldcprops           , T_DEST   );
        add( "oldpprops           ", _oldpprops           , T_DEST   );
        add( "oldsprops           ", _oldsprops           , T_DEST   );
        add( "oldtprops           ", _oldtprops           , T_DEST   );
        add( "oldlinewrap         ", _oldlinewrap         , T_FLAG   );
        add( "operator            ", _operator            , T_DEST   );
        add( "otblrul             ", _otblrul             , T_FLAG   );
        add( "outl                ", _outl                , T_TOGGLE );
        add( "outlinelevel        ", _outlinelevel        , T_VALUE  );
        add( "overlay             ", _overlay             , T_FLAG   );
        add( "page                ", _page                , T_SYMBOL );
        add( "pagebb              ", _pagebb              , T_FLAG   );
        add( "panose              ", _panose              , T_DEST   );
        add( "paperh              ", _paperh              , T_VALUE  );
        add( "paperw              ", _paperw              , T_VALUE  );
        add( "par                 ", _par                 , T_SYMBOL );
        add( "pararsid            ", _pararsid            , T_VALUE  );
        add( "pard                ", _pard                , T_FLAG   );
        add( "pc                  ", _pc                  , T_FLAG   );
        add( "pca                 ", _pca                 , T_FLAG   );
        add( "pgbrdrb             ", _pgbrdrb             , T_FLAG   );
        add( "pgbrdrfoot          ", _pgbrdrfoot          , T_FLAG   );
        add( "pgbrdrhead          ", _pgbrdrhead          , T_FLAG   );
        add( "pgbrdrl             ", _pgbrdrl             , T_FLAG   );
        add( "pgbrdropt           ", _pgbrdropt           , T_VALUE  );
        add( "pgbrdrr             ", _pgbrdrr             , T_FLAG   );
        add( "pgbrdrsnap          ", _pgbrdrsnap          , T_FLAG   );
        add( "pgbrdrt             ", _pgbrdrt             , T_FLAG   );
        add( "pghsxn              ", _pghsxn              , T_VALUE  );
        add( "pgnbidia            ", _pgnbidia            , T_FLAG   );
        add( "pgnbidib            ", _pgnbidib            , T_FLAG   );
        add( "pgnchosung          ", _pgnchosung          , T_FLAG   );
        add( "pgncnum             ", _pgncnum             , T_FLAG   );
        add( "pgncont             ", _pgncont             , T_FLAG   );
        add( "pgndbnum            ", _pgndbnum            , T_FLAG   );
        add( "pgndbnumd           ", _pgndbnumd           , T_FLAG   );
        add( "pgndbnumk           ", _pgndbnumk           , T_FLAG   );
        add( "pgndbnumt           ", _pgndbnumt           , T_FLAG   );
        add( "pgndec              ", _pgndec              , T_FLAG   );
        add( "pgndecd             ", _pgndecd             , T_FLAG   );
        add( "pgnganada           ", _pgnganada           , T_FLAG   );
        add( "pgngbnum            ", _pgngbnum            , T_FLAG   );
        add( "pgngbnumd           ", _pgngbnumd           , T_FLAG   );
        add( "pgngbnumk           ", _pgngbnumk           , T_FLAG   );
        add( "pgngbnuml           ", _pgngbnuml           , T_FLAG   );
        add( "pgnhindia           ", _pgnhindia           , T_FLAG   );
        add( "pgnhindib           ", _pgnhindib           , T_FLAG   );
        add( "pgnhindic           ", _pgnhindic           , T_FLAG   );
        add( "pgnhindid           ", _pgnhindid           , T_FLAG   );
        add( "pgnhn               ", _pgnhn               , T_VALUE  );
        add( "pgnhnsc             ", _pgnhnsc             , T_FLAG   );
        add( "pgnhnsh             ", _pgnhnsh             , T_FLAG   );
        add( "pgnhnsm             ", _pgnhnsm             , T_FLAG   );
        add( "pgnhnsn             ", _pgnhnsn             , T_FLAG   );
        add( "pgnhnsp             ", _pgnhnsp             , T_FLAG   );
        add( "pgnid               ", _pgnid               , T_VALUE  );
        add( "pgnlcltr            ", _pgnlcltr            , T_FLAG   );
        add( "pgnlcrm             ", _pgnlcrm             , T_FLAG   );
        add( "pgnrestart          ", _pgnrestart          , T_FLAG   );
        add( "pgnstart            ", _pgnstart            , T_VALUE  );
        add( "pgnstarts           ", _pgnstarts           , T_VALUE  );
        add( "pgnthaia            ", _pgnthaia            , T_FLAG   );
        add( "pgnthaib            ", _pgnthaib            , T_FLAG   );
        add( "pgnthaic            ", _pgnthaic            , T_FLAG   );
        add( "pgnucltr            ", _pgnucltr            , T_FLAG   );
        add( "pgnucrm             ", _pgnucrm             , T_FLAG   );
        add( "pgnvieta            ", _pgnvieta            , T_FLAG   );
        add( "pgnx                ", _pgnx                , T_VALUE  );
        add( "pgny                ", _pgny                , T_VALUE  );
        add( "pgnzodiac           ", _pgnzodiac           , T_FLAG   );
        add( "pgnzodiacd          ", _pgnzodiacd          , T_FLAG   );
        add( "pgnzodiacl          ", _pgnzodiacl          , T_FLAG   );
        add( "pgp                 ", _pgp                 , T_DEST   );
        add( "pgptbl              ", _pgptbl              , T_DEST   );
        add( "pgwsxn              ", _pgwsxn              , T_VALUE  );
        add( "phcol               ", _phcol               , T_FLAG   );
        add( "phmrg               ", _phmrg               , T_FLAG   );
        add( "phpg                ", _phpg                , T_FLAG   );
        add( "picbmp              ", _picbmp              , T_FLAG   );
        add( "picbpp              ", _picbpp              , T_VALUE  );
        add( "piccropb            ", _piccropb            , T_VALUE  );
        add( "piccropl            ", _piccropl            , T_VALUE  );
        add( "piccropr            ", _piccropr            , T_VALUE  );
        add( "piccropt            ", _piccropt            , T_VALUE  );
        add( "pich                ", _pich                , T_VALUE  );
        add( "pichgoal            ", _pichgoal            , T_VALUE  );
        add( "picprop             ", _picprop             , T_DEST   );
        add( "picscaled           ", _picscaled           , T_FLAG   );
        add( "picscalex           ", _picscalex           , T_VALUE  );
        add( "picscaley           ", _picscaley           , T_VALUE  );
        add( "pict                ", _pict                , T_DEST   );
        add( "picw                ", _picw                , T_VALUE  );
        add( "picwgoal            ", _picwgoal            , T_VALUE  );
        add( "plain               ", _plain               , T_FLAG   );
        add( "pmmetafile          ", _pmmetafile          , T_VALUE  );
        add( "pn                  ", _pn                  , T_DEST   );
        add( "pnacross            ", _pnacross            , T_FLAG   );
        add( "pnaiu               ", _pnaiu               , T_FLAG   );
        add( "pnaiud              ", _pnaiud              , T_FLAG   );
        add( "pnaiueo             ", _pnaiueo             , T_FLAG   );
        add( "pnaiueod            ", _pnaiueod            , T_FLAG   );
        add( "pnb                 ", _pnb                 , T_TOGGLE );
        add( "pnbidia             ", _pnbidia             , T_FLAG   );
        add( "pnbidib             ", _pnbidib             , T_FLAG   );
        add( "pncaps              ", _pncaps              , T_TOGGLE );
        add( "pncard              ", _pncard              , T_FLAG   );
        add( "pncf                ", _pncf                , T_VALUE  );
        add( "pnchosung           ", _pnchosung           , T_FLAG   );
        add( "pncnum              ", _pncnum              , T_FLAG   );
        add( "pndbnum             ", _pndbnum             , T_FLAG   );
        add( "pndbnumd            ", _pndbnumd            , T_FLAG   );
        add( "pndbnumk            ", _pndbnumk            , T_FLAG   );
        add( "pndbnuml            ", _pndbnuml            , T_FLAG   );
        add( "pndbnumt            ", _pndbnumt            , T_FLAG   );
        add( "pndec               ", _pndec               , T_FLAG   );
        add( "pndecd              ", _pndecd              , T_FLAG   );
        add( "pnf                 ", _pnf                 , T_VALUE  );
        add( "pnfs                ", _pnfs                , T_VALUE  );
        add( "pnganada            ", _pnganada            , T_FLAG   );
        add( "pngblip             ", _pngblip             , T_FLAG   );
        add( "pngbnum             ", _pngbnum             , T_FLAG   );
        add( "pngbnumd            ", _pngbnumd            , T_FLAG   );
        add( "pngbnumk            ", _pngbnumk            , T_FLAG   );
        add( "pngbnuml            ", _pngbnuml            , T_FLAG   );
        add( "pnhang              ", _pnhang              , T_FLAG   );
        add( "pni                 ", _pni                 , T_TOGGLE );
        add( "pnindent            ", _pnindent            , T_VALUE  );
        add( "pniroha             ", _pniroha             , T_FLAG   );
        add( "pnirohad            ", _pnirohad            , T_FLAG   );
        add( "pnlcltr             ", _pnlcltr             , T_FLAG   );
        add( "pnlcrm              ", _pnlcrm              , T_FLAG   );
        add( "pnlvl               ", _pnlvl               , T_VALUE  );
        add( "pnlvlblt            ", _pnlvlblt            , T_FLAG   );
        add( "pnlvlbody           ", _pnlvlbody           , T_FLAG   );
        add( "pnlvlcont           ", _pnlvlcont           , T_FLAG   );
        add( "pnnumonce           ", _pnnumonce           , T_FLAG   );
        add( "pnord               ", _pnord               , T_FLAG   );
        add( "pnordt              ", _pnordt              , T_FLAG   );
        add( "pnprev              ", _pnprev              , T_FLAG   );
        add( "pnqc                ", _pnqc                , T_FLAG   );
        add( "pnql                ", _pnql                , T_FLAG   );
        add( "pnqr                ", _pnqr                , T_FLAG   );
        add( "pnrauth             ", _pnrauth             , T_VALUE  );
        add( "pnrdate             ", _pnrdate             , T_VALUE  );
        add( "pnrestart           ", _pnrestart           , T_FLAG   );
        add( "pnrnfc              ", _pnrnfc              , T_VALUE  );
        add( "pnrnot              ", _pnrnot              , T_FLAG   );
        add( "pnrpnbr             ", _pnrpnbr             , T_VALUE  );
        add( "pnrrgb              ", _pnrrgb              , T_VALUE  );
        add( "pnrstart            ", _pnrstart            , T_VALUE  );
        add( "pnrstop             ", _pnrstop             , T_VALUE  );
        add( "pnrxst              ", _pnrxst              , T_VALUE  );
        add( "pnscaps             ", _pnscaps             , T_TOGGLE );
        add( "pnseclvl            ", _pnseclvl            , T_DEST   );
        add( "pnsp                ", _pnsp                , T_VALUE  );
        add( "pnstart             ", _pnstart             , T_VALUE  );
        add( "pnstrike            ", _pnstrike            , T_TOGGLE );
        add( "pntext              ", _pntext              , T_DEST   );
        add( "pntxta              ", _pntxta              , T_DEST   );
        add( "pntxtb              ", _pntxtb              , T_DEST   );
        add( "pnucltr             ", _pnucltr             , T_FLAG   );
        add( "pnucrm              ", _pnucrm              , T_FLAG   );
        add( "pnul                ", _pnul                , T_TOGGLE );
        add( "pnuld               ", _pnuld               , T_FLAG   );
        add( "pnuldash            ", _pnuldash            , T_FLAG   );
        add( "pnuldashd           ", _pnuldashd           , T_FLAG   );
        add( "pnuldashdd          ", _pnuldashdd          , T_FLAG   );
        add( "pnuldb              ", _pnuldb              , T_FLAG   );
        add( "pnulhair            ", _pnulhair            , T_FLAG   );
        add( "pnulnone            ", _pnulnone            , T_FLAG   );
        add( "pnulth              ", _pnulth              , T_FLAG   );
        add( "pnulw               ", _pnulw               , T_FLAG   );
        add( "pnulwave            ", _pnulwave            , T_FLAG   );
        add( "pnzodiac            ", _pnzodiac            , T_FLAG   );
        add( "pnzodiacd           ", _pnzodiacd           , T_FLAG   );
        add( "pnzodiacl           ", _pnzodiacl           , T_FLAG   );
        add( "posnegx             ", _posnegx             , T_VALUE  );
        add( "posnegy             ", _posnegy             , T_VALUE  );
        add( "posx                ", _posx                , T_VALUE  );
        add( "posxc               ", _posxc               , T_FLAG   );
        add( "posxi               ", _posxi               , T_FLAG   );
        add( "posxl               ", _posxl               , T_FLAG   );
        add( "posxo               ", _posxo               , T_FLAG   );
        add( "posxr               ", _posxr               , T_FLAG   );
        add( "posy                ", _posy                , T_VALUE  );
        add( "posyb               ", _posyb               , T_FLAG   );
        add( "posyc               ", _posyc               , T_FLAG   );
        add( "posyil              ", _posyil              , T_FLAG   );
        add( "posyin              ", _posyin              , T_FLAG   );
        add( "posyout             ", _posyout             , T_FLAG   );
        add( "posyt               ", _posyt               , T_FLAG   );
        add( "prcolbl             ", _prcolbl             , T_FLAG   );
        add( "printdata           ", _printdata           , T_FLAG   );
        add( "printim             ", _printim             , T_DEST   );
        add( "private             ", _private             , T_DEST   );
        add( "propname            ", _propname            , T_VALUE  );
        add( "proptype            ", _proptype            , T_VALUE  );
        add( "psover              ", _psover              , T_FLAG   );
        add( "psz                 ", _psz                 , T_VALUE  );
        add( "pubauto             ", _pubauto             , T_FLAG   );
        add( "pvmrg               ", _pvmrg               , T_FLAG   );
        add( "pvpara              ", _pvpara              , T_FLAG   );
        add( "pvpg                ", _pvpg                , T_FLAG   );
        add( "pwd                 ", _pwd                 , T_DEST   );
        add( "pxe                 ", _pxe                 , T_DEST   );
        add( "qc                  ", _qc                  , T_FLAG   );
        add( "qd                  ", _qd                  , T_FLAG   );
        add( "qj                  ", _qj                  , T_FLAG   );
        add( "qk                  ", _qk                  , T_FLAG   );
        add( "ql                  ", _ql                  , T_FLAG   );
        add( "qmspace             ", _qmspace             , T_SYMBOL );
        add( "qr                  ", _qr                  , T_FLAG   );
        add( "qt                  ", _qt                  , T_FLAG   );
        add( "rawclbgbdiag        ", _rawclbgbdiag        , T_FLAG   );
        add( "rawclbgcross        ", _rawclbgcross        , T_FLAG   );
        add( "rawclbgdcross       ", _rawclbgdcross       , T_FLAG   );
        add( "rawbgdkbdiag        ", _rawbgdkbdiag        , T_FLAG   );
        add( "rawclbgdkcross      ", _rawclbgdkcross      , T_FLAG   );
        add( "rawclbgdkdcross     ", _rawclbgdkdcross     , T_FLAG   );
        add( "rawclbgdkfdiag      ", _rawclbgdkfdiag      , T_FLAG   );
        add( "rawclbgdkhor        ", _rawclbgdkhor        , T_FLAG   );
        add( "rawclbgdkvert       ", _rawclbgdkvert       , T_FLAG   );
        add( "rawclbgfdiag        ", _rawclbgfdiag        , T_FLAG   );
        add( "rawclbghoriz        ", _rawclbghoriz        , T_FLAG   );
        add( "rawclbgvert         ", _rawclbgvert         , T_FLAG   );
        add( "rdblquote           ", _rdblquote           , T_SYMBOL );
        add( "red                 ", _red                 , T_VALUE  );
        add( "rempersonalinfo     ", _rempersonalinfo     , T_FLAG   );
        add( "result              ", _result              , T_DEST   );
        add( "revauth             ", _revauth             , T_VALUE  );
        add( "revauthdel          ", _revauthdel          , T_VALUE  );
        add( "revbar              ", _revbar              , T_VALUE  );
        add( "revdttm             ", _revdttm             , T_VALUE  );
        add( "revdttmdel          ", _revdttmdel          , T_VALUE  );
        add( "revised             ", _revised             , T_TOGGLE );
        add( "revisions           ", _revisions           , T_FLAG   );
        add( "revprop             ", _revprop             , T_VALUE  );
        add( "revprot             ", _revprot             , T_FLAG   );
        add( "revtbl              ", _revtbl              , T_DEST   );
        add( "revtim              ", _revtim              , T_DEST   );
        add( "ri                  ", _ri                  , T_VALUE  );
        add( "rin                 ", _rin                 , T_VALUE  );
        add( "row                 ", _row                 , T_SYMBOL );
        add( "rquote              ", _rquote              , T_SYMBOL );
        add( "rsid                ", _rsid                , T_VALUE  );
        add( "rsidroot            ", _rsidroot            , T_VALUE  );
        add( "rsidtbl             ", _rsidtbl             , T_DEST   );
        add( "rsltbmp             ", _rsltbmp             , T_FLAG   );
        add( "rslthtml            ", _rslthtml            , T_FLAG   );
        add( "rsltmerge           ", _rsltmerge           , T_FLAG   );
        add( "rsltpict            ", _rsltpict            , T_FLAG   );
        add( "rsltrtf             ", _rsltrtf             , T_FLAG   );
        add( "rslttxt             ", _rslttxt             , T_FLAG   );
        add( "rtf                 ", _rtf                 , T_DEST   );
        add( "rtlch               ", _rtlch               , T_FLAG   );
        add( "rtldoc              ", _rtldoc              , T_FLAG   );
        add( "rtlgutter           ", _rtlgutter           , T_FLAG   );
        add( "rtlmark             ", _rtlmark             , T_SYMBOL );
        add( "rtlpar              ", _rtlpar              , T_FLAG   );
        add( "rtlrow              ", _rtlrow              , T_FLAG   );
        add( "rtlsect             ", _rtlsect             , T_FLAG   );
        add( "rxe                 ", _rxe                 , T_DEST   );
        add( "s                   ", _s                   , T_VALUE  );
        add( "sa                  ", _sa                  , T_VALUE  );
        add( "saauto              ", _saauto              , T_TOGGLE );
        add( "saftnnalc           ", _saftnnalc           , T_FLAG   );
        add( "saftnnar            ", _saftnnar            , T_FLAG   );
        add( "saftnnauc           ", _saftnnauc           , T_FLAG   );
        add( "saftnnchi           ", _saftnnchi           , T_FLAG   );
        add( "saftnnchosung       ", _saftnnchosung       , T_FLAG   );
        add( "saftnncnum          ", _saftnncnum          , T_FLAG   );
        add( "saftnndbar          ", _saftnndbar          , T_FLAG   );
        add( "saftnndbnum         ", _saftnndbnum         , T_FLAG   );
        add( "saftnndbnumd        ", _saftnndbnumd        , T_FLAG   );
        add( "saftnndbnumk        ", _saftnndbnumk        , T_FLAG   );
        add( "saftnndbnumt        ", _saftnndbnumt        , T_FLAG   );
        add( "saftnnganada        ", _saftnnganada        , T_FLAG   );
        add( "saftnngbnum         ", _saftnngbnum         , T_FLAG   );
        add( "saftnngbnumd        ", _saftnngbnumd        , T_FLAG   );
        add( "saftnngbnumk        ", _saftnngbnumk        , T_FLAG   );
        add( "saftnngbnuml        ", _saftnngbnuml        , T_FLAG   );
        add( "saftnnrlc           ", _saftnnrlc           , T_FLAG   );
        add( "saftnnruc           ", _saftnnruc           , T_FLAG   );
        add( "saftnnzodiac        ", _saftnnzodiac        , T_FLAG   );
        add( "saftnnzodiacd       ", _saftnnzodiacd       , T_FLAG   );
        add( "saftnnzodiacl       ", _saftnnzodiacl       , T_FLAG   );
        add( "saftnrestart        ", _saftnrestart        , T_FLAG   );
        add( "saftnrstcont        ", _saftnrstcont        , T_FLAG   );
        add( "saftnstart          ", _saftnstart          , T_FLAG   );
        add( "sautoupd            ", _sautoupd            , T_FLAG   );
        add( "sb                  ", _sb                  , T_VALUE  );
        add( "sbasedon            ", _sbasedon            , T_VALUE  );
        add( "sbauto              ", _sbauto              , T_TOGGLE );
        add( "sbkcol              ", _sbkcol              , T_FLAG   );
        add( "sbkeven             ", _sbkeven             , T_FLAG   );
        add( "sbknone             ", _sbknone             , T_FLAG   );
        add( "sbkodd              ", _sbkodd              , T_FLAG   );
        add( "sbkpage             ", _sbkpage             , T_FLAG   );
        add( "sbys                ", _sbys                , T_FLAG   );
        add( "scaps               ", _scaps               , T_TOGGLE );
        add( "scompose            ", _scompose            , T_FLAG   );
        add( "sec                 ", _sec                 , T_VALUE  );
        add( "sect                ", _sect                , T_SYMBOL );
        add( "sectd               ", _sectd               , T_FLAG   );
        add( "sectdefaultcl       ", _sectdefaultcl       , T_VALUE  );
        add( "sectexpand          ", _sectexpand          , T_VALUE  );
        add( "sectlinegrid        ", _sectlinegrid        , T_VALUE  );
        add( "sectnum             ", _sectnum             , T_SYMBOL );
        add( "sectrsid            ", _sectrsid            , T_VALUE  );
        add( "sectspecifycl       ", _sectspecifycl       , T_VALUE  );
        add( "sectspecifygen      ", _sectspecifygen      , T_FLAG   );
        add( "sectspecifyl        ", _sectspecifyl        , T_VALUE  );
        add( "sectunlocked        ", _sectunlocked        , T_FLAG   );
        add( "sftnbj              ", _sftnbj              , T_FLAG   );
        add( "sftnnalc            ", _sftnnalc            , T_FLAG   );
        add( "sftnnar             ", _sftnnar             , T_FLAG   );
        add( "sftnnauc            ", _sftnnauc            , T_FLAG   );
        add( "sftnnchi            ", _sftnnchi            , T_FLAG   );
        add( "sftnnchosung        ", _sftnnchosung        , T_FLAG   );
        add( "sftnncnum           ", _sftnncnum           , T_FLAG   );
        add( "sftnndbar           ", _sftnndbar           , T_FLAG   );
        add( "sftnndbnum          ", _sftnndbnum          , T_FLAG   );
        add( "sftnndbnumd         ", _sftnndbnumd         , T_FLAG   );
        add( "sftnndbnumk         ", _sftnndbnumk         , T_FLAG   );
        add( "sftnndbnumt         ", _sftnndbnumt         , T_FLAG   );
        add( "sftnnganada         ", _sftnnganada         , T_FLAG   );
        add( "sftnngbnum          ", _sftnngbnum          , T_FLAG   );
        add( "sftnngbnumd         ", _sftnngbnumd         , T_FLAG   );
        add( "sftnngbnumk         ", _sftnngbnumk         , T_FLAG   );
        add( "sftnngbnuml         ", _sftnngbnuml         , T_FLAG   );
        add( "sftnnrlc            ", _sftnnrlc            , T_FLAG   );
        add( "sftnnruc            ", _sftnnruc            , T_FLAG   );
        add( "sftnnzodiac         ", _sftnnzodiac         , T_FLAG   );
        add( "sftnnzodiacd        ", _sftnnzodiacd        , T_FLAG   );
        add( "sftnnzodiacl        ", _sftnnzodiacl        , T_FLAG   );
        add( "sftnrestart         ", _sftnrestart         , T_FLAG   );
        add( "sftnrstcont         ", _sftnrstcont         , T_FLAG   );
        add( "sftnrstpg           ", _sftnrstpg           , T_FLAG   );
        add( "sftnstart           ", _sftnstart           , T_FLAG   );
        add( "sftntj              ", _sftntj              , T_FLAG   );
        add( "shad                ", _shad                , T_TOGGLE );
        add( "shading             ", _shading             , T_VALUE  );
        add( "shidden             ", _shidden             , T_FLAG   );
        add( "shift               ", _shift               , T_FLAG   );
        add( "shpbottom           ", _shpbottom           , T_VALUE  );
        add( "shpbxcolumn         ", _shpbxcolumn         , T_FLAG   );
        add( "shpbxignore         ", _shpbxignore         , T_FLAG   );
        add( "shpbxmargin         ", _shpbxmargin         , T_FLAG   );
        add( "shpbxpage           ", _shpbxpage           , T_FLAG   );
        add( "shpbyignore         ", _shpbyignore         , T_FLAG   );
        add( "shpbymargin         ", _shpbymargin         , T_FLAG   );
        add( "shpbypage           ", _shpbypage           , T_FLAG   );
        add( "shpbypara           ", _shpbypara           , T_FLAG   );
        add( "shpfblwtxt          ", _shpfblwtxt          , T_VALUE  );
        add( "shpfhdr             ", _shpfhdr             , T_VALUE  );
        add( "shpgrp              ", _shpgrp              , T_VALUE  );
        add( "shpleft             ", _shpleft             , T_VALUE  );
        add( "shplid              ", _shplid              , T_VALUE  );
        add( "shplockanchor       ", _shplockanchor       , T_FLAG   );
        add( "shppict             ", _shppict             , T_DEST   );
        add( "shpright            ", _shpright            , T_VALUE  );
        add( "shprslt             ", _shprslt             , T_VALUE  );
        add( "shptop              ", _shptop              , T_VALUE  );
        add( "shptxt              ", _shptxt              , T_VALUE  );
        add( "shpwrk              ", _shpwrk              , T_VALUE  );
        add( "shpwr               ", _shpwr               , T_VALUE  );
        add( "shpz                ", _shpz                , T_VALUE  );
        add( "sl                  ", _sl                  , T_VALUE  );
        add( "slmult              ", _slmult              , T_VALUE  );
        add( "snaptogridincell    ", _snaptogridincell    , T_FLAG   );
        add( "snext               ", _snext               , T_VALUE  );
        add( "softcol             ", _softcol             , T_FLAG   );
        add( "softlheight         ", _softlheight         , T_VALUE  );
        add( "softline            ", _softline            , T_FLAG   );
        add( "softpage            ", _softpage            , T_FLAG   );
        add( "spersonal           ", _spersonal           , T_FLAG   );
        add( "splytwnine          ", _splytwnine          , T_FLAG   );
        add( "sprsbsp             ", _sprsbsp             , T_FLAG   );
        add( "sprslnsp            ", _sprslnsp            , T_FLAG   );
        add( "sprsspbf            ", _sprsspbf            , T_FLAG   );
        add( "sprstsm             ", _sprstsm             , T_FLAG   );
        add( "sprstsp             ", _sprstsp             , T_FLAG   );
        add( "spv                 ", _spv                 , T_FLAG   );
        add( "sreply              ", _sreply              , T_FLAG   );
        add( "ssemihidden         ", _ssemihidden         , T_FLAG   );
        add( "staticval           ", _staticval           , T_VALUE  );
        add( "stextflow           ", _stextflow           , T_VALUE  );
        add( "strike              ", _strike              , T_TOGGLE );
        add( "striked             ", _striked             , T_TOGGLE );
        add( "stshfbi             ", _stshfbi             , T_VALUE  );
        add( "stshfdbch           ", _stshfdbch           , T_VALUE  );
        add( "stshfhich           ", _stshfhich           , T_VALUE  );
        add( "stshfloch           ", _stshfloch           , T_VALUE  );
        add( "stylesheet          ", _stylesheet          , T_DEST   );
        add( "styrsid             ", _styrsid             , T_VALUE  );
        add( "sub                 ", _sub                 , T_FLAG   );
        add( "subdocument         ", _subdocument         , T_VALUE  );
        add( "subfontbysize       ", _subfontbysize       , T_FLAG   );
        add( "subject             ", _subject             , T_DEST   );
        add( "super               ", _super               , T_FLAG   );
        add( "swpbdr              ", _swpbdr              , T_FLAG   );
        add( "tab                 ", _tab                 , T_SYMBOL );
        add( "tabsnoovrlp         ", _tabsnoovrlp         , T_FLAG   );
        add( "taprtl              ", _taprtl              , T_FLAG   );
        add( "tb                  ", _tb                  , T_VALUE  );
        add( "tbllkbestfit        ", _tbllkbestfit        , T_FLAG   );
        add( "tbllkborder         ", _tbllkborder         , T_FLAG   );
        add( "tbllkcolor          ", _tbllkcolor          , T_FLAG   );
        add( "tbllkfont           ", _tbllkfont           , T_FLAG   );
        add( "tbllkhdrcols        ", _tbllkhdrcols        , T_FLAG   );
        add( "tbllkhdrrows        ", _tbllkhdrrows        , T_FLAG   );
        add( "tbllklastcol        ", _tbllklastcol        , T_FLAG   );
        add( "tbllklastrow        ", _tbllklastrow        , T_FLAG   );
        add( "tbllkshading        ", _tbllkshading        , T_FLAG   );
        add( "tblrsid             ", _tblrsid             , T_FLAG   );
        add( "tc                  ", _tc                  , T_DEST   );
        add( "tcelld              ", _tcelld              , T_FLAG   );
        add( "tcf                 ", _tcf                 , T_VALUE  );
        add( "tcl                 ", _tcl                 , T_VALUE  );
        add( "tcn                 ", _tcn                 , T_FLAG   );
        add( "tdfrmtxtBottom      ", _tdfrmtxtBottom      , T_VALUE  );
        add( "tdfrmtxtLeft        ", _tdfrmtxtLeft        , T_VALUE  );
        add( "tdfrmtxtRight       ", _tdfrmtxtRight       , T_VALUE  );
        add( "tdfrmtxtTop         ", _tdfrmtxtTop         , T_VALUE  );
        add( "template            ", _template            , T_DEST   );
        add( "time                ", _time                , T_FLAG   );
        add( "title               ", _title               , T_DEST   );
        add( "titlepg             ", _titlepg             , T_FLAG   );
        add( "tldot               ", _tldot               , T_FLAG   );
        add( "tleq                ", _tleq                , T_FLAG   );
        add( "tlhyph              ", _tlhyph              , T_FLAG   );
        add( "tlmdot              ", _tlmdot              , T_FLAG   );
        add( "tlth                ", _tlth                , T_FLAG   );
        add( "tlul                ", _tlul                , T_FLAG   );
        add( "toplinepunct        ", _toplinepunct        , T_FLAG   );
        add( "tphcol              ", _tphcol              , T_FLAG   );
        add( "tphmrg              ", _tphmrg              , T_FLAG   );
        add( "tphpg               ", _tphpg               , T_FLAG   );
        add( "tposnegx            ", _tposnegx            , T_VALUE  );
        add( "tposnegy            ", _tposnegy            , T_VALUE  );
        add( "tposxc              ", _tposxc              , T_FLAG   );
        add( "tposxi              ", _tposxi              , T_FLAG   );
        add( "tposxl              ", _tposxl              , T_FLAG   );
        add( "tposx               ", _tposx               , T_VALUE  );
        add( "tposxo              ", _tposxo              , T_FLAG   );
        add( "tposxr              ", _tposxr              , T_FLAG   );
        add( "tposy               ", _tposy               , T_FLAG   );
        add( "tposyb              ", _tposyb              , T_FLAG   );
        add( "tposyc              ", _tposyc              , T_FLAG   );
        add( "tposyil             ", _tposyil             , T_FLAG   );
        add( "tposyin             ", _tposyin             , T_FLAG   );
        add( "tposyoutv           ", _tposyoutv           , T_FLAG   );
        add( "tposyt              ", _tposyt              , T_FLAG   );
        add( "tpvmrg              ", _tpvmrg              , T_FLAG   );
        add( "tpvpara             ", _tpvpara             , T_FLAG   );
        add( "tpvpg               ", _tpvpg               , T_FLAG   );
        add( "tqc                 ", _tqc                 , T_FLAG   );
        add( "tqdec               ", _tqdec               , T_FLAG   );
        add( "tqr                 ", _tqr                 , T_FLAG   );
        add( "transmf             ", _transmf             , T_FLAG   );
        add( "trauth              ", _trauth              , T_VALUE  );
        add( "trautofit           ", _trautofit           , T_TOGGLE );
        add( "trbgbdiag           ", _trbgbdiag           , T_FLAG   );
        add( "trbgcross           ", _trbgcross           , T_FLAG   );
        add( "trbgdcross          ", _trbgdcross          , T_FLAG   );
        add( "trbgdkbdiag         ", _trbgdkbdiag         , T_FLAG   );
        add( "trbgdkcross         ", _trbgdkcross         , T_FLAG   );
        add( "trbgdkdcross        ", _trbgdkdcross        , T_FLAG   );
        add( "trbgdkfdiag         ", _trbgdkfdiag         , T_FLAG   );
        add( "trbgdkhor           ", _trbgdkhor           , T_FLAG   );
        add( "trbgdkvert          ", _trbgdkvert          , T_FLAG   );
        add( "trbgfdiag           ", _trbgfdiag           , T_FLAG   );
        add( "trbghoriz           ", _trbghoriz           , T_FLAG   );
        add( "trbgvert            ", _trbgvert            , T_FLAG   );
        add( "trbrdrb             ", _trbrdrb             , T_FLAG   );
        add( "trbrdrh             ", _trbrdrh             , T_FLAG   );
        add( "trbrdrl             ", _trbrdrl             , T_FLAG   );
        add( "trbrdrr             ", _trbrdrr             , T_FLAG   );
        add( "trbrdrt             ", _trbrdrt             , T_FLAG   );
        add( "trbrdrv             ", _trbrdrv             , T_FLAG   );
        add( "trcbpat             ", _trcbpat             , T_VALUE  );
        add( "trcfpat             ", _trcfpat             , T_VALUE  );
        add( "trdate              ", _trdate              , T_VALUE  );
        add( "trftsWidthA         ", _trftsWidthA         , T_VALUE  );
        add( "trftsWidthB         ", _trftsWidthB         , T_VALUE  );
        add( "trftsWidth          ", _trftsWidth          , T_VALUE  );
        add( "trgaph              ", _trgaph              , T_VALUE  );
        add( "trhdr               ", _trhdr               , T_FLAG   );
        add( "trkeep              ", _trkeep              , T_FLAG   );
        add( "trleft              ", _trleft              , T_VALUE  );
        add( "trowd               ", _trowd               , T_FLAG   );
        add( "trpaddb             ", _trpaddb             , T_VALUE  );
        add( "trpaddfb            ", _trpaddfb            , T_VALUE  );
        add( "trpaddfl            ", _trpaddfl            , T_VALUE  );
        add( "trpaddfr            ", _trpaddfr            , T_VALUE  );
        add( "trpaddft            ", _trpaddft            , T_VALUE  );
        add( "trpaddl             ", _trpaddl             , T_VALUE  );
        add( "trpaddr             ", _trpaddr             , T_VALUE  );
        add( "trpaddt             ", _trpaddt             , T_VALUE  );
        add( "trpat               ", _trpat               , T_VALUE  );
        add( "trqc                ", _trqc                , T_FLAG   );
        add( "trql                ", _trql                , T_FLAG   );
        add( "trqr                ", _trqr                , T_FLAG   );
        add( "trrh                ", _trrh                , T_VALUE  );
        add( "trshdng             ", _trshdng             , T_VALUE  );
        add( "trspdb              ", _trspdb              , T_VALUE  );
        add( "trspdfb             ", _trspdfb             , T_VALUE  );
        add( "trspdfl             ", _trspdfl             , T_VALUE  );
        add( "trspdfr             ", _trspdfr             , T_VALUE  );
        add( "trspdft             ", _trspdft             , T_VALUE  );
        add( "trspdl              ", _trspdl              , T_VALUE  );
        add( "trspdr              ", _trspdr              , T_VALUE  );
        add( "trspdt              ", _trspdt              , T_VALUE  );
        add( "truncatefontheight  ", _truncatefontheight  , T_FLAG   );
        add( "trwWidthA           ", _trwWidthA           , T_VALUE  );
        add( "trwWidthB           ", _trwWidthB           , T_VALUE  );
        add( "trwWidth            ", _trwWidth            , T_VALUE  );
        add( "ts                  ", _ts                  , T_VALUE  );
        add( "tsbgbdiag           ", _tsbgbdiag           , T_FLAG   );
        add( "tsbgcross           ", _tsbgcross           , T_FLAG   );
        add( "tsbgdcross          ", _tsbgdcross          , T_FLAG   );
        add( "tsbgdkbdiag         ", _tsbgdkbdiag         , T_FLAG   );
        add( "tsbgdkcross         ", _tsbgdkcross         , T_FLAG   );
        add( "tsbgdkdcross        ", _tsbgdkdcross        , T_FLAG   );
        add( "tsbgdkfdiag         ", _tsbgdkfdiag         , T_FLAG   );
        add( "tsbgdkhor           ", _tsbgdkhor           , T_FLAG   );
        add( "tsbgdkvert          ", _tsbgdkvert          , T_FLAG   );
        add( "tsbgfdiag           ", _tsbgfdiag           , T_FLAG   );
        add( "tsbghoriz           ", _tsbghoriz           , T_FLAG   );
        add( "tsbgvert            ", _tsbgvert            , T_FLAG   );
        add( "tsbrdrb             ", _tsbrdrb             , T_FLAG   );
        add( "tsbrdrdgl           ", _tsbrdrdgl           , T_FLAG   );
        add( "tsbrdrdgr           ", _tsbrdrdgr           , T_FLAG   );
        add( "tsbrdrh             ", _tsbrdrh             , T_FLAG   );
        add( "tsbrdrl             ", _tsbrdrl             , T_FLAG   );
        add( "tsbrdrr             ", _tsbrdrr             , T_FLAG   );
        add( "tsbrdrt             ", _tsbrdrt             , T_FLAG   );
        add( "tsbrdrv             ", _tsbrdrv             , T_FLAG   );
        add( "tscbandhorzeven     ", _tscbandhorzeven     , T_FLAG   );
        add( "tscbandhorzodd      ", _tscbandhorzodd      , T_FLAG   );
        add( "tscbandsh           ", _tscbandsh           , T_FLAG   );
        add( "tscbandsv           ", _tscbandsv           , T_FLAG   );
        add( "tscbandverteven     ", _tscbandverteven     , T_FLAG   );
        add( "tscbandvertodd      ", _tscbandvertodd      , T_FLAG   );
        add( "tscellcbpat         ", _tscellcbpat         , T_VALUE  );
        add( "tscellcfpat         ", _tscellcfpat         , T_VALUE  );
        add( "tscellpaddb         ", _tscellpaddb         , T_VALUE  );
        add( "tscellpaddfb        ", _tscellpaddfb        , T_VALUE  );
        add( "tscellpaddfl        ", _tscellpaddfl        , T_VALUE  );
        add( "tscellpaddfr        ", _tscellpaddfr        , T_VALUE  );
        add( "tscellpaddft        ", _tscellpaddft        , T_VALUE  );
        add( "tscellpaddl         ", _tscellpaddl         , T_VALUE  );
        add( "tscellpaddr         ", _tscellpaddr         , T_VALUE  );
        add( "tscellpaddt         ", _tscellpaddt         , T_VALUE  );
        add( "tscellpct           ", _tscellpct           , T_VALUE  );
        add( "tscellwidth         ", _tscellwidth         , T_FLAG   );
        add( "tscellwidthfts      ", _tscellwidthfts      , T_FLAG   );
        add( "tscfirstcol         ", _tscfirstcol         , T_FLAG   );
        add( "tscfirstrow         ", _tscfirstrow         , T_FLAG   );
        add( "tsclastcol          ", _tsclastcol          , T_FLAG   );
        add( "tsclastrow          ", _tsclastrow          , T_FLAG   );
        add( "tscnecell           ", _tscnecell           , T_FLAG   );
        add( "tscnwcell           ", _tscnwcell           , T_FLAG   );
        add( "tscsecell           ", _tscsecell           , T_FLAG   );
        add( "tscswcell           ", _tscswcell           , T_FLAG   );
        add( "tsd                 ", _tsd                 , T_FLAG   );
        add( "tsnowrap            ", _tsnowrap            , T_FLAG   );
        add( "tsrowd              ", _tsrowd              , T_FLAG   );
        add( "tsvertalb           ", _tsvertalb           , T_FLAG   );
        add( "tsvertalc           ", _tsvertalc           , T_FLAG   );
        add( "tsvertalt           ", _tsvertalt           , T_FLAG   );
        add( "twoonone            ", _twoonone            , T_FLAG   );
        add( "tx                  ", _tx                  , T_VALUE  );
        add( "txe                 ", _txe                 , T_DEST   );
        add( "uc                  ", _uc                  , T_VALUE  );
        add( "ud                  ", _ud                  , T_DEST   );
        add( "ul                  ", _ul                  , T_TOGGLE );
        add( "ulc                 ", _ulc                 , T_VALUE  );
        add( "uld                 ", _uld                 , T_FLAG   );
        add( "uldash              ", _uldash              , T_TOGGLE );
        add( "uldashd             ", _uldashd             , T_TOGGLE );
        add( "uldashdd            ", _uldashdd            , T_TOGGLE );
        add( "uldb                ", _uldb                , T_TOGGLE );
        add( "ulhair              ", _ulhair              , T_TOGGLE );
        add( "ulhwave             ", _ulhwave             , T_TOGGLE );
        add( "ulldash             ", _ulldash             , T_TOGGLE );
        add( "ulnone              ", _ulnone              , T_FLAG   );
        add( "ulth                ", _ulth                , T_TOGGLE );
        add( "ulthd               ", _ulthd               , T_TOGGLE );
        add( "ulthdash            ", _ulthdash            , T_TOGGLE );
        add( "ulthdashd           ", _ulthdashd           , T_TOGGLE );
        add( "ulthdashdd          ", _ulthdashdd          , T_TOGGLE );
        add( "ulthldash           ", _ulthldash           , T_TOGGLE );
        add( "ululdbwave          ", _ululdbwave          , T_TOGGLE );
        add( "ulw                 ", _ulw                 , T_FLAG   );
        add( "ulwave              ", _ulwave              , T_TOGGLE );
        add( "u                   ", _u                   , T_VALUE  );
        add( "up                  ", _up                  , T_VALUE  );
        add( "upr                 ", _upr                 , T_DEST   );
        add( "urtf                ", _urtf                , T_DEST   );
        add( "useltbaln           ", _useltbaln           , T_FLAG   );
        add( "userprops           ", _userprops           , T_DEST   );
        add( "v                   ", _v                   , T_TOGGLE );
        add( "vern                ", _vern                , T_VALUE  );
        add( "version             ", _version             , T_VALUE  );
        add( "vertalb             ", _vertalb             , T_FLAG   );
        add( "vertalc             ", _vertalc             , T_FLAG   );
        add( "vertalj             ", _vertalj             , T_FLAG   );
        add( "vertalt             ", _vertalt             , T_FLAG   );
        add( "vertdoc             ", _vertdoc             , T_FLAG   );
        add( "vertsect            ", _vertsect            , T_FLAG   );
        add( "viewkind            ", _viewkind            , T_VALUE  );
        add( "viewnobound         ", _viewnobound         , T_FLAG   );
        add( "viewscale           ", _viewscale           , T_VALUE  );
        add( "viewzk              ", _viewzk              , T_VALUE  );
        add( "wbitmap             ", _wbitmap             , T_VALUE  );
        add( "wbmbitspixel        ", _wbmbitspixel        , T_VALUE  );
        add( "wbmplanes           ", _wbmplanes           , T_VALUE  );
        add( "wbmwidthbytes       ", _wbmwidthbytes       , T_VALUE  );
        add( "webhidden           ", _webhidden           , T_FLAG   );
        add( "widctlpar           ", _widctlpar           , T_FLAG   );
        add( "widowctrl           ", _widowctrl           , T_FLAG   );
        add( "windowcaption       ", _windowcaption       , T_VALUE  );
        add( "wmetafile           ", _wmetafile           , T_VALUE  );
        add( "wpeqn               ", _wpeqn               , T_FLAG   );
        add( "wpjst               ", _wpjst               , T_FLAG   );
        add( "wpsp                ", _wpsp                , T_FLAG   );
        add( "wraptrsp            ", _wraptrsp            , T_FLAG   );
        add( "wrppunct            ", _wrppunct            , T_FLAG   );
        add( "xe                  ", _xe                  , T_DEST   );
        add( "xef                 ", _xef                 , T_VALUE  );
        add( "yr                  ", _yr                  , T_VALUE  );
        add( "yts                 ", _yts                 , T_VALUE  );
        add( "yxe                 ", _yxe                 , T_FLAG   );
        add( "zwbo                ", _zwbo                , T_SYMBOL );
        add( "zwj                 ", _zwj                 , T_SYMBOL );
        add( "zwnbo               ", _zwnbo               , T_SYMBOL );
        add( "zwnj                ", _zwnj                , T_SYMBOL );

        add( "brdrnone            ", _brdrnone            , T_VALUE  );
        add( "jclisttab           ", _jclisttab           , T_VALUE  );
        add( "shp                 ", _shp                 , T_VALUE  );
        add( "shpinst             ", _shpinst             , T_VALUE  );
        add( "sn                  ", _sn                  , T_VALUE  );
        add( "sp                  ", _sp                  , T_VALUE  );
        add( "sv                  ", _sv                  , T_VALUE  );
        add( "listtable           ", _listtable           , T_DEST   );
        add( "list                ", _list                , T_DEST   );
        add( "lfolevel            ", _lfolevel            , T_DEST   );
        add( "listlevel           ", _listlevel           , T_DEST   );
        add( "listoverridetable   ", _listoverridetable   , T_DEST   );
        add( "listoverride        ", _listoverride        , T_DEST   );
        add( "listoverridestartat ", _listoverridestartat , T_DEST   );
        // asian keywords:
        no2ignorable[_hich] = true;
        no2ignorable[_loch] = true;
        no2ignorable[_dbch] = true;
        no2ignorable[_af] = true;
        no2ignorable[_adjustright] = true;
        no2ignorable[_widctlpar] = true;
        no2ignorable[_cgrid] = true;
        no2ignorable[_lin] = true;
        no2ignorable[_rin] = true;
        // language keywords:
        no2ignorable[_langfe] = true;
        no2ignorable[_langnp] = true;
        no2ignorable[_langfenp] = true;
        no2ignorable[_noproof] = true;
        no2ignorable[_lang] = true;
        // unused keywords:
        no2ignorable[_faauto] = true;
        no2ignorable[_aspalpha] = true;
        no2ignorable[_aspnum] = true;
        no2ignorable[_jclisttab] = true;
        no2ignorable[_tx] = true;
        no2ignorable[_trgaph] = true;
        no2ignorable[_trleft] = true;
        no2ignorable[_trbrdrt] = true;
        no2ignorable[_brdrs] = true;
        no2ignorable[_brdrw] = true;
        no2ignorable[_trbrdrl] = true;
        no2ignorable[_trbrdrb] = true;
        no2ignorable[_trbrdrr] = true;
        no2ignorable[_trbrdrh] = true;
        no2ignorable[_trbrdrv] = true;
        no2ignorable[_trftsWidth] = true;
        no2ignorable[_trautofit] = true;
        no2ignorable[_trpaddl] = true;
        no2ignorable[_trpaddr] = true;
        no2ignorable[_trpaddfl] = true;
        no2ignorable[_trpaddfr] = true;
        no2ignorable[_clvertalt] = true;
        no2ignorable[_clbrdrt] = true;
        no2ignorable[_clbrdrl] = true;
        no2ignorable[_clbrdrb] = true;
        no2ignorable[_clbrdrr] = true;
        no2ignorable[_cltxlrtb] = true;
        no2ignorable[_clftsWidth] = true;
        no2ignorable[_clwWidth] = true;

        no2ignorable[_shpfhdr] = true;
        no2ignorable[_shpbxcolumn] = true;
        no2ignorable[_shpbxignore] = true;
        no2ignorable[_shpbypara] = true;
        no2ignorable[_shpbyignore] = true;
        no2ignorable[_shpwr] = true;
        no2ignorable[_shpwrk] = true;
        no2ignorable[_shpfblwtxt] = true;
        no2ignorable[_shplid] = true;

    }
}























