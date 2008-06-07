package net.vitki.rtf;

public class ListRule
{
    public String  name;
    public int     list_level;
    public String  list_parent_tag;
    public String  list_parent_role;
    public String  list_child_tag;
    public String  list_child_role;
    public String  list_item_tag;
    public String  list_item_role;
    public String  list_break_tag;
    public String  list_num;
    
    public ListRule()
    {
        name = null;
        list_level = 0;
        list_parent_tag = null;
        list_parent_role = null;
        list_child_tag = null;
        list_child_role = null;
        list_item_tag = null;
        list_item_role = null;
        list_break_tag = null;
        list_num = "";
    }
    
    public ListRule (String name, int list_level,
                     String list_parent_tag, String list_parent_role,
                     String list_child_tag, String list_child_role,
                     String list_item_tag, String list_item_role,
                     String list_break_tag, String list_num)
    {
        this.name = name.trim();
        this.list_level = list_level;
        this.list_parent_tag = Util.nullify(list_parent_tag);
        this.list_parent_role = Util.nullify(list_parent_role);
        this.list_child_tag = Util.nullify(list_child_tag);
        this.list_child_role = Util.nullify(list_child_role);
        this.list_item_tag = Util.nullify(list_item_tag);
        this.list_item_role = Util.nullify(list_item_role);
        this.list_break_tag = Util.nullify(list_break_tag);
        this.list_num = list_num.trim().toLowerCase();
    }
}
