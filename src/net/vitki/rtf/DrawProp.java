package net.vitki.rtf;


public class DrawProp implements DrawConst
{
    private int     tag;
    private String  text;
    
    public DrawProp (int tag, String text)
    {
        this.tag = tag;
        this.text = text;
    }
    
    public int getTag()
    {
        return tag;
    }
    
    public String getName()
    {
        return DrawTag.getName(tag);
    }
    
    public String getText()
    {
        return text;
    }
    
    public int getType()
    {
        return DrawTag.getType(tag);
    }
    
    public boolean getBool()
    {
        switch(getType())
        {
            case DrawTag.T_BOOL:
                return "1".equals(text);
            default:
                return false;
        }
    }
    
    public int getInt()
    {
        int ival;
        double dval;
        switch (getType())
        {
            case DrawTag.T_INT:
            case DrawTag.T_LONG:
            case DrawTag.T_VALUE:
            case DrawTag.T_BOOL:
                ival = Integer.parseInt(text);
                break;
            case DrawTag.T_EMU:
                ival = Integer.parseInt(text);
                dval = (double)ival / 360000.0 * 100.0;
                ival = (int)dval;
                break;
            case DrawTag.T_FIXED:
                ival = Integer.parseInt(text);
                dval = (double)ival / 65536.0;
                ival = (int)dval;
                break;
            case DrawTag.T_ANGLE:
                ival = Integer.parseInt(text);
                dval = (double)ival / 65536.0;
                ival = (int)dval;
                break;
            case DrawTag.T_COLOR:
                ival = Integer.parseInt(text);
                break;
            case DrawTag.T_TWIPS:
                ival = Integer.parseInt(text);
                dval = Util.twips2mmd(ival);
                ival = (int)dval;
                break;
            case DrawTag.T_NONE:
            case DrawTag.T_BINARY:
            case DrawTag.T_STRING:
            case DrawTag.T_ARRAY:
            default:
                ival = 0;
                break;
        }
        return ival;
    }
    
    public double getDouble()
    {
        int ival;
        double dval;
        switch (getType())
        {
            case DrawTag.T_INT:
            case DrawTag.T_LONG:
            case DrawTag.T_VALUE:
            case DrawTag.T_BOOL:
                ival = Integer.parseInt(text);
                dval = ival;
                break;
            case DrawTag.T_EMU:
                ival = Integer.parseInt(text);
                dval = (double)ival / 360000.0 * 100.0;
                break;
            case DrawTag.T_FIXED:
                ival = Integer.parseInt(text);
                dval = (double)ival / 65536.0;
                break;
            case DrawTag.T_ANGLE:
                ival = Integer.parseInt(text);
                dval = (double)ival / 65536.0;
                break;
            case DrawTag.T_COLOR:
                ival = Integer.parseInt(text);
                dval = ival;
                break;
            case DrawTag.T_TWIPS:
                text = text.trim();
                ival = Integer.parseInt(text);
                dval = Util.twips2mmd(ival);
                break;
            case DrawTag.T_NONE:
            case DrawTag.T_BINARY:
            case DrawTag.T_STRING:
            case DrawTag.T_ARRAY:
            default:
                dval = 0.0;
                break;
        }
        return dval;
    }
    
    public String getString()
    {
        if (tag == DrawTag._ShapeType)
            return getTypeName(getInt());
        switch (getType())
        {
            case DrawTag.T_INT:
            case DrawTag.T_LONG:
            case DrawTag.T_VALUE:
                return text;
            case DrawTag.T_BOOL:
                return getBool() ? "yes" : "no";
            case DrawTag.T_TWIPS:
            case DrawTag.T_EMU:
                return Util.mmd2mm(getInt());
            case DrawTag.T_FIXED:
                return ""+getInt();
            case DrawTag.T_ANGLE:
                return ""+getInt()+"grad";
            case DrawTag.T_COLOR:
                return ColorProps.getName(getColor());
            case DrawTag.T_NONE:
            case DrawTag.T_BINARY:
            case DrawTag.T_STRING:
            case DrawTag.T_ARRAY:
            default:
                return "["+text+"]";
        }
    }
    
    public int getColor()
    {
        int v = getInt();
        int r = (v >> 0) & 0xff;
        int g = (v >> 8) & 0xff;
        int b = (v >> 16) & 0xff;
        v = (r << 16) | (g << 8) | b;
        return v;
    }
    
