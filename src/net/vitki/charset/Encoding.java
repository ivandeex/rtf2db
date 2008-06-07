package net.vitki.charset;

import java.util.Vector;
import java.util.Hashtable;
import java.io.UnsupportedEncodingException;

public abstract class Encoding
{

    public static final int CHARSET_ANSI      = 0;
    public static final int CHARSET_DEFAULT   = 1;
    public static final int CHARSET_MAC       = 77;
    public static final int CHARSET_PC437     = 437;
    public static final int CHARSET_IBM850    = 850;
    public static final int CHARSET_PRIVATE   = 2;
    public static final int CHARSET_SYMBOL    = 256;
    public static final int CHARSET_WINGDINGS = 257;
    public static final int CHARSET_MAX       = 2000;

    /**
     *  Converts single byte into unicode. Super fast. No checks.
     */
    public static char decode (int code, int ch)
    {
        return tables[code][ch];
    }
    
    /**
     *  Converts byte array into String.
     */
    public static String decode (int code, byte[] data, int off, int len)
    {
        char[] table = tables[code < 0 || code > CHARSET_MAX ? CHARSET_ANSI : code];
        char[] ca = new char[len];
        int end = off + len;
        for (int i = off; i < end; i++)
            ca[i - off] = table[data[i] < 0 ? data[i] + 256 : data[i]];
        return new String(ca);
    }

    /**
     *  Converts byte array into String.
     */
    public static String decode (int code, byte[] data)
    {
        return decode (code, data, 0, data.length);
    }

    /**
     *  Converts byte array and appends result to StringBuffer.
     */
    public static void decode (StringBuffer result, int code, byte[] data, int off, int len)
    {
        char[] table = tables[code < 0 || code > CHARSET_MAX ? CHARSET_ANSI : code];
        int end = off + len;
        for (int i = off; i < end; i++)
            result.append( table[data[i] < 0 ? data[i] + 256 : data[i]] );
    }

    /**
     *  Converts byte array and appends result to StringBuffer.
     */
    public static void decode (StringBuffer result, int code, byte[] data)
    {
        decode (result, code, data, 0, data.length);
    }

    /**
     *  Returns Java charset name for specified rtf-code or Windows codepage.
     */
    public static String getEncoding (int code)
    {
        String charset = null;
        if (code >= 0 && code <= CHARSET_MAX)
            charset = charsets[code];
        if (charset == null)
            charset = charsets[CHARSET_ANSI];
        return charset;
    }

    /**
     *  Returns language name name for specified rtf-code.
     */
    public static String getLanguage (int code, boolean abbreviation)
    {
        String desc = (String)lang_map.get( new Integer(code) );
        if (desc == null)
            desc = "none";
        desc = desc.trim().toLowerCase();
        if (abbreviation && desc.indexOf(' ') >= 0)
            desc = desc.substring(0,desc.indexOf(' ')).trim();
        return desc;
    }

    /**
     *  Returns required codepage for special fonts or 0 if no requirements.
     */
    public static int getRequiredCodepage (String font_face)
    {
        Integer val = (Integer)spec_fonts.get( font_face.trim().toLowerCase() );
        return val == null ? 0 : val.intValue();
    }

    /**
     *  Tries to find the charset code given its name. Returns 0 if not found.
     */
    public static int getCharsetCode (String name)
    {
    	if (name == null)
    		return 0;
    	name = name.trim();
    	if ("".equals(name))
    		return 0;
    	int i;
    	for (i = 1; i < charsets.length; i++)
    		if (charsets[i] != null && charsets[i].equalsIgnoreCase(name))
    			return i;
    	// Maybe it is simply a number.
    	try {
    		i = Integer.parseInt(name);
    	} catch (Exception e) {
    		i = 0;
    	}
    	return i;
    }

    /* === setup === */
    
    private static int[]     codepages;  // reference from rtf-code to Windows codepage
    private static String[]  charsets;   // Java charset name for rtf-code / codepage
    private static char[][]  tables;     // translation from codepage to unicode
    private static byte[]    pattern;    // used internally...
    private static Hashtable lang_map;   // languages and their codes
    private static Hashtable spec_fonts; // fonts with special encoding
    private static boolean   setup_done = false;

