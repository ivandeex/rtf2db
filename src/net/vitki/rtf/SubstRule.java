package net.vitki.rtf;

public class SubstRule
{
    public String  name;
    public String  subst_tag;
    public String  subst_role;
    
    public SubstRule()
    {
        name = null;
        subst_tag = null;
        subst_role = null;
    }
    
    public SubstRule(String name, String subst_tag, String subst_role)
    {
        this.name = name.trim();
        this.subst_tag = Util.nullify(subst_tag);
        this.subst_role = Util.nullify(subst_role);
    }
}
