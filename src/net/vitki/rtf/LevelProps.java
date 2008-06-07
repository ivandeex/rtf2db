package net.vitki.rtf;

import java.io.PrintWriter;

public class LevelProps
{
    public static final int ALIGN_LEFT   = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT  = 2;

    public static final int NUM_ARABIC         =  0;
    public static final int NUM_UC_ROMAN       =  1;
    public static final int NUM_LC_ROMAN       =  2;
    public static final int NUM_UC_LETTER      =  3;
    public static final int NUM_LC_LETTER      =  4;
    public static final int NUM_ORDINAL        =  5;
    public static final int NUM_CARDINAL       =  6;
    public static final int NUM_ORD_TEXT       =  7;
    public static final int NUM_SPARE_8        =  8;
    public static final int NUM_SPARE_9        =  9;
    public static final int NUM_KANJI          = 10;
    public static final int NUM_KANJI_DIGIT    = 11;
    public static final int NUM_KANA_AIUEO     = 12;
    public static final int NUM_KANA_IROHA     = 13;
    public static final int NUM_DOUBLE_CH      = 14;
    public static final int NUM_SINGLE_CH      = 15;
    public static final int NUM_KANJI_3        = 16;
    public static final int NUM_KANJI_4        = 17;
    public static final int NUM_CIRCLE         = 18;
    public static final int NUM_DBL_ARABIC     = 19;
    public static final int NUM_DBL_KANA_AIUEO = 20;
    public static final int NUM_DBL_KANA_IROHA = 21;
    public static final int NUM_0_ARABIC       = 22;
    public static final int NUM_BULLET         = 23;
    public static final int NUM_KOREAN_2       = 24;
    public static final int NUM_KOREAN_1       = 25;
    public static final int NUM_CHINESE_1      = 26;
    public static final int NUM_CHINESE_2      = 27;
    public static final int NUM_CHINESE_3      = 28;
    public static final int NUM_CHINESE_4      = 29;
    public static final int NUM_ZODIAC_1       = 30;
    public static final int NUM_ZODIAC_2       = 31;
    public static final int NUM_ZODIAC_3       = 32;
    public static final int NUM_TAIWAN_DBL_1   = 33;
    public static final int NUM_TAIWAN_DBL_2   = 34;
    public static final int NUM_TAIWAN_DBL_3   = 35;
    public static final int NUM_TAIWAN_DBL_4   = 36;
    public static final int NUM_CHINESE_DBL_1  = 37;
    public static final int NUM_CHINESE_DBL_2  = 38;
    public static final int NUM_CHINESE_DBL_3  = 39;

    public  int      start_at;
    public  int      align;
    public  int      numbering;
    public  int      tmpl_id;
    public  String   format;
    public  char     follow;
    public  boolean  continuous;
    public  String   font;

    private boolean  align_flag;
    private boolean  num_flag;

    public LevelProps()  {
        start_at = 0;
        align = TextProps.ALIGN_LEFT;
        numbering = NUM_BULLET;
        align_flag = num_flag = false;
        tmpl_id = -1;
        format = "";
        follow = '\0';
        continuous = false;
        font = "";
    }

    public void copyFrom (LevelProps cloned)  {
        this.start_at   = cloned.start_at;
        this.align      = cloned.align;
        this.numbering  = cloned.numbering;
        this.align_flag = cloned.align_flag;
        this.num_flag   = cloned.num_flag;
        this.tmpl_id    = cloned.tmpl_id;
        this.format     = cloned.format;
        this.follow     = cloned.follow;
        this.continuous = cloned.continuous;
        this.font       = cloned.font;
    }

    public void setFormat (String fmt)  {
        StringBuffer buf = new StringBuffer();
        for (int i=1; i<fmt.length(); i++)  {
            int ch = (int)fmt.charAt(i);
            if (ch < 9)  {
                buf.append( '\\' );
                buf.append( ch+1 );
            } else if ( (char)ch == '\\' ) {
                buf.append( "\\\\" );
            } else {
                buf.append( (char)ch );
            }
        }
        this.format = buf.toString();
    }

    public void setNumbering (int numbering, boolean prefer)  {
        if (this.num_flag && !prefer)
            return;
        this.numbering = numbering;
        this.num_flag = prefer;
    }

    public void setAlign (int align, boolean prefer)  {
        if (this.align_flag && !prefer)
            return;
        this.align = align;
        this.align_flag = prefer;
    }

    public static String getAlignName (int align)
    {
        switch (align)  {
            case ALIGN_LEFT:     return "left";
            case ALIGN_CENTER:   return "center";
            case ALIGN_RIGHT:    return "right";
            default:             return "bad";
        }
    }

    public static String getNumberingName (int numbering)
    {
        switch (numbering)  {
            case NUM_ARABIC    :  return "arabic";
            case NUM_UC_ROMAN  :  return "ucroman";
            case NUM_LC_ROMAN  :  return "lcroman";
            case NUM_UC_LETTER :  return "ucletter";
            case NUM_LC_LETTER :  return "lcletter";
            case NUM_ORDINAL   :  return "ordinal";
            case NUM_CARDINAL  :  return "cardinal";
            case NUM_ORD_TEXT  :  return "ordtext";
            case NUM_CIRCLE    :  return "circle";
            case NUM_BULLET    :  return "bullet";
            case NUM_DBL_ARABIC:  return "darabic";
            case NUM_0_ARABIC  :  return "zarabic";
            default            :  return "asian"+numbering;
        }
    }

    public static String getXmlNumbering (int numbering)
    {
        switch (numbering)  {
            case NUM_ARABIC    :  return "arabic";
            case NUM_UC_ROMAN  :  return "upperroman";
            case NUM_LC_ROMAN  :  return "lowerroman";
            case NUM_UC_LETTER :  return "uperalpha";
            case NUM_LC_LETTER :  return "loweralpha";
            case NUM_ORDINAL   :  return "_ordinal";
            case NUM_CARDINAL  :  return "_cardinal";
            case NUM_ORD_TEXT  :  return "_ordtext";
            case NUM_CIRCLE    :  return "_circle";
            case NUM_BULLET    :  return "bullet";
            case NUM_DBL_ARABIC:  return "_darabic";
            case NUM_0_ARABIC  :  return "_zarabic";
            default            :  return null;
        }
    }
    
    public static boolean isOrdered (int numbering)
    {
        switch (numbering)  {
            case NUM_ARABIC    :
            case NUM_UC_ROMAN  :
            case NUM_LC_ROMAN  :
            case NUM_UC_LETTER :
            case NUM_LC_LETTER :
                return true;
            case NUM_ORDINAL   :
            case NUM_CARDINAL  :
            case NUM_ORD_TEXT  :
            case NUM_CIRCLE    :
            case NUM_BULLET    :
            case NUM_DBL_ARABIC:
            case NUM_0_ARABIC  :
            default            :
                return false;
        }
    }

    public void print (PrintWriter out)
    {
        out.println ("    <level"
                     +" from="+start_at
                     +" align="+getAlignName(align)
                     +" numfmt="+getNumberingName(numbering)
                     +" format=["+format+"]"
                     +" cont="+Util.yn(continuous)
                     +" follow="+(follow=='\t'?"tab":(follow==' '?"space":"none"))
                     +" font=["+font+"]"
                     +" />");
    }
}