    private static synchronized void setupTables ()
    {
        if (setup_done)
            return;        
        setup_done = true;
        int i;
        /* === allocate structures === */
        codepages = new int[CHARSET_MAX+1];
        charsets  = new String[CHARSET_MAX+1];
        tables = new char[CHARSET_MAX+1][];
        /* === ANSI translation === */
        pattern = new byte[256];
        for (i=0; i<256; i++)  { pattern[i] = (byte)i; }
        tables[CHARSET_ANSI] = new char[256];
        for (i=0; i<256; i++)  { tables[CHARSET_ANSI][i] = (char)i; }
        /* === standard encodings === */
        standardEncodings();
        /* === special translations === */
        tables[CHARSET_PRIVATE] = new char[256];
        for (i=0; i<256; i++)  tables[CHARSET_PRIVATE][i] = (char)i;
        for (i=33; i<256; i++)  tables[CHARSET_PRIVATE][i] = (char)(0xF000 + i);
        tables[CHARSET_SYMBOL] = new char[256];
        for (i=0; i<256; i++)  tables[CHARSET_SYMBOL][i] = (char)symbol2unicode[i];
        tables[CHARSET_WINGDINGS] = new char[256];
        for (i=0; i<256; i++)  tables[CHARSET_WINGDINGS][i] = (char)wingdings2unicode[i];
        /* === undefined translations default to ANSI === */
        for (i=0; i<=CHARSET_MAX; i++)
            if (tables[i] == null)  tables[i] = tables[CHARSET_ANSI];
        /* === languages === */
        lang_map = new Hashtable();
        standardLanguages();
        /* === special fonts === */
        spec_fonts = new Hashtable();
        standardSpecialFonts();
    }

    private static void addEncoding (int rtfcode, int codepage, String charset)
    {
        char[] table = null;
        if (codepage > 0 && codepage <= CHARSET_MAX && table == null)
            table = tables[codepage];
        if (rtfcode >= 0 && rtfcode <= CHARSET_MAX && table == null)
            table = tables[rtfcode];
        if (table == null && charset != null)  {
            try  {
                table = (new String(pattern, charset)).toCharArray();
                for (int i=0; i<128; i++)  table[i] = (char)i;  //@??
            } catch (Exception e) {
                table = null;
            }
        }
        if (codepage > 0 && codepage <= CHARSET_MAX)  {
            tables[codepage] = table;
            charsets[codepage] = charset;
        }
        if (rtfcode >= 0 && rtfcode <= CHARSET_MAX)  {
            tables[rtfcode] = table;
            charsets[rtfcode] = charset;
            codepages[rtfcode] = codepage;
        }
    }
    
