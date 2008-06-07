package net.vitki.rtf;

import java.io.PrintWriter;

public class ListProps
{
    public int      no;  // used with overrides
    public int      id;
    public int      tmpl_id;
    public String   name;
    public boolean  hybrid;

    public String   style_name;
    public int      style_id;
    public boolean  named_style;

    public  int  level_count;
    public  int  level_qty;
    public  LevelProps[] levels;

    public ListProps ()  {
        no = 0;
        id = -1;
        tmpl_id = -1;
        name = null;
        hybrid = false;
        style_name = null;
        style_id = -1;
        named_style = false;
        level_count = 0;
        level_qty = 1;
        levels = new LevelProps[9];
        for (int i=0; i<9; i++)
            levels[i] = null;
    }

    public void copyFrom (ListProps cloned)  {
        this.no          = cloned.no;
        this.id          = cloned.id;
        this.tmpl_id     = cloned.tmpl_id;
        this.name        = cloned.name;
        this.hybrid      = cloned.hybrid;
        this.style_name  = cloned.style_name;
        this.style_id    = cloned.style_id;
        this.named_style = cloned.named_style;
        this.level_count = cloned.level_count;
        this.level_qty   = cloned.level_qty;
        this.levels = new LevelProps[9];
        for (int i=0; i<9; i++)  {
            if (cloned.levels[i] == null)
                levels[i] = null;
            else  {
                levels[i] = new LevelProps();
                levels[i].copyFrom (cloned.levels[i]);
            }
        }
    }

    public void setStyleName (String name)  {
        this.style_name = name;
        this.named_style = true;
    }

    public void setStyleId (int id)  {
        this.style_id = id;
        this.named_style = false;
    }

    public void setHybrid (boolean flag)  {
        this.hybrid = flag;
        level_qty = flag ? 9 : 1;
    }

    public void addLevel (LevelProps llp) throws RtfException {
        if (level_count >= 9)
            throw new RtfException ("list level count exceeded");
        levels[level_count++] = llp;
    }

    public void print (PrintWriter out, String tag)
    {
        out.println("  <"+tag+" no="+no
                    +" id="+id+" name=["+name+"]"
                    +" type="+(hybrid ? "hybrid" : "simple")
                    +" style="+(named_style ? style_name : String.valueOf(style_id))
                    +" count="+level_count+">");
        for (int j=0; j<level_qty; j++)
            levels[j].print(out);
        out.println("  </"+tag+">");
    }

}
