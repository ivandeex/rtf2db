package net.vitki.wmf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

public class Constants extends Hashtable  {

    private String begins;
    private String prefix;

    public Constants (Object obj, String begins, String prefix, boolean reverse)  {
        super();
        this.begins = begins;
        this.prefix = prefix!=null ? prefix : (begins != null ? begins : "");
        if (obj == null)   obj = this;
        Field[] fields = obj.getClass().getFields();
        for (int i=0; i<fields.length; i++)  {
            Field field = fields[i];
            String type = field.getType().getName();
            String name = field.getName();
            if (!"int".equals(type))
                continue;
            int mod = field.getModifiers();
            if (!(Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)))
                continue;
            if (begins != null && !name.startsWith(begins))
                continue;
            Integer value = null;
            try {
                value = new Integer(field.getInt(obj));
            } catch (IllegalAccessException e)  {
                System.err.println("cannot access constants of class "
                                   +obj.getClass().getName()+": "+e);
                System.exit(0);
            }
            if (reverse) {
                put (name, value);
                if (begins != null && name.startsWith(begins))
                    put (name.substring(begins.length()), value);
            } else {
                put (value, name);
            }
        }
    }

    public Constants (Object obj, String begins, String prefix)  {
        this(obj,begins,prefix,false);
    }

    public Constants (String begins, String prefix)  {
        this(null,begins,prefix);
    }

    public Constants (String begins)  {
        this(null,begins,null);
    }

    public String get(int no)  {
        Object obj = super.get(new Integer(no));
        String s = null;
        if (obj == null || (obj instanceof String))  {
            s = (String)obj;
            if (s == null)
            	s = prefix+Integer.toHexString(no)+"h";
        }
        return s;
    }

    public int get (String name)  {
        Object obj = super.get(name);
        Integer i;
        if (obj == null)
            return -1;
        else if (obj instanceof Integer)
            return ((Integer)obj).intValue();
        else
            return -1;
    }

    // windows metafile function calls
    public static final int F_AbortDoc              = 0x0052;
    public static final int F_Arc                   = 0x0817;
    public static final int F_Chord                 = 0x0830;
    public static final int F_DeleteObject          = 0x01f0;
    public static final int F_Ellipse               = 0x0418;
    public static final int F_EndDoc                = 0x005E;
    public static final int F_EndPage               = 0x0050;
    public static final int F_ExcludeClipRect       = 0x0415;
    public static final int F_ExtFloodFill          = 0x0548;
    public static final int F_FillRegion            = 0x0228;
    public static final int F_FloodFill             = 0x0419;
    public static final int F_FrameRegion           = 0x0429;
    public static final int F_IntersectClipRect     = 0x0416;
    public static final int F_InvertRegion          = 0x012A;
    public static final int F_LineTo                = 0x0213;
    public static final int F_MoveTo                = 0x0214;
    public static final int F_OffsetClipRgn         = 0x0220;
    public static final int F_OffsetViewportOrg     = 0x0211;
    public static final int F_OffsetWindowOrg       = 0x020F;
    public static final int F_PaintRegion           = 0x012B;
    public static final int F_PatBlt                = 0x061D;
    public static final int F_Pie                   = 0x081A;
    public static final int F_RealizePalette        = 0x0035;
    public static final int F_Rectangle             = 0x041B;
    public static final int F_ResetDc               = 0x014C;
    public static final int F_ResizePalette         = 0x0139;
    public static final int F_RestoreDC             = 0x0127;
    public static final int F_RoundRect             = 0x061C;
    public static final int F_SaveDC                = 0x001E;
    public static final int F_ScaleViewportExt      = 0x0412;
    public static final int F_ScaleWindowExt        = 0x0410;
    public static final int F_SelectClipRegion      = 0x012C;
    public static final int F_SelectObject          = 0x012D;
    public static final int F_SelectPalette         = 0x0234;
    public static final int F_SetTextAlign          = 0x012E;
    public static final int F_SetBkColor            = 0x0201;
    public static final int F_SetBkMode             = 0x0102;
    public static final int F_SetDibToDev           = 0x0d33;
    public static final int F_SetMapMode            = 0x0103;
    public static final int F_SetMapperFlags        = 0x0231;
    public static final int F_SetPalEntries         = 0x0037;
    public static final int F_SetPixel              = 0x041F;
    public static final int F_SetPolyFillMode       = 0x0106;
    public static final int F_SetRelabs             = 0x0105;
    public static final int F_SetROP2               = 0x0104;
    public static final int F_SetStretchBltMode     = 0x0107;
    public static final int F_SetTextCharExtra      = 0x0108;
    public static final int F_SetTextColor          = 0x0209;
    public static final int F_SetTextJustification  = 0x020A;
    public static final int F_SetViewportExt        = 0x020E;
    public static final int F_SetViewportOrg        = 0x020D;
    public static final int F_SetWindowExt          = 0x020C;
    public static final int F_SetWindowOrg          = 0x020B;
    public static final int F_StartDoc              = 0x014D;
    public static final int F_StartPage             = 0x004F;
    public static final int F_AnimatePalette        = 0x0436;
    public static final int F_BitBlt                = 0x0922;
    public static final int F_CreateBitmap          = 0x06FE;
    public static final int F_CreateBitmapIndirect  = 0x02FD;
    public static final int F_CreateBrush           = 0x00F8;
    public static final int F_CreateBrushIndirect   = 0x02FC;
    public static final int F_CreateFontIndirect    = 0x02FB;
    public static final int F_CreatePalette         = 0x00F7;
    public static final int F_CreatePatternBrush    = 0x01F9;
    public static final int F_CreatePenIndirect     = 0x02FA;
    public static final int F_CreateRegion          = 0x06FF;
    public static final int F_DibBitblt             = 0x0940;
    public static final int F_DibCreatePatternBrush = 0x0142;
    public static final int F_DibStretchBlt         = 0x0B41;
    public static final int F_DrawText              = 0x062F;
    public static final int F_Escape                = 0x0626;
    public static final int F_ExtTextOut            = 0x0A32;
    public static final int F_Polygon               = 0x0324;
    public static final int F_PolyPolygon           = 0x0538;
    public static final int F_Polyline              = 0x0325;
    public static final int F_TextOut               = 0x0521;
    public static final int F_StretchBlt            = 0x0B23;
    public static final int F_StretchDiBits         = 0x0F43;

    // pen style
    public static final int PS_ENDCAP_ROUND   = 0x00000000;
    public static final int PS_ENDCAP_SQUARE  = 0x00000100;
    public static final int PS_ENDCAP_FLAT    = 0x00000200;
    public static final int PS_ENDCAP_MASK    = 0x00000F00;

    public static final int PS_JOIN_ROUND     = 0x00000000;
    public static final int PS_JOIN_BEVEL     = 0x00001000;
    public static final int PS_JOIN_MITER     = 0x00002000;
    public static final int PS_JOIN_MASK      = 0x0000F000;

    public static final int PS_COSMETIC       = 0x00000000;
    public static final int PS_GEOMETRIC      = 0x00010000;
    public static final int PS_TYPE_MASK      = 0x000F0000;

    public static final int PS_SOLID          = 0;
    public static final int PS_DASH           = 1;      /* -------  */
    public static final int PS_DOT            = 2;      /* .......  */
    public static final int PS_DASHDOT        = 3;      /* _._._._  */
    public static final int PS_DASHDOTDOT     = 4;      /* _.._.._  */
    public static final int PS_NULL           = 5;
    public static final int PS_INSIDEFRAME    = 6;
    public static final int PS_USERSTYLE      = 7;
    public static final int PS_ALTERNATE      = 8;
    public static final int PS_STYLE_MASK     = 0xFF;

    // brush style
    public static final int BS_SOLID           = 0 ;
    public static final int BS_NULL            = 1 ;
    public static final int BS_HATCHED         = 2 ;
    public static final int BS_PATTERN         = 3 ;
    public static final int BS_INDEXED         = 4 ;
    public static final int BS_DIBPATTERN      = 5 ;
    public static final int BS_DIBPATTERNPT    = 6 ;
    public static final int BS_PATTERN8X8      = 7 ;
    public static final int BS_DIBPATTERN8X8   = 8 ;
    public static final int BS_MONOPATTERN     = 9 ;

    // hatch style
    public static final int HS_HORIZONTAL      = 0 ;      /* ----- */
    public static final int HS_VERTICAL        = 1 ;      /* ||||| */
    public static final int HS_FDIAGONAL       = 2 ;      /* \\\\\ */
    public static final int HS_BDIAGONAL       = 3 ;      /* ///// */
    public static final int HS_CROSS           = 4 ;      /* +++++ */
    public static final int HS_DIAGCROSS       = 5 ;      /* xxxxx */

    // raster operation
    public static final int ROP_SRCCOPY      = 0x00CC0020; // dest = source
    public static final int ROP_SRCPAINT     = 0x00EE0086; // dest = source OR dest
    public static final int ROP_SRCAND       = 0x008800C6; // dest = source AND dest
    public static final int ROP_SRCINVERT    = 0x00660046; // dest = source XOR dest
    public static final int ROP_SRCERASE     = 0x00440328; // dest = source AND (NOT dest )
    public static final int ROP_NOTSRCCOPY   = 0x00330008; // dest = (NOT source)
    public static final int ROP_NOTSRCERASE  = 0x001100A6; // dest = (NOT src) AND (NOT dest)
    public static final int ROP_MERGECOPY    = 0x00C000CA; // dest = (source AND pattern)
    public static final int ROP_MERGEPAINT   = 0x00BB0226; // dest = (NOT source) OR dest
    public static final int ROP_PATCOPY      = 0x00F00021; // dest = pattern
    public static final int ROP_PATPAINT     = 0x00FB0A09; // dest = DPSnoo
    public static final int ROP_PATINVERT    = 0x005A0049; // dest = pattern XOR dest
    public static final int ROP_DSTINVERT    = 0x00550009; // dest = (NOT dest)
    public static final int ROP_BLACKNESS    = 0x00000042; // dest = BLACK
    public static final int ROP_WHITENESS    = 0x00FF0062; // dest = WHITE

    // raster operation 2
    public static final int R2_BLACK          = 1;   //  0
    public static final int R2_NOTMERGEPEN    = 2;   // DPon
    public static final int R2_MASKNOTPEN     = 3;   // DPna
    public static final int R2_NOTCOPYPEN     = 4;   // PN
    public static final int R2_MASKPENNOT     = 5;   // PDna
    public static final int R2_NOT            = 6;   // Dn
    public static final int R2_XORPEN         = 7;   // DPx
    public static final int R2_NOTMASKPEN     = 8;   // DPan
    public static final int R2_MASKPEN        = 9;   // DPa
    public static final int R2_NOTXORPEN      = 10;  // DPxn
    public static final int R2_NOP            = 11;  // D
    public static final int R2_MERGENOTPEN    = 12;  // DPno
    public static final int R2_COPYPEN        = 13;  // P
    public static final int R2_MERGEPENNOT    = 14;  // PDno
    public static final int R2_MERGEPEN       = 15;  // DPo
    public static final int R2_WHITE          = 16;  // 1

    // background mode
    public static final int BKM_TRANSPARENT  = 1;
    public static final int BKM_OPAQUE       = 2;

    // poly fill mode
    public static final int PFM_ALTERNATE  = 1;
    public static final int PFM_WINDING    = 2;

    // stretch blt mode
    public static final int SBM_BLACKONWHITE = 1;
    public static final int SBM_WHITEONBLACK = 2;
    public static final int SBM_COLORONCOLOR = 3;
    public static final int SBM_HALFTONE     = 4;

    // text align
    public static final int TA_NOUPDATECP   = 0;
    public static final int TA_UPDATECP     = 1;
    public static final int TA_LEFT         = 0;
    public static final int TA_RIGHT        = 2;
    public static final int TA_CENTER       = 6;
    public static final int TA_TOP          = 0;
    public static final int TA_BOTTOM       = 8;
    public static final int TA_BASELINE     = 24;
    public static final int TA_RTLREADING   = 256;

    // extended text out flags
    public static final int ETO_OPAQUE         = 0x0002;
    public static final int ETO_CLIPPED        = 0x0004;
    public static final int ETO_GLYPH_INDEX    = 0x0010;
    public static final int ETO_RTLREADING     = 0x0080;
    public static final int ETO_NUMERICSLOCAL  = 0x0400;
    public static final int ETO_NUMERICSLATIN  = 0x0800;
    public static final int ETO_IGNORELANGUAGE = 0x1000;
    public static final int ETO_PDY            = 0x2000;

    // object handlers
    public static final int OBJ_PEN          = 1;
    public static final int OBJ_BRUSH        = 2;
    public static final int OBJ_DC           = 3;
    public static final int OBJ_METADC       = 4;
    public static final int OBJ_PAL          = 5;
    public static final int OBJ_FONT         = 6;
    public static final int OBJ_BITMAP       = 7;
    public static final int OBJ_REGION       = 8;
    public static final int OBJ_METAFILE     = 9;
    public static final int OBJ_MEMDC        = 10;
    public static final int OBJ_EXTPEN       = 11;
    public static final int OBJ_ENHMETADC    = 12;
    public static final int OBJ_ENHMETAFILE  = 13;

    // modes for SetMapMode
    public static final int MM_TEXT        = 1;    // each unit is 1pt
    public static final int MM_LOMETRIC    = 2;    // each unit is 0.1mm
    public static final int MM_HIMETRIC    = 3;    // each unit is 0.01mm
    public static final int MM_LOENGLISH   = 4;    // each unit is 0.01 inch
    public static final int MM_HIENGLISH   = 5;    // each unit is 0.001 inch
    public static final int MM_TWIPS       = 6;    // each unit is 1/1440 inch
    public static final int MM_ISOTROPIC   = 7;    // scale depends on window/viewport extents
    public static final int MM_ANISOTROPIC = 8;    // scale depends on window/viewport extents
    public static final int MM_DPI         = 9;    // isotropic, placeable meta file

    // enhanced metafile function codes
    public static final int EMR_AbortPath               = 68;
    public static final int EMR_AngleArc                = 41;
    public static final int EMR_Arc                     = 45;
    public static final int EMR_ArcTo                   = 55;
    public static final int EMR_BeginPath               = 59;
    public static final int EMR_BitBlt                  = 76;
    public static final int EMR_Vhord                   = 46;
    public static final int EMR_CloseFigure             = 61;
    public static final int EMR_CreateBrushIndirect     = 39;
    public static final int EMR_CreateDibPatternBrushPt = 94;
    public static final int EMR_CreateMonoBrush         = 93;
    public static final int EMR_CreatePalette           = 49;
    public static final int EMR_CreatePen               = 38;
    public static final int EMR_DeleteObject            = 40;
    public static final int EMR_Ellipse                 = 42;
    public static final int EMR_EndPath                 = 60;
    public static final int EMR_EOF                     = 14;
    public static final int EMR_ExcludeClipRect         = 29;
    public static final int EMR_ExtCreateFontIndirectW  = 82;
    public static final int EMR_ExtCreatePen            = 95;
    public static final int EMR_ExtFloodFill            = 53;
    public static final int EMR_ExtSelectClipRgn        = 75;
    public static final int EMR_ExtTextOutA             = 83;
    public static final int EMR_ExtTextOutW             = 84;
    public static final int EMR_FillPath                = 62;
    public static final int EMR_FillRgn                 = 71;
    public static final int EMR_FlattenPath             = 65;
    public static final int EMR_FrameRgn                = 72;
    public static final int EMR_GdiComment              = 70;
    public static final int EMR_Header                  = 1;
    public static final int EMR_IntersectClipRect       = 30;
    public static final int EMR_InvertRgn               = 73;
    public static final int EMR_LineTo                  = 54;
    public static final int EMR_SetPixelV               = 15;
    public static final int EMR_MaskBlt                 = 78;
    public static final int EMR_ModifyWorldTransform    = 36;
    public static final int EMR_MoveToEx                = 27;
    public static final int EMR_OffsetClipRgn           = 26;
    public static final int EMR_PaintRgn                = 74;
    public static final int EMR_Pie                     = 47;
    public static final int EMR_PlgBlt                  = 79;
    public static final int EMR_PolyBezier              = 2;
    public static final int EMR_PolyBezier16            = 85;
    public static final int EMR_PolyBezierTo            = 5;
    public static final int EMR_PolyBezierTo16          = 88;
    public static final int EMR_PolyDraw                = 56;
    public static final int EMR_PolyDraw16              = 92;
    public static final int EMR_PolyGon                 = 3;
    public static final int EMR_PolyGon16               = 86;
    public static final int EMR_PolyLine                = 4;
    public static final int EMR_PolyLine16              = 87;
    public static final int EMR_PolyLineTo              = 6;
    public static final int EMR_PolyLineTo16            = 89;
    public static final int EMR_PolyPolygon             = 8;
    public static final int EMR_PolyPolygon16           = 91;
    public static final int EMR_PolyPolyLine            = 7;
    public static final int EMR_PolyPolyLine16          = 90;
    public static final int EMR_PolyTextOutA            = 96;
    public static final int EMR_PolyTextOutW            = 97;
    public static final int EMR_RealizePalette          = 52;
    public static final int EMR_Rectangle               = 43;
    public static final int EMR_ResizePalette           = 51;
    public static final int EMR_RestoreDC               = 34;
    public static final int EMR_RoundRect               = 44;
    public static final int EMR_SaveDC                  = 33;
    public static final int EMR_ScaleViewportExtEx      = 31;
    public static final int EMR_ScaleWindowExtEx        = 32;
    public static final int EMR_SelectClipPath          = 67;
    public static final int EMR_SelectObject            = 37;
    public static final int EMR_SelectPalette           = 48;
    public static final int EMR_SetArcDirection         = 57;
    public static final int EMR_SetBkColor              = 25;
    public static final int EMR_SetBkMode               = 18;
    public static final int EMR_SetBrushOrgEx           = 13;
    public static final int EMR_SetColorAdjustment      = 23;
    public static final int EMR_SetDiBitsToDevice       = 80;
    public static final int EMR_SetMapMode              = 17;
    public static final int EMR_SetMapperFlags          = 16;
    public static final int EMR_SetMetArgN              = 28;
    public static final int EMR_SetMiterLimit           = 58;
    public static final int EMR_SetPaletteEntries       = 50;
    public static final int EMR_SetPolyFillMode         = 19;
    public static final int EMR_SetRop2                 = 20;
    public static final int EMR_SetStretchBltMode       = 21;
    public static final int EMR_SetTextAlign            = 22;
    public static final int EMR_SetTextColor            = 24;
    public static final int EMR_SetViewportExtEx        = 11;
    public static final int EMR_SetViewportOrgEx        = 12;
    public static final int EMR_SetWindowExtEx          = 9;
    public static final int EMR_SetWindowOrgEx          = 10;
    public static final int EMR_SetWorldTransform       = 35;
    public static final int EMR_StretchBlt              = 77;
    public static final int EMR_StretchDiBits           = 81;
    public static final int EMR_StrokeAndFillPath       = 63;
    public static final int EMR_StrokePath              = 64;
    public static final int EMR_WidenPath               = 66;

    // stock (standard) objects
    public static final int STOCK_WHITE_BRUSH         = 0;
    public static final int STOCK_LTGRAY_BRUSH        = 1;
    public static final int STOCK_GRAY_BRUSH          = 2;
    public static final int STOCK_DKGRAY_BRUSH        = 3;
    public static final int STOCK_BLACK_BRUSH         = 4;
    public static final int STOCK_NULL_BRUSH          = 5;
    public static final int STOCK_HOLLOW_BRUSH        = STOCK_NULL_BRUSH;
    public static final int STOCK_WHITE_PEN           = 6;
    public static final int STOCK_BLACK_PEN           = 7;
    public static final int STOCK_NULL_PEN            = 8;
    public static final int STOCK_OEM_FIXED_FONT      = 10;
    public static final int STOCK_ANSI_FIXED_FONT     = 11;
    public static final int STOCK_ANSI_VAR_FONT       = 12;
    public static final int STOCK_SYSTEM_FONT         = 13;
    public static final int STOCK_DEVICE_DEFAULT_FONT = 14;
    public static final int STOCK_DEFAULT_PALETTE     = 15;
    public static final int STOCK_SYSTEM_FIXED_FONT   = 16;
    
    static final long serialVersionUID = 0x1aec0001; 
}
