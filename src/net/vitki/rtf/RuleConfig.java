package net.vitki.rtf;

import java.util.Hashtable;

public class RuleConfig
{
    private Hashtable options;
    private Hashtable flags;
    private Hashtable div_rules;
    private Hashtable list_rules;
    private Hashtable subst_rules;
    private Hashtable aliases;
    
    public RuleConfig()
    {
        options = new Hashtable();
        flags = new Hashtable();
        div_rules = new Hashtable();
        list_rules = new Hashtable();
        subst_rules = new Hashtable();
        aliases = new Hashtable();
        defaultOptions();
    }
    
    public void setOption (String name, String value)
    {
        options.put(name, value);
        flags.remove(name);
    }
    
    public String getOption (String name)
    {
        return (String)options.get(name);
    }
    
    public boolean getFlag (String name)
    {
        Boolean bool = (Boolean)flags.get(name);
        if (bool != null)
            return bool.booleanValue();
        String s = (String)options.get(name);
        boolean flag = false;
        if (s != null)  {
            s = s.trim().toLowerCase();
            flag = ("1".equals(s) || "on".equals(s) || "y".equals(s) ||
                    "yes".equals(s) || "t".equals(s) || "true".equals(s));
        }
        flags.put (name, new Boolean(flag));
        return flag;
    }
    
    public void setAlias (String alias, String name)
    {
        aliases.put (alias.trim(), name.trim());
    }
    
    public String getName (String alias)
    {
        return (String)aliases.get(alias);
    }
    
    public void setDivRule (String name, int div_level,
                            String div_tag, String div_role,
                            String div_num)
    {
        DivRule div;
        name = name.trim();
        div = new DivRule (name, div_level, div_tag, div_role, div_num);
        div_rules.put (name, div);
    }
    
    public DivRule getDivRule (String name)
    {
        return (DivRule)div_rules.get(name);
    }
    
    public void setListRule (String name, int list_level,
                             String list_parent_tag, String list_parent_role,
                             String list_child_tag, String list_child_role,
                             String list_item_tag, String list_item_role,
                             String list_break_tag, String list_num)
    {
        ListRule list;
        name = name.trim();
        list = new ListRule (name, list_level,
                             list_parent_tag, list_parent_role,
                             list_child_tag, list_child_role,
                             list_item_tag, list_item_role,
                             list_break_tag, list_num);
        list_rules.put (name, list);
    }
    
    public ListRule getListRule (String name)
    {
        return (ListRule)list_rules.get(name);
    }
    
    public void setSubstRule (String name, String subst_tag, String subst_role)
                              
    {
        SubstRule subst;
        name = name.trim();
        subst = new SubstRule (name, subst_tag, subst_role);
        subst_rules.put (name, subst);
    }
    
    public SubstRule getSubstRule (String name)
    {
        return (SubstRule)subst_rules.get(name);
    }
    
    public void defaultOptions()
    {
        options.put( "encoding", "UTF-8" );
        options.put( "indentation", "2" );
        options.put( "debugging", "false" );
        options.put( "tracing", "false" );
        options.put( "skip-tracing", "false" );
        options.put( "flush-tracing", "false" );
        options.put( "debug-rtf-tables", "false" );
        options.put( "dump-info", "true" );
        options.put( "dump-page-props", "false" );
        options.put( "dump-section-props", "false" );
        options.put( "dump-stylesheet", "false" );
        options.put( "dump-all-styles", "false" );
        options.put( "auto-pictures", "true" );
        options.put( "pixels-per-inch", "120" );
        options.put( "auto-lists", "true" );
        options.put( "guess-div-labels", "true" );
        options.put( "div-label-pattern", "((?:\\d+\\.)+(?:\\d+)?\\s+).*" );
        options.put( "public-dtd", "-//OASIS//DTD DocBook XML V4.1.2//EN" );
        options.put( "system-dtd", "http://www.oasis-open.org/docbook/xml/4.0/docbookx.dtd" );
        options.put( "dump-all-fields", "false" );
        options.put( "trace-fonts", "true" );
        options.put( "dump-shapes", "false" );
        options.put( "draw-shapes", "true" );
        options.put( "plain-output", "false" );
    }

}
