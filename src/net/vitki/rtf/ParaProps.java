package net.vitki.rtf;

public class ParaProps
{	
    int     no;
    int     align;
    int     list_no;
    int     list_level;
    int     list_type;
    String  bullet;
    boolean page_break;

    public ParaProps()
    {
        clear();
        clearList();
    }
    
    public ParaProps (ParaProps props)
    {
        copyFrom(props);
    }
    
    void clear()
    {
        no = -1;
        align = TextProps.ALIGN_LEFT;
        list_no = -1;
        list_level = -1;
        list_type = -1;
        page_break = false;
    }
    
    void clearList()
    {
        bullet = null;
        page_break = false;
    }
    
    public void copyFrom (ParaProps props)
    {
        this.no = props.no;
        this.align = props.align;
        this.list_no = props.list_no;
        this.list_level = props.list_level;
        this.list_type = props.list_type;
        this.bullet = props.bullet;
        this.page_break = props.page_break;
    }
    
}

