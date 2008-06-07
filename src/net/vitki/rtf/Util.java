package net.vitki.rtf;


public abstract class Util
{
    public static final double TWIPS_2_MMD = 0.1764164;

    public static int twips2mmd (int twips, int scale)
    {
        return (int)Math.round( (double)twips * TWIPS_2_MMD * (double)scale / 100.0 );
    }

    public static int twips2mmd (int twips)
    {
        return twips2mmd (twips, 100);
    }
    
    public static String mmd2mm (int mmd)
    {
        String sign = mmd < 0 ? "-" : "";
        if (mmd < 0)  mmd = -mmd;
        if (mmd % 10 == 0)
            return sign+(mmd/10)+"mm";
        else
            return sign+(mmd/10)+"."+(mmd%10)+"mm";
    }

    public static String trimSemicolon (String s)  {
        return s.endsWith(";") ? s.substring(0,s.length()-1) : s;
    }

    public static String yn (boolean f)  {
        return f ? "yes" : "no";
    }
    
    public static int font2size (int value)  {
        return value / 2;
    }
    
    public static String nullify (String s)  {
        if (s == null)
            return null;
        s = s.trim();
        if ("".equals(s))
            s = null;
        return s;
    }
    
    public static int mmd2px (int mmd, int pixels_per_inch)  {
        if (pixels_per_inch <= 0)
            pixels_per_inch = 90;
        return (int)Math.round( (double)mmd / 254.0 * (double)pixels_per_inch );
    }
    
    public static String canonicName (String s)  {
        s = s.trim();
        for (int i=0; i<c_cyr.length(); i++)
            s = s.replace(c_cyr.charAt(i), c_lat.charAt(i));
        s = "_"+s;
        return s.toLowerCase();
    }
    
    private static final String
    c_cyr = "+=*/.,;: \u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f",
    c_lat = "_________abvgdewzijklmnoprstufhccssxyxeuaABVGDEWZIJKLMNOPRSTUFHCCSSXYXEUA";
}