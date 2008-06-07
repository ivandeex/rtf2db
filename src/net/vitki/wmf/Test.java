package net.vitki.wmf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import net.vitki.wmf.render.ImageIoRenderer;
import net.vitki.wmf.render.ScanRenderer;
import net.vitki.wmf.wmeta.WmfRecord;

public class Test  {

    public static void debug (RecordReader rd, RecordTable wmf)  throws IOException {
        HashSet set = new HashSet();
        System.out.println("functions: ");
        for (int i=0; i<wmf.getSize(); i++) {
            String name = WmfRecord.funcName(wmf.get(i).getFunc());
            if (set.contains(name))  continue;
            set.add(name);
            System.out.print(name+", ");
        }
        System.out.println(".");
        System.out.println("total "+wmf.getSize()+" records");
        System.out.println("rest is "+rd.countRestBytes()+" bytes");
        System.out.println("------------------ vector: -------------------");
        for (int i=0; i<wmf.getSize(); i++)    System.out.println(wmf.get(i));
    }

    public static void oneFile (String name, Properties props)  {
        try {
            oneFile (name, name+".log", name+".png", props);
        } catch (Exception e) {
            System.err.println("Exception: "+e);
            e.printStackTrace(System.err);
        }
    }

    public static void oneFile (String wmfName, String logName, String outName,
                                Properties props)
        throws Exception
    {
        RecordReader rd = new RecordReader(new FileInputStream(wmfName), props);
        System.err.println("read "+wmfName);
        long t1, t2, t3, t4, t5, t6;
        t1 = System.currentTimeMillis();
        RecordTable table;
        table = rd.readMetaFile();
        t2 = System.currentTimeMillis();
        System.setOut(new PrintStream(new FileOutputStream(logName)));
    	ImageIoRenderer iir;
    	iir = new ImageIoRenderer (props, table);
    	t3 = t4 = t5 = t6 = System.currentTimeMillis();
    	if (props.getProperty("incremental") != null) {
    		int n = table.getSize();
    		for (int i = 2; i <= n; i++) {
    			iir.renderOnly(table, i);
            	t4 = t5 = System.currentTimeMillis();
            	String sno = "" + i;
            	while (sno.length() < 4)
            		sno = "0" + sno;
            	iir.write (outName+"-"+sno+".png");
    		}
    	} else if (props.getProperty("incrementalto") != null) {
    		int upto = Integer.parseInt(props.getProperty("incrementalto").trim());
    		iir.renderOnly(table, upto);
        	t4 = System.currentTimeMillis();
        	System.err.println("write "+outName);
        	t5 = System.currentTimeMillis();
        	iir.write (""+outName);    		
    	} else {
    		iir.render(table);
        	t4 = System.currentTimeMillis();
        	System.err.println("write "+outName);
        	t5 = System.currentTimeMillis();
        	iir.write (""+outName);    		
    	}
    	t6 = System.currentTimeMillis();
    	System.out.println("------------------");
    	System.out.println("reading: "+(t2-t1)+" ms...");
    	System.out.println("rendering: "+(t4-t3)+" ms...");
    	System.out.println("writing: "+(t6-t5)+" ms...");
    	System.out.println("image size is "+iir.getWidth()+"x"+iir.getHeight());
    	System.out.flush();
    }

    public static void main(String[] args)  throws Exception  {
        String path = null;
        boolean bad_usage = false;
        Properties props = new Properties();
        props.setProperty ("alpha", "white");
        props.setProperty ("debug", "1");
        props.setProperty ("timing", "0");
        props.setProperty ("logging", "1");
        props.setProperty ("prefix", "");
        props.setProperty ("format", "png");
        props.setProperty ("mat", "#ffffff");
        props.setProperty ("white", "#ffffff");
        props.setProperty ("width", "-1");
        props.setProperty ("height", "-1");
        props.setProperty ("scale", "20%");
        props.setProperty ("maxsize", "1024");
        props.setProperty ("prohibit", "F_AbortDoc");
        props.setProperty ("default-charset", "cp1250");
    	for (int i = 0; i < args.length; i++) {
    		String opt = args[i];
    		if ("-graymat".equals(opt)) {
    	      	props.setProperty ("mat", "#d8d8c8");
    	       	props.setProperty ("white", "#f0f8f0");
    		} else if ("-normalsize".equals(opt)) {
    	        props.setProperty ("width", "1200");
    	        props.setProperty ("height", "860");
    	        props.setProperty ("scale", "best");
    		} else if ("-noquirks".equals(opt)) {
    			props.setProperty ("ignore-quirks", "yes");
    		} else if ("-incremental".equals(opt)) {
    			props.setProperty ("incremental", "yes");
    		} else if ("-incrementalto".equals(opt)) {
    			props.setProperty ("incrementalto", args[++i]);
    		} else if (opt.startsWith("-")) {
    			bad_usage = true;
    			break;
    		} else if (path != null){
    			bad_usage = true;
    			break;
    		} else {
    			path = opt;
    		}
    	}
        File f = null;
    	boolean one_file = true;
    	if (path == null)
    		bad_usage = true;
    	else {
    		f = new File(path);
    		if (f.isDirectory())
    			one_file = false;
    		else if (!f.isFile())
    			bad_usage = true;
    	}
		if (bad_usage) {
			System.err.println("usage: java net.vitki.wmf.Test"
								+" [-graymat] [-normalsize] [-noquirks]"
								+" [-incremental] [-incrementalto <N>]"
								+" file|directory");
			System.exit(1);
		}
        if (one_file) {
            oneFile(path, props);
        } else {
        	File[] ls = f.listFiles();
        	for (int i=0; i<ls.length; i++)  {
        		String name = ls[i].getName();
        		int l = name.length();
        		boolean ok = l>4 && name.substring(l-4).equalsIgnoreCase(".wmf");
        		if (!ok)
        			ok = l>4 && name.substring(l-4).equalsIgnoreCase(".emf");
        		if (ok)  oneFile(path + "/" + name,props);
        	}
        }
        System.out.println("done");
    }
}