    private static void standardEncodings()
    {
        addEncoding(   0  , 1252 , "ANSI"     );   // ANSI
        addEncoding(   1  , 1252 , "Cp1252"   );   // Default
        addEncoding(   2  ,   -1 , null       );   // Symbol (FONT_PRIVATE)
        addEncoding(   3  ,   -1 , null       );   // Invalid
        addEncoding(  77  ,   -1 , "MacRoman" );   // Mac
        addEncoding( 128  ,   -1 , "MS932"    );   // Shift JIS
        addEncoding( 129  ,   -1 , "MS949"    );   // Hangul
        addEncoding( 130  , 1361 , "Johab"    );   // Johab
        addEncoding( 134  ,  936 , "MS936"    );   // GB2312
        addEncoding( 136  ,  950 , "MS950"    );   // Big5
        addEncoding( 161  , 1253 , "Cp1253"   );   // Greek
        addEncoding( 162  , 1254 , "Cp1254"   );   // Turkish
        addEncoding( 163  , 1258 , "Cp1258"   );   // Vietnamese
        addEncoding( 177  , 1255 , "Cp1255"   );   // Hebrew
        addEncoding( 178  , 1256 , "Cp1256"   );   // Arabic
        addEncoding( 179  , 1256 , "Cp1256"   );   // Arabic Traditional
        addEncoding( 180  , 1256 , "Cp1256"   );   // Arabic User
        addEncoding( 181  , 1255 , "Cp1255"   );   // Hebrew User
        addEncoding( 186  , 1257 , "Cp1257"   );   // Baltic
        addEncoding( 204  , 1251 , "Cp1251"   );   // Russian
        addEncoding( 222  ,  874 , "MS874"    );   // Thai
        addEncoding( 238  , 1250 , "Cp1250"   );   // East European
        addEncoding( 254  ,  437 , "Cp437"    );   // PC 437
        addEncoding( 256  ,   -1 , null       );   // FONT_SYMBOL
        addEncoding( 257  ,   -1 , null       );   // FONT_WINGDINGS
        addEncoding(  -1  ,  437 , "Cp437"    );   // United States IBM
        addEncoding(  -1  ,  708 , "Cp708"    );   // Arabic (ASMO 708)
        addEncoding(  -1  ,  709 , "Cp709"    );   // Arabic (ASMO 449+, BCON V4)
        addEncoding(  -1  ,  710 , "Cp710"    );   // Arabic (transparent Arabic)
        addEncoding(  -1  ,  711 , "Cp711"    );   // Arabic (Nafitha Enhanced)
        addEncoding(  -1  ,  720 , "Cp720"    );   // Arabic (transparent ASMO)
        addEncoding(  -1  ,  819 , "Cp819"    );   // Windows 3.1 (US and EU)
        addEncoding(  -1  ,  850 , "Cp850"    );   // IBM multilingual
        addEncoding(  -1  ,  852 , "Cp852"    );   // Eastern European
        addEncoding(  -1  ,  860 , "Cp860"    );   // Portuguese
        addEncoding(  -1  ,  862 , "Cp862"    );   // Hebrew
        addEncoding(  -1  ,  863 , "Cp863"    );   // French Canadian
        addEncoding(  -1  ,  864 , "Cp864"    );   // Arabic
        addEncoding(  -1  ,  865 , "Cp865"    );   // Norwegian
        addEncoding(  -1  ,  866 , "Cp866"    );   // Soviet Union
        addEncoding(  -1  ,  874 , "MS874"    );   // Thai
        addEncoding(  -1  ,  932 , "MS932"    );   // Japanese
        addEncoding(  -1  ,  936 , "MS936"    );   // Simplified Chinese
        addEncoding(  -1  ,  949 , "MS949"    );   // Korean
        addEncoding(  -1  ,  950 , "MS950"    );   // Traditional Chinese
        addEncoding(  -1  , 1250 , "Cp1250"   );   // Windows 3.1 (Eastern Europe)
        addEncoding(  -1  , 1251 , "Cp1251"   );   // Windows 3.1 (Cyrillic)
        addEncoding(  -1  , 1252 , "Cp1252"   );   // Western European
        addEncoding(  -1  , 1253 , "Cp1253"   );   // Greek
        addEncoding(  -1  , 1254 , "Cp1254"   );   // Turkish
        addEncoding(  -1  , 1255 , "Cp1255"   );   // Hebrew
        addEncoding(  -1  , 1256 , "Cp1256"   );   // Arabic
        addEncoding(  -1  , 1257 , "Cp1257"   );   // Baltic
        addEncoding(  -1  , 1258 , "Cp1258"   );   // Vietnamese
        addEncoding(  -1  , 1361 , "Johab"    );   // Johab
    }

    private static void addLanguage (int code, String desc)
    {
        lang_map.put( new Integer(code), desc );
    }

