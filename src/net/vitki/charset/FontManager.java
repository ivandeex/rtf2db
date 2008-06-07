package net.vitki.charset;

import java.util.HashMap;
import java.util.TreeMap;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.net.URL;
import java.io.File;

import java.awt.Font;

public abstract class FontManager
{

    public static int    MAX_CACHED_SCALED_FONTS = 20;
    public static String CONFIG_FILE = "etc/fntmgr/_config.txt";

    public static ManagedFont getFont (String fontname, int style,
    								double width, double height)  {
    	return getFont(fontname, style, width, height, false);
    }

    public static synchronized ManagedFont getFont (String fontname, int style,
    								double width, double height, boolean try_subst)  {
        setup (null);
        String skey = ManagedFont.getKey(fontname,style,width,height);
        ManagedFont mf = (ManagedFont) scaled_hash.get(skey);
        if (mf != null)    {
            scaled_tree.remove (mf.getStamp());
            scaled_tree.put (mf.touch(), mf);
            return mf;
        }
        ManagedFont base_mf = getFont(fontname, try_subst);
        if (base_mf == null)
        	return null;
        mf = new ManagedFont (base_mf);
        mf.setProps (style, width, height);
        if (scaled_hash.size() >= MAX_CACHED_SCALED_FONTS)  {
            Long oldest_stamp = (Long) scaled_tree.firstKey();
            ManagedFont oldest_mf = (ManagedFont) scaled_tree.remove (oldest_stamp);
            String oldest_skey = oldest_mf.getKey();
            scaled_hash.remove (oldest_skey);
        }
        scaled_hash.put (skey, mf);
        scaled_tree.put (mf.touch(), mf);
        return mf;
    }

    private static final String[] localized_font_name_suffixes = { "cyr" };

    public static ManagedFont getFont (String fontname, boolean try_subst)  {
    	ManagedFont mf = getFont(fontname);
    	if (mf == null && try_subst) {
        	fontname = fontname.trim();
        	for (int i = 0; i < localized_font_name_suffixes.length; i++) {
        		String suf = localized_font_name_suffixes[i];
        		if (fontname.toLowerCase().endsWith(" " + suf)) {
        			String fname = fontname.substring(0, fontname.length() - suf.length() - 1).trim();
        			mf = getFont(fname);
        			if (mf != null)
        				break;
        		}
        	}
    	}
    	return mf;
    }

    public static synchronized ManagedFont getFont (String fontname)  {
        setup (null);
        ManagedFont mf = (ManagedFont) nonscaled_fonts.get( fontname.trim() );
        if (mf == null)
            return null;
        if (mf.getFont() != null)
            return mf;
        if (mf.getPath() == null)
            return null;
        Font font = null;
        InputStream is = null;
        String path = mf.getPath();
        try  {
            if (path.startsWith("resource:"))
                is = loader.getResourceAsStream(path.substring(9));
            else
                is = new FileInputStream(path);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            System.err.println("cannot read font "+path+": "+e);
            font = null;
        }
        try { is.close(); } catch (Exception e) {}
        if (font == null)  {
            mf.setPath(null);
            return null;
        }
        mf.setFont(font);
        return mf;
    }

    /** setup **/

    private static TreeMap scaled_tree;
    private static HashMap scaled_hash;
    private static HashMap nonscaled_fonts;
    private static ClassLoader loader;
    private static boolean setup_done = false;

    public static synchronized void setup (Object obj)   {
        if (setup_done)    return;
        setup_done = true;
        if (obj != null)
            loader = obj.getClass().getClassLoader();
        if (loader == null)
            loader = ClassLoader.getSystemClassLoader();
        scaled_tree = new TreeMap();
        scaled_hash = new HashMap();
        nonscaled_fonts = new HashMap();
        String name = resolveFile (CONFIG_FILE);
        InputStream is = null;
        try  {
            if (name.startsWith("resource:"))
                is = loader.getResourceAsStream(name.substring(9));
            else
                is = new FileInputStream (name);
            InputStreamReader isr = new InputStreamReader (is);
            LineNumberReader lnr = new LineNumberReader (isr);
            while(true)
            {
                int charset = 0;
                String line = lnr.readLine();
                if (line == null)    break;
                line = line.trim();
                if (line.equals(""))   continue;
                if (line.charAt(0)=='#')    continue;
                int pos = line.indexOf('=');
                if (pos <= 0)    continue;
                String face = line.substring(0,pos).trim();
                String file = line.substring(pos+1).trim();
                pos = face.indexOf('/');
                if (pos >= 0)  {
                    try {
                        charset = Integer.parseInt(face.substring(pos+1).trim());
                    } catch (Exception e) {
                        charset = 0;
                    }
                    face = face.substring(0,pos).trim();
                }
                String path = resolveFile(file);
                if (path == null)
                    continue;
                ManagedFont mf;
                mf = new ManagedFont( face, path, null, Font.PLAIN, charset, 1.0, 1.0 );
                nonscaled_fonts.put( face, mf );
                nonscaled_fonts.put( face.toLowerCase(), mf );
                nonscaled_fonts.put( trimSpaces(face), mf );
                nonscaled_fonts.put( trimSpaces(face).toLowerCase(), mf );
            }
        } catch (IOException ioe)  {
            System.err.println("cannot read font configuration: "+ioe);
        }
        try { is.close(); } catch (Exception e) {}
    }

    private static String resolveFile (String name)  {
        String file = resolveStaticFile(name);
        if (file == null)  {
            URL url = loader.getResource(name);
            if (url != null && url.getProtocol().equals("jar"))
                return "resource:"+name;
        }
        if (file == null) {
            System.err.println("warning: file ["+name+"] not found");
            return name;
        }
        return file;
    }

    private static String resolveStaticFile (String name)  {
        String rname = name.replace('\\','/');
        URL url = loader.getResource(rname);
        File file;
        if (url == null || !url.getProtocol().equals("file")) {
            file = new File(name);
            if (!file.exists())
                return null;
            else
                file = new File(url.getFile());
            if (!file.exists())
                return null;
        } else
            file = new File(url.getFile());
        return file.getPath();
    }

    private static String trimSpaces (String s)  {
        StringBuffer b = new StringBuffer();
        for (int i=0; i<s.length(); i++)   {
            char c = s.charAt(i);
            if (c != ' ')    b.append(c);
        }
        return b.toString();
    }

    /** test **/

    public static void main (String[] arg)  throws Exception  {
        ManagedFont mf = getFont ("symbol", Font.PLAIN, 50.0, 0.0);
        if (mf == null)  {
            System.err.println ("cannot get font");
            return;
        }
    }

}
