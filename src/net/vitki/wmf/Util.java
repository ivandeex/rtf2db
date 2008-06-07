package net.vitki.wmf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

public abstract class Util  {

    public final static String getString (Object obj)  {
        String s = "";
        String name = obj.getClass().getName();
        int pos = name.lastIndexOf('.');
        if (pos >= 0)   name = name.substring(pos+1);
        pos = name.lastIndexOf('$');
        if (pos >= 0)   name = name.substring(pos+1);
        s += name+"(";
        Field[] field = obj.getClass().getFields();
        for (int i=0; i<field.length; i++)  {
            name = field[i].getName();
            String type = field[i].getType().getName();
            s += name+"=";
            try {
                if ("int".equals(type))
                    s += Integer.toHexString(field[i].getInt(obj))+"h";
                else if ("long".equals(type))
                    s += Long.toHexString(field[i].getLong(obj))+"h";
                else
                    s += "?";
            } catch (IllegalAccessException e) {
                s += "?";
            }
            if (i < field.length-1)
                s += ",";
        }
        s += ")";
        return s;
    }

}
