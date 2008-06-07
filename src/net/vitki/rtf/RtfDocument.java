package net.vitki.rtf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.Hashtable;

public class RtfDocument extends RtfContainer
{
    private StyleSheet stylesheet;
    private String title;
    private String language;
    
    public RtfDocument()
    {
        super();
        stylesheet = new StyleSheet();
        title = null;
        language = "none";
    }
    
    public void setStylesheet (StyleSheet stylesheet)
    {
        this.stylesheet = stylesheet;
        setTitle (stylesheet.getInfoProps().title);
    }
    
    public void setTitle (String title)
    {
        this.title = title;
    }
    
    public void setLanguage (int code)
    {
        if (lang_map == null)
            initLanguages();
        String desc = (String)lang_map.get( new Integer(code) );
        if (desc == null)
            desc = "none";
        desc = desc.trim().toLowerCase();
        if (desc.indexOf(' ') >= 0)
            desc = desc.substring(0,desc.indexOf(' ')).trim();
        language = desc;
    }

    public void dump (XmlWriter out, DumpHelper ctx)
    throws RtfException, SAXException
    {
        out.startDocument();
        out.startElement("article", "lang", language);
        out.charElement("title", title);
        out.startElement("articleinfo");
        if (ctx.getFlag("dump-info"))
            stylesheet.getInfoProps().dump(out);
        if (ctx.getFlag("dump-page-props"))
            stylesheet.getPageProps().dump(out);
        if (ctx.getFlag("dump-section-props"))
            stylesheet.getSectProps().dump(out);
        if (ctx.getFlag("dump-stylesheet"))
            stylesheet.dumpStyles(out, ctx.getFlag("dump-all-styles"));
        out.endElement("articleinfo");
        super.dump(out, ctx);
        ctx.endDivisions(-1);
        out.endElement("article");
        out.endDocument();
    }
    
    private static Hashtable lang_map = null;
    
    private static void addLanguage (int code, String desc)
    {
        lang_map.put( new Integer(code), desc );
    }

    private static synchronized void initLanguages()
    {
        if (lang_map != null)
            return;
        lang_map = new Hashtable();
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
        addLanguage( 12300	,"fr French Cote d'Ivoire");
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
}
