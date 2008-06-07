package net.vitki.rtf;

public class StyleTransform
{
    public String  name;
    public String  canonic_name;
    public int     list_level;
    public String  list_parent_tag;
    public String  list_parent_role;
    public String  list_child_tag;
    public String  list_child_role;
    public String  list_break_tag;
    public String  list_item_tag;
    public String  list_item_role;
    public String  list_num;
    public int     list_type;
    public boolean list_ordered;
    public boolean list_auto;
    public int     list_no;
    public int     list_children;
    
    public int     div_level;
    public String  div_tag;
    public String  div_role;
    public String  div_num;
    public int     div_para_count;

    public String  subst_tag;
    public String  subst_role;

    public StyleTransform()
    {
        name = null;
        canonic_name = null;
        list_level = 0;
        list_parent_tag = null;
        list_parent_role = null;
        list_child_tag = null;
        list_child_role = null;
        list_break_tag = null;
        list_item_tag = null;
        list_item_role = null;
        list_type = -1;
        list_ordered = false;
        list_auto = false;
        list_no = -1;
        list_num = "";
        list_children = 0;
        div_level = 0;
        div_tag = null;
        div_role = null;
        div_num = "";
        div_para_count = 0;
        subst_tag = null;
        subst_role = null;
    }
    
    public void copyFrom (ListRule list)
    {
        this.list_level = list.list_level;
        this.list_parent_tag = list.list_parent_tag;
        this.list_parent_role = list.list_parent_role;
        this.list_child_tag = list.list_child_tag;
        this.list_child_role = list.list_child_role;
        this.list_break_tag = list.list_break_tag;
        this.list_item_tag = list.list_item_tag;
        this.list_item_role = list.list_item_role;
        this.list_num = list.list_num;
        this.list_ordered =  this.list_num.startsWith("num");
    }
    
    public void copyFrom (DivRule div)
    {
        this.div_level = div.div_level;
        this.div_tag = div.div_tag;
        this.div_role = div.div_role;
        this.div_num = div.div_num;
    }

    public void copyFrom (SubstRule subst)
    {
        this.subst_tag = subst.subst_tag;
        this.subst_role = subst.subst_role;
    }
}
