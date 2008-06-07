package net.vitki.rtf;

import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class InfoProps {

    public  String   title;
    public  String   subject;
    public  String   category;
    public  String   author;
    public  String   editor;
    public  String   manager;
    public  String   company;
    public  int      version;
    public  int      changeno;
    public  Date     created;
    public  Date     edited;
    public  Date     printed;
    public  String[] keywords;

    public InfoProps()
    {
        title = subject = category = "";
        author = editor = manager = company = "";
        version = changeno = 0;
        created = new Date();
        edited  = new Date();
        printed = new Date();
        keywords = null;
    }

    public void setKeywords (String str)
    {
        StringTokenizer st = new StringTokenizer (str, " \t\r\n\f,;./-|&\\\"'[](){}<>");
        if (st.countTokens() == 0)  {
            keywords = null;
            return;
        }
        keywords = new String [st.countTokens()];

        for (int i=0; i<keywords.length; i++)
            keywords[i] = st.nextToken();
    }
    
    public void print (PrintWriter out)
    {
        out.println("<info>");
        out.println("  <content title=["+title+"] subject=["+subject+"] category=["+category+"] />");
        out.println("  <people author=["+author+"] editor=["+editor+"] manager=["+manager+"] />");
        out.println("  <change version="+version+" changeno="+changeno+" />");
        out.println("  <unit company=["+company+"] />");
        if (keywords != null)  {
            out.print("  <keywords set=");
            for (int i=0; i<keywords.length; i++)
                out.print("["+keywords[i]+"]");
            out.println(" />");
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm,dd.MM.yyyy");
        out.println("  <time created=["+df.format(created)+"]"
                    +" edited=["+df.format(edited)+"]"
                    +" printed=["+df.format(printed)+"] />");
        out.println("</info>");
    }
    
    public void dump (XmlWriter out)  throws SAXException
    {
        Attributes atts = out.newAttr();
        out.startElement("author");
        out.charElement("othername", author);
        out.endElement("author");
        out.startElement("editor"); 
        out.charElement("othername", editor);
        out.endElement("editor");
        out.startElement("manager");
        out.charElement("othername", manager);
        out.endElement("manager");
        out.charElement("orgname", company);
        out.charAttrElement("edition", "role", "version", ""+version);
        out.charAttrElement("pubsnumber", "role", "scn", ""+changeno);
        out.startElement("subjectset");
        out.startElement("subject", "role", "subject");
        out.charElement("subjectterm", subject);
        out.endElement("subject");
        out.startElement("subject", "role", "category");
        out.charElement("subjectterm", category);
        out.endElement("subject");
        out.endElement("subjectset");
        dumpDate (out, "created", created);
        dumpDate (out, "edited", edited);
        dumpDate (out, "printed", printed);
        if (keywords != null)  {
            out.startElement ("keywordset");
            for (int no=0; no < keywords.length; no++)
                out.charElement ("keyword", keywords[no]);
            out.endElement ("keywordset");
        }
    }

    private void dumpDate (XmlWriter out, String role, Date stamp)
    throws SAXException
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime (stamp);
        Attributes atts = out.newAttr();
        out.addAttr (atts, "role", role);
        out.addAttr (atts, "year", cal.get(Calendar.YEAR));
        out.addAttr (atts, "month", cal.get(Calendar.MONTH)+1);
        out.addAttr (atts, "day", cal.get(Calendar.DAY_OF_MONTH));
        out.addAttr (atts, "hour", cal.get(Calendar.HOUR_OF_DAY));
        out.addAttr (atts, "min", cal.get(Calendar.MINUTE));
        out.emptyElement ("date", atts);
    }

}
