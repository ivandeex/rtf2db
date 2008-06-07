package net.vitki.rtf;

import java.io.File;

public class Converter extends DocAnalyser
{    
    
    public static void main (String[] args) throws Exception
    {
        String name = "qqq";
        String dir = "d:/java/rtf/test/";
        String file = dir+name;
        String rules = dir+"options.opt";
        if (args.length > 0)  file = args[0];
        if (args.length > 1)  rules = args[1];
        convert (file, rules, true);
    }
    
    public static void convert (String input_file, String rule_file,
                                boolean verbose)
    throws RtfException
    {
        if (input_file == null || rule_file == null)
            throw new RtfException("illegal argument");        
        Converter aa;
        aa = new Converter(rule_file);
        input_file = input_file.trim();
        String prefix = input_file;
        boolean need_convert = false;
        if (input_file.toLowerCase().endsWith(".rtf"))  {
            prefix = input_file.substring(0,input_file.length()-4);
            need_convert = false;
        } else if (input_file.toLowerCase().endsWith(".doc")) {
            prefix = input_file.substring(0,input_file.length()-4);
            need_convert = true;
        } else {
            prefix = input_file;
            File rtf = new File(prefix+".rtf");
            File doc = new File(prefix+".doc");
            if (doc.exists() && !rtf.exists())
                need_convert = true;
        }
        String doc_name = prefix+".doc";
        String rtf_name = prefix+".rtf";
        if (need_convert)  {
            String host = aa.getRules().getOption("converter-host");
            if (host == null || "".equals(host))
                throw new RtfException("doc-to-rtf conversion host not set");
            WordConverter wc = new WordConverter(host);
            long sms = System.currentTimeMillis();
            boolean ok = wc.convert(doc_name,rtf_name);
            if (!ok)
                throw new RtfException("conversion of "+doc_name+" aborted "+
                                       "at stage "+wc.getStage()+
                                       " with exception "+
                                       wc.getException().toString());
            long ems = System.currentTimeMillis();
            double dms = (Math.round((ems-sms) / 100.0)) / 10.0;
            if (verbose)
                System.out.println("conversion of "+doc_name+" done in "+
                                   dms+" seconds");
        }
        aa.setAllFiles(prefix);
        long sms = System.currentTimeMillis();
        aa.parse();
        aa.dump();
        long ems = System.currentTimeMillis();
        aa.flushStreams();
        double dms = (Math.round((ems-sms) / 100.0)) / 10.0;
        if (verbose)
            System.out.println("conversion of "+rtf_name+
                               " done in "+dms+" seconds with "+
                               aa.getGcCount()+" gc");
    }
    
    public Converter(RuleConfig cfg) throws RtfException
    {
        super(cfg);
    }
    
    public Converter(String file_name) throws RtfException
    {
        super(file_name);
    }
    
    public void setAllFiles (String prefix)  throws RtfException
    {
        super.setAllFiles(prefix);
    }
    
}