    private static void standardLanguages()
    {
        addLanguage( 1078	,"Afrikaans"            );
        addLanguage( 1052	,"Albanian"             );
        addLanguage( 1025	,"Arabic"               );
        addLanguage( 5121	,"Arabic Algeria"	);
        addLanguage( 15361	,"Arabic Bahrain"	);
        addLanguage( 3073	,"Arabic Egypt"         );
        addLanguage( 1          ,"Arabic General"	);
        addLanguage( 2049	,"Arabic Iraq"          );
        addLanguage( 11265	,"Arabic Jordan"	);
        addLanguage( 13313	,"Arabic Kuwait"	);
        addLanguage( 12289	,"Arabic Lebanon"	);
        addLanguage( 4097	,"Arabic Libya"         );
        addLanguage( 6145	,"Arabic Morocco"	);
        addLanguage( 8193	,"Arabic Oman"          );
        addLanguage( 16385	,"Arabic Qatar"         );
        addLanguage( 10241	,"Arabic Syria"         );
        addLanguage( 7169	,"Arabic Tunisia"	);
        addLanguage( 14337	,"Arabic U.A.E."	);
        addLanguage( 9217	,"Arabic Yemen"         );
        addLanguage( 1067	,"Armenian"             );
        addLanguage( 1101	,"Assamese"             );
        addLanguage( 2092	,"Azeri Cyrillic"	);
        addLanguage( 1068	,"Azeri Latin"          );
        addLanguage( 1069	,"Basque"               );
        addLanguage( 1093	,"Bengali"              );
        addLanguage( 4122	,"Bosnia Herzegovina"	);
        addLanguage( 1026	,"Bulgarian"            );
        addLanguage( 1109	,"Burmese"              );
        addLanguage( 1059	,"Byelorussian"         );
        addLanguage( 1027	,"Catalan"              );
        addLanguage( 2052	,"Chinese China"	);
        addLanguage( 4          ,"Chinese General"	);
        addLanguage( 3076	,"Chinese Hong Kong"	);
        addLanguage( 3076	,"Chinese Macao"	);
        addLanguage( 4100	,"Chinese Singapore"	);
        addLanguage( 1028	,"Chinese Taiwan"	);
        addLanguage( 1050	,"Croatian"             );
        addLanguage( 1029	,"Czech"                );
        addLanguage( 1030	,"Danish"               );
        addLanguage( 2067	,"Dutch Belgium"	);
        addLanguage( 1043	,"Dutch Standard"	);
        addLanguage( 3081	,"en English Australia"	);
        addLanguage( 10249	,"en English Belize"	);
        addLanguage( 2057	,"en English British"	);
        addLanguage( 4105	,"en English Canada"	);
        addLanguage( 9225	,"en English Caribbean"	);
        addLanguage( 9          ,"en English General"	);
        addLanguage( 6153	,"en English Ireland"	);
        addLanguage( 8201	,"en English Jamaica"	);
        addLanguage( 5129	,"en English New Zealand");
        addLanguage( 13321	,"en English Philippines");
        addLanguage( 7177	,"en English South Africa");
        addLanguage( 11273	,"en English Trinidad"	);
        addLanguage( 1033	,"en English United States");
        addLanguage( 1033	,"en English Zimbabwe"	);
        addLanguage( 1061	,"Estonian"             );
        addLanguage( 1080	,"Faeroese"             );
        addLanguage( 1065	,"Farsi"                );
        addLanguage( 1035	,"Finnish"              );
        addLanguage( 1036	,"fr French"            );
        addLanguage( 2060	,"fr French Belgium"	);
        addLanguage( 11276	,"fr French Cameroon"	);
        addLanguage( 3084	,"fr French Canada"	);
        addLanguage( 12300	,"fr French Cote d�Ivoire");
        addLanguage( 5132	,"fr French Luxemburg"	);
        addLanguage( 13324	,"fr French Mali"	);
        addLanguage( 6156	,"fr French Monaco"	);
        addLanguage( 8204	,"fr French Reunion"	);
        addLanguage( 10252	,"fr French Senegal"	);
        addLanguage( 4108	,"fr French Swiss"	);
        addLanguage( 7180	,"fr French West Indies");
        addLanguage( 9228	,"fr French Zaire"	);
        addLanguage( 1122	,"Frisian"              );
        addLanguage( 1084	,"Gaelic"               );
        addLanguage( 2108	,"Gaelic Ireland"	);
        addLanguage( 1110	,"Galician"             );
        addLanguage( 1079	,"Georgian"             );
        addLanguage( 1031	,"de German"            );
        addLanguage( 3079	,"de German Austrian"	);
        addLanguage( 5127	,"de German Liechtenstein");
        addLanguage( 4103	,"de German Luxemburg"	);
        addLanguage( 2055	,"de German Switzerland");
        addLanguage( 1032	,"Greek"                );
        addLanguage( 1095	,"Gujarati"             );
        addLanguage( 1037	,"Hebrew"               );
        addLanguage( 1081	,"Hindi"                );
        addLanguage( 1038	,"Hungarian"            );
        addLanguage( 1039	,"Icelandic"            );
        addLanguage( 1057	,"Indonesian"           );
        addLanguage( 1040	,"Italian"              );
        addLanguage( 2064	,"Italian Switzerland"	);
        addLanguage( 1041	,"Japanese"             );
        addLanguage( 1099	,"Kannada"              );
        addLanguage( 1120	,"Kashmiri"             );
        addLanguage( 2144	,"Kashmiri India"	);
        addLanguage( 1087	,"Kazakh"               );
        addLanguage( 1107	,"Khmer"                );
        addLanguage( 1088	,"Kirghiz"              );
        addLanguage( 1111	,"Konkani"              );
        addLanguage( 1042	,"Korean"               );
        addLanguage( 2066	,"Korean Johab"         );
        addLanguage( 1108	,"Lao"                  );
        addLanguage( 1062	,"Latvian"              );
        addLanguage( 1063	,"Lithuanian"           );
        addLanguage( 2087	,"Lithuanian Classic"	);
        addLanguage( 1086	,"Macedonian"           );
        addLanguage( 1086	,"Malay"                );
        addLanguage( 2110	,"Malay Brunei Darussalam"	);
        addLanguage( 1100	,"Malayalam"            );
        addLanguage( 1082	,"Maltese"              );
        addLanguage( 1112	,"Manipuri"             );
        addLanguage( 1102	,"Marathi"              );
        addLanguage( 1104	,"Mongolian"            );
        addLanguage( 1121	,"Nepali"               );
        addLanguage( 2145	,"Nepali India"         );
        addLanguage( 1044	,"Norwegian Bokmal"	);
        addLanguage( 2068	,"Norwegian Nynorsk"	);
        addLanguage( 1096	,"Oriya"                );
        addLanguage( 1045	,"Polish"               );
        addLanguage( 1046	,"Portuguese Brazil"	);
        addLanguage( 2070	,"Portuguese Iberian"	);
        addLanguage( 1094	,"Punjabi"              );
        addLanguage( 1047	,"Rhaeto-Romanic"	);
        addLanguage( 1048	,"Romanian"             );
        addLanguage( 2072	,"Romanian Moldova"	);
        addLanguage( 1049	,"ru Russian"           );
        addLanguage( 2073	,"ru Russian Moldova"	);
        addLanguage( 1083	,"Sami Lappish"         );
        addLanguage( 1103	,"Sanskrit"             );
        addLanguage( 3098	,"Serbian Cyrillic"	);
        addLanguage( 2074	,"Serbian Latin"	);
        addLanguage( 1113	,"Sindhi"               );
        addLanguage( 1051	,"Slovak"               );
        addLanguage( 1060	,"Slovenian"            );
        addLanguage( 1070	,"Sorbian"              );
        addLanguage( 11274	,"Spanish Argentina"	);
        addLanguage( 16394	,"Spanish Bolivia"	);
        addLanguage( 13322	,"Spanish Chile"	);
        addLanguage( 9226	,"Spanish Colombia"	);
        addLanguage( 5130	,"Spanish Costa Rica"	);
        addLanguage( 7178	,"Spanish Dominican Republic"	);
        addLanguage( 12298	,"Spanish Ecuador"	);
        addLanguage( 17418	,"Spanish El Salvador"	);
        addLanguage( 4106	,"Spanish Guatemala"	);
        addLanguage( 18442	,"Spanish Honduras"	);
        addLanguage( 2058	,"Spanish Mexico"	);
        addLanguage( 3082	,"Spanish Modern"	);
        addLanguage( 19466	,"Spanish Nicaragua"	);
        addLanguage( 6154	,"Spanish Panama"	);
        addLanguage( 15370	,"Spanish Paraguay"	);
        addLanguage( 10250	,"Spanish Peru"         );
        addLanguage( 20490	,"Spanish Puerto Rico"	);
        addLanguage( 1034	,"Spanish Traditional"	);
        addLanguage( 14346	,"Spanish Uruguay"	);
        addLanguage( 8202	,"Spanish Venezuela"	);
        addLanguage( 1072	,"Sutu"                 );
        addLanguage( 1089	,"Swahili"              );
        addLanguage( 1053	,"Swedish"              );
        addLanguage( 2077	,"Swedish Finland"	);
        addLanguage( 1064	,"Tajik"                );
        addLanguage( 1097	,"Tamil"                );
        addLanguage( 1092	,"Tatar"                );
        addLanguage( 1098	,"Telugu"               );
        addLanguage( 1054	,"Thai"                 );
        addLanguage( 1105	,"Tibetan"              );
        addLanguage( 1073	,"Tsonga"               );
        addLanguage( 1074	,"Tswana"               );
        addLanguage( 1055	,"Turkish"              );
        addLanguage( 1090	,"Turkmen"              );
        addLanguage( 1058	,"Ukrainian"            );
        addLanguage( 1056	,"Urdu"                 );
        addLanguage( 2080	,"Urdu India"           );
        addLanguage( 2115	,"Uzbek Cyrillic"	);
        addLanguage( 1091	,"Uzbek Latin"          );
        addLanguage( 1075	,"Venda"                );
        addLanguage( 1066	,"Vietnamese"           );
        addLanguage( 1106	,"Welsh"                );
        addLanguage( 1076	,"Xhosa"                );
        addLanguage( 1085	,"Yiddish"              );
        addLanguage( 1077	,"Zulu"                 );
    }
    
