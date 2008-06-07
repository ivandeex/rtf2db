package net.vitki.rtf;

public class DivRule
{
    public String  name;
    public int     div_level;
    public String  div_tag;
    public String  div_role;
    public String  div_num;
    
    public DivRule()
    {
        name   = null;
        div_level = 0;
        div_tag = null;
        div_role = null;
        div_num = "";
    }
    
    public DivRule (String name, int div_level,
                    String div_tag, String div_role,
                    String div_num)
    {
        this.name = name.trim();
        this.div_level = div_level;
        this.div_tag = Util.nullify(div_tag);
        this.div_role = Util.nullify(div_role);
        this.div_num = div_num.trim().toLowerCase();
    }
}