    public static String getTypeName (int type)  {
        switch(type)  {
            case D_FREEFORM              :  return "Freeform";
            case D_RECTANGLE             :  return "Rectangle";
            case D_ROUND_RECTANGLE       :  return "RoundRectangle";
            case D_ELLIPSE               :  return "Ellipse";
            case D_DIAMOND               :  return "Diamond";
            case D_ISOSCELES_TRIANGLE    :  return "IsoscelesTriangle";
            case D_RT_TRIANGLE           :  return "RightTriangle";
            case D_PARALLELOGRAM         :  return "Parallelogram";
            case D_TRAPEZOID             :  return "Trapezoid";
            case D_HEXAGON               :  return "Hexagon";
            case D_OCTAGON               :  return "Octagon";
            case D_PLUS_SIGN             :  return "PlusSign";
            case D_STAR                  :  return "Star";
            case D_ARROW                 :  return "Arrow";
            case D_THICK_ARROW           :  return "ThickArrow";
            case D_HOME_PLATE            :  return "HomePlate";
            case D_CUBE                  :  return "Cube";
            case D_BALLOON               :  return "Balloon";
            case D_SEAL                  :  return "Seal";
            case D_ARC                   :  return "Arc";
            case D_LINE                  :  return "Line";
            case D_PLAQUE                :  return "Plaque";
            case D_CAN                   :  return "Can";
            case D_DONUT                 :  return "Donut";
            case D_TEXT_SIMPLE           :  return "TextSimple";
            case D_TEXT_OCTAGON          :  return "TextOctagon";
            case D_TEXT_HEXAGON          :  return "TextHexagon";
            case D_TEXT_CURVE            :  return "TextCurve";
            case D_TEXT_WAVE             :  return "TextWave";
            case D_TEXT_RING             :  return "TextRing";
            case D_TEXT_ON_CURVE         :  return "TextOnCurve";
            case D_TEXT_ON_RING          :  return "TextOnRing";
            case D_CO_1                  :  return "Callout1";
            case D_CO_2                  :  return "Callout2";
            case D_CO_3                  :  return "Callout3";
            case D_ACCENT_CO_1           :  return "AccentCallout1";
            case D_ACCENT_CO_2           :  return "AccentCallout2";
            case D_ACCENT_CO_3           :  return "AccentCallout3";
            case D_BORDER_CO_1           :  return "BorderCallout1";
            case D_BORDER_CO_2           :  return "BorderCallout2";
            case D_BORDER_CO_3           :  return "BorderCallout3";
            case D_ACCENT_BORDER_CO_1    :  return "AccentBorderCallout1";
            case D_ACCENT_BORDER_CO_2    :  return "AccentBorderCallout2";
            case D_ACCENT_BORDER_CO_3    :  return "AccentBorderCallout3";
            case D_RIBBON                :  return "Ribbon";
            case D_RIBBON2               :  return "Ribbon2";
            case D_CHEVRON               :  return "Chevron";
            case D_PENTAGON              :  return "Pentagon";
            case D_NO_SMOKING            :  return "No smoking";
            case D_SEAL8                 :  return "Seal8";
            case D_SEAL16                :  return "Seal16";
            case D_SEAL32                :  return "Seal32";
            case D_WEDGE_RECTANGLE_CO    :  return "WedgeRectangleCallout ";
            case D_WEDGE_RRECT_CO        :  return "WedgeRRectCallout ";
            case D_WEDGE_ELLIPSE_CO      :  return "WedgeEllipseCallout ";
            case D_WAVE                  :  return "Wave";
            case D_FOLDED_CORNER         :  return "FoldedCorner";
            case D_LT_ARROW              :  return "LeftArrow";
            case D_DN_ARROW              :  return "DownArrow";
            case D_UP_ARROW              :  return "UpArrow";
            case D_LT_RT_ARROW           :  return "LeftRightArrow";
            case D_UP_DN_ARROW           :  return "UpDownArrow";
            case D_IRREGULARSEAL1        :  return "IrregularSeal1";
            case D_IRREGULARSEAL2        :  return "IrregularSeal2";
            case D_LIGHTNING_BOLT        :  return "LightningBolt";
            case D_HEART                 :  return "Heart";
            case D_PICTURE_FRAME         :  return "PictureFrame";
            case D_QUAD_ARROW            :  return "QuadArrow";
            case D_LT_ARROW_CO           :  return "LeftArrowCallout";
            case D_RT_ARROW_CO           :  return "RightArrowCallout";
            case D_UP_ARROW_CO           :  return "UpArrowCallout";
            case D_DN_ARROW_CO           :  return "DownArrowCallout";
            case D_LT_RT_ARROW_CO        :  return "LeftRightArrowCallout";
            case D_UP_DN_ARROW_CO        :  return "UpDownArrowCallout";
            case D_QUAD_ARROW_CO         :  return "QuadArrowCallout";
            case D_BEVEL                 :  return "Bevel";
            case D_LT_BRACKET            :  return "LeftBracket";
            case D_RT_BRACKET            :  return "RightBracket";
            case D_LT_BRACE              :  return "LeftBrace";
            case D_RT_BRACE              :  return "RightBrace";
            case D_LT_UP_ARROW           :  return "LeftUpArrow";
            case D_BENT_UP_ARROW         :  return "BentUpArrow";
            case D_BENT_ARROW            :  return "BentArrow";
            case D_SEAL24                :  return "Seal24";
            case D_STRIPED_RT_ARROW      :  return "StripedRightArrow";
            case D_NOTCHED_RT_ARROW      :  return "NotchedRightArrow";
            case D_BLOCK_ARC             :  return "BlockArc";
            case D_SMILEY_FACE           :  return "SmileyFace";
            case D_VERT_SCROLL           :  return "VerticalScroll";
            case D_HOR_SCROLL            :  return "HorizontalScroll";
            case D_CIRCULAR_ARROW        :  return "CircularArrow";
            case D_NOTCHED_CIRC_ARROW    :  return "NotchedCircularArrow";
            case D_U_TURN_ARROW          :  return "UturnArrow";
            case D_CURVED_RT_ARROW       :  return "CurvedRightArrow";
            case D_CURVED_LT_ARROW       :  return "CurvedLeftArrow";
            case D_CURVED_UP_ARROW       :  return "CurvedUpArrow";
            case D_CURVED_DN_ARROW       :  return "CurvedDownArrow";
            case D_CLOUD_CO              :  return "CloudCallout ";
            case D_ELLIPSE_RIBBON        :  return "EllipseRibbon";
            case D_ELLIPSE_RIBBON_2      :  return "EllipseRibbon2";
            case D_FCHART_PROCESS        :  return "FlowChartProcess";
            case D_FCHART_DECISION       :  return "FlowChartDecision";
            case D_FCHART_INPUT_OUTPUT   :  return "FlowChartInputOutput";
            case D_FCHART_PREDEF_PROC    :  return "FlowChartPredefinedProcess";
            case D_FCHART_INTERN_STOR    :  return "FlowChartInternalStorage";
            case D_FCHART_DOCUMENT       :  return "FlowChartDocument";
            case D_FCHART_MULTIDOC       :  return "FlowChartMultidocument";
            case D_FCHART_TERMINATOR     :  return "FlowChartTerminator";
            case D_FCHART_PREPARATION    :  return "FlowChartPreparation";
            case D_FCHART_MANUAL_INPUT   :  return "FlowChartManualInput";
            case D_FCHART_MANUAL_OP      :  return "FlowChartManualOperation";
            case D_FCHART_CONNECTOR      :  return "FlowChartConnector";
            case D_FCHART_PUNCHED_CARD   :  return "FlowChartPunchedCard";
            case D_FCHART_PUNCHED_TAPE   :  return "FlowChartPunchedTape";
            case D_FCHART_SUM_JUNCT      :  return "FlowChartSummingJunction";
            case D_FCHART_OR             :  return "FlowChartOr";
            case D_FCHART_COLLATE        :  return "FlowChartCollate";
            case D_FCHART_SORT           :  return "FlowChartSort";
            case D_FCHART_EXTRACT        :  return "FlowChartExtract";
            case D_FCHART_MERGE          :  return "FlowChartMerge";
            case D_FCHART_OFFL_STORAGE   :  return "FlowChartOfflineStorage";
            case D_FCHART_ONLN_STORAGE   :  return "FlowChartOnlineStorage";
            case D_FCHART_MAGNETIC_TAPE  :  return "FlowChartMagneticTape";
            case D_FCHART_MAGNETIC_DISK  :  return "FlowChartMagneticDisk";
            case D_FCHART_MAGNETIC_DRUM  :  return "FlowChartMagneticDrum";
            case D_FCHART_DISPLAY        :  return "FlowChartDisplay";
            case D_FCHART_DELAY          :  return "FlowChartDelay";
            case D_TEXT_PLAIN_TEXT       :  return "TextPlainText ";
            case D_TEXT_STOP             :  return "TextStop";
            case D_TEXT_TRIANGLE         :  return "TextTriangle";
            case D_TEXT_TRIANGLE_INV     :  return "TextTriangleInverted";
            case D_TEXT_CHEVRON          :  return "TextChevron";
            case D_TEXT_CHEVRON_INV      :  return "TextChevronInverted";
            case D_TEXT_RING_INSIDE      :  return "TextRingInside";
            case D_TEXT_RING_OUTSIDE     :  return "TextRingOutside";
            case D_TEXT_ARCH_UP_CURVE    :  return "TextArchUpCurve";
            case D_TEXT_ARCH_DN_CURVE    :  return "TextArchDownCurve";
            case D_TEXT_CIRCLE_CURVE     :  return "TextCircleCurve";
            case D_TEXT_BUTTON_CURVE     :  return "TextButtonCurve";
            case D_TEXT_ARCH_UP_POUR     :  return "TextArchUpPour";
            case D_TEXT_ARCH_DN_POUR     :  return "TextArchDownPour";
            case D_TEXT_CIRCLE_POUR      :  return "TextCirclePour";
            case D_TEXT_BUTTON_POUR      :  return "TextButtonPour";
            case D_TEXT_CURVE_UP         :  return "TextCurveUp";
            case D_TEXT_CURVE_DN         :  return "TextCurveDown";
            case D_TEXT_CASCADE_UP       :  return "TextCascadeUp";
            case D_TEXT_CASCADE_DN       :  return "TextCascadeDown";
            case D_TEXT_WAVE1            :  return "TextWave1";
            case D_TEXT_WAVE2            :  return "TextWave2";
            case D_TEXT_WAVE3            :  return "TextWave3";
            case D_TEXT_WAVE4            :  return "TextWave4";
            case D_TEXT_INFL             :  return "TextInflate";
            case D_TEXT_DEFL             :  return "TextDeflate";
            case D_TEXT_INFL_BTM         :  return "TextInflateBottom";
            case D_TEXT_DEFL_BTM         :  return "TextDeflateBottom";
            case D_TEXT_INFL_TOP         :  return "TextInflateTop";
            case D_TEXT_DEFL_TOP         :  return "TextDeflateTop";
            case D_TEXT_DEFL_INFL        :  return "TextDeflateInflate";
            case D_TEXT_DEFL_INFL_DEFL   :  return "TextDeflateInflateDeflate";
            case D_TEXT_FADE_RT          :  return "TextFadeRight";
            case D_TEXT_FADE_LT          :  return "TextFadeLeft";
            case D_TEXT_FADE_UP          :  return "TextFadeUp";
            case D_TEXT_FADE_DN          :  return "TextFadeDown";
            case D_TEXT_SLANT_UP         :  return "TextSlantUp";
            case D_TEXT_SLANT_DN         :  return "TextSlantDown";
            case D_TEXT_CAN_UP           :  return "TextCanUp";
            case D_TEXT_CAN_DN           :  return "TextCanDown";
            case D_FCHART_ALT_PROCESS    :  return "FlowChartAlternateProcess";
            case D_FCHART_OFFP_CONN      :  return "FlowChartOffPageConnector";
            case D_CO_90                 :  return "Callout90";
            case D_ACCENT_CO_90          :  return "AccentCallout90";
            case D_BORDER_CO_90          :  return "BorderCallout90";
            case D_ACCENT_BORDER_CO_90   :  return "AccentBorderCallout90";
            case D_LT_RT_UP_ARROW        :  return "LeftRightUpArrow";
            case D_SUN                   :  return "Sun";
            case D_MOON                  :  return "Moon";
            case D_BRACKET_PAIR          :  return "BracketPair";
            case D_BRACE_PAIR            :  return "BracePair";
            case D_SEAL4                 :  return "Seal4";
            case D_DOUBLE_WAVE           :  return "DoubleWave";
            case D_HOST_CONTROL          :  return "HostControl";
            case D_TEXT_BOX              :  return "TextBox";
            default                      :  return "NONE";
        }
    }
    
}