    /* === non-standard fonts === */
    
    private static void addSpecialFont (String face, int codepage)
    {
        spec_fonts.put( face.trim().toLowerCase(), new Integer(codepage) );
    }
    
    private static void standardSpecialFonts()
    {
        addSpecialFont( "Symbol",    CHARSET_SYMBOL );
        addSpecialFont( "Wingdings", CHARSET_WINGDINGS );
        addSpecialFont( "MT Extra",  CHARSET_PRIVATE );
    }
    
    private static int[] symbol2unicode = {
     0,      0,      0,      0,      0,      0,      0,      0,      // 0
     0,      0,      0,      0,      0,      0,      0,      0,      // 8
     0,      0,      0,      0,      0,      0,      0,      0,      // 16
     0,      0,      0,      0,      0,      0,      0,      0,      // 24
     0,      0x0021, 0x2200, 0x0023, 0x2203, 0x0025, 0x0026, 0x220d, // 32
     0x0028, 0x0029, 0x002a, 0x002b, 0x002c, 0x002d, 0x002e, 0x002f, // 40
     0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037, // 48
     0x0038, 0x0039, 0x003a, 0x003b, 0x003c, 0x003d, 0x003e, 0x003f, // 56
     0x2245, 0x0391, 0x0392, 0x03a7, 0x0394, 0x0395, 0x03a6, 0x0393, // 64
     0x0397, 0x0399, 0x03d1, 0x039a, 0x039b, 0x039c, 0x039d, 0x039f, // 72
     0x03a0, 0x0398, 0x03a1, 0x03a3, 0x03a4, 0x03a5, 0x03c2, 0x03a9, // 80
     0x039e, 0x03a8, 0x0396, 0x005b, 0x2234, 0x005d, 0x22a5, 0x005f, // 88
     0x00af, 0x03b1, 0x03b2, 0x03c7, 0x03b4, 0x03b5, 0x03d5, 0x03b3, // 96
     0x03b7, 0x03b9, 0x03c6, 0x03ba, 0x03bb, 0x03bc, 0x03bd, 0x03bf, // 104
     0x03c0, 0x03b8, 0x03c1, 0x03c3, 0x03c4, 0x03c5, 0x03d6, 0x03c9, // 112
     0x03be, 0x03c8, 0x03b6, 0x007b, 0x007c, 0x007d, 0x007e, 0,      // 120
     0,      0,      0,      0,      0,      0,      0,      0,      // 128
     0,      0,      0,      0,      0,      0,      0,      0,      // 136
     0,      0,      0,      0,      0,      0,      0,      0,      // 144
     0,      0,      0,      0,      0,      0,      0,      0,      // 152
     0,      0x03d2, 0x2032, 0x2264, 0x2044, 0x221e, 0x0192, 0x2663, // 160
     0x2666, 0x2665, 0x2660, 0x2194, 0x2190, 0x2191, 0x2192, 0x2193, // 168
     0x00b0, 0x00b1, 0x2033, 0x2265, 0x00d7, 0x221d, 0x2202, 0x2022, // 176
     0x00f7, 0x2260, 0x2261, 0x2248, 0x2026, 0x2502, 0x2500, 0x21b5, // 184
     0x2135, 0x2111, 0x211c, 0x2118, 0x2297, 0x2295, 0x2298, 0x2229, // 192
     0x222a, 0x2283, 0x2287, 0x2284, 0x2282, 0x2286, 0x2208, 0x2209, // 200
     0x2220, 0x2207, 0x00ae, 0x00a9, 0x2122, 0x220f, 0x221a, 0x2219, // 208
     0x00ac, 0x2227, 0x2228, 0x21d4, 0x21d0, 0x21d1, 0x21d2, 0x21d3, // 216
     0x25ca, 0x2329, 0x00ae, 0x00a9, 0x2122, 0x2211, 0x239b, 0x239c, // 224
     0x239d, 0x23a1, 0x23a2, 0x23a3, 0x23a7, 0x23a8, 0x23a9, 0x23aa, // 232
     0,      0x232a, 0x222b, 0x2320, 0x23ae, 0x2321, 0x239e, 0x239f, // 240
     0x23a0, 0x23a4, 0x23a5, 0x23a6, 0x23ab, 0x23ac, 0x23ad, 0       // 248
    };


