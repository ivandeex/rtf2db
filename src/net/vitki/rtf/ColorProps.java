package net.vitki.rtf;

import java.io.PrintWriter;

public class ColorProps
{
    int  red;
    int  green;
    int  blue;
    public ColorProps ()  {
        clear();
    }
    public void clear()  {
        red = green = blue = 0;
    }
    public int getValue()  {
        return (((red & 0xff) << 16) + ((green & 0xff) << 8) + (blue & 0xff));
    }
    public static String toHexString (int value)  {
        String s = Integer.toHexString(value);
        while (s.length() < 6)  s = "0"+s;
        return "#"+s;
    }
    public String toHexString()  {
        return toHexString(getValue());
    }
    public static String getName(int value)  {
        switch(value)  {
            case 0x000000:  return "black";
            case 0xc0c0c0:  return "silver";
            case 0x808080:  return "gray";
            case 0xffffff:  return "white";
            case 0x800000:  return "maroon";
            case 0xff0000:  return "red";
            case 0x800080:  return "purple";
            case 0xff00ff:  return "fuchsia";
            case 0x008000:  return "green";
            case 0x00ff00:  return "lime";
            case 0x808000:  return "olive";
            case 0xffff00:  return "yellow";
            case 0x000080:  return "navy";
            case 0x0000ff:  return "blue";
            case 0x008080:  return "teal";
            case 0x00ffff:  return "aqua";
            default:        return toHexString(value);
        }
    }
    public String getName() {
        return getName(getValue());
    }
    public void print (PrintWriter out, int no) {
        out.println("  <color no="+no+" value="+getName()+" />");
    }
}