    private static int[] wingdings2unicode = {
     0,      0,      0,      0,      0,      0,      0,      0,      // 0
     0,      0,      0,      0,      0,      0,      0,      0,      // 8
     0,      0,      0,      0,      0,      0,      0,      0,      // 16
     0,      0,      0,      0,      0,      0,      0,      0,      // 24
     0,      0x270f, 0x2702, 0x2701, 0,      0,      0,      0,      // 32
     0x260e, 0x2706, 0x2709, 0x270a, 0,      0,      0,      0,      // 40
     0,      0,      0,      0,      0,      0,      0x231b, 0x2328, // 48
     0,      0,      0,      0,      0,      0,      0,      0x270d, // 56
     0,      0x270c, 0,      0,      0,      0x261c, 0x261e, 0x261d, // 64
     0x261f, 0,      0x263a, 0,      0x2639, 0,      0x2620, 0,      // 72
     0,      0x2708, 0x263c, 0x2602, 0,      0x271d, 0x271e, 0,      // 80
     0x2720, 0x2721, 0x262a, 0x262f, 0,      0x2638, 0x2648, 0x2649, // 88
     0x264a, 0x264b, 0x264c, 0x264d, 0x264e, 0x264f, 0x2650, 0x2651, // 96
     0x2652, 0x2653, 0,      0,      0x25cf, 0x274d, 0x25a0, 0x25a1, // 104
     0x2610, 0x2751, 0x2752, 0,      0,      0x25c6, 0x2756, 0,      // 112
     0x2327, 0,      0x2318, 0x2740, 0x273f, 0x275d, 0x275e, 0x25af, // 120
     0x24ea, 0x2780, 0x2781, 0x2782, 0x2783, 0x2784, 0x2785, 0x2786, // 128
     0x2787, 0x2788, 0x2789, 0,      0x2776, 0x2777, 0x2778, 0x2779, // 136
     0x277a, 0x277b, 0x277c, 0x277d, 0x277e, 0x277f, 0x2767, 0x2619, // 144
     0x2619, 0x2767, 0x2767, 0x2619, 0x2619, 0x2767, 0x2019, 0x2022, // 152
     0x25fe, 0x25ca, 0x25ca, 0x25c9, 0x2609, 0x20dd, 0x25ef, 0x25fc, // 160
     0x2610, 0,      0x2726, 0x2605, 0x2736, 0x2738, 0x2739, 0x272f, // 168
     0,      0x2316, 0x2727, 0,      0,      0x272a, 0x2730, 0x231a, // 176
     0x231a, 0x231a, 0x231a, 0x231a, 0x231a, 0x231a, 0x231a, 0x231a, // 184
     0x231a, 0x231a, 0x231a, 0x2936, 0x2937, 0x293b, 0x27a6, 0x293f, // 192
     0x2934, 0x2938, 0x2935, 0x2723, 0x2724, 0,      0,      0,      // 200
     0,      0,      0,      0,      0,      0x232b, 0x2326, 0,      // 208
     0x27a2, 0,      0,      0,      0x27b2, 0,      0,      0x2190, // 216
     0x2192, 0x2191, 0x2193, 0x2196, 0x2197, 0x2199, 0x2198, 0,      // 224
     0x2794, 0,      0,      0,      0,      0,      0x2798, 0x21e6, // 232
     0x21e8, 0x21e7, 0x21e9, 0x21d4, 0x21f3, 0x21d6, 0x21d7, 0x21d9, // 240
     0x21d8, 0x25ad, 0x25ab, 0x2718, 0x2714, 0x2612, 0x2611, 0       // 248
    };
        
    static
    {
        setupTables();
    }

}







